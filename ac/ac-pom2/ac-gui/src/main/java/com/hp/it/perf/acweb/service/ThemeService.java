package com.hp.it.perf.acweb.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;

import com.hp.it.perf.acweb.util.Constant;

public class ThemeService {

	public static String DEFAULT_THEME = "redmond";
	public static String THEME_SESSION_KEY = "theme_session_key";
	public static String THEME_SESSION_URL = "theme_session_url";
	
	public static String THEME_SESSION_KEYLIST = "theme_session_keylist";
	public static String THEME_SESSION_URLLIST = "theme_session_urllist";
	
	private PropertyLoadService propertyLoadService = new PropertyLoadService();
	
	
	public Map<String, String> listThemes(){
		Map<String, String> values = propertyLoadService.queryProperties(Constant.ACWEB_THEME_NAME);
		return values;
	}
	
	public boolean enableThemeList(HttpSession session){
		String sKeys = (String)session.getServletContext().getAttribute(THEME_SESSION_KEYLIST);
		String sUrls = (String)session.getServletContext().getAttribute(THEME_SESSION_URLLIST);
		if(sKeys != null && sUrls != null && !sKeys.trim().isEmpty()){
			//using current values
		}else{
			List<String> keys = new ArrayList<String>(5);
			List<String> urls = new ArrayList<String>(5);
			Map<String, String> values = listThemes();
			for(String k: values.keySet()){
				keys.add(k);
				urls.add(values.get(k));
			}
			
			session.getServletContext().setAttribute(THEME_SESSION_KEYLIST, new JSONArray(keys).toString());
			session.getServletContext().setAttribute(THEME_SESSION_URLLIST, new JSONArray(urls).toString());
		}
		return true;
	}
	
	public boolean enableTheme(String v, HttpSession session){
		if(null == v){
			String key = (String)session.getAttribute(THEME_SESSION_KEY);
			if(key != null){
				//using default values
			}else{
				Map<String, String> values = listThemes();
				String url = values.get(DEFAULT_THEME);			
				session.setAttribute(THEME_SESSION_KEY, DEFAULT_THEME);
				session.setAttribute(THEME_SESSION_URL, url);
			}
		}else{
			Map<String, String> values = listThemes();
			String url = values.get(v);			
			session.setAttribute(THEME_SESSION_KEY, v);
			session.setAttribute(THEME_SESSION_URL, url);
		}
		return true;
	}
}
