package com.hp.hpsc.logservice.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpsc.logservice.dao.DBConnetionPool;
import com.hp.hpsc.logservice.parser.beans.StatisticErrorBean;
import com.hp.hpsc.logservice.parser.beans.TopIPAggregateBean;

public class DAOService {

    private static Logger logger = LoggerFactory.getLogger(DAOService.class);

    /**
     * search DB and return a errorlog list according the date.
     * 
     * @param collectDate
     * @return
     */
    public List<StatisticErrorBean> retrieveErrorLogListByCollectDate(String collectDate) {
        Connection con = DBConnetionPool.getConnection();

        String sql = "select * from errorlogstatistic where reportdate= ? order by featurename";
        logger.debug("sql for retrieveErrorLogList is: {}",sql);
        PreparedStatement st = null;
        ResultSet rs = null;
        List<StatisticErrorBean> list = new ArrayList<StatisticErrorBean>();
        try {
            st = con.prepareStatement(sql);
            st.setString(1, collectDate);
            rs = st.executeQuery();
            while (rs.next()) {
                StatisticErrorBean errorLog = new StatisticErrorBean();
                Map<String, Integer> map = new HashMap<String, Integer>();
                errorLog.setFeatureName(rs.getString("featurename"));
                map.put(rs.getString("errormsg"), rs.getInt("erroramount"));
                errorLog.setErrorDetails(map);
                errorLog.setCollectDate(rs.getString("reportdate"));
                list.add(errorLog);
            }
        } catch (SQLException e) {
            logger.error("SQLException while processing ResultSet", e);
        } finally {
            try {
                st.close();
                rs.close();
            } catch (SQLException e) {
                logger.error("SQLException while closing statement or resultset");
                st = null;
                rs = null;
            }
            DBConnetionPool.returnConnectionToPool(con);
        }
        logger.debug("errorlog list is: {}", list);

        return list;
    }

    /**
     * If the error errormsg/amount map is not empty, save StatisticErrorBean into DB, else do nothing.
     * 
     * @param errorLog
     */
    public void saveErrorLogToDB(StatisticErrorBean errorLog) {

        Map<String, Integer> errorAmountMap = errorLog.getErrorDetails();
        logger.debug("errorAmountMap is: {}", errorAmountMap);
        String featureName = errorLog.getFeatureName();
        logger.debug("featureName is: {}", featureName);
        String collectionDate = errorLog.getCollectDate();
        logger.debug("errolog collectionDate is: {}", collectionDate);

        if (errorAmountMap != null && !(errorAmountMap.isEmpty())) {
            Connection con = DBConnetionPool.getConnection();
            String sql = "insert into errorlogstatistic (featurename,errormsg,erroramount,reportdate) values(?,?,?,?)";
            logger.debug("sql for saving errorlog is: {}",sql);
            PreparedStatement ps = null;

            try {
                ps = con.prepareStatement(sql);
                for (String key : errorAmountMap.keySet()) {
                    ps.setString(1, featureName);
                    ps.setString(2, key);
                    ps.setInt(3, errorAmountMap.get(key));
                    ps.setString(4, collectionDate);
                    ps.execute();
                }

            } catch (SQLException e) {
                logger.error("SQLException while saving errorlog", e);
            } finally {
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException e) {
                        logger.error("SQLException while closing prepareStatement");
                        ps = null;
                    }
                }
                DBConnetionPool.returnConnectionToPool(con);
            }

        }
    }

    /**
     * get the topip list according the collect date by query DB.
     *
     * @param reportDate
     * @return
     */
    public List<TopIPAggregateBean> getTopIpListByCollectDate(Date reportDate) {
        Connection con = DBConnetionPool.getConnection();

        String sql = "select * from topip where reportdate= ?";
        logger.debug("sql for getTopIpList is: {}",sql);
        PreparedStatement st = null;
        ResultSet rs = null;
        List<TopIPAggregateBean> topIpList = new ArrayList<TopIPAggregateBean>();
        try {
            st = con.prepareStatement(sql);
            st.setDate(1, new java.sql.Date(reportDate.getTime()));
            rs = st.executeQuery();
            while (rs.next()) {
                TopIPAggregateBean topIp = new TopIPAggregateBean();
                topIp.setCount(rs.getInt("count"));
                topIp.setDate(new java.util.Date(rs.getDate("reportdate").getTime()));
                topIp.setIp(rs.getString("ip"));
                topIp.setUserAgent(rs.getString("useragent"));
                topIpList.add(topIp);
            }
        } catch (SQLException e) {
            logger.error("SQLException while processing resultset for topip", e);
        } finally {
            try {
                st.close();
                rs.close();
            } catch (SQLException e) {
                logger.error("SQLException while closing prepareStatement or ResultSet", e);
                st = null;
                rs = null;
            }
            DBConnetionPool.returnConnectionToPool(con);
        }
        logger.debug("topip list is: {}", topIpList);
        return topIpList;
    }

    /**
     * Save the topIp bean into DB.
     *
     * @param topIp
     */
    public void saveTopIpToDB(TopIPAggregateBean topIp) {

        long count = topIp.getCount();
        logger.debug("count is: {}", count);
        Date date = topIp.getDate();
        logger.debug("date is: {}", date);
        String ip = topIp.getIp();
        logger.debug("ip is: {}", ip);
        String userAgent = topIp.getUserAgent();
        logger.debug("userAgent is {}", userAgent);

        Connection con = DBConnetionPool.getConnection();
        String sql = "insert into topip (count,ip,useragent,reportdate) values(?,?,?,?)";
        logger.debug("sql for saveTopIp is: {}",sql);
        PreparedStatement ps = null;

        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, (int)count);
            ps.setString(2, ip);
            ps.setString(3, userAgent);
            ps.setDate(4, new java.sql.Date(date.getTime()));
            ps.execute();
        } catch (SQLException e) {
            logger.error("SQLException while saving topip", e);
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                logger.error("SQLException while closing PreparedStatement for saving topip");
                ps = null;
            }
            DBConnetionPool.returnConnectionToPool(con);
        }
    }
}
