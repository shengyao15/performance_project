package com.hp.it.perf.ac.rest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
import com.hp.it.perf.ac.app.hpsc.search.bean.ConsumerDetailReport;
import com.hp.it.perf.ac.app.hpsc.search.bean.ConsumerRequestReport;
import com.hp.it.perf.ac.app.hpsc.search.bean.ProducerReport;
import com.hp.it.perf.ac.app.hpsc.search.bean.QueryCondition;
import com.hp.it.perf.ac.app.hpsc.search.service.HpscQueryService;
import com.hp.it.perf.ac.rest.exceptions.ConflictException;
import com.hp.it.perf.ac.rest.util.CacheConstants;
import com.hp.it.perf.ac.rest.util.Constant;
import com.hp.it.perf.ac.rest.util.QueryConstants;
import com.hp.it.perf.ac.rest.util.Utils;

@Controller
@RequestMapping({ "/report" })
public class ReportServiceController {

	private static final Logger log = LoggerFactory
			.getLogger(ReportServiceController.class);

	@Autowired
	@Qualifier("hpscQueryService")
	private HpscQueryService hpscQuery;

	@RequestMapping(value = "/consumer", method = RequestMethod.GET)
	@ResponseBody
	@Cacheable(cacheName = CacheConstants.REPORT)
	public Map<String, List<ConsumerRequestReport>> getConsumerReport(
			@RequestParam(value = QueryConstants.START_TIME, required = false) final String startTime,
			@RequestParam(value = QueryConstants.END_TIME, required = false) final String endTime,
			@RequestParam(value = QueryConstants.ESTIMATEnINETY, required = false) final String estimateNinety) {
		String logPrefix = this.getClass().getName()
				+ ".getConsumerReport(...): ";
		log.debug(logPrefix + "enter.");
		log.debug(logPrefix + "startTime: {}, endTime: {}.", startTime, endTime);

		// construct query condition.
		QueryCondition queryCondition = Utils.getQueryCondition(startTime,
				endTime);
		// if estimateNinety parameter exists and equal false
		if (!StringUtils.isBlank(estimateNinety)
				&& estimateNinety.equalsIgnoreCase("false")) {
			queryCondition.setEstimateNinety(false);
		}
		List<ConsumerRequestReport> consumerReports = null;
		try {
			consumerReports = hpscQuery
					.getConsumerRequestReport(queryCondition);
		} catch (Exception e) {
			log.error(logPrefix
					+ " Request processing failed; nested exception is {}", e);
			// do not throw exception, just return null
		}
		// check not empty
		Map<String, List<ConsumerRequestReport>> result = new HashMap<String, List<ConsumerRequestReport>>();
		// result.put(Constant.KEY_AA_DATE,
		// RestPreconditions.checkNotEmpty(consumerReports));
		result.put(Constant.KEY_AA_DATE, consumerReports);
		log.debug(logPrefix + "return.");
		return result;
	}

	@RequestMapping(value = "/consumer/detail", method = RequestMethod.GET)
	@ResponseBody
	@Cacheable(cacheName = CacheConstants.REPORT)
	public Map<String, List<ConsumerDetailReport>> getConsumerDetailReport(
			@RequestParam(value = QueryConstants.START_TIME, required = false) final String startTime,
			@RequestParam(value = QueryConstants.END_TIME, required = false) final String endTime,
			@RequestParam(value = QueryConstants.ESTIMATEnINETY, required = false) final String estimateNinety) {
		String logPrefix = this.getClass().getName()
				+ ".getConsumerDetailReport(...): ";
		log.debug(logPrefix + "enter.");
		log.debug(logPrefix + "startTime: {}, endTime: {}.", startTime, endTime);

		// construct query condition.
		QueryCondition queryCondition = Utils.getQueryCondition(startTime,
				endTime);
		// if estimateNinety parameter exists and equal false
		if (!StringUtils.isBlank(estimateNinety)
				&& estimateNinety.equalsIgnoreCase("false")) {
			queryCondition.setEstimateNinety(false);
		}
		List<ConsumerDetailReport> consumerDetailReports = null;
		try {
			consumerDetailReports = hpscQuery
					.getConsumerDetailReport(queryCondition);
		} catch (Exception e) {
			log.error(logPrefix
					+ " Request processing failed; nested exception is {}", e);
			// do not throw exception, just return null
		}
		// check not empty
		Map<String, List<ConsumerDetailReport>> result = new HashMap<String, List<ConsumerDetailReport>>();
		// result.put(Constant.KEY_AA_DATE,
		// RestPreconditions.checkNotEmpty(consumerDetailReports));
		result.put(Constant.KEY_AA_DATE, consumerDetailReports);
		log.debug(logPrefix + "return.");
		return result;
	}

