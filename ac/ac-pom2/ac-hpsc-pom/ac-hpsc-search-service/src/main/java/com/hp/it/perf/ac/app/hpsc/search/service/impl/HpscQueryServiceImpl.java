package com.hp.it.perf.ac.app.hpsc.search.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import com.hp.it.perf.ac.app.hpsc.search.bean.ConsumerDetailReport;
import com.hp.it.perf.ac.app.hpsc.search.bean.ConsumerHomeInfo;
import com.hp.it.perf.ac.app.hpsc.search.bean.ConsumerRequestReport;
import com.hp.it.perf.ac.app.hpsc.search.bean.ProducerHomeInfo;
import com.hp.it.perf.ac.app.hpsc.search.bean.ProducerReport;
import com.hp.it.perf.ac.app.hpsc.search.bean.QueryCondition;
import com.hp.it.perf.ac.app.hpsc.search.dao.SearchDao;
import com.hp.it.perf.ac.app.hpsc.search.dao.impl.SpringJDBCSearchDaoImpl.NameAnd90Count;
import com.hp.it.perf.ac.app.hpsc.search.service.HpscQueryService;
import com.hp.it.perf.ac.common.core.AcSession;
import com.hp.it.perf.ac.common.model.AcCategory;
import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;
import com.hp.it.perf.ac.common.model.AcDictionary;
import com.hp.it.perf.ac.common.model.AcType;
import com.hp.it.perf.ac.common.model.AcidSelectors;
import com.hp.it.perf.ac.common.model.AcidSelectors.AcidRange;
import com.hp.it.perf.ac.service.data.AcRepositoryService;

@Service
public class HpscQueryServiceImpl implements HpscQueryService {

	@Inject
	private AcDictionary dictionary;

	@Inject
	private AcSession session;

	@Inject
	private AcRepositoryService repositoryService;

	@Inject
	private SearchDao dao;

	private static Logger logger = LoggerFactory
			.getLogger(HpscQueryServiceImpl.class);

	@Override
	public ConsumerHomeInfo getConsumerHomeInfo(QueryCondition queryCondition) {
		Map<String, String> timeWindow = getTimeWindowMap(queryCondition);
		return dao.getConsumerHomeInfo(timeWindow);
	}

	@Override
	public List<ProducerHomeInfo> getProducerHomeInfo(
			QueryCondition queryCondition) {
		Map<String, String> timeWindow = getTimeWindowMap(queryCondition);
		return dao.getProducerHomeInfo(timeWindow,
				getFeatureRange("PortalBizLog"));
	}

	@Override
	public List<ConsumerRequestReport> getConsumerRequestReport(
			QueryCondition queryCondition) {
		Map<String, String> timeWindow = getTimeWindowMap(queryCondition);
		return dao.getConsumerRequestReport(timeWindow, queryCondition.isEstimateNinety());
	}
	
	@Override
	public List<ConsumerRequestReport> getConsumerRequestDetailReport(
			QueryCondition queryCondition) {
		Map<String, String> timeWindow = getTimeWindowMap(queryCondition);
		String request = queryCondition.getRequest();
		return dao.getConsumerRequestDetailReport(timeWindow, request, queryCondition.isEstimateNinety());
	}
	
	@Override
	public List<ConsumerDetailReport> getConsumerDetailReport(
			QueryCondition queryCondition) {
		Map<String, String> timeWindow = getTimeWindowMap(queryCondition);
		return dao.getConsumerDetailReport(timeWindow, queryCondition.isEstimateNinety());
	}

	@Override
	public List<ProducerReport> getProducerReport(QueryCondition queryCondition) {
		Map<String, String> timeWindow = getTimeWindowMap(queryCondition);
		return dao.getProducerReport(timeWindow, queryCondition.isEstimateNinety());
	}

	@Override
	public List<ProducerReport> getProducerDetailReoprt(
			QueryCondition queryCondition) {
		Map<String, String> timeWindow = getTimeWindowMap(queryCondition);
		return dao.getProducerDetailReoprt(timeWindow, queryCondition.getPart(), queryCondition.isEstimateNinety());
	}

	@Override
	public List<AcCommonDataWithPayLoad> getLogDetail(
			QueryCondition queryCondition) {
		List<AcCommonDataWithPayLoad> list = new ArrayList<AcCommonDataWithPayLoad>();
		List<Long> acids = queryCondition.getAcids();
		if (!acids.isEmpty() && acids != null) {
			for (Long acid : acids) {
					AcCommonDataWithPayLoad acCommonDataWithPayLoad = repositoryService
							.getCommonDataWithPayLoad(acid);
					list.add(acCommonDataWithPayLoad);
			}
		}
		return list;
	}
	
