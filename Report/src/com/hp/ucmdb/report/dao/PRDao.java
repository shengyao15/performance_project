/**
 * Copyright : HP
 * project : Report
 * Create by : Steven Zhang(Ling Kai)
 * Create on : 2011-7-11
 */
package com.hp.ucmdb.report.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.dbutils.DbUtils;

import com.hp.ucmdb.report.bean.PRDetailBean;
import com.hp.ucmdb.report.bean.PRSummaryBean;
import com.hp.ucmdb.report.db.DBConnectionManager;
import com.hp.ucmdb.report.util.AllConstants;
import com.hp.ucmdb.report.util.ReportUtil;
import com.hp.ucmdb.report.util.StringConverter;

public class PRDao{
	private List<Integer> errorFileIdList = new ArrayList<Integer>();
	private static final String ORDER_CONDITION = " order by cpdr.recordseqno ";
	private static final String QUERY_PR_SUMMARY =
		"select cpdf.fileid,                       "+ 
		"       cpdf.filename,                     "+ 
		"       cpdf.filedatasource,               "+ 
		"       cpdf.processstartdate,             "+ 
		"       cpdf.processenddate,               "+ 
		"       cpdf.recordcount,                  "+ 
		"       cpdf.filestatus,                   "+ 
		"       cpdf.recordsinerror,               "+
		"       cpdf.dscontactemail                "+ 
		"  from cim_pr_data_files cpdf             "+ 
		" where cpdf.processenddate >= sysdate - 1 "; 
	
	private static final String QUERY_PR_DETAIL = 
		"select cpdr.recordid,             "+
		"        cpdr.fileid,              "+
		"        cpdr.recordseqno,         "+
		"        cpdr.name,                "+
		"        cpdr.extid,               "+
		"        cpdr.org_brand,           "+
		"        cpdr.org_model,           "+
		"        cpdr.pr_brand,            "+
		"        cpdr.pr_model,            "+
		"        cpdr.recordstatus         "+
		"   from cim_pr_data_records cpdr  "+
		"  where cpdr.fileid in ";


	public ArrayList<PRSummaryBean> getSummary() {
		ReportUtil.getLogger().info("call PRDao getSummary ... ");
		ArrayList<PRSummaryBean> prSummaryList = new ArrayList<PRSummaryBean>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = DBConnectionManager.getConnection();
			String sql = QUERY_PR_SUMMARY;
			ReportUtil.getLogger().info("sql = " + sql);
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				prSummaryList.add(makeSummaryBean(rs));
			}
		}

		catch (SQLException e) {
			ReportUtil.getLogger().error(e.toString());
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(con, ps, rs);
		}
		return prSummaryList;
	}
	
	private PRSummaryBean makeSummaryBean(ResultSet rs)throws SQLException{
		PRSummaryBean bean = new PRSummaryBean();
		bean.setFileId(rs.getInt("fileId"));
		bean.setFileName(StringConverter.Convert(rs.getString("filename")));
		bean.setFileDataSource(StringConverter.Convert(rs.getString("filedatasource")));
		bean.setProcessStartDate(rs.getTimestamp("processstartdate"));
		bean.setProcessEndDate(rs.getTimestamp("processenddate"));
		Integer recordCount = (rs.getObject("recordcount") == null) ? 0 : rs.getInt("recordcount");
		bean.setRecordCount(recordCount);
		Integer errorRecordsCount = (rs.getObject("recordsinerror") == null) ? 0 : rs.getInt("recordsinerror");
		bean.setRecordsinError(errorRecordsCount);
		bean.setRecordSuccess(recordCount-errorRecordsCount);
		bean.setFileStatus(StringConverter.Convert(rs.getString("FileStatus")));
		String dsContactEmail = (rs.getObject("dscontactemail") == null) ? "" : rs.getString("dscontactemail");
		bean.setDsContactEmail(dsContactEmail.trim());
		if(!errorRecordsCount.equals(0)){
			bean.setHaveErrorRecords(true);
			errorFileIdList.add(bean.getFileId());
		}
		return bean;
	}
	
	public ArrayList<PRDetailBean> getDetail(String fileErrorIds) {
		ReportUtil.getLogger().info("call PRDao getDetail ... ");
		ArrayList<PRDetailBean> prDetailList = new ArrayList<PRDetailBean>();
		if("".equals(fileErrorIds)){
			return prDetailList;
		}
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = DBConnectionManager.getConnection();
			String sql = QUERY_PR_DETAIL + AllConstants.LEFT_BRACKET
					+ fileErrorIds + AllConstants.RIGHT_BRACKET
					+ ORDER_CONDITION;
			ReportUtil.getLogger().info("sql = " + sql);
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				prDetailList.add(makeDetailBean(rs));
			}
		}

		catch (SQLException e) {
			ReportUtil.getLogger().error(e.toString());
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(con, ps, rs);
		}
		return prDetailList;
	}
	
	private PRDetailBean makeDetailBean(ResultSet rs)throws SQLException{
		PRDetailBean bean = new PRDetailBean();
		bean.setRecordId(rs.getInt("recordid"));
		bean.setFileId(rs.getInt("fileId"));
		bean.setRecordSeqNo(rs.getInt("recordseqno"));
		bean.setName(StringConverter.Convert(rs.getString("name")));
		bean.setExtId(StringConverter.Convert(rs.getString("extid")));
		bean.setOrgBrand(StringConverter.Convert(rs.getString("org_brand")));
		bean.setOrgModel(StringConverter.Convert(rs.getString("org_model")));
		bean.setPrBrand(StringConverter.Convert(rs.getString("pr_brand")));
		bean.setPrModel(StringConverter.Convert(rs.getString("pr_model")));
		bean.setRecordStatus(getReportStatus().getString(StringConverter.Convert(rs.getString("recordstatus"))));
		return bean;
	}

	public List<Integer> getErrorFileIdList() {
		return errorFileIdList;
	}
	
	private static PropertiesConfiguration getReportStatus() {
		PropertiesConfiguration config = null;
		try {
			config = new PropertiesConfiguration(
					AllConstants.RECORD_STATUS_FILE);
		} catch (ConfigurationException e) {

			ReportUtil.getLogger().error(e);
		}
		// TODO: change the refreshDelay for this reload strategy
		config.setReloadingStrategy(new FileChangedReloadingStrategy());
	
		return config;
	}
}
