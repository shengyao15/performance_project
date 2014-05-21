package com.hp.hpsc.logservice.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.hp.hpsc.logservice.parser.beans.StatisticErrorBean;
import com.hp.hpsc.logservice.parser.beans.UrlFolderBean;
import com.hp.hpsc.logservice.utils.PropUtils;

public class ProducerErrorService extends CommonService {

	private static Logger logger = Logger.getLogger(ProducerErrorTask.class);
	
	public List<StatisticErrorBean> statisticAllProducerLog(
			String reportS) throws IOException,
			ParseException, InterruptedException, ExecutionException {

		List<StatisticErrorBean> resultList = new ArrayList<StatisticErrorBean>();

		List<UrlFolderBean> urlFolderList = PropUtils.readAllProducer();
		
		
		// use multi thread to filter/download/parser log
		ExecutorService es = Executors.newFixedThreadPool(10);
		CompletionService<List<StatisticErrorBean>> cs = new ExecutorCompletionService<List<StatisticErrorBean>>(
				es);

		for (int i = 0; i < urlFolderList.size(); i++) {
			cs.submit(new ProducerErrorTask(urlFolderList.get(i).getUrl(), client, urlFolderList.get(i).getFolder(), reportS,
					this));
		}
		

		for (int i = 0; i < urlFolderList.size(); i++) {
			List<StatisticErrorBean> list = cs.take().get();
			combineList(resultList, list);

		}
		es.shutdown();

		return resultList;
	}

	public Map<String, Integer> statisticProducerLog(String folder,
			String reportS, String url) throws  Exception {

		// report date
		Date reportD = calculateReportDate(reportS);
		Calendar reportMinus1 = calculateCalendarReportMinus1(reportD);
		Calendar reportAdd1 = calculateCalendarReportAdd1(reportD);

		String directoryURL = decorateDirectoryURL(url);
		String downloadURL = decorateDownloadURL(url);

		// file list
		List<String> fileList = filterFileList(folder, reportMinus1,
				directoryURL);
		if (fileList == null) {
			return null;
		}

		// error list
		List<String> errorList = filterErrorList4Producer(reportMinus1,
				reportAdd1, fileList, downloadURL);
		if (errorList.size() == 0) {
			return null;
		}

		// parse error list
		List<String> errorMsgList = parseErrorMsgList4Producer(errorList);

		// statistics and sort
		Map<String, Integer> resultMap = calculateProducerResultMap(errorMsgList);

		return resultMap;
	}

	private Map<String, Integer> calculateProducerResultMap(
			List<String> errorMsgList) {
		Map<String, Integer> map = new HashMap<String, Integer>();

		for (String temp : errorMsgList) {
			Integer count = map.get(temp);
			map.put(temp, (count == null) ? 1 : count + 1);
		}

		Map<String, Integer> resultMap = sortMapByValue(map);

		for (String key : resultMap.keySet()) {
			logger.debug(resultMap.get(key) + " : " + key);
		}

		return resultMap;
	}

	private List<String> parseErrorMsgList4Producer(List<String> errorList) {
		List<String> errorMsgList = new ArrayList<String>();
		for (String error : errorList) {
			int begin = error.indexOf("{");
			String s1 = error.substring(begin + 1);

			int end = s1.indexOf("}");
			if (end == -1) {
				end = s1.indexOf(":");
			}
			s1 = s1.substring(0, end);
			errorMsgList.add(s1);
		}
		return errorMsgList;
	}

	private List<String> filterErrorList4Producer(Calendar reportMinus1,
			Calendar reportAdd1, List<String> fileList, String downloadURL)
			throws Exception {
		List<String> errorList = new ArrayList<String>();

		for (String file : fileList) {
			InputStream input = client.loadInputStream(file, downloadURL);
			InputStreamReader isr = new InputStreamReader(input);
			BufferedReader br = new BufferedReader(isr);
			String temp = "";
			while ((temp = br.readLine()) != null) {
				if (temp.length() > 10) {
					String s1 = temp.substring(0, 10);
					Date d1 = null;
					try {
						d1 = sdf2.get().parse(s1);
					} catch (Exception e) {
						continue;
					}

					Calendar c1 = Calendar.getInstance();
					c1.setTime(d1);

					if (c1.after(reportMinus1) && c1.before(reportAdd1)) {
						errorList.add(temp);
					}
				}

			}
			br.close();
			isr.close();
			input.close();
		}

		return errorList;
	}

}
