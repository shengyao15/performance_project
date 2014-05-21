package com.hp.it.perf.ac.rest.controller;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.googlecode.ehcache.annotations.Cacheable;
import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;
import com.hp.it.perf.ac.common.model.AcidHelper;
import com.hp.it.perf.ac.rest.model.LogDetailWrapper;
import com.hp.it.perf.ac.rest.util.CacheConstants;
import com.hp.it.perf.ac.service.data.AcRepositoryService;

@Controller
@RequestMapping({ "/logdetail" })
public class LogDetailController {

	private static final Logger log = LoggerFactory
			.getLogger(LogDetailController.class);

	@Inject
	private AcRepositoryService repositoryService;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	@Cacheable(cacheName = CacheConstants.LOGDETAIL)
	public LogDetailWrapper getLogDetail(@PathVariable("id") final String id) {
		String logPrefix = this.getClass().getName() + ".getLogDetail(...): ";
		log.debug(logPrefix + "enter.");
		log.debug(logPrefix + "id: {}.", id);
		long acid = 0;
		AcCommonDataWithPayLoad commonDate = null;
		try {
			acid = AcidHelper.getInstance().parseHexString(id);
		} catch (NumberFormatException e) {
			log.error(logPrefix + "invalid acid {}" + id);
			// do not throw exception, just return null
			return null;
		}
		try {
			log.debug("acid: " + acid);
			commonDate = repositoryService.getCommonDataWithPayLoad(acid);
		} catch (Exception e) {
			log.error(logPrefix
					+ " Request processing failed; nested exception is {}", e);
			// do not throw exception, just return null 
			return null;
		}
		// check not empty and return
		//RestPreconditions.checkNotNull(commonDate);
		if(commonDate == null) 
			return null;
		LogDetailWrapper result = new LogDetailWrapper(commonDate);
		log.debug(logPrefix + "return.");
		return result;
	}
}
