package com.hp.it.perf.ac.client.load;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.TimeZone;

import javax.management.remote.JMXServiceURL;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.GenericXmlApplicationContext;

import com.hp.it.perf.ac.common.core.AcSessionToken;
import com.hp.it.perf.ac.core.access.AcAccessException;
import com.hp.it.perf.ac.core.access.AcCoreAccess;
import com.hp.it.perf.ac.core.service.AcServiceException;
import com.hp.it.perf.ac.load.common.AcPredicate;
import com.hp.it.perf.ac.load.content.AcArchiveContentFetcher;
import com.hp.it.perf.ac.load.content.AcCompositeContentFetcher;
import com.hp.it.perf.ac.load.content.AcContentFetcher;
import com.hp.it.perf.ac.load.content.AcContentMetadata;
import com.hp.it.perf.ac.load.content.AcLoadException;
import com.hp.it.perf.ac.load.content.AcReaderContent;
import com.hp.it.perf.ac.load.content.AcStreamSuppliers;
import com.hp.it.perf.ac.load.parse.AcTextPipelineParseBuilder;
import com.hp.it.perf.ac.load.parse.AcTextStreamProcessor;
import com.hp.it.perf.ac.load.parse.impl.TextPatternScanner;
import com.hp.it.perf.ac.load.parse.plugins.AcBeanRangeCheckPlugin;
import com.hp.it.perf.ac.load.parse.plugins.AcProcessErrorCheckPlugin;
import com.hp.it.perf.ac.load.parse.plugins.AcTextProcessLoggingPlugin;
import com.hp.it.perf.ac.load.parse.plugins.AcTextProcessPluginManager;
import com.hp.it.perf.ac.load.util.Timeable;
import com.hp.it.perf.ac.service.transfer.AcSender;
import com.hp.it.perf.ac.service.transfer.AcTransferSender;
import com.hp.it.perf.ac.service.transfer.AcTransferService;
import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;

public class AcLoadClientMain {

	private static final int BLOCK_SIZE = Integer.getInteger("sendBlockSize",
			1000);

	private static final int ERROR_DETECT_SIZE = Integer.getInteger(
			"errorDetectSize", 1000);

	private static Map<Class<?>, AcClientLoadFactory> beanClasses = new LinkedHashMap<Class<?>, AcClientLoadFactory>();

	private GenericXmlApplicationContext coreClient;

	private static final String[] DefaultIgnoreNames = { "/healthcheck/",
			"/access.log", ".out", ".lck", ".tar", ".tar.gz", ".gz", ".jar",
			".zip" };

	@Argument(value = "type", alias = "t", description = "Load Type (file, monitor)")
	private String type = "file";

	@Argument(value = "location", alias = "l", description = "AC server JMX URL")
	private String location = System.getProperty("ac.load.jmxurl",
			//"service:jmx:rmi:///jndi/rmi://localhost:1099/root");
"service:jmx:rmi:///jndi/rmi://d6t0009g.atlanta.hp.com:12099/filemonitor");
	@Argument(value = "startTime", alias = "b", description = "Start/Begin time")
	private String startTime = System.getProperty("startTime");

	@Argument(value = "endTime", alias = "e", description = "End time")
	private String endTime = System.getProperty("endTime");

	@Argument(value = "ignore", alias = "i", description = "Ignore name patterns")
	private String[] ignoreNames = new String[0];

	@Argument(value = "appendIgnore", alias = "a", description = "Add ignore name patterns")
	private boolean appendIgnore = true;

	@Argument(value = "profile", alias = "p", description = "Profile Id")
	private int profileId = 1;

	@Argument(value = "session", alias = "s", description = "Session Id")
	private int sessionId = 1;

	private static void initClient() {
		// load supported bean class for parser
		ServiceLoader<AcClientLoadFactory> loader = ServiceLoader
				.load(AcClientLoadFactory.class);
		for (Iterator<AcClientLoadFactory> it = loader.iterator(); it.hasNext();) {
			AcClientLoadFactory loadFactory = it.next();
			for (Class<?> beanClass : loadFactory.getSupportedBeanClassList()) {
				beanClasses.put(beanClass, loadFactory);
			}
		}
		if (beanClasses.isEmpty()) {
			throw new IllegalArgumentException("no parser bean class loaded");
		}
	}

	public static void main(String[] args) {
		//initClient();
		AcLoadClientMain main = new AcLoadClientMain();
		try {
			List<String> targets = Args.parse(main, args);
			main.setupCoreClient();
			AcTransferSender sender = main.connect();
			try {
				main.processContents(targets, sender);
			} finally {
				sender.close();
			}
		} catch (IllegalArgumentException e) {
			System.err.println("Error: " + e.getMessage());
			Args.usage(main);
			System.err.println("[<Location>] ...");
			System.exit(1);
		} catch (AcLoadException e) {
			System.err.println("Error: Parse files fail. (" + e.getMessage()
					+ ")");
			e.printStackTrace();
			System.exit(2);
		} catch (AcAccessException e) {
			System.err.println("Error: Cannot access AC Server. ("
					+ e.getMessage() + ")");
			e.printStackTrace();
			System.exit(3);
		} catch (AcServiceException e) {
			System.err.println("Error: AC Service access error. ("
					+ e.getMessage() + ")");
			e.printStackTrace();
			System.exit(3);
		} catch (IOException e) {
			System.err.println("Error: IO error. (" + e.getMessage() + ")");
			e.printStackTrace();
			System.exit(4);
		}
	}

