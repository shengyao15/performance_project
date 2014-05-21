package com.hp.hpsc.logservice.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hp.hpsc.logservice.client.Link;
import com.hp.hpsc.logservice.parser.beans.StatisticErrorBean;

@Deprecated
public class ProducerErrorServiceSingleThread extends CommonService {

	private static Logger logger = Logger.getLogger(ProducerErrorServiceSingleThread.class);
	
	public List<StatisticErrorBean> statisticAllProducerLog(String reportS,
			String[] urls) throws  Exception {

		List<StatisticErrorBean> resultList = new ArrayList<StatisticErrorBean>();
		String mainFolder = "/opt/sasuapps/itrc/logs";

		for (int i = 0; i < urls.length; i++) {

			List<StatisticErrorBean> errorBeanList = new ArrayList<StatisticErrorBean>();

			logger.debug("url--------" + urls[i]);
			String directoryURL = decorateDirectoryURL(urls[i]);

			List<Link> list = client.readDirectory(mainFolder, directoryURL);

			Map<String, String> allPath = new HashMap<String, String>();
			for (Link link : list) {
				allPath.put(link.getName(), mainFolder + "/" + link.getName()
						+ "/main/error");
			}

			for (String key : allPath.keySet()) {
				logger.debug("looking at path " + allPath.get(key));
				Map<String, Integer> producerLog = statisticProducerLog(
						allPath.get(key), reportS, urls[i]);
				if (producerLog != null && producerLog.size() > 0) {
					StatisticErrorBean bean = new StatisticErrorBean();
					bean.setCollectDate(reportS);
					bean.setFeatureName(key);
					bean.setErrorDetails(producerLog);
					errorBeanList.add(bean);

				}
			}
			
			resultList = combineList(resultList, errorBeanList);
		}

		
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
						// TODO Auto-generated catch block
						e.printStackTrace();
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