	@RequestMapping(value = "/producer", method = RequestMethod.GET)
	@ResponseBody
	@Cacheable(cacheName = CacheConstants.REPORT)
	public Map<String, List<ProducerReport>> getProducerReport(
			@RequestParam(value = QueryConstants.START_TIME, required = false) final String startTime,
			@RequestParam(value = QueryConstants.END_TIME, required = false) final String endTime,
			@RequestParam(value = QueryConstants.ESTIMATEnINETY, required = false) final String estimateNinety) {
		String logPrefix = this.getClass().getName()
				+ ".getProducerReport(...): ";
		log.debug(logPrefix + "enter.");
		log.debug(logPrefix + "startTime: {}, endTime: {}.", startTime, endTime);

		// construct query condition.
		QueryCondition queryCondition = Utils.getQueryCondition(startTime,
				endTime);
		// if estimateNinety parameter exists and equal false
		if (!StringUtils.isBlank(estimateNinety)
				&& estimateNinety.equalsIgnoreCase("false")) {
			queryCondition.setEstimateNinety(false);
		}
		List<ProducerReport> producerReports = null;
		try {
			producerReports = hpscQuery.getProducerReport(queryCondition);
		} catch (Exception e) {
			log.error(logPrefix
					+ " Request processing failed; nested exception is {}", e);
			// do not throw exception, just return null
		}
		// check not empty
		Map<String, List<ProducerReport>> result = new HashMap<String, List<ProducerReport>>();
		result.put(Constant.KEY_AA_DATE, producerReports);
		log.debug(logPrefix + "return.");
		return result;
	}

	@RequestMapping(value = "/producer/detail", method = RequestMethod.GET)
	@ResponseBody
	@Cacheable(cacheName = CacheConstants.REPORT)
	public Map<String, List<ProducerReport>> getProducerDetailReoprt(
			@RequestParam(value = QueryConstants.START_TIME, required = false) final String startTime,
			@RequestParam(value = QueryConstants.END_TIME, required = false) final String endTime,
			@RequestParam(value = QueryConstants.WSRP_PART, required = false) final String part,
			@RequestParam(value = QueryConstants.ESTIMATEnINETY, required = false) final String estimateNinety) {
		String logPrefix = this.getClass().getName()
				+ ".getProducerDetailReoprt(...): ";
		log.debug(logPrefix + "enter.");
		log.debug(logPrefix + "startTime: {}, endTime: {}, part: {}.",
				new Object[] { startTime, endTime, part });

		if (StringUtils.isBlank(part)) {
			log.error(logPrefix
					+ "required parameter part should not be blank!");
			throw new ConflictException(
					"required parameter part should not be blank!");
		}
		QueryCondition queryCondition = Utils.getQueryCondition(startTime,
				endTime);
		queryCondition.setPart(part);
		// if estimateNinety parameter exists and equal false
		if (!StringUtils.isBlank(estimateNinety)
				&& estimateNinety.equalsIgnoreCase("false")) {
			queryCondition.setEstimateNinety(false);
		}
		List<ProducerReport> producerDetailReports = null;
		try {
			log.debug("part: " + queryCondition.getPart());
			producerDetailReports = hpscQuery
					.getProducerDetailReoprt(queryCondition);
		} catch (Exception e) {
			log.error(logPrefix
					+ " Request processing failed; nested exception is {}", e);
			// do not throw exception, just return null
		}
		// check not empty
		Map<String, List<ProducerReport>> result = new HashMap<String, List<ProducerReport>>();
		result.put(Constant.KEY_AA_DATE, producerDetailReports);
		log.debug(logPrefix + "return.");
		return result;
	}
}
