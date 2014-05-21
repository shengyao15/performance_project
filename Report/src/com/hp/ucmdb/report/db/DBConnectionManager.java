/**
 * Copyright : HP
 * project : Report
 * Create by : Steven Zhang(Ling Kai)
 * Create on : 2011-7-11
 */
package com.hp.ucmdb.report.db;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.log4j.Logger;

import com.hp.ucmdb.report.util.AllConstants;

public class DBConnectionManager {


	private static DataSource dataSource;

	private static Logger logger = Logger.getLogger(DBConnectionManager.class);

	public static void setupDataSource() {
		try {
			Properties p = new Properties();
			p.load(new FileInputStream(new File(AllConstants.PROPERTIES_FILE)));

			Properties config = new Properties();
			config.setProperty("driverClassName",
					p.getProperty(AllConstants.DB_DRIVER, ""));
			config.setProperty("url",
					p.getProperty(AllConstants.DB_CONNECION, ""));
			config.setProperty("username",
					p.getProperty(AllConstants.DB_USERNAME, ""));
			config.setProperty("password",
					p.getProperty(AllConstants.DB_PASSWORD, ""));
			config.setProperty("initialSize",
					p.getProperty(AllConstants.DB_THRD_CNT, ""));
			config.setProperty("maxAcive",
					p.getProperty(AllConstants.DB_THRD_CNT, ""));
			config.setProperty("minIdle", "1");
			config.setProperty("maxWait", "2000");

			dataSource = BasicDataSourceFactory.createDataSource(config);
		} catch (Exception e) {
			logger.error("error occurred while initate database connection.", e);
		}
	}

	public static Connection getConnection() {
		try {
			if(null==dataSource){
				setupDataSource();
			}
			return dataSource.getConnection();
		} catch (SQLException e) {
			logger.error("error occurred while get db connection.", e);
			return null;
		}
	}



}
