package com.hp.hpsc.logservice.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hp.hpsc.logservice.parser.beans.SPFPortalLog;
import com.hp.hpsc.logservice.parser.beans.SPFWebAccessLog;
import com.hp.hpsc.logview.retrievers.HttpContentRetriever;
import com.hp.it.perf.ac.load.common.AcMapper;
import com.hp.it.perf.ac.load.common.AcPredicate;
import com.hp.it.perf.ac.load.common.AcReduceCallback;
import com.hp.it.perf.ac.load.content.AcContentHandler;
import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcContentMetadata;
import com.hp.it.perf.ac.load.content.AcInputStreamContent;
import com.hp.it.perf.ac.load.content.AcLoadException;
import com.hp.it.perf.ac.load.content.AcReaderContent;
import com.hp.it.perf.ac.load.parse.AcTextStreamProcessor;
import com.hp.it.perf.ac.load.parse.impl.TextPatternScanner;
import com.hp.it.perf.ac.load.process.PivotViewBuilder;
import com.hp.it.perf.ac.load.process.PivotViewCallback;
import com.hp.it.perf.ac.load.process.support.AcMathAggregators;
import com.hp.it.perf.ac.load.util.Timeable;

public class ParserTestMain {

	public static String parsePortalErrorLogs(InputStream input,
			String filePath, Date startDate, Date endDate) throws IOException {
		Class<?> parserTyper = SPFPortalLog.class;
		PivotViewBuilder portalErrorStatistics = setupPortalErrorStatistics();
		Iterator<Map<String, Object>> iter;
		try {
			iter = performParseAndAggregate(input, filePath, startDate,
					endDate, parserTyper, portalErrorStatistics);
		} catch (AcLoadException e) {
			throw new IOException(e);
		}
		// FOR TESTING ONLY
		StringBuilder result = new StringBuilder();
		result.append("PORTLET_TITLE, ERROR_COUNT, ERROR_INFO\n");
		while (iter.hasNext()) {
			Map<String, Object> item = iter.next();
			String portlet = (String) item.get("portletTitle");
			Number errorCount = (Number) item.get("count");
			String errorCategory = (String) item.get("errorCategory");
			StringBuilder builder = new StringBuilder();
			builder.append(portlet).append(", ");
			builder.append(errorCount).append(", ");
			builder.append(errorCategory);
			result.append(builder).append("\n");
		}
		return result.toString();
	}

	protected static Iterator<Map<String, Object>> performParseAndAggregate(
			InputStream input, String filePath, Date startDate, Date endDate,
			Class<?> parserTyper, PivotViewBuilder pivotViewBuilder)
			throws AcLoadException {
		AcTextStreamProcessor processor = TextPatternScanner
				.createAutodetectProcessor(new Timeable.Comparator(startDate,
						endDate), parserTyper);
		AcContentMetadata metadata = new AcContentMetadata();
		metadata.setBasename(filePath);
		metadata.setReloadable(false);
		AcReaderContent content = new AcInputStreamContent(input, metadata);
		final PivotViewCallback pivotCallback = pivotViewBuilder
				.pivotCallback();
		processor.process(content, null, new AcContentHandler() {

			@Override
			public void handleLoadError(AcLoadException error,
					AcContentLine line) throws AcLoadException {
				System.err.println(line);
				throw error;
			}

			@Override
			public void init(AcContentMetadata metadata) {
			}

			@Override
			public void handle(Object data, AcContentLine line)
					throws AcLoadException {
				synchronized (pivotCallback) {
					pivotCallback.apply(data);
				}
			}

			@Override
			public void destroy() {
			}
		});
		Iterator<Map<String, Object>> iter = pivotCallback.createView()
				.listAll();
		return iter;
	}

	private static PivotViewBuilder setupPortalErrorStatistics() {
		PivotViewBuilder builder = new PivotViewBuilder();
		builder.addFilter("renderError", new AcPredicate<Object>() {

			@Override
			public boolean apply(Object data) {
				SPFPortalLog log = (SPFPortalLog) data;
				return log.getRenderError() != null;
			}

		});
		builder.addGroupField("portletTitle", new AcMapper<Object, Object>() {

			@Override
			public Object apply(Object data) {
				SPFPortalLog log = (SPFPortalLog) data;
				return log.getRenderError().getPortletTitle();
			}

		});
		builder.addGroupField("errorCategory", new AcMapper<Object, Object>() {

			@Override
			public Object apply(Object data) {
				SPFPortalLog log = (SPFPortalLog) data;
				String errorMsg = log.getRenderError().getErrorMessage()
						.split("\\n")[0];
				if (errorMsg
						.startsWith("com.vignette.portal.portlet.website.PortletTimedOutException")) {
					errorMsg = errorMsg.split(":")[0];
				}
				return errorMsg;
			}

		});
		builder.addValueField("count", AcMathAggregators.count());
		return builder;
	}

