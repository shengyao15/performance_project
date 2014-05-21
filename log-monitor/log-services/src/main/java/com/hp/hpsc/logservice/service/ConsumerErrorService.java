package com.hp.hpsc.logservice.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.hp.hpsc.logservice.parser.beans.ConsumerErrorBean;
import com.hp.hpsc.logservice.parser.beans.StatisticErrorBean;
import com.hp.hpsc.logservice.parser.beans.UrlFolderBean;
import com.hp.hpsc.logservice.utils.PropUtils;

public class ConsumerErrorService extends CommonService{

	private static Logger logger = Logger.getLogger(ConsumerErrorService.class);
	
	public List<StatisticErrorBean> statisticConsumerLog(String reportS) throws ParseException, IOException, InterruptedException, ExecutionException {
		
		//report date
		Date reportD = calculateReportDate(reportS);
		Calendar reportMinus1 = calculateCalendarReportMinus1(reportD);
		Calendar reportAdd1 = calculateCalendarReportAdd1(reportD);

		List<UrlFolderBean> urlFolderList = PropUtils.readAllConsumer();
		
		List<StatisticErrorBean> resultList = new ArrayList<StatisticErrorBean>();
		
		// use multi thread to filter/download/parser log
		ExecutorService es = Executors.newFixedThreadPool(10);
		CompletionService<List<StatisticErrorBean>> cs = new ExecutorCompletionService<List<StatisticErrorBean>>(
				es);

		for (int i = 0; i < urlFolderList.size(); i++) {
			cs.submit(new ConsumerErrorTask(urlFolderList.get(i).getUrl(), urlFolderList.get(i).getFolder(), reportMinus1, reportAdd1, reportS, this));
		}
		
		for (int i = 0; i < urlFolderList.size(); i++) {
			List<StatisticErrorBean> list = cs.take().get();
			if(list == null){
				logger.debug("list is null");
				continue;
			}
			combineList(resultList, list);
		}
		es.shutdown();
		
		return resultList;
		
	}


	public List<StatisticErrorBean> calculateConsumerResultMap(
			List<ConsumerErrorBean> errorList, String reportS) {
		List<StatisticErrorBean> statisticErrorBeanList = new ArrayList<StatisticErrorBean>();
		
		for(ConsumerErrorBean cbean : errorList){
			StatisticErrorBean pbean = new StatisticErrorBean();
			pbean.setCollectDate(reportS);
			pbean.setFeatureName(cbean.getFeatureName());
			Map<String,Integer> map = new HashMap<String, Integer>();
		    for (String msg : cbean.getErrorMsg()) {  
				map.put(msg, Collections.frequency(cbean.getErrorMsg(), msg));  
		    }  
			pbean.setErrorDetails(map);
			statisticErrorBeanList.add(pbean);
		}
		
		return statisticErrorBeanList;
	}

	List<ConsumerErrorBean> filterAndParserErrorList4Consumer(
			Calendar reportMinus1, Calendar reportAdd1, List<String> fileList, String url)
			throws Exception {
		List<String> errorList = new ArrayList<String>();
		List<ConsumerErrorBean> list = new ArrayList<ConsumerErrorBean>(); 
		
		for (String file : fileList) {
			InputStream input = client.loadInputStream(file, url);
			InputStreamReader isr = new InputStreamReader(input);
			BufferedReader br = new BufferedReader(isr);
			String temp = "";
			
			boolean nextLineFlag = false;
			String keyTmp = "";
			
			while ((temp = br.readLine()) != null) {
				
				if(nextLineFlag){
					errorList.add(keyTmp+ " | " + temp);
					nextLineFlag = false;
					
					boolean exitFlag = false;
					
					for(ConsumerErrorBean bean : list){
						if(bean.getFeatureName().equals(keyTmp)){
							exitFlag = true;
							bean.getErrorMsg().add(temp);
							break;
						}
					}
					
					if(!exitFlag){
						ConsumerErrorBean bean = new ConsumerErrorBean();
						bean.setFeatureName(keyTmp);
						bean.getErrorMsg().add(temp);
						list.add(bean);
					}
				}
				
				if (temp.length() > 10) {
					String s1 = temp.substring(0, 10);
					Date d1 = null;

					try {
						d1 = sdf.get().parse(s1);
					} catch (Exception e) {
						continue;
					}

					Calendar c1 = Calendar.getInstance();
					c1.setTime(d1);

					if (c1.after(reportMinus1) && c1.before(reportAdd1)) {
						if(temp.contains(ERROR_KEY_MSG)){
							nextLineFlag = true;
							
							int begin = temp.indexOf(ERROR_KEY_MSG);
							keyTmp = temp.substring(begin + ERROR_KEY_MSG.length()+1);

							int end = keyTmp.indexOf(",");
							keyTmp = keyTmp.substring(0, end);
							
						}
						
					}
				}

			}
			br.close();
			isr.close();
			input.close();
		}
		return list;
	}

}
