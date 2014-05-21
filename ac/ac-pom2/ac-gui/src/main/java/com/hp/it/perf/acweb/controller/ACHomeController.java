package com.hp.it.perf.acweb.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.hp.it.perf.acweb.HomeForm;
import com.hp.it.perf.acweb.service.*;
import com.hp.it.perf.acweb.util.Constant;


@Controller
public class ACHomeController {

	private PropertyLoadService propertyLoadService = new PropertyLoadService();
	private ThemeService themeService = new ThemeService();
	
	protected String modelname = "achome";
	
	private void loadModelValue(Model model){
		model.addAttribute(Constant.ACWEB_URL_HOME, propertyLoadService.loadProperty(Constant.ACWEB_URL_HOME));
		
		model.addAttribute(Constant.ACSERVICE_URL_HOMECONSUMER, propertyLoadService.loadProperty(Constant.ACSERVICE_URL_HOMECONSUMER));
		model.addAttribute(Constant.ACSERVICE_URL_HOMEPRODUCER, propertyLoadService.loadProperty(Constant.ACSERVICE_URL_HOMEPRODUCER));
		
	}
	
	protected String modelview = "home";
	@RequestMapping(value="/home",method = RequestMethod.GET)
	public String get(@ModelAttribute("homeForm") HomeForm homeForm, HttpSession session, Model model){
		
		
        loadModelValue(model);
    	themeService.enableThemeList(session);
        themeService.enableTheme(homeForm.getTheme(), session);
        
        /*
         * get form data
         */
        String starttime = homeForm.getStarttime();
        String endtime = homeForm.getEndtime();
        int duration = Integer.parseInt(propertyLoadService.loadProperty(Constant.ACSERVICE_PARA_DURATION));
        if(starttime == null && endtime == null){
        	Date endDate = new Date();
        	endtime = formatDate(endDate);
        	Calendar cal = Calendar.getInstance();
        	cal.setTime(endDate);
        	cal.add(Calendar.HOUR_OF_DAY, -duration);
        	starttime = formatDate(cal.getTime());
        }else if(starttime != null && endtime == null){
        	endtime = formatDate(new Date());
        }else if(starttime == null && endtime != null){
        	Calendar cal = Calendar.getInstance();
        	cal.setTime(new Date(endtime));
        	cal.add(Calendar.HOUR_OF_DAY, -duration);
        	starttime = formatDate(cal.getTime());
        }
        model.addAttribute("starttime", starttime);
        model.addAttribute("endtime", endtime);
    	model.addAttribute(modelname, homeForm);
    	

    	return modelview;
    }

	/*
	 * format date according to the "yyyy-MM-dd HH:mm:ss" pattern
	 */
	private String formatDate(Date date){
		String timePattern = "yyyy-MM-dd HH:mm:ss";
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timePattern);
    	String dateStr = simpleDateFormat.format(date);
    	return dateStr;
    	
	}
}
