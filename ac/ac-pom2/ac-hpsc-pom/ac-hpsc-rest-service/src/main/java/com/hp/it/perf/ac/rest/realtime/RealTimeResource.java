package com.hp.it.perf.ac.rest.realtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;

import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.specimpl.PathSegmentImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;

import com.google.common.collect.Lists;
import com.hp.it.perf.ac.app.hpsc.realtime.RealtimeService;
import com.hp.it.perf.ac.common.realtime.MessageBean;
import com.hp.it.perf.ac.common.realtime.RealTimeBean;
import com.hp.it.perf.ac.common.realtime.GranularityType;
import com.hp.it.perf.ac.common.realtime.RealTimeIdHelper;
import com.hp.it.perf.ac.common.realtime.ValueType;
import com.hp.it.perf.ac.rest.exceptions.ConflictException;
import com.hp.it.perf.ac.rest.model.RealTimeFeatureError;
import com.hp.it.perf.ac.rest.util.QueryConstants;
import com.hp.it.perf.ac.rest.util.Utils;

@Controller
@Path("/realtime")
public class RealTimeResource {

	private static final Logger log = LoggerFactory
			.getLogger(RealTimeResource.class);

	@Inject
	private RealtimeService realtimeService;

	public void setRealtimeService(RealtimeService realtimeService) {
		this.realtimeService = realtimeService;
	}

