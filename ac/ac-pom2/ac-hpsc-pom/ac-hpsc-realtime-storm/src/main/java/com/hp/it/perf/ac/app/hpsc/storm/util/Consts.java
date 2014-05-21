package com.hp.it.perf.ac.app.hpsc.storm.util;

import java.util.concurrent.TimeUnit;

public class Consts {
	
	static {
		JMX_URL = System.getProperty("jmxURL", "service:jmx:rmi://localhost:11090/jndi/rmi://localhost:11090/root");
	}
	
	// refer to ac-hpsc-core/src/main/resources/hpsc_category.txt
	public final static int REQUEST_CATEGORY = 1;
	public final static int BIZ_PORTALET_CATEGORY = 2;
	public final static int WSRP_CATEGORY = 6;
	
	// refer to com.hp.it.perf.ac.app.hpsc.HpscDictionary.HpscContextType
	public final static int CONTEXT_ERROR_CODE = 7;
	public final static int CONTEXT_NOWSRP_CODE = 8;
	
	public static final long TURN_OFF_MILLISECCONDS = TimeUnit.MILLISECONDS.convert(2, TimeUnit.MINUTES);
	
	private static String JMX_URL;
	
	public static String getJMXUrl() {
		return JMX_URL;
	}
	
	public static final String OBJECT_NAME = "com.hp.it.perf.ac.app.hpsc.realtime:type=RealtimeDataProxy,sessionId=1";
	
	public final static long JMX_RECONNECT_INCREASE_MILLSECONDS = 60000L;
	
	public final static long JMX_RECONNECT_MAX_INTERVAL_MILLSECONDS = 3600000L;
	
	public static final int MAX_CACHE_SIZE = 100000;
	
}
