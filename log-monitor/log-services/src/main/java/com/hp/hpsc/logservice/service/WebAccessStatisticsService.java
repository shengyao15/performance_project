package com.hp.hpsc.logservice.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

import com.hp.hpsc.logservice.parser.beans.SPFWebAccessLog;
import com.hp.hpsc.logservice.parser.beans.TopIPAggregateBean;
import com.hp.hpsc.logservice.parser.beans.TopIPAggregateBean.StatisticsGranularities;
import com.hp.hpsc.logview.client.FolderViewLaunch;
import com.hp.hpsc.logview.po.Link;
import com.hp.hpsc.logview.retrievers.HttpContentRetriever;
import com.hp.hpsc.logview.util.Configurations;
import com.hp.hpsc.logview.util.Configurations.ConfigurationException;

public class WebAccessStatisticsService {

	public static int STATISTICS_LIMIT = 50;
	
	public class StatisticsServiceException extends Exception{
		public StatisticsServiceException() {
		}

		public StatisticsServiceException(Throwable r) {
			super(r);
		}

		public StatisticsServiceException(String message) {
			super(message);
		}
	}
	
	public static void main(String[] args) throws Exception{
		Date date = new Date(new Date().getTime() - 172800000l);
		
		WebAccessStatisticsService service = new WebAccessStatisticsService();
		List<TopIPAggregateBean> beans = service.service(date, StatisticsGranularities.DAY, -1);

		int i=0;
		for(TopIPAggregateBean bean: beans){
			i++;
			System.out.println(" -- "+bean.getIp()+" | "+bean.getCount()+" | "+bean.getUserAgent()+" | "+bean.getDate());
		}
		
		System.out.println("beans size = "+beans.size());
	}
	
	private void testDownload(List<HttpContentRetriever> httpContentRetrievers){
		byte[] buffer = new byte[1024];
		
		for(HttpContentRetriever retriever: httpContentRetrievers){
		int n = 0;
		
		try{
			while (-1 != (n = retriever.getInputStream().read(buffer))) {
				System.out.println(new String(buffer, 0, n, "ISO-8859-1"));
			}
			
			System.out.println(" -------------------- another file ------------------------");
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			retriever.close();
		}
		}
	}
	public List<TopIPAggregateBean> service(final Date startDate, StatisticsGranularities gran, int limition) throws StatisticsServiceException {
		List<HttpContentRetriever> httpContentRetrievers = new ArrayList<HttpContentRetriever>();
		final Date endDate = TopIPAggregateBean.getEndTime(startDate, gran);
		
		FolderViewLaunch viewLaunch = new FolderViewLaunch();
		
		try {
			String[] accessURLs = Configurations.getConfigArray(Configurations.ConfugrationKeys.ACCESSLOG_FOLDER_URL);
			if(accessURLs != null && accessURLs.length > 0){
				for(String accessUrl : accessURLs){
					List<Link> links = viewLaunch.listAllView(accessUrl, startDate.getTime(), endDate.getTime(), true);
					//TODO enable the filter for folder and file name
					if(links != null && links.size() > 0){
						for(Link link: links){
							System.out.println("valid URLs: "+link.getUri() + "; modified date: "+link.getLastModifiedDate() + "; size: "+link.getSize());
							httpContentRetrievers.add(new HttpContentRetriever(link.getUri()));
						}
					}
				}
			}	
		} catch (ConfigurationException e1) {
			throw new StatisticsServiceException(e1);
		}
		
		//Test codes. can be removed.
		//testDownload(httpContentRetrievers);		
		//return null;
		
		
		final Class<?> parserTyper = SPFWebAccessLog.class;
		PivotViewBuilder webAccessStat = setupWebAccessStatistics();
		final PivotViewCallback pivotCallback = webAccessStat.pivotCallback();
		ExecutorService threadPool = Executors.newFixedThreadPool(httpContentRetrievers.size());
		CompletionService<Object> completeService = new ExecutorCompletionService<Object>(
				threadPool);
		for (final HttpContentRetriever retriver : httpContentRetrievers) {
			final String filePath = retriver.getUrl();
			completeService.submit(new Runnable() {

				@Override
				public void run() {
					try {
						performParseAndAggregate(retriver.getInputStream(),
								filePath, startDate, endDate, parserTyper,
								pivotCallback);
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						retriver.close();
					}
				}
			}, null);
		}
		for (int i = 0; i < httpContentRetrievers.size(); i++) {
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
		
		
		int limit = STATISTICS_LIMIT;
		if(limition > 0){
			limit = limition;
		}else if(limition < 0){
			limit = Integer.MAX_VALUE;
		}
		List<TopIPAggregateBean> results = new ArrayList<TopIPAggregateBean>(STATISTICS_LIMIT);
		while (iter.hasNext() && limit>0) {
			Map<String, Object> item = iter.next();
			String remoteAddress = (String) item.get("remoteAddress");
			long count = ((Number) item.get("count")).longValue();
			String userAgent = (String) item.get("userAgent");
			TopIPAggregateBean bean = new TopIPAggregateBean(remoteAddress, userAgent, count, startDate);
			results.add(bean);
			limit--;
		}
		
		return results;
	}
	
	
	private static void performParseAndAggregate(InputStream input,
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
				//error.printStackTrace();
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
	
	private static PivotViewBuilder setupWebAccessStatistics() {
		PivotViewBuilder builder = new PivotViewBuilder();
		builder.addFilter("includedUrls", new AcPredicate<Object>() {

			@Override
			public boolean apply(Object data) {
				SPFWebAccessLog log = (SPFWebAccessLog) data;
				return log.getRequestPath().indexOf("/resource3/") == -1
						&& log.getRequestPath().indexOf("/healthcheck/") == -1;
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

}
