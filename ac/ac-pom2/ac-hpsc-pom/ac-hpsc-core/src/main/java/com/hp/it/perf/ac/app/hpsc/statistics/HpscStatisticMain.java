package com.hp.it.perf.ac.app.hpsc.statistics;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.hp.it.perf.ac.app.hpsc.beans.HPJVMGCLog;
import com.hp.it.perf.ac.app.hpsc.beans.OpenPortalLog;
import com.hp.it.perf.ac.app.hpsc.beans.PortletBusinessLog;
import com.hp.it.perf.ac.app.hpsc.beans.PortletErrorLog;
import com.hp.it.perf.ac.app.hpsc.beans.PortletErrortraceLog;
import com.hp.it.perf.ac.app.hpsc.beans.PortletPerformanceLog;
import com.hp.it.perf.ac.app.hpsc.beans.SPFPerformanceLog;
import com.hp.it.perf.ac.app.hpsc.beans.SPFPortalLog;
import com.hp.it.perf.ac.app.hpsc.beans.SPFWebAccessLog;
import com.hp.it.perf.ac.load.common.AcPredicate;
import com.hp.it.perf.ac.load.content.AcArchiveContentFetcher;
import com.hp.it.perf.ac.load.content.AcCompositeContentFetcher;
import com.hp.it.perf.ac.load.content.AcReaderContent;
import com.hp.it.perf.ac.load.content.AcContentFetcher;
import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcContentMetadata;
import com.hp.it.perf.ac.load.content.AcInputStreamContent;
import com.hp.it.perf.ac.load.content.AcLoadException;
import com.hp.it.perf.ac.load.content.AcStreamSuppliers;
import com.hp.it.perf.ac.load.parse.AcTextStreamProcessor;
import com.hp.it.perf.ac.load.parse.impl.TextPatternScanner;
import com.hp.it.perf.ac.load.parse.plugins.AcProcessErrorCheckPlugin;
import com.hp.it.perf.ac.load.parse.plugins.AcTextProcessLoggingPlugin;
import com.hp.it.perf.ac.load.parse.plugins.AcTextProcessPluginManager;
import com.hp.it.perf.ac.load.util.Timeable;

public class HpscStatisticMain {

	private static List<Mapping<?>> processorClasses = new ArrayList<Mapping<?>>();
	private static Map<String, Class<?>> logClasses = new HashMap<String, Class<?>>();

	static {
		// register beans
		registerLogClass("SPFPerformance", SPFPerformanceLog.class);
		registerLogClass("Business", PortletBusinessLog.class);
		registerLogClass("Error", PortletErrorLog.class);
		registerLogClass("Errortrace", PortletErrortraceLog.class);
		registerLogClass("Performance", PortletPerformanceLog.class);
		registerLogClass("Portal", SPFPortalLog.class);
		registerLogClass("OpenPortal", OpenPortalLog.class);
		registerLogClass("GC", HPJVMGCLog.class);
		registerLogClass("WebAccess", SPFWebAccessLog.class);

		// register processor
		registerProcessor(PortalPerfStatisticProcessor.class);
		registerProcessor(PortletPerfStatisticProcessor.class);
		registerProcessor(OpenPortalLogProcessor.class);
		registerProcessor(PortalReqWSRPStatisticProcessor.class);
		registerProcessor(PortalErrorStatisticProcessor.class);
		registerProcessor(SPFWebAccessStatisticsProcessor.class);
		registerProcessor(PortletBizStatisticProcessor.class);
	}

	private static class Mapping<T> {
		Class<T> type;
		Class<? extends StatisticProcessor<T>> processorType;

		public Mapping(Class<T> type,
				Class<? extends StatisticProcessor<T>> processorType) {
			this.type = type;
			this.processorType = processorType;
		}

		public void registerProcessor(HpscStatisticContentHandler handler) {
			try {
				handler.addProcessor(type, processorType.newInstance());
			} catch (Exception e) {
				throw new IllegalArgumentException("Create processor error: "
						+ processorType, e);
			}
		}
	}

