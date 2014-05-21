package com.hp.it.perf.ac.app.hpsc.search.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.hp.it.perf.ac.app.hpsc.search.bean.ConsumerDetailReport;
import com.hp.it.perf.ac.app.hpsc.search.bean.ConsumerHomeInfo;
import com.hp.it.perf.ac.app.hpsc.search.bean.ConsumerRequestReport;
import com.hp.it.perf.ac.app.hpsc.search.bean.ProducerHomeInfo;
import com.hp.it.perf.ac.app.hpsc.search.bean.ProducerReport;
import com.hp.it.perf.ac.app.hpsc.search.bean.QueryCondition.TimeWindow;
import com.hp.it.perf.ac.app.hpsc.search.dao.SearchDao;

@Repository
public class SpringJDBCSearchDaoImpl implements SearchDao {

	@Inject
	private JdbcTemplate temp;
	
	private static Logger logger = LoggerFactory
			.getLogger(SpringJDBCSearchDaoImpl.class);

	public class NameAnd90Count {
		String name;
		Integer ninetyPercent;
		String request;
		String part;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getNinetyPercent() {
			return ninetyPercent;
		}

		public void setNinetyPercent(Integer ninetyPercent) {
			this.ninetyPercent = ninetyPercent;
		}

		public String getRequest() {
			return request;
		}

		public void setRequest(String request) {
			this.request = request;
		}

		public String getPart() {
			return part;
		}

		public void setPart(String part) {
			this.part = part;
		}
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.temp = new JdbcTemplate(dataSource);
	}

