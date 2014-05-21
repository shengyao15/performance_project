package com.hp.hpsc.logview.retrievers;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

import com.hp.hpsc.logview.util.Configurations;
import com.hp.hpsc.logview.util.Configurations.ConfigurationException;

public class HttpConnectionManager {

	public final static int TimeOut = 120000;
	private static HttpConnectionManagerParams conparams= new HttpConnectionManagerParams();
	
	//private static HttpConnectionManager instance = null;
	
	private static MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
	
	private static HttpClient client = null;
	
	private HttpConnectionManager(){};
	
	public static HttpClient getHttpClient(){
		if(client == null){			
			synchronized(conparams){
				if(client != null){
					return client;
				}
				conparams.setMaxTotalConnections(Configurations.getConfigInt(Configurations.ConfugrationKeys.MAX_TOTAL_CONNECTIONS, MultiThreadedHttpConnectionManager.DEFAULT_MAX_TOTAL_CONNECTIONS));
				conparams.setDefaultMaxConnectionsPerHost(Configurations.getConfigInt(Configurations.ConfugrationKeys.MAX_HOST_CONNECTIONS, MultiThreadedHttpConnectionManager.DEFAULT_MAX_HOST_CONNECTIONS));
				conparams.setConnectionTimeout(Configurations.getConfigInt(Configurations.ConfugrationKeys.CONNECTIONS_TIMEOUT, TimeOut));
				connectionManager.setParams(conparams);
				client = new HttpClient(connectionManager);
				//System.out.println("total connections: "+connectionManager.getMaxTotalConnections());
				//System.out.println("time out: "+connectionManager.getParams().getConnectionTimeout());
			}
		}
		return client;
	}
		
}
