package com.hp.it.perf.acservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.hp.it.perf.acweb.service.PropertyLoadService;
import com.hp.it.perf.acweb.util.Constant;

@Controller
@RequestMapping("/uiservice")
public class UIServiceController {
	
	private PropertyLoadService propertyService = new PropertyLoadService();
	
	@RequestMapping(value="/{type}", method = RequestMethod.GET)
    public @ResponseBody String getForDay(@PathVariable String type) {
		
		String content = propertyService.loadContent(Constant.ACSERVICE_DATA_PREFIX+type);
		if(content == null){
			throw new RuntimeException("bad configuration for type = "+type);
		}
    	
    	return content;
    }


}
