/**
 * Copyright : HP
 * project : Report
 * Create by : Steven Zhang(Ling Kai)
 * Create on : 2011-7-7
 */
package com.hp.ucmdb.report.build;

import com.hp.ucmdb.report.util.AllConstants;
import com.hp.ucmdb.report.util.FileUtils;
import com.hp.ucmdb.report.util.ReportUtil;

public class BuildMail {
	
	public static String buildSubject(){
		return "CIS Processing Report";
	}
	
	public static String buildMsgContent(){
		ReportUtil.getLogger().info("Built Message Content Start ... ");
		//generate html
		GenerateReport generate = new GenerateReport();
		generate.generate();
		//get html to content
		String reportFilePath = getReportFileAbsolutePath(AllConstants.HTML_TMP_FILE);
        String reportContent = FileUtils.readFileToString(reportFilePath);
        ReportUtil.getLogger().info("Built Message Content End ... ");
		return reportContent;
	}
	
	public static String getReportFileAbsolutePath(String fileName) {
		String path = System.getProperty("user.dir") + AllConstants.HTML_FOLDER
		+ AllConstants.HTML_SPRIT + fileName;
		ReportUtil.getLogger().info("Report file absolute path is: " + path);
		return path;
	}
}
