package com.hp.it.perf.acweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.hp.it.perf.acweb.LoglistForm;
import com.hp.it.perf.acweb.service.PropertyLoadService;
import com.hp.it.perf.acweb.service.WebTemplateService;
import com.hp.it.perf.acweb.util.Constant;


@Controller
@RequestMapping("/loglist")
public class LogListWebContoller {
	
	private String service_parameter_starttime = "starttime=";
	private String service_parameter_endtime = "endtime=";
	private String service_parameter_category = "category=";
	private String service_parameter_type = "type=";
	private String service_parameter_limit = "limit=";
	private String service_parameter_searchstr = "name=";
	private char parameter_char = '?';
	private char parameter_link_char = '&';
	
	
	private PropertyLoadService propertyLoadService = new PropertyLoadService();
	private WebTemplateService webTemplateService = new WebTemplateService();
	
	protected String modelview = "loglistIndex";
	protected String modelname = "loglist";
	
	private void loadModelValue(Model model){
		model.addAttribute(Constant.ACWEB_URL_LOGLIST, propertyLoadService.loadProperty(Constant.ACWEB_URL_LOGLIST));
		model.addAttribute(Constant.ACWEB_URL_LOGDETAIL, propertyLoadService.loadProperty(Constant.ACWEB_URL_LOGDETAIL));
		model.addAttribute(Constant.ACWEB_URL_LOGCHAIN, propertyLoadService.loadProperty(Constant.ACWEB_URL_LOGCHAIN));
		
		model.addAttribute(Constant.ACSERVICE_PARA_LIMITION, propertyLoadService.loadProperty(Constant.ACSERVICE_PARA_LIMITION));
		
		model.addAttribute(Constant.ACSERVICE_URL_CATEGORY, propertyLoadService.loadProperty(Constant.ACSERVICE_URL_CATEGORY));
		
	}
	
	private String initGet(LoglistForm loglistForm, Model model){
	   	String serviceParamters = 
    			parameter_char+service_parameter_starttime+loglistForm.getStarttime()
    			+parameter_link_char+service_parameter_endtime+loglistForm.getEndtime()
    			+parameter_link_char+service_parameter_category+loglistForm.getCategory()
    			+parameter_link_char+service_parameter_type+loglistForm.getType()
    			+parameter_link_char+service_parameter_limit+loglistForm.getLimit()
	   			+parameter_link_char+service_parameter_searchstr+loglistForm.getName();
  	
    	model.addAttribute(Constant.ACSERVICE_URL_LOGLIST, propertyLoadService.loadProperty(Constant.ACSERVICE_URL_LOGLIST) + serviceParamters);
    	
    	model.addAttribute(modelname, loglistForm);
    	
    	model.addAttribute(Constant.ACWEB_TABLE_HEADER, webTemplateService.queryTableTemplate(Constant.ACWEB_TABLE_DEFAULT));
    	return modelview;
	}
	  
    @RequestMapping(method = RequestMethod.GET)
    public String get(@RequestParam(value="starttime", required=false) String starttime, @RequestParam(value="endtime", required=false) String endtime, 
			@RequestParam(value="category", required=false) String category, @RequestParam(value="type", required=false) String type, 
			@RequestParam(value="name", required=false) String name, Model model){
    	
    	loadModelValue(model);
    	    	
    	LoglistForm loglistForm = new LoglistForm();
    	loglistForm.setStarttime(starttime);
    	loglistForm.setEndtime(endtime);
    	loglistForm.setCategory(category);
    	loglistForm.setType(type);
    	loglistForm.setName(name);
    	
    	if(null == category || category.isEmpty()){
    		loglistForm.setCategory("1");
    	}
    	
    	if(null == type || type.isEmpty()){
    		loglistForm.setType("1");
    	}
    	
    	try{
    	loglistForm.setLimit(Integer.parseInt(propertyLoadService.loadProperty(Constant.ACSERVICE_PARA_LIMITION)));
    	}catch(NumberFormatException nfe){
    		loglistForm.setLimit(Constant.MAX_RETURN_ROWS);
    	};
    	
    	return initGet(loglistForm, model);
    }
	
    @RequestMapping(method = RequestMethod.POST)
    public String post(@ModelAttribute("loglistForm") LoglistForm loglistForm, Model model){
    	System.out.println("Start time:"+loglistForm.getStarttime());
    	System.out.println("End time:"+loglistForm.getEndtime());
    	System.out.println("Name: "+loglistForm.getName());
    	System.out.println("limit: "+loglistForm.getLimit());
    	System.out.println("category: "+loglistForm.getCategory());
    	System.out.println("type: "+loglistForm.getType());
    	model.addAttribute(modelname, loglistForm);
    	
    	loadModelValue(model);
    	
    	String serviceParamters = 
    			parameter_char+service_parameter_starttime+loglistForm.getStarttime()
    			+parameter_link_char+service_parameter_endtime+loglistForm.getEndtime()
    			+parameter_link_char+service_parameter_category+loglistForm.getCategory()
    			+parameter_link_char+service_parameter_type+loglistForm.getType()
    			+parameter_link_char+service_parameter_limit+loglistForm.getLimit()
	   			+parameter_link_char+service_parameter_searchstr+loglistForm.getName();
    	
    	model.addAttribute(Constant.ACSERVICE_URL_LOGLIST, propertyLoadService.loadProperty(Constant.ACSERVICE_URL_LOGLIST) + serviceParamters);

    	model.addAttribute(Constant.ACWEB_TABLE_HEADER, webTemplateService.queryTableTemplate(loglistForm.getCategory()));
    	return modelview;
    }
}