	private AcTransferSender connect() throws AcAccessException,
			AcServiceException, IOException {
		AcCoreAccess acCoreAccess = coreClient.getBean(AcCoreAccess.class);

		AcTransferSender sender = new AcTransferSender(
				acCoreAccess.getService(AcTransferService.class));
		return sender;
	}

	private void processContents(List<String> args, AcSender sender)
			throws AcLoadException, IOException {
		final String[] ignoreNameList;
		if (appendIgnore) {
			ignoreNameList = new String[ignoreNames.length
					+ DefaultIgnoreNames.length];
			System.arraycopy(DefaultIgnoreNames, 0, ignoreNameList, 0,
					DefaultIgnoreNames.length);
			System.arraycopy(ignoreNames, 0, ignoreNameList,
					DefaultIgnoreNames.length, ignoreNames.length);
		} else {
			ignoreNameList = ignoreNames;
		}
		AcTextProcessPluginManager
				.addDefaultPlugin(new AcTextProcessLoggingPlugin());
		AcTextProcessPluginManager
				.addDefaultPlugin(new AcProcessErrorCheckPlugin(
						ERROR_DETECT_SIZE));

		final Timeable.Comparator comparator = new Timeable.Comparator(
				parseTime(startTime), parseTime(endTime));
		// use auto detect processor
		AcTextStreamProcessor processor = TextPatternScanner
				.createAutodetectProcessor(comparator, beanClasses.keySet()
						.toArray(new Class[0]));

		final AcPredicate<AcContentMetadata> metadataIgnoreFilter = new AcPredicate<AcContentMetadata>() {

			@Override
			public boolean apply(AcContentMetadata data) {
				String name = data.getBasename();
				for (String s : ignoreNameList) {
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
		// if type is file
		if ("file".equals(type)) {
			for (String path : args) {
				AcArchiveContentFetcher archiveContentFetcher;
				if (path.startsWith("http://") || path.startsWith("https://")) {
					URL url;
					try {
						url = new URL(path);
					} catch (IOException e) {
						System.err.println("Invalid URL: " + path);
						throw new IllegalArgumentException(e);
					}
					archiveContentFetcher = new AcArchiveContentFetcher(
							AcStreamSuppliers.createURLSupplier(url)) {
						@Override
						protected boolean acceptEntry(
								AcContentMetadata entryMetadata) {
							return !metadataIgnoreFilter.apply(entryMetadata);
						}
					};
				} else {
					File file = new File(path);
					if (comparator.compareDate(new Date(file.lastModified())) > 0) {
						// the file is before time window, ignore
						continue;
					}
					archiveContentFetcher = new AcArchiveContentFetcher(file) {
						@Override
						protected boolean acceptEntry(
								AcContentMetadata entryMetadata) {
							return !metadataIgnoreFilter.apply(entryMetadata);
						}
					};
				}
				archiveContentFetcher.setSizeLimitWithBuffer(1024 * 1024 * 1);// 1MB
				fetchers.add(archiveContentFetcher);
			}
			contentFetcher = new AcCompositeContentFetcher(
					fetchers.toArray(new AcContentFetcher[fetchers.size()]));
			AcReaderContent content;
			AcDataBeanSender handler = new AcDataBeanSender(sender, beanClasses);
			handler.setHostname(InetAddress.getLocalHost().getHostName());
			handler.setBlockSize(BLOCK_SIZE);
			try {
				while ((content = contentFetcher.next()) != null) {
					processor.process(content, null, handler);
				}
			} finally {
				try {
					contentFetcher.close();
				} catch (IOException ignored) {
				}
			}
		} else if ("monitor".equals(type)) {
			if (args.isEmpty()) {
				throw new IllegalArgumentException(
						"need jmx url as argument for remote monitor");
			}
			JMXServiceURL jmxURL = new JMXServiceURL(args.get(0));
			AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
					.createAutodetectPipelineParseBuilder(beanClasses.keySet()
							.toArray(new Class[0]));
			AcTextProcessPluginManager
					.addDefaultPlugin(new AcBeanRangeCheckPlugin(comparator));
			AcDataBeanMixAgent handler = new AcDataBeanMixAgent(sender,
					beanClasses);
			handler.setBlockSize(BLOCK_SIZE);
			MonitorContentDispatcher dispatcher = new MonitorContentDispatcher(
					pipelineBuilder, handler);
			dispatcher.monitor(jmxURL);
		}
	}

	private void setupCoreClient() {
		AcSessionToken sessionToken = new AcSessionToken(profileId, sessionId);
		GenericXmlApplicationContext coreAppContext = new GenericXmlApplicationContext();
		coreAppContext.load("classpath:/ac-load-client.xml");

		coreAppContext.getBeanFactory().registerResolvableDependency(
				AcSessionToken.class, sessionToken);

		Properties prop = new Properties();
		prop.setProperty("ac.load.jmxurl", this.location);
		PropertyPlaceholderConfigurer propConfig = new PropertyPlaceholderConfigurer();
		propConfig.setProperties(prop);
		coreAppContext.addBeanFactoryPostProcessor(propConfig);

		coreAppContext.refresh();
		coreClient = coreAppContext;
	}

	private static Date parseTime(String property) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		try {
			if (property == null) {
				return null;
			}
			return format.parse(property);
		} catch (ParseException e) {
			throw new IllegalArgumentException("parse " + property + " error",
					e);
		}
	}
}
