package com.hp.hpsc.logservice.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.hp.hpsc.logservice.parser.beans.UrlFolderBean;

public class PropUtils {

	private static Logger logger = Logger.getLogger(PropUtils.class);
	static Map<String, String> propMap = new LinkedHashMap<String, String>();
	static Properties p = new Properties();

	static {
		InputStream in = PropUtils.class.getClassLoader().getResourceAsStream(
				"config/url.properties");
		try {
			p.load(in);
			Iterator it = p.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				logger.debug(key + ":" + value);
				propMap.put(key, value);
			}

			System.out.println("--------------");
			for (String k : propMap.keySet()) {
				System.out.println(k + "   " + propMap.get(k));
			}

			System.out.println("--------------");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String read(String key) throws IOException {
		return (String) propMap.get(key);
	}

	public static List<UrlFolderBean> readAllConsumer() throws IOException {

		List<UrlFolderBean> list = new ArrayList<UrlFolderBean>();

		Integer consumerMaxSize = Integer.valueOf(read("consumer.max.size"));
		for (int i = 1; i <= consumerMaxSize; i++) {
			UrlFolderBean bean = new UrlFolderBean();
			String url = propMap.get("consumer." + i + ".url");
			String folder = propMap.get("consumer." + i + ".folder");
			if (url == null || "".equals(url) || folder == null
					|| "".equals(folder)) {
				continue;
			}

			bean.setUrl(url);
			bean.setFolder(folder);
			list.add(bean);
		}

		return list;
	}

	public static List<UrlFolderBean> readAllProducer() throws IOException {

		List<UrlFolderBean> list = new ArrayList<UrlFolderBean>();

		Integer consumerMaxSize = Integer.valueOf(read("producer.max.size"));
		for (int i = 1; i <= consumerMaxSize; i++) {
			UrlFolderBean bean = new UrlFolderBean();
			String url = propMap.get("producer." + i + ".url");
			String folder = propMap.get("producer." + i + ".folder");
			if (url == null || "".equals(url) || folder == null
					|| "".equals(folder)) {
				continue;
			}

			bean.setUrl(url);
			bean.setFolder(folder);
			list.add(bean);
		}

		return list;
	}
}