	@Override
	public ConsumerHomeInfo getConsumerHomeInfo(Map<String, String> timeWindow) {
		String sql = "select count(acid) access, max(duration) max, min(duration) min, count(distinct(name)) total from SPF_PERFORMANCE_LOG "
				+ " WHERE date_time >= ? and date_time <= ?";
		String failSql = "select count(acid) fail from SPF_PERFORMANCE_LOG where log_status <> 'OK' and date_time >= ? and date_time <= ?";
		RowMapper<ConsumerHomeInfo> rowMapper = new RowMapper<ConsumerHomeInfo>() {
			@Override
			public ConsumerHomeInfo mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				ConsumerHomeInfo consumerHomeInfo = new ConsumerHomeInfo();
				consumerHomeInfo.setTotalRequestURLs(rs.getLong("access"));
				consumerHomeInfo.setMaxTime(rs.getInt("max"));
				consumerHomeInfo.setMinTime(rs.getInt("min"));
				consumerHomeInfo.setTotal(rs.getInt("total"));
				return consumerHomeInfo;
			}
		};
		ConsumerHomeInfo info = temp.queryForObject(sql, new Object[]{timeWindow.get("startTime"), timeWindow.get("endTime")}, rowMapper);
		int fail = temp.queryForInt(failSql, new Object[]{timeWindow.get("startTime"), timeWindow.get("endTime")});
		double failRate = fail * 100.0 / info.getTotalRequestURLs();
		info.setFail(fail);
		info.setFailRate(failRate);
		return info;
	}

	@Override
	public List<ProducerHomeInfo> getProducerHomeInfo(
			Map<String, String> timeWindow, Map<String, long[]> featureRange) {
		StringBuffer sql = new StringBuffer();
		sql.append("select inall.total total, inall.feature feature, ifnull(notok.fail, 0) fail, ifnull((fail/total)*100,0) failRate from ");
		sql.append("(select sum(totals.counter) total, totals.feature from (select case ");
		Map<String, long[]> map = featureRange;
		for (Map.Entry<String, long[]> entry : map.entrySet()) {
			String featrue = entry.getKey();
			long[] acidRange = entry.getValue();
			sql.append("when acid between " + acidRange[0] + " and "
					+ acidRange[1] + " then '" + featrue + "' ");
		}
		sql.append("else 'others' ");
		sql.append("end as feature, 1 counter ");
		sql.append("from PORTLET_BUSINESS_LOG where date_time >= '");
		sql.append(timeWindow.get("startTime"));
		sql.append("' and date_time <= '");
		sql.append(timeWindow.get("endTime"));
		sql.append("' order by acid) totals group by totals.feature) inall ");
		sql.append("left join ");
		sql.append("(select sum(fails.counter) fail, fails.feature from (select case ");
		for (Map.Entry<String, long[]> entry : map.entrySet()) {
			String featrue = entry.getKey();
			long[] acidRange = entry.getValue();
			sql.append("when acid between " + acidRange[0] + " and "
					+ acidRange[1] + " then '" + featrue + "' ");
		}
		sql.append("else 'others' ");
		sql.append("end as feature, 1 counter ");
		sql.append("from PORTLET_BUSINESS_LOG where date_time >= '");
		sql.append(timeWindow.get("startTime"));
		sql.append("' and date_time <= '");
		sql.append(timeWindow.get("endTime"));
		sql.append("' and status = 'FATAL' order by acid) fails group by fails.feature) notok ");
		sql.append("on inall.feature = notok.feature");
		RowMapper<ProducerHomeInfo> rowMapper = new RowMapper<ProducerHomeInfo>() {
			@Override
			public ProducerHomeInfo mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				ProducerHomeInfo producerHomeInfo = new ProducerHomeInfo();
				producerHomeInfo.setFail(rs.getInt("fail"));
				producerHomeInfo.setTotal(rs.getInt("total"));
				producerHomeInfo.setFeature(rs.getString("feature"));
				producerHomeInfo.setFailRate(rs.getDouble("failRate"));
				return producerHomeInfo;
			}
		};
		return temp.query(sql.toString(), rowMapper);
	}

	@Override
	public List<ConsumerRequestReport> getConsumerRequestReport(
			Map<String, String> timeWindow) {
		return getConsumerRequestReport(timeWindow, true);
	}
	
	@Override
	public List<ConsumerRequestReport> getConsumerRequestReport(
			Map<String, String> timeWindow, final boolean estimate) {
		String sql ;
		sql = "select consumer.* from (select name request, 'REQUEST' part,count(acid) counts, min(duration) min, max(duration) max, floor(avg(duration)) avg, round(std(duration)) std " 
				+ (estimate ? "" : ", percentile(duration) ninetypercent ")
				+ ", count(case log_status when 'OK' then null else 1 end) error from SPF_PERFORMANCE_LOG where date_time >= ? and date_time <= ? group by name " 
				+ "union select parent.name request, (case detail.type when 'WSRP_CALL' then concat(detail.type,'[',detail.name,']') else detail.name end) part, count(detail.acid) counts, min(detail.duration) min, max(detail.duration) max, floor(avg(detail.duration)) avg, round(std(detail.duration)) std "
				+ (estimate ? "" : ", percentile(detail.duration) ninetypercent")
				+ ", count(case detail.log_status when 'OK' then null else 1 end) error from SPF_PERFORMANCE_LOG parent, SPF_PERFORMANCE_LOG_DETAIL detail where parent.acid = detail.parent_acid " 
				+ "and parent.date_time >= ? and parent.date_time <= ? group by parent.name,detail.name) consumer order by consumer.request";
		
		RowMapper<ConsumerRequestReport> rowMapper = new RowMapper<ConsumerRequestReport>() {
			@Override
			public ConsumerRequestReport mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				ConsumerRequestReport consumerReport = new ConsumerRequestReport();
				consumerReport.setRequest(rs.getString("request"));
				consumerReport.setPart(rs.getString("part"));
				consumerReport.setCount(rs.getInt("counts"));
				consumerReport.setDurAvg(rs.getDouble("avg"));
				consumerReport.setDurMax(rs.getInt("max"));
				consumerReport.setDurMin(rs.getInt("min"));
				consumerReport.setDurStd(rs.getDouble("std"));
				consumerReport.setError(rs.getInt("error"));
				if(estimate){
					consumerReport.setDur90((int) estimateNinety(
							consumerReport.getCount(), consumerReport.getDurMin(),
							consumerReport.getDurAvg(), consumerReport.getDurMax(), consumerReport.getDurStd()));
				} else {
					consumerReport.setDur90(rs.getInt("ninetypercent"));
				}
				return consumerReport;
			}
		};
		return temp.query(sql, new Object[]{timeWindow.get("startTime"), timeWindow.get("endTime"), timeWindow.get("startTime"), timeWindow.get("endTime")}, rowMapper);
	}

	@Override
	public List<ConsumerRequestReport> getConsumerRequestReportOnly(
			Map<String, String> timeWindow) {
		return getConsumerRequestReportOnly(timeWindow, true);
	}
	
	@Override
	public List<ConsumerRequestReport> getConsumerRequestReportOnly(
			Map<String, String> timeWindow, final boolean estimate) {
		String sql = "select parent.name request, count(parent.acid) counts, min(parent.duration) min, max(parent.duration) max, floor(avg(parent.duration)) avg, round(std(parent.duration),2) std " 
				+ (estimate ? "" : ", percentile(parent.duration) ninetypercent ")
				+ ",count(case parent.log_status when 'OK' then null else 1 end) error from SPF_PERFORMANCE_LOG parent " 
				+ "where parent.date_time >= ? and parent.date_time <= ? group by parent.name";
		RowMapper<ConsumerRequestReport> rowMapper = new RowMapper<ConsumerRequestReport>() {
			@Override
			public ConsumerRequestReport mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				ConsumerRequestReport consumerReport = new ConsumerRequestReport();
				consumerReport.setRequest(rs.getString("request"));
				consumerReport.setPart(rs.getString("part"));
				consumerReport.setCount(rs.getInt("counts"));
				consumerReport.setDurAvg(rs.getDouble("avg"));
				consumerReport.setDurMax(rs.getInt("max"));
				consumerReport.setDurMin(rs.getInt("min"));
				consumerReport.setDurStd(rs.getDouble("std"));
				consumerReport.setError(rs.getInt("error"));
				if(estimate){
					consumerReport.setDur90((int) estimateNinety(
							consumerReport.getCount(), consumerReport.getDurMin(),
							consumerReport.getDurAvg(), consumerReport.getDurMax(), consumerReport.getDurStd()));
				} else {
					consumerReport.setDur90(rs.getInt("ninetypercent"));
				}
				return consumerReport;
			}
		};
		return temp.query(sql, new Object[]{timeWindow.get("startTime"), timeWindow.get("endTime")}, rowMapper);
	}

	@Override
	public List<ConsumerRequestReport> getConsumerRequestDetailReport(
			Map<String, String> timeWindow, String request) {
		return getConsumerRequestDetailReport(timeWindow, request, true);
	}
	@Override
	public List<ConsumerRequestReport> getConsumerRequestDetailReport(
			Map<String, String> timeWindow, String request, final boolean estimate) {
		String sql = "select parent.name request, detail.name part, count(detail.acid) counts, min(detail.duration) min, max(detail.duration) max, floor(avg(detail.duration)) avg, round(std(detail.duration)) std " 
				+ (estimate ? "" : ", percentile(detail.duration) ninetypercent ") 
				+ ", count(case detail.log_status when 'OK' then null else 1 end) error from SPF_PERFORMANCE_LOG parent, SPF_PERFORMANCE_LOG_DETAIL detail " 
				+ "where parent.acid = detail.parent_acid and parent.date_time>=? and parent.date_time <=? and parent.name =? group by detail.name";
		RowMapper<ConsumerRequestReport> rowMapper = new RowMapper<ConsumerRequestReport>() {
			@Override
			public ConsumerRequestReport mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				ConsumerRequestReport consumerReport = new ConsumerRequestReport();
				consumerReport.setRequest(rs.getString("request"));
				consumerReport.setPart(rs.getString("part"));
				consumerReport.setCount(rs.getInt("counts"));
				consumerReport.setDurAvg(rs.getDouble("avg"));
				consumerReport.setDurMax(rs.getInt("max"));
				consumerReport.setDurMin(rs.getInt("min"));
				consumerReport.setDurStd(rs.getDouble("std"));
				consumerReport.setError(rs.getInt("error"));
				if(estimate){
					consumerReport.setDur90((int) estimateNinety(
							consumerReport.getCount(), consumerReport.getDurMin(),
							consumerReport.getDurAvg(), consumerReport.getDurMax(), consumerReport.getDurStd()));
				} else {
					consumerReport.setDur90(rs.getInt("ninetypercent"));
				}
				return consumerReport;
			}
		};
		return temp.query(sql, new Object[]{timeWindow.get("startTime"), timeWindow.get("endTime"), request}, rowMapper);
	}

	@Override
	public List<ConsumerDetailReport> getConsumerDetailReport(
			Map<String, String> timeWindow) {
		return getConsumerDetailReport(timeWindow, true);
	}
	@Override
	public List<ConsumerDetailReport> getConsumerDetailReport(
			Map<String, String> timeWindow, final boolean estimate) {
		String sql = "select detail.name name, count(detail.acid) counts, min(detail.duration) min, max(detail.duration) max, floor(avg(detail.duration)) avg, round(std(detail.duration),2) std " 
				+ (estimate ? "" : ", percentile(detail.duration) ninetypercent ") 
				+ ", count(case detail.log_status when 'OK' then null else 1 end) error from "
				+ " SPF_PERFORMANCE_LOG parent, SPF_PERFORMANCE_LOG_DETAIL detail where parent.acid = detail.parent_acid and "
				+ " parent.date_time >= ? and parent.date_time <= ? group by detail.name";
		RowMapper<ConsumerDetailReport> rowMapper = new RowMapper<ConsumerDetailReport>() {
			@Override
			public ConsumerDetailReport mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				ConsumerDetailReport consumerDetailReport = new ConsumerDetailReport();
				consumerDetailReport.setPortletName(rs.getString("name"));
				consumerDetailReport.setCount(rs.getInt("counts"));
				consumerDetailReport.setAvg(rs.getInt("avg"));
				consumerDetailReport.setMax(rs.getInt("max"));
				consumerDetailReport.setMin(rs.getInt("min"));
				consumerDetailReport.setStd(rs.getDouble("std"));
				consumerDetailReport.setError(rs.getInt("error"));
				if(estimate){
					consumerDetailReport.setNinetyPercent((int) estimateNinety(
							consumerDetailReport.getCount(), consumerDetailReport.getMin(),
							consumerDetailReport.getAvg(), consumerDetailReport.getMax(), consumerDetailReport.getStd()));
				} else {
					consumerDetailReport.setNinetyPercent(rs.getInt("ninetypercent"));
				}
				return consumerDetailReport;
			}
		};
		return temp.query(sql, new Object[]{timeWindow.get("startTime"), timeWindow.get("endTime")}, rowMapper);
	}

	@Override
	public List<ProducerReport> getProducerReport(Map<String, String> timeWindow) {
		return getProducerReport(timeWindow, true);
	}
	@Override
	public List<ProducerReport> getProducerReport(Map<String, String> timeWindow, final boolean estimate) {
		String sql = "select concat(perf.portlet_name,'(',perf.portlet_method,')') name, count(perf.acid) counts, floor(avg(perf.duration)) avg, min(perf.duration) min, max(perf.duration) max, round(std(perf.duration),2) std "
				+ (estimate ? "" : ", percentile(perf.duration) ninetypercent ") 
				+ ", count(case biz.status when 'FATAL' then 1 else null end) error from PORTLET_PERFORMANCE_LOG perf, PORTLET_BUSINESS_LOG biz where biz.transaction_id = perf.transaction_id "
				+ "and perf.date_time >= ? and perf.date_time <= ? group by perf.portlet_method, perf.portlet_name";
		RowMapper<ProducerReport> rowMapper = new RowMapper<ProducerReport>() {
			@Override
			public ProducerReport mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				ProducerReport producerReport = new ProducerReport();
				producerReport.setName(rs.getString("name"));
				producerReport.setCount(rs.getInt("counts"));
				producerReport.setAvg(rs.getInt("avg"));
				producerReport.setMax(rs.getInt("max"));
				producerReport.setMin(rs.getInt("min"));
				producerReport.setStd(rs.getDouble("std"));
				producerReport.setError(rs.getInt("error"));
				if(estimate){
					producerReport.setNinetyPercent((int) estimateNinety(
							producerReport.getCount(), producerReport.getMin(),
							producerReport.getAvg(), producerReport.getMax(),
							producerReport.getStd()));
				} else {
					producerReport.setNinetyPercent(rs.getInt("ninetypercent"));
				}
				return producerReport;
			}
		};
		return temp.query(sql, new Object[]{timeWindow.get("startTime"), timeWindow.get("endTime")}, rowMapper);
	}

	@Override
	public List<ProducerReport> getProducerDetailReoprt(
			Map<String, String> timeWindow, String part) {
		return getProducerDetailReoprt(timeWindow, part, true);
	}
	@Override
	public List<ProducerReport> getProducerDetailReoprt(
			Map<String, String> timeWindow, String part, final boolean estimate) {
		String sql = "select detail.name name, count(detail.acid) counts, floor(avg(detail.duration)) avg, min(detail.duration) min, max(detail.duration) max, round(std(detail.duration),2) std " 
				+ (estimate ? "" : ", percentile(detail.duration) ninetypercent ")
				+ " from PORTLET_PERFORMANCE_LOG parent, PORTLET_PERFORMANCE_LOG_DETAIL detail where parent.acid = detail.acid " 
				+ " and parent.date_time >= ? and parent.date_time <= ? and concat(parent.portlet_name,'(',parent.portlet_method,')') =? group by detail.name";
		RowMapper<ProducerReport> rowMapper = new RowMapper<ProducerReport>() {
			@Override
			public ProducerReport mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				ProducerReport producerReport = new ProducerReport();
				producerReport.setName(rs.getString("name"));
				producerReport.setCount(rs.getInt("counts"));
				producerReport.setAvg(rs.getInt("avg"));
				producerReport.setMax(rs.getInt("max"));
				producerReport.setMin(rs.getInt("min"));
				producerReport.setStd(rs.getDouble("std"));
				if(estimate){
					producerReport.setNinetyPercent((int) estimateNinety(
							producerReport.getCount(), producerReport.getMin(),
							producerReport.getAvg(), producerReport.getMax(),
							producerReport.getStd()));
				} else {
					producerReport.setNinetyPercent(rs.getInt("ninetypercent"));
				}
				return producerReport;
			}
		};
		return temp.query(sql, new Object[]{timeWindow.get("startTime"), timeWindow.get("endTime"), part}, rowMapper);
	}

	@Override
	public synchronized void deleteDataInDB(TimeWindow timeWindow, boolean deleteAll){
		
		if(deleteAll){
			logger.debug("Delete all data in database");
			deleteAllData();
			return;
		}
		
		if(timeWindow == null){
			logger.error("Time window for deleting is null");
			return;
		}
		
		if(timeWindow.getStartTime() == null || timeWindow.getEndTime() == null){
			logger.warn("Either start time or end time for deleting is null. Will not do any deleting action");
			return;
		} 
		
		/*long startTime = timeWindow.getStartTime().getTime();
		long endTime = timeWindow.getEndTime().getTime();*/
		String startTimeString = formatDate(timeWindow.getStartTime());
		String endTimeString = formatDate(timeWindow.getEndTime());
				
		/*String deleteCommonSQL = "delete from context, common using AC_COMMON_DATA common left join AC_CONTEXT context on common.acid = context.acid where common.created >= "
				+ startTime + " and common.created <= " + endTime;*/
		String deleteSPFPerformanceSQL = "delete from detail, parent using SPF_PERFORMANCE_LOG parent left join SPF_PERFORMANCE_LOG_DETAIL detail on parent.ACID = detail.PARENT_ACID where parent.DATE_TIME >= '"
				+ startTimeString
				+ "' and parent.DATE_TIME <= '"
				+ endTimeString + "' ";
		String deletePortletPerformanceSQL = "delete from detail, parent using PORTLET_PERFORMANCE_LOG parent left join PORTLET_PERFORMANCE_LOG_DETAIL detail on parent.ACID = detail.ACID where parent.DATE_TIME >= '"
				+ startTimeString
				+ "' and parent.DATE_TIME <= '"
				+ endTimeString + "' ";
		String deletePortletBusinessSQL = "delete from PORTLET_BUSINESS_LOG where DATE_TIME >= '"
				+ startTimeString
				+ "' and DATE_TIME <= '"
				+ endTimeString
				+ "' ";
		String deletePortletErrorSQL = "delete from PORTLET_ERROR_LOG where DATE_TIME >= '"
				+ startTimeString
				+ "' and DATE_TIME <= '"
				+ endTimeString
				+ "' ";
		String deletePortletErrortraceSQL = "delete from PORTLET_ERRORTRACE_LOG where DATE_TIME >= '"
				+ startTimeString
				+ "' and DATE_TIME <= '"
				+ endTimeString
				+ "' ";
		
		String[] deleteSQLs = new String[] { deleteSPFPerformanceSQL,
				deletePortletPerformanceSQL, deletePortletBusinessSQL,
				deletePortletErrorSQL, deletePortletErrortraceSQL };
		
		ExecutorService threadPool = Executors.newFixedThreadPool(6);
		List<Future<?>> futures = new ArrayList<Future<?>>();
		for(final String sql : deleteSQLs){
			Future<?> future = threadPool.submit(new Runnable() {

				@Override
				public void run() {
					temp.execute(sql);
				}

			});
			futures.add(future);
		}
		for(Future<?> furture : futures) {
			try {
				furture.get();
			} catch (Exception e) {
				logger.error("Get furture error");
			} 
		}
		threadPool.shutdown();
	}
	
	private void deleteAllData(){
		logger.debug("Delete all data in database");
		
		/*String deleteContextSQL = "delete from AC_CONTEXT";
		String deleteCommonSQL = "delete from AC_COMMON_DATA";*/
		String deleteSPFPerformanceDetailSQL = "delete from SPF_PERFORMANCE_LOG_DETAIL";
		String deleteSPFPerformanceSQL = "delete from SPF_PERFORMANCE_LOG";
		String deletePortletPerformanceDetailSQL = "delete from PORTLET_PERFORMANCE_LOG_DETAIL";
		String deletePortletPerformanceSQL = "delete from PORTLET_PERFORMANCE_LOG";
		String deletePortletBusinessSQL = "delete from PORTLET_BUSINESS_LOG";
		String deletePortletErrorSQL = "delete from PORTLET_ERRORTRACE_LOG";
		String deletePortletErrortraceSQL = "delete from PORTLET_ERROR_LOG";
	
		String[] deleteSQLs = new String[] { deleteSPFPerformanceDetailSQL,
				deleteSPFPerformanceSQL, deletePortletPerformanceDetailSQL,
				deletePortletPerformanceSQL, deletePortletBusinessSQL,
				deletePortletErrorSQL, deletePortletErrortraceSQL };
	
		for(String deletSQL : deleteSQLs){
			temp.execute(deletSQL);
		}
	}
	
	@Override
	public void handledBySql(String sql, Object[] args, RowCallbackHandler rch){

		//String sql = "select acid from AC_COMMON_DATA where created >= ? and created <= ?";
		logger.debug("Handle each row for SQL: " + sql);
		LocalSimplePreparedStatementCreator psc;
		if(args == null || args.length == 0){
			psc = new LocalSimplePreparedStatementCreator(sql);
		} else {
			psc = new LocalSimplePreparedStatementCreator(sql, args);
		}
		temp.query(psc, rch);
		
	}
	
	private static class LocalSimplePreparedStatementCreator implements PreparedStatementCreator, SqlProvider {

		private final String sql;
		
		private final Object[] args;

		public LocalSimplePreparedStatementCreator(String sql) {
			Assert.notNull(sql, "SQL must not be null");
			this.sql = sql;
			args = null;
		}
		
		public LocalSimplePreparedStatementCreator(String sql, Object[] args) {
			Assert.notNull(sql, "SQL must not be null");
			this.sql = sql;
			this.args = args;
		}

		public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
			PreparedStatement pstmt = con.prepareStatement(sql, java.sql.ResultSet.TYPE_FORWARD_ONLY,
                    java.sql.ResultSet.CONCUR_READ_ONLY);
            pstmt.setFetchSize(Integer.MIN_VALUE);
            if(this.args != null){
            	for (int i = 0; i < this.args.length; i++) {
    				Object arg = this.args[i];
    				doSetValue(pstmt, i + 1, arg);
    			}
            }
            return pstmt;
			//return con.prepareStatement(this.sql);
		}

		public String getSql() {
			return this.sql;
		}
		
		// This is from 
		protected void doSetValue(PreparedStatement ps, int parameterPosition, Object argValue) throws SQLException {
			if (argValue instanceof SqlParameterValue) {
				SqlParameterValue paramValue = (SqlParameterValue) argValue;
				StatementCreatorUtils.setParameterValue(ps, parameterPosition, paramValue, paramValue.getValue());
			}
			else {
				StatementCreatorUtils.setParameterValue(ps, parameterPosition, SqlTypeValue.TYPE_UNKNOWN, argValue);
			}
		}
	}
	
	private String formatDate(Date date) {
		if(date == null){
			return "1900-01-01 00:00:00";
		}
		SimpleDateFormat dateformat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return dateformat.format(date);
	}

	@Deprecated
	public List<NameAnd90Count> getNameAnd90Count4ProducerReport(
			Map<String, String> timeWindow) {
		String sql = "select detail.name, ceil(count(detail.acid)*0.9)-1 90percentcount from SPF_PERFORMANCE_LOG parent, SPF_PERFORMANCE_LOG_DETAIL detail where parent.acid = detail.parent_acid and parent.date_time >= '"
				+ timeWindow.get("startTime")
				+ "' and parent.date_time <= '"
				+ timeWindow.get("endTime") + "' group by detail.name";
		List<NameAnd90Count> list = temp.query(sql,
				new RowMapper<NameAnd90Count>() {
					@Override
					public NameAnd90Count mapRow(ResultSet rs, int detailRowNum)
							throws SQLException {
						NameAnd90Count bean = new NameAnd90Count();
						bean.setName(rs.getString("name"));
						bean.setNinetyPercent(rs.getInt("90percentcount"));
						return bean;
					}

				});
		return list;
	}

	@Deprecated
	public List<NameAnd90Count> getNameAnd90Count4ConsumerReport(
			Map<String, String> timeWindow) {
		String sql = "select detail.name name, parent.name request, concat(detail.type,'[',detail.name,']') part, ceil(count(detail.acid)*0.9)-1 90percentcount from SPF_PERFORMANCE_LOG_DETAIL detail, SPF_PERFORMANCE_LOG parent where parent.acid = detail.parent_acid and parent.date_time >='"
				+ timeWindow.get("startTime")
				+ "' and parent.date_time <= '"
				+ timeWindow.get("endTime") + "' group by detail.name";
		List<NameAnd90Count> list = temp.query(sql,
				new RowMapper<NameAnd90Count>() {
					@Override
					public NameAnd90Count mapRow(ResultSet rs, int detailRowNum)
							throws SQLException {
						NameAnd90Count bean = new NameAnd90Count();
						bean.setName(rs.getString("name"));
						bean.setNinetyPercent(rs.getInt("90percentcount"));
						bean.setPart(rs.getString("part"));
						bean.setRequest(rs.getString("request"));
						return bean;
					}

				});
		return list;
	}

	@Deprecated
	public List<NameAnd90Count> getNameAnd90Count4WsrpReport(
			Map<String, String> timeWindow) {
		String sql = "select concat(portlet_name,'(',portlet_method,')') part, ceil(count(acid)*0.9)-1 90percentcount from PORTLET_PERFORMANCE_LOG where date_time >= '"
				+ timeWindow.get("startTime")
				+ "' and date_time <= '"
				+ timeWindow.get("endTime")
				+ "' group by concat(portlet_name,'(',portlet_method,')')";
		List<NameAnd90Count> list = temp.query(sql,
				new RowMapper<NameAnd90Count>() {
					@Override
					public NameAnd90Count mapRow(ResultSet rs, int detailRowNum)
							throws SQLException {
						NameAnd90Count bean = new NameAnd90Count();
						bean.setNinetyPercent(rs.getInt("90percentcount"));
						bean.setPart(rs.getString("part"));
						return bean;
					}

				});
		return list;
	}

	@Deprecated
	public List<NameAnd90Count> getNameAnd90Count4WsrpDetailReport(
			Map<String, String> timeWindow, String part) {
		StringBuffer sql = new StringBuffer(
				"select concat(parent.portlet_name,'(',parent.portlet_method,')') part, detail.name name, ceil(count(detail.acid)*0.9)-1 90percentcount ");
		sql.append("from PORTLET_PERFORMANCE_LOG_DETAIL detail, PORTLET_PERFORMANCE_LOG parent");
		sql.append(" where parent.acid = detail.acid and concat(parent.portlet_name,'(',parent.portlet_method,')') = ? and ");
		sql.append("parent.date_time >= ? and parent.date_time <= ? group by detail.name");
		List<NameAnd90Count> list = temp.query(sql.toString(), new Object[]{part, timeWindow.get("startTime"), timeWindow.get("endTime")}, 
				new RowMapper<NameAnd90Count>() {
					@Override
					public NameAnd90Count mapRow(ResultSet rs, int detailRowNum)
							throws SQLException {
						NameAnd90Count bean = new NameAnd90Count();
						bean.setNinetyPercent(rs.getInt("90percentcount"));
						bean.setPart(rs.getString("part"));
						bean.setName(rs.getString("name"));
						return bean;
					}

				});
		return list;
	}

	@Deprecated
	public ConsumerDetailReport getProducerReport(
			NameAnd90Count namesAnd90Count, Map<String, String> timeWindow) {
		String timeQuery = "parent.date_time >= '"
				+ timeWindow.get("startTime") + "' and parent.date_time <= '"
				+ timeWindow.get("endTime") + "' ";
		StringBuffer sql = new StringBuffer(
				"select total.name name, total.counts counts, total.min min, total.max max, floor(total.avg) avg, round(total.std,2) std, ninety.duration ninetypercent, error.errors error from ");
		sql.append("(select detail.duration, detail.name from SPF_PERFORMANCE_LOG parent, SPF_PERFORMANCE_LOG_DETAIL detail where parent.acid = detail.parent_acid and detail.name ='");
		sql.append(namesAnd90Count.getName());
		sql.append("' and ");
		sql.append(timeQuery);
		sql.append(" order by detail.duration limit ");
		sql.append(namesAnd90Count.getNinetyPercent());
		sql.append(",1) ninety,");
		sql.append("(select detail.name, count(detail.acid) counts, min(detail.duration) min, max(detail.duration) max, avg(detail.duration) avg, std(detail.duration) std from SPF_PERFORMANCE_LOG parent, SPF_PERFORMANCE_LOG_DETAIL detail where parent.acid = detail.parent_acid and detail.name= '");
		sql.append(namesAnd90Count.getName());
		sql.append("' and ");
		sql.append(timeQuery);
		sql.append(") total,");
		sql.append("(select ifnull(detail.name,'");
		sql.append(namesAnd90Count.getName());
		sql.append("') name, ifnull(count(detail.acid),0) errors from SPF_PERFORMANCE_LOG parent, SPF_PERFORMANCE_LOG_DETAIL detail where parent.acid = detail.parent_acid and detail.name = '");
		sql.append(namesAnd90Count.getName());
		sql.append("' and detail.log_status <> 'OK' and ");
		sql.append(timeQuery);
		sql.append(") error ");
		sql.append("where total.name = ninety.name and error.name = total.name");
		RowMapper<ConsumerDetailReport> rowMapper = new RowMapper<ConsumerDetailReport>() {
			@Override
			public ConsumerDetailReport mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				ConsumerDetailReport producerReport = new ConsumerDetailReport();
				producerReport.setPortletName(rs.getString("name"));
				producerReport.setCount(rs.getInt("counts"));
				producerReport.setAvg(rs.getInt("avg"));
				producerReport.setMax(rs.getInt("max"));
				producerReport.setMin(rs.getInt("min"));
				producerReport.setNinetyPercent(rs.getInt("ninetypercent"));
				producerReport.setStd(rs.getDouble("std"));
				producerReport.setError(rs.getInt("error"));
				return producerReport;
			}
		};
		return temp.queryForObject(sql.toString(), rowMapper);
	}

	@Deprecated
	public ConsumerRequestReport getConsumerReport(
			NameAnd90Count namesAnd90Count, Map<String, String> timeWindow) {
		String timeQuery = "parent.date_time >= '"
				+ timeWindow.get("startTime") + "' and parent.date_time <= '"
				+ timeWindow.get("endTime") + "' ";
		StringBuffer sql = new StringBuffer("select '");
		sql.append(namesAnd90Count.getRequest());
		sql.append("' request, '");
		sql.append(namesAnd90Count.getPart());
		sql.append("' part, total.counts counts, total.min min, total.max max, floor(total.avg) avg, round(total.std,2) std, ninety.duration ninetypercent, error.errors error from ");
		sql.append("(select detail.duration, detail.name from SPF_PERFORMANCE_LOG_DETAIL detail, SPF_PERFORMANCE_LOG parent where parent.acid = detail.parent_acid and detail.name ='");
		sql.append(namesAnd90Count.getName());
		sql.append("' and ");
		sql.append(timeQuery);
		sql.append(" order by detail.duration limit ");
		sql.append(namesAnd90Count.getNinetyPercent());
		sql.append(",1) ninety,");
		sql.append("(select detail.name, count(detail.acid) counts, min(detail.duration) min, max(detail.duration) max, avg(detail.duration) avg, std(detail.duration) std from SPF_PERFORMANCE_LOG_DETAIL detail, SPF_PERFORMANCE_LOG parent where parent.acid = detail.parent_acid and detail.name='");
		sql.append(namesAnd90Count.getName());
		sql.append("' and ");
		sql.append(timeQuery);
		sql.append(") total,");
		sql.append("(select ifnull(detail.name,'");
		sql.append(namesAnd90Count.getName());
		sql.append("') name, ifnull(count(detail.acid),0) errors from SPF_PERFORMANCE_LOG_DETAIL detail, SPF_PERFORMANCE_LOG parent where parent.acid = detail.parent_acid and detail.name = '");
		sql.append(namesAnd90Count.getName());
		sql.append("' and detail.log_status <> 'OK' and ");
		sql.append(timeQuery);
		sql.append(") error ");
		sql.append("where total.name = ninety.name and error.name = total.name");
		RowMapper<ConsumerRequestReport> rowMapper = new RowMapper<ConsumerRequestReport>() {
			@Override
			public ConsumerRequestReport mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				ConsumerRequestReport consumerReport = new ConsumerRequestReport();
				consumerReport.setRequest(rs.getString("request"));
				consumerReport.setPart(rs.getString("part"));
				consumerReport.setCount(rs.getInt("counts"));
				consumerReport.setDurAvg(rs.getDouble("avg"));
				consumerReport.setDurMax(rs.getInt("max"));
				consumerReport.setDurMin(rs.getInt("min"));
				consumerReport.setDur90(rs.getInt("ninetypercent"));
				consumerReport.setDurStd(rs.getDouble("std"));
				consumerReport.setError(rs.getInt("error"));
				return consumerReport;
			}
		};
		return temp.queryForObject(sql.toString(), rowMapper);
	}

	@Deprecated
	public ProducerReport getWsrpReport(NameAnd90Count namesAnd90Count,
			Map<String, String> timeWindow) {
		String timeQuery = "date_time >= '" + timeWindow.get("startTime")
				+ "' and date_time <= '" + timeWindow.get("endTime") + "' ";
		StringBuffer sql = new StringBuffer(
				"select total.part part, total.counts counts, total.min min, total.max max, floor(total.avg) avg, round(total.std,2) std, ninety.duration ninetypercent, error.errors error from ");
		sql.append("(select duration, concat(portlet_name,'(',portlet_method,')') part from PORTLET_BUSINESS_LOG where concat(portlet_name,'(',portlet_method,')') ='");
		sql.append(namesAnd90Count.getPart());
		sql.append("' and ");
		sql.append(timeQuery);
		sql.append("order by duration limit ");
		sql.append(namesAnd90Count.getNinetyPercent());
		sql.append(",1) ninety, ");
		sql.append("(select concat(portlet_name,'(',portlet_method,')') part, count(acid) counts, min(duration) min, max(duration) max, avg(duration) avg, std(duration) std from PORTLET_BUSINESS_LOG where concat(portlet_name,'(',portlet_method,')') ='");
		sql.append(namesAnd90Count.getPart());
		sql.append("' and ");
		sql.append(timeQuery);
		sql.append(") total, ");
		sql.append("(select ifnull(concat(portlet_name,'(',portlet_method,')'),'");
		sql.append(namesAnd90Count.getPart());
		sql.append("') part, ifnull(count(acid),0) errors from PORTLET_BUSINESS_LOG where concat(portlet_name,'(',portlet_method,')') ='");
		sql.append(namesAnd90Count.getPart());
		sql.append("' and status <> 'OK' and ");
		sql.append(timeQuery);
		sql.append(") error ");
		sql.append("where total.part = ninety.part and error.part = total.part");
		RowMapper<ProducerReport> rowMapper = new RowMapper<ProducerReport>() {
			@Override
			public ProducerReport mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				ProducerReport wsrpReport = new ProducerReport();
				wsrpReport.setName(rs.getString("part"));
				wsrpReport.setCount(rs.getInt("counts"));
				wsrpReport.setAvg(rs.getInt("avg"));
				wsrpReport.setMax(rs.getInt("max"));
				wsrpReport.setMin(rs.getInt("min"));
				wsrpReport.setNinetyPercent(rs.getInt("ninetypercent"));
				wsrpReport.setStd(rs.getFloat("std"));
				wsrpReport.setError(rs.getInt("error"));
				return wsrpReport;
			}
		};
		return temp.queryForObject(sql.toString(), rowMapper);
	}

	@Deprecated
	public ProducerReport getWsrpDetailReport(NameAnd90Count namesAnd90Count,
			Map<String, String> timeWindow) {
		String timeQuery = "parent.date_time >= '"
				+ timeWindow.get("startTime") + "' and parent.date_time <= '"
				+ timeWindow.get("endTime") + "' ";
		StringBuffer sql = new StringBuffer(
				"select total.name name, total.counts counts, total.min min, total.max max, floor(total.avg) avg, round(total.std,2) std, ninety.duration ninetypercent from ");
		sql.append("(select detail.name name, detail.duration duration from PORTLET_PERFORMANCE_LOG parent, PORTLET_PERFORMANCE_LOG_DETAIL detail where parent.acid = detail.acid and concat(parent.portlet_name,'(',parent.portlet_method,')') ='");
		sql.append(namesAnd90Count.getPart());
		sql.append("' and detail.name ='");
		sql.append(namesAnd90Count.getName());
		sql.append("' and ");
		sql.append(timeQuery);
		sql.append("order by detail.duration limit 1,1) ninety, ");
		sql.append("(select detail.name name, count(detail.acid) counts, min(detail.duration) min, max(detail.duration) max, avg(detail.duration) avg, std(detail.duration) std from PORTLET_PERFORMANCE_LOG parent,PORTLET_PERFORMANCE_LOG_DETAIL detail WHERE parent.acid = detail.acid and concat(parent.portlet_name,'(',parent.portlet_method,')') ='");
		sql.append(namesAnd90Count.getPart());
		sql.append("' and detail.name ='");
		sql.append(namesAnd90Count.getName());
		sql.append("' and ");
		sql.append(timeQuery);
		sql.append(") total ");
		sql.append("where total.name = ninety.name");
		RowMapper<ProducerReport> rowMapper = new RowMapper<ProducerReport>() {
			@Override
			public ProducerReport mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				ProducerReport wsrpReport = new ProducerReport();
				wsrpReport.setName(rs.getString("name"));
				wsrpReport.setCount(rs.getInt("counts"));
				wsrpReport.setAvg(rs.getInt("avg"));
				wsrpReport.setMax(rs.getInt("max"));
				wsrpReport.setMin(rs.getInt("min"));
				wsrpReport.setNinetyPercent(rs.getInt("ninetypercent"));
				wsrpReport.setStd(rs.getFloat("std"));
				return wsrpReport;
			}
		};
		return temp.queryForObject(sql.toString(), rowMapper);
	}
	
	private static final double factor1 = 0.0801;
	private static final double factor2 = 0.1191;
	private static final double factor3 = 0.8855;
	
	private static double estimateNinety(int count, double min, double average, double max, double std){
		if(count < 10){
			return max;
		}
		double avg_min = average - min;
		if(avg_min == 0.0){
			return average;
		}
		double std_d_avgmin = std / avg_min; 
		double estimate = (average + std)/ (factor1 * std_d_avgmin * std_d_avgmin + factor2 * std_d_avgmin + factor3 );
		return estimate;
	}

}