	public static void main(String[] args) {
		AcTextProcessPluginManager
				.addDefaultPlugin(new AcTextProcessLoggingPlugin());
		AcTextProcessPluginManager
				.addDefaultPlugin(new AcProcessErrorCheckPlugin(500));
		String logTypeName = null;
		int startArgs = 0;
		if (args.length >= 1) {
			logTypeName = args[0];
		}
		// just for handle old cmd parameters
		if (logClasses.containsKey(logTypeName)) {
			startArgs++;
		}
		final String filterString = System.getProperty("logparser.filter");
		AcPredicate<AcContentLine> filter = null;
		if (filterString != null) {
			filter = new AcPredicate<AcContentLine>() {

				@Override
				public boolean apply(AcContentLine content) {
					return content.getCurrentLines().contains(filterString);
				}
			};
		}
		boolean verbose = Boolean.getBoolean("logparser.verbose");
		boolean debug = Boolean.getBoolean("logparser.debug");

		HpscStatisticContentHandler handler = new HpscStatisticContentHandler();
		handler.setDebug(debug);
		handler.setVerbose(verbose);
		for (Mapping<?> mapping : processorClasses) {
			mapping.registerProcessor(handler);
		}

		final Timeable.Comparator comparator = new Timeable.Comparator(
				parseTime("startTime"), parseTime("endTime"));
		// use auto detect processor
		AcTextStreamProcessor processor = TextPatternScanner
				.createAutodetectProcessor(comparator, logClasses.values()
						.toArray(new Class[0]));

		try {
			if (args.length == startArgs) {
				AcReaderContent content = new AcInputStreamContent(System.in);
				processor.process(content, filter, handler);
			} else {
				final AcPredicate<AcContentMetadata> metadataIgnoreFilter = new AcPredicate<AcContentMetadata>() {

					private String[] ignoredNamePattern = { "/healthcheck/",
							"/access.log", ".out", ".lck" };

					@Override
					public boolean apply(AcContentMetadata data) {
						String name = data.getBasename();
						for (String s : ignoredNamePattern) {
							if (name != null && name.contains(s)) {
								return true;
							}
						}
						Date lastModified = new Date(data.getLastModified());
						if (comparator.compareDate(lastModified) > 0) {
							// the file is before time window, ignore
							return true;
						}
						return false;
					}
				};
				AcContentFetcher contentFetcher;
				List<AcContentFetcher> fetchers = new ArrayList<AcContentFetcher>();
				for (int i = startArgs; i < args.length; i++) {
					String path = args[i];
					AcArchiveContentFetcher archiveContentFetcher;
					if (path.startsWith("http://")
							|| path.startsWith("https://")) {
						archiveContentFetcher = new AcArchiveContentFetcher(
								AcStreamSuppliers.createURLSupplier(new URL(
										path))) {
							@Override
							protected boolean acceptEntry(
									AcContentMetadata entryMetadata) {
								return !metadataIgnoreFilter
										.apply(entryMetadata);
							}
						};
					} else {
						File file = new File(path);
						if (comparator
								.compareDate(new Date(file.lastModified())) > 0) {
							// the file is before time window, ignore
							continue;
						}
						archiveContentFetcher = new AcArchiveContentFetcher(
								file) {
							@Override
							protected boolean acceptEntry(
									AcContentMetadata entryMetadata) {
								return !metadataIgnoreFilter
										.apply(entryMetadata);
							}
						};
					}
					archiveContentFetcher
							.setSizeLimitWithBuffer(1024 * 1024 * 1);// 1MB
					fetchers.add(archiveContentFetcher);
				}
				contentFetcher = new AcCompositeContentFetcher(
						fetchers.toArray(new AcContentFetcher[fetchers.size()]));
				AcReaderContent content;
				try {
					while ((content = contentFetcher.next()) != null) {
						processor.process(content, filter, handler);
						if (verbose) {
							System.out.println("VERBOSE: " + handler);
						}
					}
				} catch (IOException e) {
					System.err.println("ERROR: " + e);
				} finally {
					try {
						contentFetcher.close();
					} catch (IOException ignored) {
					}
				}
				handler.printStatistics(System.out);
			}
		} catch (AcLoadException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public static void registerLogClass(String name, Class<?> logClass) {
		logClasses.put(name, logClass);
	}

	@SuppressWarnings({ "unchecked" })
	public static <T> void registerProcessor(
			Class<? extends StatisticProcessor<T>> processorClass) {
		processorClasses
				.add(new Mapping<T>(
						(Class<T>) ((ParameterizedType) processorClass
								.getGenericInterfaces()[0])
								.getActualTypeArguments()[0], processorClass));
	}

	private static Date parseTime(String propKey) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		try {
			String property = System.getProperty(propKey);
			if (property == null) {
				return null;
			}
			return format.parse(property);
		} catch (ParseException e) {
			throw new IllegalArgumentException("parse " + propKey + " error", e);
		}
	}
}
