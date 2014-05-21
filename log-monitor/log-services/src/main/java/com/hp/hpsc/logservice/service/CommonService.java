package com.hp.hpsc.logservice.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.hp.hpsc.logservice.client.Link;
import com.hp.hpsc.logservice.client.LogviewClient;
import com.hp.hpsc.logservice.parser.beans.StatisticErrorBean;

public class CommonService {

	private static Logger logger = Logger.getLogger(CommonService.class);
	// file date format / consumer log format
	// protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	protected static final ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};

	// producer log format
	//protected SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");

	protected static final ThreadLocal<SimpleDateFormat> sdf2 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("MM/dd/yyyy");
		}
	};
	
	
	// consumer log key word
	protected String ERROR_KEY_MSG = "The portlet with title,";

	protected LogviewClient client = new LogviewClient();

	protected Date calculateReportDate(String reportS)
			throws ParseException {
		Date reportD = null;
		try {
			reportD = sdf.get().parse(reportS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reportD;
	}

	protected Calendar calculateCalendarReportMinus1(Date reportD) {
		Calendar reportMinus1 = Calendar.getInstance();
		reportMinus1.setTime(reportD);
		reportMinus1.add(Calendar.DATE, -1);
		return reportMinus1;
	}

	protected Calendar calculateCalendarReportAdd1(Date reportD) {
		Calendar reportAdd1 = Calendar.getInstance();
		reportAdd1.setTime(reportD);
		reportAdd1.add(Calendar.DATE, 1);
		return reportAdd1;
	}

	protected String decorateDirectoryURL(String url) {
		String result = url
				+ "Directory4ClientServlet?path=";
		return result;
	}

	protected String decorateDownloadURL(String url) {
		String result = url
				+ "Download4ClientServlet?path=";
		return result;
	}

	protected void printStatisticErrorBean(List<StatisticErrorBean> beanList) {
		for (StatisticErrorBean bean : beanList) {
			// logger.debug(bean.getDate());
			logger.debug(bean.getFeatureName());
			Map<String, Integer> errorDetails = bean.getErrorDetails();
			for (Entry<String, Integer> entry : errorDetails.entrySet()) {
				logger.debug(entry.getValue() + " : " + entry.getKey());
			}

		}
	}

	protected List<String> filterFileList(String folder, Calendar reportMinus1,
			String url) throws Exception {

		List<Link> list = client.readDirectory(folder, url);
		
		if (list == null || list.size() == 0) {
			return null;
		}
		// check file last modified date
		List<String> fileList = new ArrayList<String>();
		for (Link link : list) {
			if (!link.isFolderFlag()) {
					String s1 = link.getLastModifiedDate();
					Date d1 = null;
					try {
						d1 = sdf.get().parse(s1);
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
					Calendar c1 = Calendar.getInstance();
					c1.setTime(d1);
					if (c1.after(reportMinus1)) {
						logger.debug(link.getName());
						fileList.add(link.getUri());
					}
			}
		}
		return fileList;
	}

	protected Map<String, Integer> sortMapByValue(Map<String, Integer> oriMap) {
		if (oriMap == null || oriMap.isEmpty()) {
			return null;
		}

		List<Map.Entry<String, Integer>> entryList = new ArrayList<Map.Entry<String, Integer>>(
				oriMap.entrySet());

		Collections.sort(entryList, new MapValueComparator());

		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();

		for (Map.Entry<String, Integer> entry : entryList) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

    protected List<StatisticErrorBean> combineList(
			List<StatisticErrorBean> resultList, List<StatisticErrorBean> list) {

		for (StatisticErrorBean bean : list) {

			boolean exist = false;
			for (StatisticErrorBean bean2 : resultList) {
				if (bean.getFeatureName().equals(bean2.getFeatureName())) {
					exist = true;
					Map<String, Integer> errorDetails = bean.getErrorDetails();
					combineErrorDetail(bean2, errorDetails);
					break;
				}
			}

			if (!exist) {
				logger.debug("not exist, add..." + bean.getFeatureName());
				resultList.add(bean);
			}

		}

		return resultList;
	}

	protected void combineErrorDetail(StatisticErrorBean bean,
			Map<String, Integer> producerLog) {
		logger.debug("combine the feature: " + bean.getFeatureName());

		Map<String, Integer> producerLogOrg = bean.getErrorDetails();

		for (String key : producerLog.keySet()) {
			if (producerLogOrg.containsKey(key)) {
				Integer number = producerLogOrg.get(key);
				producerLogOrg.put(key, number + producerLog.get(key));
			} else {
				producerLogOrg.put(key, producerLog.get(key));
			}

		}

	}

	public static void main(String[] args) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("a", 1);

		map.put("a", 2);

		logger.debug(map.get("a"));
	}
}
