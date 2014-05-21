package com.hp.hpsc.logview.parsers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class UtilInstanceCollection {

	private static UtilInstanceCollection instance = new UtilInstanceCollection();
	private static Map<String, Pattern> patterns = new ConcurrentHashMap<String, Pattern>(5);
	private static Map<String, DateFormat> formatters = new ConcurrentHashMap<String, DateFormat>(5);
	
	private UtilInstanceCollection(){}
	
	public static UtilInstanceCollection getInstance(){
		return instance;
	}
	
	public Pattern getPattern(String k){
		if(k == null){
			return null;
		}
		Pattern p = patterns.get(k);
		if(p != null){
			return p;
		}else{
			p = Pattern.compile(k);
			patterns.put(k, p);
			return p;
		}
	}
	
	public DateFormat getFormatter(String k){
		if(k == null){
			return null;
		}
		DateFormat f = formatters.get(k);
		if(f != null){
			return f;
		}else{
			f = new SimpleDateFormat(k, Locale.ENGLISH);
			f.setTimeZone(TimeZone.getTimeZone("UTC"));
			formatters.put(k, f);
			return f;
		}
	}
}
