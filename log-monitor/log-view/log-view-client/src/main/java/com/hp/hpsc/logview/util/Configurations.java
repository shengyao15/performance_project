package com.hp.hpsc.logview.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class Configurations {

	public static final String[] DEFAULT_CONFIG_FILENAMES = new String[]{"logview_configs.properties", "accesslog_configs.properties"};
	
	private static Properties configs = new Properties();
	private static Configurations instance = new Configurations();
	
	private Configurations(){}
	
	public static Configurations getInstance(){
		return instance;
	}
	
	public static enum ConfugrationKeys {
		MAX_HOST_CONNECTIONS, MAX_TOTAL_CONNECTIONS, CONNECTIONS_TIMEOUT, 
		REGREX_PATTEN, DATE_FORMAT_STRING, 
		ENALBE_DATE_IN_PARSER, ENALBE_ROOTPATH_IN_PARSER, ENALBE_DATEFILTER_IN_PARSER, ENABLE_DATE_SORT_DESC, ENABLE_FOLDER_CASCADE,
		CLASSNAME_RETREIVER_INSTANCE, CLASSNAME_PARSER_INSTANCE, ACCESSLOG_FOLDER_URL, ACCESSLOG_FILEFILTER
	}
	
	public class ConfigurationException extends Exception {
		public ConfigurationException(){}
		public ConfigurationException(Throwable r){
			super(r);
		}
		public ConfigurationException(String message){
			super(message);
		}
	}
	

	public static String[] getConfigArray(ConfugrationKeys key) throws ConfigurationException{
		if(key == null){
			throw Configurations.getInstance().new ConfigurationException("Not valid key -- key is null");
		}
		if(configs.size() == 0){
			load(null);
		}
		
		List<String> results = new ArrayList<String>(5);
		Enumeration<Object> keys = configs.keys();
		while(keys.hasMoreElements()){
			String tmp = (String)keys.nextElement();
			if(tmp != null && tmp.contains(key.toString())){
				results.add(configs.getProperty(tmp));
			}
		}
		
		String[] array = new String[results.size()];
		array = results.toArray(array);
		return array;
	}
	
	public static String getConfigString(ConfugrationKeys key) throws ConfigurationException{
		if(key == null){
			throw Configurations.getInstance().new ConfigurationException("Not valid key -- key is null");
		}
		if(configs.size() == 0){
			load(null);
		}
		return (String)configs.get(key.toString());
	}
	
	public static String getConfigString(ConfugrationKeys key, String defaultValue){
		if(key == null){
			return defaultValue;
		}
		if(configs.size() == 0){
			try {
				load(null);
			} catch (ConfigurationException e) {
				return defaultValue;
			}
		}	
		return (String)configs.get(key.toString());
	}
	
	public static int getConfigInt(ConfugrationKeys key) throws ConfigurationException{
		if(key == null){
			throw Configurations.getInstance().new ConfigurationException("Not valid key -- key is null");
		}
		if(configs.size() == 0){
			load(null);
		}
		String p = (String)configs.get(key.toString());
		try{
			int result = Integer.valueOf(p).intValue();
			return result;
		}catch(NumberFormatException nfe){
			throw Configurations.getInstance().new ConfigurationException(nfe);
		}
	}
	
	public static int getConfigInt(ConfugrationKeys key, int defaultValue){
		if(key == null){
			return defaultValue;
		}
		if(configs.size() == 0){
			return defaultValue;
		}
		String p = (String)configs.get(key.toString());
		try{
			int result = Integer.valueOf(p).intValue();
			return result;
		}catch(NumberFormatException nfe){
			return defaultValue;
		}
	}
	
	public static int load(String[] fileNames) throws ConfigurationException{
		InputStream reader = null;
		
		if(fileNames == null || fileNames.length == 0){
			for(String config : DEFAULT_CONFIG_FILENAMES){
				reader = Configurations.class.getClassLoader().getResourceAsStream(config);		
				try {
					Properties tmp = new Properties();
					tmp.load(reader);
					if(!tmp.isEmpty()){
						configs.putAll(tmp);
					}
				} catch (IOException e) {
					Configurations.getInstance().new ConfigurationException(e);
				}finally{
					try {
						reader.close();
					} catch (IOException e) {
					}
				}
			}
		}else{
			for(String config : fileNames){
				reader = Configurations.class.getClassLoader().getResourceAsStream(config);		
				try {
					Properties tmp = new Properties();
					tmp.load(reader);
					if(!tmp.isEmpty()){
						configs.putAll(tmp);
					}
				} catch (IOException e) {
					Configurations.getInstance().new ConfigurationException(e);
				}finally{
					try {
						reader.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return configs.size();
	}
}
