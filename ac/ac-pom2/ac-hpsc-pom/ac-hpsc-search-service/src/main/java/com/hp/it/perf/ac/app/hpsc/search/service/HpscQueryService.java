package com.hp.it.perf.ac.app.hpsc.search.service;

import java.util.List;

import org.springframework.jdbc.core.RowCallbackHandler;

import com.hp.it.perf.ac.app.hpsc.search.bean.ConsumerDetailReport;
import com.hp.it.perf.ac.app.hpsc.search.bean.ConsumerHomeInfo;
import com.hp.it.perf.ac.app.hpsc.search.bean.ConsumerRequestReport;
import com.hp.it.perf.ac.app.hpsc.search.bean.ProducerHomeInfo;
import com.hp.it.perf.ac.app.hpsc.search.bean.ProducerReport;
import com.hp.it.perf.ac.app.hpsc.search.bean.QueryCondition;
import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;
import com.hp.it.perf.ac.core.AcService;

public interface HpscQueryService extends AcService {
	
	// condition: time window.
	public ConsumerHomeInfo getConsumerHomeInfo(QueryCondition queryCondition);
	
	// condition: time window.
	public List<ProducerHomeInfo> getProducerHomeInfo(QueryCondition queryCondition);
	
	// condition: time window.
	public List<ConsumerRequestReport> getConsumerRequestReport(QueryCondition queryCondition);
	
	// condition: time window, request name.
	public List<ConsumerRequestReport> getConsumerRequestDetailReport(QueryCondition queryCondition);
	
	// condition: time window.
	public List<ConsumerDetailReport> getConsumerDetailReport(QueryCondition queryCondition);
	
	// condition: time window.
	public List<ProducerReport> getProducerReport(QueryCondition queryCondition);
	
	// condition: time window, portletName+phaseName(part).
	public List<ProducerReport> getProducerDetailReoprt(QueryCondition queryCondition);
	
	// condition: acid list.
	public List<AcCommonDataWithPayLoad> getLogDetail(QueryCondition queryCondition);
	
	// condition: time window
	// condition: deleteAll is a flag for deleting all data in database
	public void deleteDataInDB(QueryCondition queryCondition, boolean deleteAll);

	// condition: sql, prepared argurment objects, RowCallbackHandler
	public void handledBySql(String sql, Object[] args, RowCallbackHandler rch);
	
	
	
}