	public static String parseWebAccessLogs(
			HttpContentRetriever[] httpContentRetrievers,
			final String filePath, final Date startDate, final Date endDate)
			throws IOException {
		final Class<?> parserTyper = SPFWebAccessLog.class;
		PivotViewBuilder webAccessStat = setupWebAccessStatistics();
		final PivotViewCallback pivotCallback = webAccessStat.pivotCallback();
		ExecutorService threadPool = Executors.newFixedThreadPool(1);
		CompletionService<Object> completeService = new ExecutorCompletionService<Object>(
				threadPool);
		for (final HttpContentRetriever retriver : httpContentRetrievers) {
			completeService.submit(new Runnable() {

				@Override
				public void run() {
					try {
						performParseAndAggregate(retriver.getInputStream(),
								filePath, startDate, endDate, parserTyper,
								pivotCallback);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}, null);
		}
		for (int i = 0; i < httpContentRetrievers.length; i++) {
			try {
				completeService.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		threadPool.shutdown();
		Iterator<Map<String, Object>> iter = pivotCallback.createView()
				.listAll();
		// FOR TESTING ONLY
		StringBuilder result = new StringBuilder();
		result.append("remoteAddress, count, userAgent\n");
		int limit = 50;
		while (iter.hasNext() && limit>=0) {
			Map<String, Object> item = iter.next();
			String remoteAddress = (String) item.get("remoteAddress");
			Number count = (Number) item.get("count");
			String userAgent = (String) item.get("userAgent");
			StringBuilder builder = new StringBuilder();
			builder.append(remoteAddress).append(", ");
			builder.append(count).append(", ");
			builder.append(userAgent);
			result.append(builder).append("\n");
			limit--;
		}
		return result.toString();
	}

	private static PivotViewBuilder setupWebAccessStatistics() {
		PivotViewBuilder builder = new PivotViewBuilder();
		builder.addFilter("ignoreUrls", new AcPredicate<Object>() {

			@Override
			public boolean apply(Object data) {
				SPFWebAccessLog log = (SPFWebAccessLog) data;
				return log.getRequestPath().indexOf("/resource3/") != -1
						|| log.getRequestPath().indexOf("/healthcheck/") != -1;
			}

		});
		builder.addGroupField("remoteAddress", new AcMapper<Object, Object>() {

			@Override
			public Object apply(Object data) {
				SPFWebAccessLog log = (SPFWebAccessLog) data;
				return log.getRemoteAddress();
			}

		});
		builder.addValueField("userAgent",
				new AcReduceCallback<Object, String>() {

					@Override
					public Object createReduceContext() {
						return new String[1];
					}

					@Override
					public String getResult(Object context) {
						return ((String[]) context)[0];
					}

					@Override
					public void reduce(Object item, Object context) {
						((String[]) context)[0] = ((SPFWebAccessLog) item)
								.getUserAgent();

					}
				});
		builder.addValueField("count", AcMathAggregators.count());
		builder.addSortField("count", Long.class).setReverse(true);
		return builder;
	}

	protected static void performParseAndAggregate(InputStream input,
			String filePath, Date startDate, Date endDate,
			Class<?> parserTyper, final PivotViewCallback pivotCallback)
			throws AcLoadException {
		AcTextStreamProcessor processor = TextPatternScanner
				.createAutodetectProcessor(new Timeable.Comparator(startDate,
						endDate), parserTyper);
		AcContentMetadata metadata = new AcContentMetadata();
		metadata.setBasename(filePath);
		metadata.setReloadable(false);
		AcReaderContent content = new AcInputStreamContent(input, metadata);
		processor.process(content, null, new AcContentHandler() {

			@Override
			public void handleLoadError(AcLoadException error,
					AcContentLine line) throws AcLoadException {
				System.err.println(line);
				//throw error;
			}

			@Override
			public void init(AcContentMetadata metadata) {
			}

			@Override
			public void handle(Object data, AcContentLine line)
					throws AcLoadException {
				synchronized (pivotCallback) {
					pivotCallback.apply(data);
				}
			}

			@Override
			public void destroy() {
			}
		});
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		URL fileLocation = ParserTestMain.class.getClassLoader().getResource(
				"sample_portal.txt");
		try {
			System.out.println(parsePortalErrorLogs(fileLocation.openStream(),
					fileLocation.toString(), null, null));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// fileLocation = ParserTestMain.class.getClassLoader().getResource(
		// "sample_webaccess.txt");
		try {
			System.out
					.println(parseWebAccessLogs(
							new HttpContentRetriever[] {
//									new HttpContentRetriever(
//											"http://d6t0009g.atlanta.hp.com/files/logs-prod/web/w1/WHA-HPP-ANON/access_log-20140316.gz"),
//									new HttpContentRetriever(
//											"http://d6t0009g.atlanta.hp.com/files/logs-prod/web/w2/WHA-HPP-ANON/access_log-20140316.gz"),
//									new HttpContentRetriever(
//											"http://d6t0009g.atlanta.hp.com/files/logs-prod/web/w3/WHA-HPP-ANON/access_log-20140316.gz"),
//									new HttpContentRetriever(
//											"http://d6t0009g.atlanta.hp.com/files/logs-prod/web/w4/WHA-HPP-ANON/access_log-20140316.gz") 
									},
							"", null, null));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
