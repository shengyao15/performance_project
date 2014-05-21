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

import org.apache.commons.dbutils.DbUtils;

import com.hp.ucmdb.report.bean.AdapterDetailBean;
import com.hp.ucmdb.report.bean.AdapterSummaryBean;
import com.hp.ucmdb.report.db.DBConnectionManager;
import com.hp.ucmdb.report.util.AllConstants;
import com.hp.ucmdb.report.util.ReportUtil;
import com.hp.ucmdb.report.util.StringConverter;

public class AdapterDao{
	private List<Integer> errorFileIdList = new ArrayList<Integer>();
	private static final String ORDER_CONDITION = " order by cdr.recordseqno ";
	private static final String QUERY_ADAPTER_SUMMARY =
		"select cdf.fileId,                       "+
		"       cdf.filename,                     "+
		"       cdf.filedatasource,               "+
		"       cdf.processstartdate,             "+
		"       cdf.processenddate,               "+
		"       cdf.recordcount,                  "+
		"       cdf.filestatus,                   "+
		"       cdf.recordsinerror,               "+
		"       cdf.dscontactemail                "+
		"  from cim_data_files cdf                "+
		" where cdf.processenddate >= sysdate - 1 ";
	
	private static final String QUERY_ADAPTER_DETAIL =
		"select cde.errorid,                              "+
		"       cde.fileid,                               "+
		"       cdr.recordseqno,                          "+
		"       cde.attributename,                        "+
		"       cde.attributevalue,                       "+
		"       cde.errornbr,                             "+
		"       cde.errormessage                          "+
		"  from cim_data_errors cde, cim_data_records cdr "+
		" where cdr.recordid = cde.recordid               "+
		"   and cde.fileid in ";

	public ArrayList<AdapterSummaryBean> getSummary() {
		ReportUtil.getLogger().info("call AdapterDao getSummary ... ");
		ArrayList<AdapterSummaryBean> adapterSummaryList = new ArrayList<AdapterSummaryBean>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = DBConnectionManager.getConnection();
			String sql = QUERY_ADAPTER_SUMMARY;
			ReportUtil.getLogger().info("sql = " + sql);
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				adapterSummaryList.add(makeSummaryBean(rs));
			}
		}

		catch (SQLException e) {
			ReportUtil.getLogger().error(e.toString());
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(con, ps, rs);
		}
		return adapterSummaryList;
	}
	
	private AdapterSummaryBean makeSummaryBean(ResultSet rs)throws SQLException{
		AdapterSummaryBean bean = new AdapterSummaryBean();
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
	
	public ArrayList<AdapterDetailBean> getDetail(String fileErrorIds) {
		ReportUtil.getLogger().info("call AdapterDao getDetail ... ");
		ArrayList<AdapterDetailBean> adapterDetailList = new ArrayList<AdapterDetailBean>();
		if("".equals(fileErrorIds)){
			return adapterDetailList;
		}
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = DBConnectionManager.getConnection();
			String sql = QUERY_ADAPTER_DETAIL + AllConstants.LEFT_BRACKET
					+ fileErrorIds + AllConstants.RIGHT_BRACKET
					+ ORDER_CONDITION;
			ReportUtil.getLogger().info("sql = " + sql);
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				adapterDetailList.add(makeDetailBean(rs));
			}
		}

		catch (SQLException e) {
			ReportUtil.getLogger().error(e.toString());
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(con, ps, rs);
		}
		return adapterDetailList;
	}
	
	private AdapterDetailBean makeDetailBean(ResultSet rs)throws SQLException{
		AdapterDetailBean bean = new AdapterDetailBean();
		bean.setErrorId(rs.getInt("errorid"));
		bean.setFileId(rs.getInt("fileId"));
		bean.setRecordSeqNo(rs.getInt("recordseqno"));
		bean.setAttributeName(StringConverter.Convert(rs.getString("attributename")));
		bean.setAttributeValue(StringConverter.Convert(rs.getString("attributevalue")));
		bean.setErrorNbr(StringConverter.Convert(rs.getString("errornbr")));
		bean.setErrorMessage(StringConverter.Convert(rs.getString("errormessage")));
		return bean;
	}
	
	public List<Integer> getErrorFileIdList() {
		return errorFileIdList;
	}

}
