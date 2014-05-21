package com.hp.it.perf.ac.rest.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.googlecode.ehcache.annotations.Cacheable;
import com.hp.it.perf.ac.rest.model.Category;
import com.hp.it.perf.ac.rest.service.IService;
import com.hp.it.perf.ac.rest.util.CacheConstants;
import com.hp.it.perf.ac.rest.util.Constant;
import com.hp.it.perf.ac.rest.util.QueryConstants;

@Controller
@RequestMapping({ "/category" })
public class CategoryServiceController {

	private static final Logger log = LoggerFactory
			.getLogger(CategoryServiceController.class);

	@Autowired
	private IService service;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@Cacheable(cacheName = CacheConstants.CATEGORY)
	public Map<String, Category[]> getCategories(
			@RequestParam(value = QueryConstants.INCLUDE_ALL, required = false) final String includeAll) {
		String logPrefix = this.getClass().getName() + ".getCategories(): ";
		log.debug(logPrefix + "enter.");

		Category[] categories = null;
		try {
			if (includeAll != null && !includeAll.trim().equalsIgnoreCase("")) {
				categories = service.getCategories(true);
			} else {
				categories = service.getCategories(false);
			}
		} catch (Exception e) {
			log.error(logPrefix
					+ " Request processing failed; nested exception is {}", e);
			// do not throw exception, just return null 
		}
		// check not empty
		//categories = (Category[]) RestPreconditions.checkNotEmpty(categories);
		Map<String, Category[]> result = new HashMap<String, Category[]>();
		result.put(Constant.KEY_TREE_DATE, categories);
		log.debug(logPrefix + "return.");
		return result;
	}
}
