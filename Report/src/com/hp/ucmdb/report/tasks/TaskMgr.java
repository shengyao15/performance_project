/**
 * Copyright : HP
 * project : Report
 * Create by : Steven Zhang(Ling Kai)
 * Create on : 2011-7-7
 */
package com.hp.ucmdb.report.tasks;

import com.hp.ucmdb.report.util.AllConstants;
import com.hp.ucmdb.report.util.ReportUtil;

public class TaskMgr {

	
	public void sendMail() {
		new SendTask(ReportUtil.getConfig().getInt(AllConstants.START_DELAY),
				ReportUtil.getConfig().getLong(AllConstants.SEND_DAYS)
						* ReportUtil.getConfig().getLong(
								AllConstants.SEND_MILLISECONDS_A_DAY));

	}
}
