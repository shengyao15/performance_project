package com.hp.it.perf.ac.app.hpsc.search.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowCallbackHandler;

import com.hp.it.perf.ac.app.hpsc.search.bean.ConsumerDetailReport;
import com.hp.it.perf.ac.app.hpsc.search.bean.ConsumerHomeInfo;
import com.hp.it.perf.ac.app.hpsc.search.bean.ConsumerRequestReport;
import com.hp.it.perf.ac.app.hpsc.search.bean.ProducerHomeInfo;
import com.hp.it.perf.ac.app.hpsc.search.bean.ProducerReport;
import com.hp.it.perf.ac.app.hpsc.search.bean.QueryCondition.TimeWindow;
import com.hp.it.perf.ac.app.hpsc.search.dao.impl.SpringJDBCSearchDaoImpl.NameAnd90Count;
import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.common.model.AcidSelectors.AcidRange;


public interface SearchDao {
	
	 public ConsumerHomeInfo getConsumerHomeInfo(Map<String, String> timeWindow);
	 
	 public List<ProducerHomeInfo> getProducerHomeInfo(Map<String, String> timeWindow,Map<String, long[]> featureRange);
	 
	 public List<ConsumerRequestReport> getConsumerRequestReport(Map<String,String> timeWindow);
	 
	 public List<ConsumerRequestReport> getConsumerRequestReport(Map<String,String> timeWindow, boolean estimate);
	 
	 public List<ConsumerRequestReport> getConsumerRequestReportOnly(Map<String,String> timeWindow);
	 
	 public List<ConsumerRequestReport> getConsumerRequestReportOnly(Map<String,String> timeWindow, boolean estimate);
	 
	 public List<ConsumerRequestReport> getConsumerRequestDetailReport(Map<String,String> timeWindow, String request);
	 
	 public List<ConsumerRequestReport> getConsumerRequestDetailReport(Map<String,String> timeWindow, String request, boolean estimate);
	
	 public List<ConsumerDetailReport> getConsumerDetailReport(Map<String,String> timeWindow);
	 
	 public List<ConsumerDetailReport> getConsumerDetailReport(Map<String,String> timeWindow, boolean estimate);
	 
	 public List<ProducerReport> getProducerReport(Map<String, String> timeWindow);
	 
	 public List<ProducerReport> getProducerReport(Map<String, String> timeWindow, boolean estimate);
	 
	 public List<ProducerReport> getProducerDetailReoprt(Map<String, String> timeWindow, String part);
	 
	 public List<ProducerReport> getProducerDetailReoprt(Map<String, String> timeWindow, String part, boolean estimate);
	 
	 public void deleteDataInDB(TimeWindow timeWindow, boolean deleteAll);
	 
	 public void handledBySql(String sql, Object[] args, RowCallbackHandler rch);
	 
	 @Deprecated
	 public List<NameAnd90Count> getNameAnd90Count4ProducerReport(Map<String, String> timeWindow);
	 
	 @Deprecated
	 public List<NameAnd90Count> getNameAnd90Count4ConsumerReport(Map<String, String> timeWindow);
	 
	 @Deprecated
	 public List<NameAnd90Count> getNameAnd90Count4WsrpReport(Map<String,String> timeWindow);
	 
	 @Deprecated
	 public List<NameAnd90Count> getNameAnd90Count4WsrpDetailReport(Map<String, String> timeWindow,String part);
	 
	 @Deprecated
	 public ConsumerDetailReport getProducerReport(NameAnd90Count namesAnd90Count ,Map<String, String> timeWindow);
	 
	 @Deprecated
	 public ConsumerRequestReport getConsumerReport(NameAnd90Count namesAnd90Count ,Map<String, String> timeWindow);
	 
	 @Deprecated
	 public ProducerReport getWsrpReport(NameAnd90Count namesAnd90Count ,Map<String, String> timeWindow);
	 
	 @Deprecated
	 public ProducerReport getWsrpDetailReport(NameAnd90Count namesAnd90Count ,Map<String, String> timeWindow);
 
}
