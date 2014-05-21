package com.hp.it.perf.acweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.hp.it.perf.acweb.service.PropertyLoadService;
import com.hp.it.perf.acweb.util.Constant;


@Controller
@RequestMapping("/chain")
public class ACChainWebContoller {
	
	private PropertyLoadService propertyLoadService = new PropertyLoadService();
	
	protected String modelview = "chain";
	
    @RequestMapping(method = RequestMethod.GET)

    public String get(@RequestParam(value="acid", required=false) String acid, Model model){
    	//How to getacId

    	model.addAttribute(Constant.ACSERVICE_URL_CHAIN, propertyLoadService.loadProperty(Constant.ACSERVICE_URL_CHAIN)+"/"+acid);
    	model.addAttribute(Constant.ACSERVICE_URL_LOGDETAIL, propertyLoadService.loadProperty(Constant.ACSERVICE_URL_LOGDETAIL));
    	return modelview;
    }
	
}
