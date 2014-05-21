package com.hp.it.perf.acweb.service;

import com.hp.it.perf.acweb.util.Constant;

public class WebTemplateService {

	private PropertyLoadService propertyLoadService = new PropertyLoadService();
	
	public String queryTableTemplate(String type){
		String t = type;
		if(type == null || !"consumer".equalsIgnoreCase(type)){
			System.out.println("No support for this table template :"+type);
			t = Constant.ACWEB_TABLE_DEFAULT;
		}
		
		String key = Constant.ACWEB_TABLE_PROFIX + t;
		
		return propertyLoadService.loadContent(key);
	}
	
	public String queryReportTemplate(String type){
		String t = type;
		if(type == null){
			System.out.println("No support for this table template :"+type);
			t = Constant.ACWEB_TABLE_DEFAULT;
		}
		
		String key = Constant.ACWEB_TABLE_PROFIX + t;
		
		return propertyLoadService.loadContent(key);
	}
}
