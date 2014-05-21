package com.hp.hpsc.logservice.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.hp.hpsc.logservice.parser.beans.ConsumerErrorBean;
import com.hp.hpsc.logservice.parser.beans.StatisticErrorBean;

public class ConsumerErrorTask implements Callable<List<StatisticErrorBean>> {

	private static Logger logger = Logger.getLogger(ConsumerErrorTask.class);
	
	private String url;
	private String  folder;
	private Calendar reportMinus1;
	private Calendar reportAdd1;
	private String reportS;
	private ConsumerErrorService service;

	public ConsumerErrorTask(String url, String folder,
			Calendar reportMinus1, Calendar reportAdd1, String reportS,
			ConsumerErrorService service) {
		this.url = url;
		this.folder = folder;
		this.reportMinus1 = reportMinus1;
		this.reportAdd1 = reportAdd1;
		this.reportS = reportS;
		this.service = service;
	}

	public List<StatisticErrorBean> call() throws Exception {
		List<StatisticErrorBean> list = new ArrayList<StatisticErrorBean>();
		
		try {
			String directoryURL = service.decorateDirectoryURL(url);
			String downloadURL = service.decorateDownloadURL(url);
			
			List<String> fileList = service.filterFileList(folder, reportMinus1, directoryURL);
			
			if(fileList == null || fileList.size() == 0){
				logger.debug("fileList is null");
				return null;
			}
			
			// calculate error list
			List<ConsumerErrorBean> errorList = service.filterAndParserErrorList4Consumer(reportMinus1,
					reportAdd1, fileList, downloadURL);
			if(errorList == null || errorList.size() == 0){
				logger.debug("errorList is null");
				return null;
			}
			
			// statistics & sort
			list = service.calculateConsumerResultMap(errorList, reportS);
			
			if(list == null){
				logger.debug("StatisticErrorBean list is null");
			}
			
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return list;
	}


}
