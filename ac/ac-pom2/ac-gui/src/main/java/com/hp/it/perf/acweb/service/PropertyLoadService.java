package com.hp.it.perf.acweb.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.springframework.core.io.ClassPathResource;

public class PropertyLoadService {

	private String config = "com/hp/it/perf/acweb/config/config.properties";
	private static Properties properties;
	
	private void init(){
		InputStream input = null;
		if(properties == null){
			properties = new Properties();
			try {
				input = new ClassPathResource(config).getInputStream();
				properties.load(input);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(input != null){
					try {
						input.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	public Map<String, String> queryProperties(String name){
		Map<String, String> result = new TreeMap<String, String>();
		if(null != name){
			init();
			Enumeration<?> keys = properties.keys();
			while(null != keys && keys.hasMoreElements()){
				String key = (String)keys.nextElement();
				if(key.startsWith(name)){
					StringBuffer buffer = new StringBuffer().append(key);
					buffer.delete(0, name.length());
					result.put(buffer.toString(), properties.getProperty(key));
				}
			}
		}
		return result;
	}
	
	public List<String> searchKeysbyPrefix(String name){
		List<String> result = new ArrayList<String>(5);
		if(null != name){
			init();
			Enumeration<?> keys = properties.keys();
			while(null != keys && keys.hasMoreElements()){
				String key = (String)keys.nextElement();
				if(key.startsWith(name)){
					result.add(key);
				}
			}
		}
		return result;
	}
	public String loadProperty(String type){
		if(null != type){
			init();			
			String value = properties.getProperty(type);
			return value;
		}else{
			return null;
		}
	}
	
	public String loadContent(String type){
		if(null != type){
			init();
			
			String filename = properties.getProperty(type);
			if(filename != null){
				BufferedReader in = null;
				StringBuffer buffer = new StringBuffer();
				try {
					in = new BufferedReader(new InputStreamReader(new ClassPathResource(filename).getInputStream()));					
					
					String str = null;
					while(true){
						str = in.readLine();
						if(str != null){
							buffer.append(str);
						}else{
							break;
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					if(in != null){
						try {
							in.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
				return buffer.toString();
			}
		}
		
		return null;
		
	}

}
