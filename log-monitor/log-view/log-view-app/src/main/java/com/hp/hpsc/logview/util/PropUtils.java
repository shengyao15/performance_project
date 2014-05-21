package com.hp.hpsc.logview.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PropUtils {

	private static Set<String> logPathSet = new HashSet<String>();
	public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";
	public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";
	static {
		InputStream in = PropUtils.class.getClassLoader().getResourceAsStream(
				"logview/log_path.properties");
		Properties p = new Properties();
		try {
			p.load(in);
			Iterator it = p.entrySet().iterator();
			Set<String> set = new HashSet<String>();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String value = (String) entry.getValue();
				
				if(value != null || value.contains(DEFAULT_PLACEHOLDER_PREFIX)){
					value = combineEnv(value);
				}
				
				if(value!=null || !"".equals(value)){
					set.add(value);
				}
				
			}
			
			logPathSet = verify(set);
			
		} catch (IOException e) {
		}
	}

	public static Set<String> getLogPathSet() {
		return logPathSet;
	}

	private static Set<String> verify(Set<String> logPath) {
		Set<String> set = new HashSet<String>();
		for (String s : logPath) {
			if (s == null || "".equals(s)) {
				continue;
			}
			File file = new File(s);
			if (file.exists()) {
				set.add(s);
			}
		}
		return set;
	}



	public static void main(String[] args) throws Exception {
		InputStream in = PropUtils.class.getClassLoader().getResourceAsStream(
				"logview/log_path.properties");
		Properties p = new Properties();
		
		p.load(in);
		
		System.setProperty("com.vignette.portal.installdir.path","aaa");
		System.setProperty("appserver.home.dir","bbb");
		System.setProperty("LOG_ROOT","ccc");
		System.setProperty("log_dir","ddd");
		
		Iterator it = p.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String value = (String) entry.getValue();
			
			if(value.contains(DEFAULT_PLACEHOLDER_PREFIX)){
				value = combineEnv(value);
			}
			
		}
		
	}

	private static String combineEnv(String value) {
		
		StringBuffer sb = new StringBuffer(value);
		int index1 = sb.indexOf("${") + 2;
		int index2 = sb.indexOf("}");
		String envParam = sb.substring(index1, index2);
		String envValue = System.getProperty(envParam);
		
		if(envValue == null || "".equals(envValue)){
			return "";
		}
		
		StringBuffer sb2 = sb.replace(index1-2, index2+1, envValue);
		return sb2.toString();
		
		
	}
}
