/**
 * Copyright : HP
 * project : Report
 * Create by : Steven Zhang(Ling Kai)
 * Create on : 2011-7-7
 */
package com.hp.ucmdb.report.tasks;

import java.util.Timer;
import java.util.TimerTask;

import com.hp.ucmdb.report.bean.EmailMapingMap;
import com.hp.ucmdb.report.build.BuildMail;
import com.hp.ucmdb.report.send.SendMail;
import com.hp.ucmdb.report.util.AllConstants;
import com.hp.ucmdb.report.util.FileUtils;
import com.hp.ucmdb.report.util.ReportUtil;

public class SendTask extends TimerTask {
	SendTask(int startDelay, Long internal) {
		ReportUtil.getLogger().info(
				" Start SendTask task. Get parameter:" + startDelay + " " + " " + internal);
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(this, startDelay, internal);
	}
	@Override
	public void run() {
		//send all in one mail 
		SendMail sm1 = new SendMail();
		sm1.init();
		sm1.sendMail();
		//send multi-mail one by one
		for(String dsContactEmail : EmailMapingMap.emailMapingMap.keySet()){
			ReportUtil.getLogger().info("Send mail file: "+ dsContactEmail+".html" + " to "+ dsContactEmail);
			SendMail sm2 = new SendMail();
			sm2.setSmtpHost(ReportUtil.getConfig().getString(AllConstants.SMTP_HOST));
			sm2.setMailFrom(ReportUtil.getConfig().getString(AllConstants.MAIL_FROM));
			sm2.setMailTo(dsContactEmail);
			sm2.setSubject(BuildMail.buildSubject());
			String reportFilePath = BuildMail.getReportFileAbsolutePath(dsContactEmail+".html");
	        String reportContent = FileUtils.readFileToString(reportFilePath);
			sm2.setMsgContent(reportContent);
			sm2.sendMail();
		}
		
		
	}
	
}
