package com.hp.it.perf.ac.app.hpsc.storm.trident;

import java.util.concurrent.ScheduledExecutorService;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;

import com.hp.it.perf.ac.app.hpsc.storm.spout.AcCommonDataReceiverSpout;
import com.hp.it.perf.ac.app.hpsc.storm.util.Consts;
import com.hp.it.perf.ac.app.hpsc.storm.util.MemoryDBState;

public class StormMain {
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Config conf = new Config();
        conf.setMaxSpoutPending(5);
        conf.setDebug(false);
        AcCommonDataReceiverSpout spout = new AcCommonDataReceiverSpout(Consts.getJMXUrl());
        if(args == null || args.length==0) {
        	conf.put(Config.STORM_LOCAL_DIR, "storm-local");
        	conf.setNumWorkers(1);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("acData", conf, AcCommonDataTrident.buildTopology(spout));
			  
            try {
				Thread.sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				ScheduledExecutorService threadPool = MemoryDBState.getThreadPool();
				if(threadPool != null) {
					threadPool.shutdownNow();
				}
				cluster.killTopology("acData");
				cluster.shutdown();
			}
            
        } else {
            conf.setNumWorkers(1);
            try {
				StormSubmitter.submitTopology(args[0], conf, AcCommonDataTrident.buildTopology(spout));
			} catch (Exception e) {
				e.printStackTrace();
			} 
			
        }
	}

}
