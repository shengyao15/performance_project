/**
 * Copyright : HP
 * project : Report
 * Create by : Steven Zhang(Ling Kai)
 * Create on : 2011-7-7
 */
package com.hp.ucmdb.report.main;

import com.hp.ucmdb.report.tasks.TaskMgr;
import com.hp.ucmdb.report.util.ReportUtil;

public class Main {

private static Boolean isStart = false;
	
	public void start() {
		/*************************** Initialization ***********************************/
		ReportUtil.getLogger().info("Report onInitialization.");
		//TODO Initialization
		
		/*************************** Start Tasks *************************************/
		ReportUtil.getLogger().info("Report Start Tasks.");
		
		TaskMgr tMgr = new TaskMgr();
		tMgr.sendMail();
		
		ReportUtil.getLogger().info("Report All tasks are started.");
	}
	
	private void stop() {
		ReportUtil.getLogger().info("Report is ready to shut down.");
		System.exit(0);
	}
	
	/**
	 * Static method called by prunsrv to start/stop the service. Pass the
	 * argument "start" to start the service, and pass "stop" to stop the
	 * service.
	 */
	   
	   private static Main main=null;
	   public static void windowsService(String args[]) {
	      String cmd = "start";
	      if(args.length > 0) {
	         cmd = args[0];
	      }
	      ReportUtil.getLogger().info("Report windows Service Cmd is "+cmd);
	    	  if("start".equals(cmd)) {
	    		  if(isStart==false) {
	    			  try {
	    				  Main main = new Main();
	    				  main.start();
	    			  } catch (Exception e) {
	    				  e.printStackTrace();
	    			  }
	    			  isStart=true;
	    		  }else {
	    			  ReportUtil.getLogger().info("Report already started");
	    		  }
	    	  }
	    	  else if("stop".equals(cmd)) {
	    		  if(isStart=true) {
	    			  isStart=false;
	    			  main.stop();
	    		  }else {
	    			  ReportUtil.getLogger().info("Report already stopped");
	    		  }
	    	  }
	   }
	
	
	
	public static void main(String[] args) {
		Main.windowsService(new String[] {"start"});
	}

}