	@Override
	public void deleteDataInDB(QueryCondition queryCondition, boolean deleteAll){
		dao.deleteDataInDB(queryCondition.getTimeWindow(), deleteAll);
	}

	private Map<String, long[]> getFeatureRange(String categoryName) {
		Map<String, long[]> featureRanges = new HashMap<String, long[]>();
		AcCategory category = dictionary.category(categoryName);
		AcType[] acTypes = category.types();
		for (int i = 0; i < acTypes.length; i++) {
			String feature = acTypes[i].name();
			AcidRange acidRange = AcidSelectors.singleSelector()
					.profile(this.session.getProfile().getProfileId())
					.category(category.code()).type(acTypes[i].code()).build();
			if (acidRange != null) {
				featureRanges.put(feature, new long[] { acidRange.getFrom(),
						acidRange.getTo() });
			}
		}
		return featureRanges;
	}
	
	@Override
	public void handledBySql(String sql, Object[] args, RowCallbackHandler rch){
		dao.handledBySql(sql, args, rch);
	}

	private Map<String, String> getTimeWindowMap(QueryCondition queryCondition) {
		Map<String, String> timeWindow = new HashMap<String, String>();
		SimpleDateFormat dateformat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		timeWindow.put("startTime", dateformat.format(queryCondition
				.getTimeWindow().getStartTime()));
		timeWindow.put("endTime",
				dateformat.format(queryCondition.getTimeWindow().getEndTime()));
		return timeWindow;
	}

	@Deprecated
	private List<ConsumerDetailReport> getProducerReportByDB(
			QueryCondition queryCondition) {
		Map<String, String> timeWindow = getTimeWindowMap(queryCondition);
		List<NameAnd90Count> namesAnd90Counts = dao
				.getNameAnd90Count4ProducerReport(timeWindow);
		List<ConsumerDetailReport> list = new ArrayList<ConsumerDetailReport>();
		for (NameAnd90Count namesAnd90Count : namesAnd90Counts) {
			list.add(dao.getProducerReport(namesAnd90Count, timeWindow));
		}
		return list;
	}

	@Deprecated
	private List<ConsumerRequestReport> getConsumerReportByDB(
			QueryCondition queryCondition) {
		Map<String, String> timeWindow = getTimeWindowMap(queryCondition);
		List<NameAnd90Count> namesAnd90Counts = dao
				.getNameAnd90Count4ConsumerReport(timeWindow);
		List<ConsumerRequestReport> list = new ArrayList<ConsumerRequestReport>();
		for (NameAnd90Count namesAnd90Count : namesAnd90Counts) {
			list.add(dao.getConsumerReport(namesAnd90Count, timeWindow));
		}
		return list;
	}

	@Deprecated
	private List<ProducerReport> getWsrpReportbyDB(QueryCondition queryCondition) {
		Map<String, String> timeWindow = getTimeWindowMap(queryCondition);
		List<NameAnd90Count> namesAnd90Counts = dao
				.getNameAnd90Count4WsrpReport(timeWindow);
		List<ProducerReport> list = new ArrayList<ProducerReport>();
		for (NameAnd90Count namesAnd90Count : namesAnd90Counts) {
			list.add(dao.getWsrpReport(namesAnd90Count, timeWindow));
		}
		return list;
	}

	@Deprecated
	private List<ProducerReport> getWsrpDetailReoprtByDB(
			QueryCondition queryCondition) {
		Map<String, String> timeWindow = getTimeWindowMap(queryCondition);
		List<ProducerReport> list = new ArrayList<ProducerReport>();
		try {
			List<NameAnd90Count> namesAnd90Counts = dao
					.getNameAnd90Count4WsrpDetailReport(timeWindow,
							queryCondition.getPart());
			for (NameAnd90Count namesAnd90Count : namesAnd90Counts) {
				list.add(dao.getWsrpDetailReport(namesAnd90Count, timeWindow));
			}
		} catch (NullPointerException ex) {
			logger.error("Can not find the part information", ex);
		} catch (EmptyResultDataAccessException ex) {
			logger.error("Can not find the part information", ex);
		}
		return list;
	}

}
