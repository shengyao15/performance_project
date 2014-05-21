package com.hp.hpsc.logservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.hp.hpsc.logservice.client.Link;
import com.hp.hpsc.logservice.client.LogviewClient;
import com.hp.hpsc.logservice.parser.beans.StatisticErrorBean;

public class ProducerErrorTask implements Callable<List<StatisticErrorBean>> {

	private static Logger logger = Logger.getLogger(ProducerErrorTask.class);
	
	private String url;
	private LogviewClient client;
	private String mainFolder;
	private String reportS;
	private ProducerErrorService service;

	public ProducerErrorTask(String url, LogviewClient client, String mainFolder,
			String reportS, ProducerErrorService service) {
		this.url = url;
		this.client = client;
		this.mainFolder = mainFolder;
		this.reportS = reportS;
		this.service = service;
	}

	public List<StatisticErrorBean> call() throws Exception {
		List<StatisticErrorBean> errorBeanList = new ArrayList<StatisticErrorBean>();
		
		try {
			logger.debug("url--------" + url);
			String directoryURL = service.decorateDirectoryURL(url);

			List<Link> list = client.readDirectory(mainFolder, directoryURL);

			Map<String, String> allPath = new HashMap<String, String>();
			for (Link link : list) {
				allPath.put(link.getName(), mainFolder + "/" + link.getName()
						+ "/main/error");
			}

			for (String key : allPath.keySet()) {
				
				// main logic
				Map<String, Integer> producerLog = service
						.statisticProducerLog(allPath.get(key), reportS, url);
				
				// convert to bean
				if (producerLog != null && producerLog.size() > 0) {
					StatisticErrorBean bean = new StatisticErrorBean();
					bean.setCollectDate(reportS);
					bean.setFeatureName(key);
					bean.setErrorDetails(producerLog);
					errorBeanList.add(bean);
				}
			}
			return errorBeanList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return errorBeanList;
	}

}
