package com.hp.it.perf.ac.load.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.it.perf.ac.app.hpsc.beans.HPJVMGCLog;
import com.hp.it.perf.ac.app.hpsc.beans.PortletBusinessLog;
import com.hp.it.perf.ac.app.hpsc.beans.PortletErrorLog;
import com.hp.it.perf.ac.app.hpsc.beans.PortletErrortraceLog;
import com.hp.it.perf.ac.app.hpsc.beans.PortletPerformanceLog;
import com.hp.it.perf.ac.app.hpsc.beans.SBSClientLog;
import com.hp.it.perf.ac.app.hpsc.beans.SPFPerformanceLog;
import com.hp.it.perf.ac.app.hpsc.beans.SPFPortalLog;
import com.hp.it.perf.ac.app.hpsc.beans.SPFPerformanceLog.Detail;
import com.hp.it.perf.ac.common.data.AcDataStore;
import com.hp.it.perf.ac.common.data.store.AcRandomAccssDataStore;
import com.hp.it.perf.ac.common.data.store.AcRandomAccssDataStore.Mode;
import com.hp.it.perf.ac.load.common.AcMapper;
import com.hp.it.perf.ac.load.common.AcPredicate;
import com.hp.it.perf.ac.load.common.AcReduceCallback;
import com.hp.it.perf.ac.load.content.AcReaderContent;
import com.hp.it.perf.ac.load.content.AcContentHandler;
import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcFileContent;
import com.hp.it.perf.ac.load.content.AcLoadException;
import com.hp.it.perf.ac.load.parse.AcTextStreamProcessor;
import com.hp.it.perf.ac.load.parse.impl.TextPatternScanner;
import com.hp.it.perf.ac.load.parse.plugins.AcTextProcessLoggingPlugin;
import com.hp.it.perf.ac.load.parse.plugins.AcTextProcessPluginManager;
import com.hp.it.perf.ac.load.process.PivotView;
import com.hp.it.perf.ac.load.process.PivotViewBuilder;
import com.hp.it.perf.ac.load.set.AcSet;
import com.hp.it.perf.ac.load.set.AcSetItem;
import com.hp.it.perf.ac.load.set.AcSetProcessHandler;
import com.hp.it.perf.ac.load.set.AcSetProcessScanner;
import com.hp.it.perf.ac.load.set.impl.AcMapperSetProcessScanner;
import com.hp.it.perf.ac.load.set.impl.AcMapperSetType;
import com.hp.it.perf.ac.load.util.Calculator;
import com.hp.it.perf.ac.load.util.StatisticsUnit;
import com.hp.it.perf.ac.load.util.StatisticsUnits;