	@DELETE
	@Path("/delete/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public String deleteDataById(@PathParam("id") String id) {
		String logPrefix = this.getClass().getName() + ".deleteDataById(...): ";
		log.debug(logPrefix + " enter.");
		log.debug(logPrefix + " id: {}.", id);
		realtimeService.deleteData(id);
		log.debug(logPrefix + "return.");
		return "success!";
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Cache(maxAge=3600)
	public RealTimeBean getDataById(@PathParam("id") String id) {
		String logPrefix = this.getClass().getName() + ".getDataById(...): ";
		log.debug(logPrefix + " enter.");
		log.debug(logPrefix + " id: {}.", id);
		RealTimeBean res = realtimeService.getDataById(id);
		log.debug(logPrefix + "return.");
		return res == null ? new RealTimeBean() : res;
	}

	@GET
	@Path("/{valuetype}/{granularity}/latest")
	@Produces(MediaType.APPLICATION_JSON)
	public RealTimeBean getLatestByValueTypeAndGranularity(
			@PathParam("valuetype") String valueType,
			@PathParam("granularity") String granularity) {
		String logPrefix = this.getClass().getName() + ".getLatestByValueTypeAndGranularity(...): ";
		log.debug(logPrefix + " enter.");
		log.debug(logPrefix + " valueType: {}, granularity: {}.", valueType, granularity);
		ValueType vt = checkValueType(valueType);
		GranularityType gt = checkGranularity(granularity);
		RealTimeBean res = null;
		try {
			// TODO: remove the hard code
			// default category is 1: request
			res = realtimeService.getLatestScore(RealTimeIdHelper.getInstance().getId(vt.getIndex(), gt.getIndex(), 1, 1));
		}catch (Exception e) {
			log.error(logPrefix + " Error occurs when call realtimeService.getLatestScore()", e);
		}
		return res == null ? new RealTimeBean() : res;
	}

	@GET
	@Path("/{valuetype}/{granularity}/topscores")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<RealTimeBean> getTopFeatures(
			@PathParam("valuetype") String valueType,
			@PathParam("granularity") String granularity, 
			@DefaultValue("10") @QueryParam("page.size") int pageSize,
			@DefaultValue("desc") @QueryParam("page.sort.dir") String sortDir) {
		String logPrefix = this.getClass().getName() + ".getTopFeatures(...): ";
		log.debug(logPrefix + " enter.");
		log.debug(logPrefix + " valueType: {}, granularity: {}, page.size: {}, page.sort.dir: {}.", valueType, granularity, pageSize, sortDir);
		ValueType vt = checkValueType(valueType);
		GranularityType gt = checkGranularity(granularity);
		Map<Integer, RealTimeBean> scores = null;
		try {
			scores = realtimeService.getAllScore();
		} catch (Exception e) {
			log.error(logPrefix + " Error occurs when call realtimeService.getAllScore()", e);
		}
		// filter the request category: 1
		Set<Integer> filters = new HashSet<Integer>();
		filters.add(1);
		Collection<RealTimeBean> result = filterByValueTypeAndGranularityAndCategory(scores, 
				vt.getIndex(), gt.getIndex(), filters, pageSize, sortDir);
		return result;
	}
	
	@GET
	@Path("/{granularity}/error/consumer")
	@Produces(MediaType.APPLICATION_JSON)
	public List<RealTimeFeatureError> getConsumerErrors(
			@PathParam("granularity") String granularity,
			@QueryParam("starttime") String startTime,
			@DefaultValue("0") @QueryParam("page") int page,
			@DefaultValue("10") @QueryParam("page.size") int pageSize,
			@DefaultValue("count") @QueryParam("page.sort") String[] sort,
			@DefaultValue("asc") @QueryParam("page.sort.dir") String sortDir) {
		// TODO: remove the hard coded feature
		PathSegment feature = new PathSegmentImpl("feature;category=1;featureType=1", false);
		return  getErrorByGranularityAndCategoryAndFeatureTypeAndStartTimeGreaterThan(
				granularity, feature, startTime, page, pageSize, sort, sortDir);
	}
	
	@GET
	@Path("/{granularity}/error/{feature}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<RealTimeFeatureError> getErrorByGranularityAndCategoryAndFeatureTypeAndStartTimeGreaterThan(
			@PathParam("granularity") String granularity,
			@PathParam("feature") PathSegment feature,
			@QueryParam("starttime") String startTime,
			@DefaultValue("0") @QueryParam("page") int page,
			@DefaultValue("120") @QueryParam("page.size") int pageSize,
			@DefaultValue("count") @QueryParam("page.sort") String[] sort,
			@DefaultValue("asc") @QueryParam("page.sort.dir") String sortDir) {
		String logPrefix = this.getClass().getName() + ".getErrorByGranularityAndCategoryAndFeatureTypeAndStartTimeGreaterThan(...): ";
		log.debug(logPrefix + " enter.");
		log.debug(logPrefix + " granularity: {}, feature: {}, startTime: {}.", granularity, feature, startTime);
		log.debug(logPrefix + " page: {}, page.size: {}, page.sort: {}, page.sort.dir: {}.", page, pageSize, sort, sortDir);
		MultivaluedMap<String, String> featureMap = feature.getMatrixParameters();
		int category = checkFeatureMap(featureMap, QueryConstants.RT_CATEGORY);
		int featureType = checkFeatureMap(featureMap, QueryConstants.RT_FEATURE_TYPE);
		GranularityType gt = checkGranularity(granularity);
		long end = judgeTime(startTime, gt, pageSize, false);
		log.debug(logPrefix + " category: {}, featureType: {}, judged time: {}.", category, featureType, end);
		List<MessageBean> result = null;
		try {
			result = realtimeService.
					getByGranularityAndCategoryAndFeatureTypeAndStartTimeGreaterThan(
							gt.getIndex(), 
							category, 
							featureType, 
							Utils.calculateDuration(end, gt.getIndex(), 2, true), 
							new PageRequest(page, Integer.MAX_VALUE, Sort.Direction
									.fromString(sortDir), sort)).getContent();
		} catch (Exception e) {
			log.error(logPrefix + " Error occurs when call realtimeService.getByGranularityAndCategoryAndFeatureTypeAndStartTimeGreaterThan()", e);
		}
		log.debug(logPrefix + "result size: {}.", result != null ? result.size() : null);
		// group the feature error
		if(result != null && result.size() > 0) {
			Map<String, Integer> errors = new HashMap<String, Integer>();
			for(MessageBean d : result) {
				if(d.getMessage() != null && !d.getMessage().trim().equalsIgnoreCase("")) {
					String[] msgs = d.getMessage().split(":", 2);
					if(msgs.length > 0) {
						String errorType = msgs[0];
						if(!errors.containsKey(errorType)) {
							errors.put(errorType, d.getCount());
						} else {
							errors.put(errorType, errors.get(errorType) + d.getCount());
						}
					}
				}
			}
			List<RealTimeFeatureError> featureErrors = new ArrayList<RealTimeFeatureError>();
			for(Entry<String, Integer> error : errors.entrySet()) {
				featureErrors.add(new RealTimeFeatureError(gt.getIndex(), category, featureType, error.getKey(), error.getValue()));
			}
			return featureErrors;
		}
		return null;
	}
	
	@GET
	@Path("/{granularity}/error/producer")
	@Produces(MediaType.APPLICATION_JSON)
	public List<RealTimeFeatureError> getProducerErrors(
			@PathParam("granularity") String granularity,
			@QueryParam("starttime") String startTime,
			@DefaultValue("120") @QueryParam("page.size") int pageSize) {
		String logPrefix = this.getClass().getName() + ".getProducerErrors(...): ";
		log.debug(logPrefix + " enter.");
		log.debug(logPrefix + " granularity: {}, startTime: {}.", granularity, startTime);
		log.debug(logPrefix + " page.size: {}.", pageSize);
		GranularityType gt = checkGranularity(granularity);
		long end = judgeTime(startTime, gt, pageSize, false);
		log.debug(logPrefix + "judged time: {}.", end);
		List<RealTimeBean> result = null;
		try {
			result = realtimeService
					.getByGranularityAndValueTypeAndCategoryAndStartTimeGreaterThan(
							gt.getIndex(), 
							ValueType.ErrorCount.getIndex(), 
							// TODO: Remove the hard-coded category: 2
							2, 
							Utils.calculateDuration(end, gt.getIndex(), 2, true), 
							new PageRequest(0, Integer.MAX_VALUE)).getContent();
		} catch (Exception e) {
			log.error(logPrefix + " Error occurs when call realtimeService.getDataByGranularityAndValueTypeAndStartTimeGreaterThan()", e);
		}
		log.debug(logPrefix + "result size: {}.", result != null ? result.size() : null);
		// group the producer errors.
		if(result != null && result.size() > 0) {
			Map<Integer, Integer> errors = new HashMap<Integer, Integer>();
			for(RealTimeBean d : result) {
				int id = RealTimeIdHelper.getInstance().getId(ValueType.ErrorCount.getIndex(), gt.getIndex(), d.getCategory(), d.getFeatureType());
				if(!errors.containsKey(id)) {
					errors.put(id, (int)d.getValue());
				} else {
					errors.put(id, errors.get(id) + (int)d.getValue());
				}
			}
			List<RealTimeFeatureError> producerErrors = new ArrayList<RealTimeFeatureError>();
			for(Entry<Integer, Integer> error : errors.entrySet()) {
				if(error.getValue() > 0) {
					producerErrors.add(new RealTimeFeatureError(
							gt.getIndex(),
							RealTimeIdHelper.getInstance().getCategory(error.getKey()), 
							RealTimeIdHelper.getInstance().getFeatureType(error.getKey()), 
							"",
							error.getValue()));
				}
			}
			return producerErrors;
		}
		return null;
	}
	
	@GET
	@Path("/{valuetype}/{granularity}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<RealTimeBean> getDataByValueTypeAndGranularity(
			@PathParam("valuetype") String valueType,
			@PathParam("granularity") String granularity,
			@QueryParam("starttime") String startTime,
			@DefaultValue("0") @QueryParam("page") int page,
			@DefaultValue("120") @QueryParam("page.size") int pageSize,
			@DefaultValue("_id") @QueryParam("page.sort") String[] sort,
			@DefaultValue("desc") @QueryParam("page.sort.dir") String sortDir) {
		String logPrefix = this.getClass().getName() + ".getDataByValueTypeAndGranularity(...): ";
		log.debug(logPrefix + " enter.");
		log.debug(logPrefix + " valueType: {}, granularity: {}, startTime: {}.", valueType, granularity, startTime);
		log.debug(logPrefix + " page: {}, page.size: {}, page.sort: {}, page.sort.dir: {}.", page, pageSize, sort, sortDir);
		ValueType vt = checkValueType(valueType);
		GranularityType gt = checkGranularity(granularity);
		long end = judgeTime(startTime, gt, pageSize, false);
		List<RealTimeBean> result = null;
		try {
			result = realtimeService
					.getDataByGranularityAndValueTypeAndStartTimeGreaterThan(
							gt.getIndex(),
							vt.getIndex(),
							Utils.calculateDuration(end, gt.getIndex(), 2, true),
							new PageRequest(page, pageSize, Sort.Direction
									.fromString(sortDir), sort)).getContent();
		} catch (Exception e) {
			log.error(logPrefix + " Error occurs when call realtimeService.getDataByGranularityAndValueTypeAndStartTimeGreaterThan()", e);
		}
		log.debug(logPrefix + "result size: {}.", result != null ? result.size() : null);
		if (result != null && result.size() < pageSize) {
			//return handleReturnResult(result, pageSize, gt.getIndex(), end);
		}
		log.debug(logPrefix + "return.");
		return Lists.reverse(result);
	}

	@GET
	@Path("/{valuetype}/{granularity}/{feature}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<RealTimeBean> getDataByValueTypeAndGranularityAndFeature(
			@PathParam("valuetype") String valueType,
			@PathParam("granularity") String granularity,
			@PathParam("feature") PathSegment feature,
			@QueryParam("starttime") String startTime,
			@DefaultValue("0") @QueryParam("page") int page,
			@DefaultValue("120") @QueryParam("page.size") int pageSize,
			@DefaultValue("startTime") @QueryParam("page.sort") String[] sort,
			@DefaultValue("desc") @QueryParam("page.sort.dir") String sortDir,
			@DefaultValue("true") @QueryParam("fillup") String fillup) {
		String logPrefix = this.getClass().getName() + ".getDataByValueTypeAndGranularityAndFeature(...): ";
		log.debug(logPrefix + " enter.");
		log.debug(logPrefix + " valueType: {}, granularity: {}, feature: {}, startTime: {}.", valueType, granularity, feature, startTime);
		log.debug(logPrefix + " page: {}, page.size: {}, page.sort: {}, page.sort.dir: {}.", page, pageSize, sort, sortDir);
		MultivaluedMap<String, String> featureMap = feature.getMatrixParameters();
		int category = checkFeatureMap(featureMap, QueryConstants.RT_CATEGORY);
		int featureType = checkFeatureMap(featureMap, QueryConstants.RT_FEATURE_TYPE);
		ValueType vt = checkValueType(valueType);
		GranularityType gt = checkGranularity(granularity);
		long end = judgeTime(startTime, gt, pageSize, false);
		log.debug(logPrefix + " category: {}, featureType: {}, judged time: {}.", category, featureType, end);
		List<RealTimeBean> result = null;
		try {
			result = realtimeService
					.getByGranularityAndValueTypeAndCategoryAndFeatureTypeAndStartTimeGreaterThan(
							gt.getIndex(),
							vt.getIndex(),
							category,
							featureType,
							// fix the query issue, we now use greater than a time to query the result, 
							// but actually the result which start time is equal to the query time is also we need.
							// so just decrease the time by one granularity 
							Utils.calculateDuration(end, gt.getIndex(), 2, true),
							new PageRequest(page, pageSize, Sort.Direction
									.fromString(sortDir), sort)).getContent();
		} catch (Exception e) {
			log.error(logPrefix + 
					" Error occurs when call realtimeService.findByGranularityAndValueTypeAndCategoryAndFeatureTypeAndStartTimeGreaterThan()", e);
		}
		log.debug(logPrefix + "result size: {}.", result != null ? result.size() : null);
		if (result != null && result.size() < pageSize && fillup != null && fillup.trim().equalsIgnoreCase("true")) {
			return handleReturnResult(result, pageSize, gt.getIndex(), category, featureType, end);
		}
		log.debug(logPrefix + "return.");
		return Lists.reverse(result);
	}

	@GET
	@Path("/{valuetype}/{granularity}/{feature}/latest")
	@Produces(MediaType.APPLICATION_JSON)
	public RealTimeBean getLatestByValueTypeAndGranularityAndFeature(
			@PathParam("valuetype") String valueType,
			@PathParam("granularity") String granularity,
			@PathParam("feature") PathSegment feature) {
		String logPrefix = this.getClass().getName() + ".getLatestByValueTypeAndGranularityAndFeature(...): ";
		log.debug(logPrefix + " enter.");
		log.debug(logPrefix + " valueType: {}, granularity: {}, feature: {}.", valueType, granularity, feature);
		MultivaluedMap<String, String> featureMap = feature.getMatrixParameters();
		int category = checkFeatureMap(featureMap, QueryConstants.RT_CATEGORY);
		// for feature the category is 2
		// TODO: remove the hard code
		if (category == 0) {
			category = 2;
		}
		int featureType = checkFeatureMap(featureMap, QueryConstants.RT_FEATURE_TYPE);
		ValueType vt = checkValueType(valueType);
		GranularityType gt = checkGranularity(granularity);
		RealTimeBean res = null;
		try {
			res = realtimeService.getLatestScore(RealTimeIdHelper
					.getInstance().getId(vt.getIndex(), gt.getIndex(), category,
							featureType));
		} catch (Exception e) {
			log.error(logPrefix + " Error occurs when call realtimeService.getLatestScore()", e);
		}
		log.debug(logPrefix + "return.");
		return res == null ? new RealTimeBean() : res;
	}

	@GET
	@Path("/granularities")
	@Produces(MediaType.APPLICATION_JSON)
	@Cache(maxAge=3600)
	public List<GranularityType> getGranularityTypes() {
		return GranularityType.getGranularityTypeList();
	}

	@GET
	@Path("/valuetypes")
	@Produces(MediaType.APPLICATION_JSON)
	@Cache(maxAge=3600)
	public ValueType[] getValueTypes() {
		return ValueType.values();
	}

	@GET
	@Path("/{granularity}/duration")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> getGranularityDuration(
			@PathParam("granularity") String granularity,
			@DefaultValue("50") @QueryParam("page.size") int pageSize) {
		String logPrefix = this.getClass().getName() + ".getGranularityDuration(...): ";
		log.debug(logPrefix + " enter.");
		log.debug(logPrefix + " granularity: {}, page.size: {}.", granularity, pageSize);
		GranularityType gt = checkGranularity(granularity);
		Map<String, String> duration = new HashMap<String, String>();
		duration.put("startTime", Utils.long2String(judgeTime(null, gt, pageSize, false)));
		duration.put("endTime", Utils.long2String(judgeTime(null, gt, pageSize, true)));
		log.debug(logPrefix + "return.");
		return duration;
	}

	private long judgeTime(String startTime, GranularityType gt, int pageSize,
			boolean start) {
		// judge the latest time, the order is
		// 1. get from the query parameter
		// 2. get from latest start time in memory
		// 3. get the current system time
		Long now = Utils.getCurrentDate(gt);
		if (startTime == null) {
			try {
				now = realtimeService.getGruanularityLatestStartTime(gt.getIndex());
				log.debug("Retrieved lastest update time: {}.", now);
			} catch (Exception e) {
				log.error("Error occurs when getGruanularityLatestStartTime.", e);
			}
			if (now == null) {
				now = Utils.getCurrentDate(gt);
			}
		} else {
			now = Utils.getCurrentDate(gt, startTime);
		}
		if (start) {
			return now;
		}
		return Utils.calculateDuration(now, gt.getIndex(), pageSize, true);
	}

	private List<RealTimeBean> handleReturnResult(List<RealTimeBean> result,
			int pageSize, int granularity, int category, int featureType, long now) {
		log.debug("handleReturnResult: result: {}, pageSize: {}, granularity: {}.", result.size(), pageSize, granularity);
		Map<Long, RealTimeBean> results = new HashMap<Long, RealTimeBean>();
		for(RealTimeBean d : result) {
			results.put(d.getStartTime(), d);
		}
		RealTimeBean[] template = new RealTimeBean[pageSize];
		for(int i = 0; i < pageSize; i++) {
			long time = Utils.calculateDuration(now, granularity, i + 1, false);
			if(!results.containsKey(time)) {
				RealTimeBean tmp = new RealTimeBean();
				tmp.setStartTime(time);
				tmp.setCategory(category);
				tmp.setFeatureType(featureType);
				template[i] = tmp;
			} else {
				template[i] = results.get(time);
			}
		}
		return Arrays.asList(template);
	}

	private ValueType checkValueType(String valueType) {
		try {
			ValueType vt = ValueType.valueOf(valueType);
			return vt;
		} catch (Exception e) {
			log.error("Invalid valueType!", e);
			throw new ConflictException("Invalid valueType!");
		}
	}

	private GranularityType checkGranularity(String granularity) {
		try {
			GranularityType gt = GranularityType.valueOf(granularity);
			return gt;
		} catch (Exception e) {
			log.error("Invalid granularity!", e);
			throw new ConflictException("Invalid granularity!");
		}
	}

	private int checkFeatureMap(MultivaluedMap<String, String> featureMap,
			String key) {
		if (featureMap.containsKey(key)) {
			try {
				return Integer.valueOf(featureMap.get(key).get(0));
			} catch (NumberFormatException e) {
				log.error("Invalid category or feature type!", e);
				throw new ConflictException("Invalid category or feature type!");
			}
		}
		return 0;
	}
	
	private Collection<RealTimeBean> filterByValueTypeAndGranularityAndCategory(Map<Integer, RealTimeBean> scores,
			int vt, int gt, Collection<Integer> filters, int pageSize, String sortDir) {
		if(scores == null || scores.isEmpty()) 
			return Collections.emptySet();
		log.debug("filterByValueTypeAndGranularityAndCategory: scores size: {}.", scores.size());
		@SuppressWarnings({ "serial"})
		class RealTimeBeanWrapper extends RealTimeBean {
			
			private double count;
			
			private RealTimeBeanWrapper(RealTimeBean clone) {
				this.setCategory(clone.getCategory());
				this.setFeatureType(clone.getFeatureType());
				this.setGranularity(clone.getGranularity());
				this.setId(clone.getId());
				this.setStartTime(clone.getStartTime());
				this.setValue(clone.getValue());
				this.setValueType(clone.getValueType());
			}
			
			public void setCount(double count) {
				this.count = count;
			}
			
			public double getCount() {
				return count;
			}
		}
		List<RealTimeBeanWrapper> escaped = new ArrayList<RealTimeBeanWrapper>();
		for(Integer id : scores.keySet()) {
			// filter valueType, granularity, category
			if(RealTimeIdHelper.getInstance().getValueType(id) == vt 
					&& RealTimeIdHelper.getInstance().getGranulityType(id) == gt
					&& !filters.contains(RealTimeIdHelper.getInstance().getCategory(id))) {
				RealTimeBeanWrapper wrapper = new RealTimeBeanWrapper(scores.get(id));
				int category = RealTimeIdHelper.getInstance().getCategory(id);
				int featureType= RealTimeIdHelper.getInstance().getFeatureType(id);
				// get the access count for the same granulate, category and featureType
				int accessId = RealTimeIdHelper.getInstance().getId(ValueType.TotalCount.getIndex(), gt, category, featureType);
				log.debug("filterByValueTypeAndGranularityAndCategory: accessId: {}.", accessId);
				wrapper.setCount(scores.get(accessId) == null ? 0 : scores.get(accessId).getValue());
				log.debug("Category: {}, Feature: {}, access count: {}.", wrapper.getCategory(), wrapper.getFeatureType(), wrapper.getCount());
				escaped.add(wrapper);
			}
		}
		log.debug("filterByValueTypeAndGranularityAndCategory: escaped size: {}.", escaped.size());
		// sort by feature access count
		Collections.sort(escaped, new Comparator<RealTimeBeanWrapper>() {

			@Override
			public int compare(RealTimeBeanWrapper r1, RealTimeBeanWrapper r2) {
				if(r1.getCount() > r2.getCount()) 
					return 1;
				else if (r1.getCount() < r2.getCount())
					return -1;
				else 
					return 0;
			}
		});
		if(sortDir.equalsIgnoreCase(Sort.Direction.DESC.toString())) {
			Collections.reverse(escaped);
			log.debug("filterByValueTypeAndGranularityAndCategory: result reversed!");
		}
		List<RealTimeBean> result = new ArrayList<RealTimeBean>(escaped.size());
		for(int i = 0; i < escaped.size(); i++) {
			RealTimeBean r = new RealTimeBean();
			result.add(r);
		}
		Collections.copy(result, escaped);
		return result.size() > pageSize ? result.subList(0, pageSize > 0 ? pageSize : 0) : result;
	}
}
