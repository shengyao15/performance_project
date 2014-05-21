package com.hp.it.perf.ac.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.googlecode.ehcache.annotations.Cacheable;
import com.hp.it.perf.ac.common.model.AcidHelper;
import com.hp.it.perf.ac.rest.model.ChainEntry;
import com.hp.it.perf.ac.rest.service.IService;
import com.hp.it.perf.ac.rest.util.CacheConstants;

@Controller
@RequestMapping({ "/chain" })
public class ChainServiceController {

	private static final Logger log = LoggerFactory
			.getLogger(ChainServiceController.class);

	@Autowired
	private IService service;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	@Cacheable(cacheName = CacheConstants.CHAIN)
	public ChainEntry getChain(@PathVariable("id") final String id) {
		String logPrefix = this.getClass().getName() + ".getChain(Long id): ";
		log.debug(logPrefix + "enter.");
		log.debug(logPrefix + "id: {}.", id);
		long acid = 0;
		try {
			acid = AcidHelper.getInstance().parseHexString(id);
		} catch(NumberFormatException e) {
			log.error(logPrefix + "invalid acid {}" + id);
			// do not throw exception, just return null 
			return null;
		}
		log.debug(logPrefix + "return.");
		return service.getFullChain(acid);
	}
}