public class LogParserTestMain {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
//		parse("\\\\spperf\\remote_share\\temp_file\\SBSClient.log");
		parse("data/gc.28060.log");
		parse("data/error.log");
		parse("data/errortrace.log");
		parse("data/business.log");
		parse("data/performance.log");
		parse("data/20120612_040942204.log");
		parse("data/20120614_081402067_portal.log");
	}

	private static <T> void parse(String fileName) throws AcLoadException,
			IOException {
		AcTextProcessPluginManager.addDefaultPlugin(new AcTextProcessLoggingPlugin());
		AcTextStreamProcessor processor = TextPatternScanner
				.createAutodetectProcessor(SPFPortalLog.class,
						SPFPerformanceLog.class, PortletPerformanceLog.class,
						PortletBusinessLog.class, PortletErrorLog.class,
						PortletErrortraceLog.class, HPJVMGCLog.class, SBSClientLog.class);

//		 AcTextStreamProcessor processor = TextPatternScanner
//		 .createAutodetectProcessor(SBSClientLog.class);

		// Properties properties = new Properties();
		// properties.put(AcTextParserConstant.PROPERTY_ERRORMODE, "true");
		// processor.setProcessProperties(properties);

		File file = new File(fileName);
		AcReaderContent content = new AcFileContent(file, "ISO-8859-1");
		AcContentHandler handler;
		handler = new com.hp.it.perf.ac.load.content.AcContentCounter();
//		 handler = new com.hp.it.perf.ac.load.content.AcContentWriter();
		AcPredicate<AcContentLine> filter = new AcPredicate<AcContentLine>() {

			@Override
			public boolean apply(AcContentLine content) {
				// return content.getCurrentLines().contains("unknown");
				return true;
			}
		};
//		List<Object> list = null;
		int max = 3;
		for (int j = 0; j < max; j++) {
			processor.process(content, filter, handler);

			System.out.println(handler);

			// Iterator<Object> iter = processor.iterator(content, filter,
			// null);
			// list = new ArrayList<Object>(10000);
			// int i = 0;
			// long start = System.currentTimeMillis();
			// while (iter.hasNext()) {
			// list.add(iter.next());
			// i++;
			// }
			// System.out.println(i + " " + (System.currentTimeMillis() -
			// start));
		}

		if (Thread.currentThread().isAlive()) {
			return;
		}

		// data store
		// must
		// - auto loading (from file, zip file, web, or other end point)
		// - support remote stage (web)
		// - support distribution mode
		// optional
		// - find data quickly
		// - memory size protection
		for (int j = 0; j < max; j++) {
			File storeFile = new File("data/store.log");
			AcDataStore datastore = new AcRandomAccssDataStore(storeFile,
					Mode.OVERWRITE);
			Iterator<?> iter = processor.iterator(content, filter, null);
			long start = System.currentTimeMillis();
			List<Long> keys = new ArrayList<Long>(30000);
			while (iter.hasNext()) {
				keys.add(datastore.add(iter.next()));
			}
			System.out.println("Store write: " + storeFile.length() + " : "
					+ (System.currentTimeMillis() - start));

			start = System.currentTimeMillis();
			for (Long key : keys) {
				datastore.get(key);
			}
			datastore.close();
			System.out.println("Store read: " + storeFile.length() + " : "
					+ (System.currentTimeMillis() - start));

			start = System.currentTimeMillis();
			datastore = new AcRandomAccssDataStore(storeFile, Mode.READONLY);
			System.out.println("Store pre-load: " + storeFile.length() + " : "
					+ (System.currentTimeMillis() - start));

			start = System.currentTimeMillis();
			for (Long key : keys) {
				datastore.get(key);
			}
			datastore.close();
			System.out.println("Store post-load: " + storeFile.length() + " : "
					+ (System.currentTimeMillis() - start));
		}

		{
			Iterator<?> iter = processor.iterator(content, filter, null);

			AcMapperSetType<?> categorySetType = new AcMapperSetType<SPFPerformanceLog>(
					new AcMapper<SPFPerformanceLog, String>() {

						@Override
						public String apply(SPFPerformanceLog data) {
							return data.getThreadName();
						}
					}, SPFPerformanceLog.class);
			AcSetProcessScanner setScanner = new AcMapperSetProcessScanner(
					categorySetType);
			AcSetProcessHandler processHandler = new AcSetProcessHandler() {

				@Override
				public void handle(AcSetItem item, AcSet set, Object origin) {
					// System.out.print(item.getIndex() + "@");
					// System.out.print(item.getOriginIndex() + ":");
					// System.out.println(item.getValue());
				}

				@Override
				public void onStart(AcSet set) {
					System.out.println(" == > " + set.getKey());
				}

				@Override
				public void onEnd(AcSet set) {
					System.out.println(" < == " + set.getKey());
				}
			};
			setScanner.setProcessHandler(processHandler);
			setScanner.scan(iter);
		}

		{
			Iterator<?> iter = processor.iterator(content, filter, null);
			PivotViewBuilder pivotBuilder = new PivotViewBuilder();
			pivotBuilder.addGroupField("sessionId",
					new AcMapper<Object, Object>() {

						@Override
						public Object apply(Object object) {
							SPFPerformanceLog log = (SPFPerformanceLog) object;
							return log.getHpscDiagnosticId().split("\\+")[0];
						}

					});
			pivotBuilder.addGroupField("threadName",
					new AcMapper<Object, Object>() {

						@Override
						public Object apply(Object object) {
							SPFPerformanceLog log = (SPFPerformanceLog) object;
							return log.getThreadName();
						}
					});
			pivotBuilder.addValueField("duration",
					new AcReduceCallback<Object, Calculator>() {

						@Override
						public Object createReduceContext() {
							return StatisticsUnits.newIntStatisticsUnit();
						}

						@Override
						public void reduce(Object item, Object context) {
							SPFPerformanceLog log = (SPFPerformanceLog) item;
							int duration = 0;
							for (Detail detail : log.getDetailList()) {
								if (detail.getType() == Detail.Type.REQUEST) {
									duration = detail.getDuration();
									break;
								}
							}
							StatisticsUnit unit = (StatisticsUnit) context;
							unit.add();
							unit.setInt(duration);
						}

						@Override
						public Calculator getResult(Object context) {
							return Calculator.build(((StatisticsUnit) context)
									.toLongArray());
						}
					});
			PivotView pivotView = pivotBuilder.pivot(iter);
			for (Iterator<Map<String, Object>> data = pivotView.listAll(); data
					.hasNext();) {
				// data.next();
				System.out.println(data.next());
			}
		}
	}
}
