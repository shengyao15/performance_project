package com.hp.it.perf.ac.rest.controller;

import java.util.ArrayList;
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
import com.hp.it.perf.ac.app.hpsc.search.bean.QueryCondition;
import com.hp.it.perf.ac.app.hpsc.search.service.HpscQueryService;
import com.hp.it.perf.ac.common.core.AcSession;
import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.common.model.AcidSelectors;
import com.hp.it.perf.ac.common.model.AcidSelectors.AcidRange;
import com.hp.it.perf.ac.common.realtime.TimeWindow;
import com.hp.it.perf.ac.rest.exceptions.ConflictException;
import com.hp.it.perf.ac.rest.model.LoglistWrapper;
import com.hp.it.perf.ac.rest.util.CacheConstants;
import com.hp.it.perf.ac.rest.util.Constant;
import com.hp.it.perf.ac.rest.util.QueryConstants;
import com.hp.it.perf.ac.rest.util.Utils;
import com.hp.it.perf.ac.service.persist.AcPersistCondition;
import com.hp.it.perf.ac.service.persist.AcPersistService;

@Controller
@RequestMapping({ "/loglist" })
public class LogListServiceController {

	private static final Logger log = LoggerFactory
			.getLogger(LogListServiceController.class);

	@Autowired
	@Qualifier("hpscQueryService")
	private HpscQueryService hpscQuery;

	@Autowired
	@Qualifier("persistService")
	private AcPersistService persistService;

	@Autowired
	@Qualifier("acSession")
	private AcSession acSession;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@Cacheable(cacheName = CacheConstants.LOGLIST)
	public Map<String, List<LoglistWrapper>> getCommonDetail(
			@RequestParam(value = QueryConstants.START_TIME, required = false) final String startTime,
			@RequestParam(value = QueryConstants.END_TIME, required = false) final String endTime,
			@RequestParam(value = QueryConstants.CATEGORY, required = false) final String category,
			@RequestParam(value = QueryConstants.TYPE, required = false) final String type,
			@RequestParam(value = QueryConstants.NAME, required = false) final String name,
			@RequestParam(value = QueryConstants.LIMIT, required = false) final String limit) {
		String logPrefix = this.getClass().getName() + ".getCommonDetail(): ";
		log.debug(logPrefix + "enter.");
		log.debug(
				logPrefix
						+ "startTime: {}, endTime: {}, category: {}, type: {}, limit: {}.",
				new Object[] { startTime, endTime, category, type, limit });
		// construct query condition.
		QueryCondition queryCondition = Utils.getQueryCondition(startTime,
				endTime);
		if (!StringUtils.isBlank(category)
				&& !category.trim().equals(Constant.FLAG_STRING)) {
			try {
				queryCondition.setCategory(Integer.valueOf(category));
			} catch (NumberFormatException e) {
				log.error(logPrefix + "invalid category!", e);
				throw new ConflictException("category is not valid data.", e);
			}
		}
		if (!StringUtils.isBlank(type)
				&& !type.trim().equals(Constant.FLAG_STRING)) {
			try {
				queryCondition.setType(Integer.valueOf(type));
			} catch (NumberFormatException e) {
				log.error(logPrefix + "invalid type!", e);
				throw new ConflictException("type is not valid data.", e);
			}
		}
		if (!StringUtils.isBlank(limit)) {
			try {
				queryCondition.setLimitCount(Integer.valueOf(limit));
			} catch (NumberFormatException e) {
				log.error(logPrefix + "invalid limit count!", e);
				throw new ConflictException("limit count is not valid data.", e);
			}
		}
		// enable name
		if (!StringUtils.isBlank(name)) {
			queryCondition.setName(name);
		}
		// change the default value of category to 1
		if(queryCondition.getCategory() == -1) {
			queryCondition.setCategory(1);
		}
		
		AcPersistCondition condition = toCondition(queryCondition);

		// retrieve common data from backed service
		AcCommonData[] commonDetails = null;
		try {
			log.debug("queryCondition: category: "
					+ queryCondition.getCategory() + ", type: "
					+ queryCondition.getType());
			commonDetails = persistService.readCommonData(condition);
		} catch (Exception e) {
			log.error(logPrefix
					+ " Request processing failed; nested exception is {}", e);
			// do not throw exception, just return null
		}
		// check not empty
		// RestPreconditions.checkNotEmpty(commonDetails);
		List<LoglistWrapper> wrappers = new ArrayList<LoglistWrapper>();
		for (AcCommonData data : commonDetails) {
			wrappers.add(new LoglistWrapper(data));
			data = null;
		}
		commonDetails = null;
		Map<String, List<LoglistWrapper>> result = new HashMap<String, List<LoglistWrapper>>();
		result.put(Constant.KEY_AA_DATE, wrappers);
		log.debug(logPrefix + "return.");
		return result;
	}

	private AcPersistCondition toCondition(QueryCondition queryCondition) {
		AcPersistCondition condition = new AcPersistCondition();

		TimeWindow timeWindow = new TimeWindow();
		timeWindow.setStartTime(queryCondition.getTimeWindow().getStartTime());
		timeWindow.setEndTime(queryCondition.getTimeWindow().getEndTime());
		condition.setTimeWindow(timeWindow);

		AcidRange acidRange = null;
		acidRange = AcidSelectors.singleSelector()
				.profile(acSession.getProfile().getProfileId())
				.category(queryCondition.getCategory())
				.type(queryCondition.getType())
				.level(queryCondition.getLevel()).build();
		condition.setAcidRange(acidRange);

		condition.setName(queryCondition.getName());
		if (queryCondition.getOrderBy() != null
				&& queryCondition.getOrderBy().trim().length() > 0) {
			condition.setOrderBy(queryCondition.getOrderBy());
			condition.setOrderByDirection(false);
		} else if (queryCondition.isQueryByDurationDesc()) {
			condition.setOrderBy("duration");
			condition.setOrderByDirection(false);
		}
		condition.setLimitNumber(queryCondition.getLimitCount());

		return condition;
	}
}
