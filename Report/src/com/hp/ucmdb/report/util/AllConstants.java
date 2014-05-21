/**
 * Copyright : HP
 * project : Report
 * Create by : Steven Zhang(Ling Kai)
 * Create on : 2011-7-7
 */
package com.hp.ucmdb.report.util;

public class AllConstants {
	public static final String PROPERTIES_FILE = "conf/Report.properties";
	public static final String RECORD_STATUS_FILE = "conf/RecordStatus.properties";
	
	/*************************************** TIMER **********************************************************/
	public static final String START_DELAY = "timer_Start_Delay";
	public static final String SEND_DAYS = "send_Days";
	public static final String SEND_MILLISECONDS_A_DAY = "send_Milliseconds_A_Day";
	/*************************************** Mail **********************************************************/
	public static final String SMTP_HOST = "smtp_Host";
	public static final String MAIL_FROM = "mail_From";
	public static final String MAIL_TO = "mail_To";
	public static final String MAIL_CC_TO = "mail_cc_To";
	public static final String MAIL_BCC_TO = "mail_bcc_To";
	/*************************************** Html **********************************************************/
	public static final String HTML_FOLDER = "/html";
	public static final String HTML_TMP_FILE = "all.html";
	public static final String HTML_SPRIT = "/";
	/*************************************** DB Related Constants ******************************************/
	public static final String DB_DRIVER = "driverURL";
	public static final String DB_CONNECION = "connectionURL";
	public static final String DB_USERNAME = "dbUserName";
	public static final String DB_PASSWORD = "dbPassword";
	public static final String DB_THRD_CNT = "dbThrdCount";
	/*************************************** Punctuation ******************************************/
	public static final String LEFT_BRACKET = "(";
	public static final String RIGHT_BRACKET = ")";

}
