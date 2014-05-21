package com.hp.it.perf.ac.rest.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.googlecode.ehcache.annotations.Cacheable;
import com.hp.it.perf.ac.app.hpsc.search.bean.ConsumerHomeInfo;
import com.hp.it.perf.ac.app.hpsc.search.bean.ProducerHomeInfo;
import com.hp.it.perf.ac.app.hpsc.search.service.HpscQueryService;
import com.hp.it.perf.ac.rest.util.CacheConstants;
import com.hp.it.perf.ac.rest.util.QueryConstants;
import com.hp.it.perf.ac.rest.util.Utils;

@Controller
@RequestMapping({ "/home" })
public class HomeServiceController {

	private static final Logger log = LoggerFactory
			.getLogger(HomeServiceController.class);

	@Autowired
	@Qualifier("hpscQueryService")
	private HpscQueryService hpscQuery;

	@RequestMapping(value = "/consumer", method = RequestMethod.GET)
	@ResponseBody
	@Cacheable(cacheName = CacheConstants.HOME_CONSUMER)
	public ConsumerHomeInfo getConsumerHomeInfo(
			@RequestParam(value = QueryConstants.START_TIME, required = false) final String startTime,
			@RequestParam(value = QueryConstants.END_TIME, required = false) final String endTime) {
		String logPrefix = this.getClass().getName()
				+ ".getConsumerHomeInfo(String startTime, String endTime): ";
		log.debug(logPrefix + "enter.");
		log.debug(logPrefix + "startTime: {}, endTime: {}.", startTime, endTime);
		// retrieve consumer home info from backed service
		ConsumerHomeInfo consumerHomeInfo = null;
		try {
			log.debug("query start time: " + Utils.getQueryCondition(startTime, endTime).getTimeWindow().getStartTime());
			log.debug("query end time: " + Utils.getQueryCondition(startTime, endTime).getTimeWindow().getEndTime());
			consumerHomeInfo = hpscQuery.getConsumerHomeInfo(Utils.getQueryCondition(startTime, endTime));
		} catch (Exception e) {
			log.error(logPrefix + " Request processing failed; nested exception is {}", e);
			// do not throw exception, just return null 
		}
		log.debug(logPrefix + "return.");
		return consumerHomeInfo;
	}

	@RequestMapping(value = "/producer", method = RequestMethod.GET)
	@ResponseBody
	@Cacheable(cacheName = CacheConstants.HOME_PRUDUCER)
	public List<ProducerHomeInfo> getProducerHomeInfo(
			@RequestParam(value = QueryConstants.START_TIME, required = false) final String startTime,
			@RequestParam(value = QueryConstants.END_TIME, required = false) final String endTime) {
		String logPrefix = this.getClass().getName()
				+ ".getProducerHomeInfo(String startTime, String endTime): ";
		log.debug(logPrefix + "enter.");
		log.debug(logPrefix + "startTime: {}, endTime: {}.", startTime, endTime);
		// retrieve producer home info from backed service
		List<ProducerHomeInfo> producerHomeInfos = null;
		try {
			producerHomeInfos = hpscQuery.getProducerHomeInfo(Utils.getQueryCondition(startTime, endTime));
		} catch (Exception e) {
			log.error(logPrefix + " Request processing failed; nested exception is {}", e);
			// do not throw exception, just return null 
		}
		log.debug(logPrefix + "return.");
		return producerHomeInfos;
	}
}
