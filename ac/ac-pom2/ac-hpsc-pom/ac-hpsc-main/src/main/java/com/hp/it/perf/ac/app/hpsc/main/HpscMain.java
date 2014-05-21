package com.hp.it.perf.ac.app.hpsc.main;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.hp.it.perf.ac.app.hpsc.HpscDictionary;
import com.hp.it.perf.ac.common.core.AcProfile;
import com.hp.it.perf.ac.common.core.AcRealtimeSession;
import com.hp.it.perf.ac.common.core.AcSessionConstants;
import com.hp.it.perf.ac.common.core.DefaultAcProfile;
import com.hp.it.perf.ac.core.AcCoreContext;
import com.hp.it.perf.ac.core.AcService;
import com.hp.it.perf.ac.core.access.AcCoreLauncher;
import com.hp.it.perf.ac.launch.AcLauncherMain;

public class HpscMain {

	private AcCoreContext acCoreContext;
	private AcLauncherMain laucherMain;

	private static final int PROFILE_ID = Integer.getInteger(
			"ac.hpsc.profileId", 1);
	private static final int LIVE_SESSION = Integer.getInteger(
			"ac.hpsc.sessionId", 1);;

	@PostConstruct
	public void init() throws Exception {
		laucherMain = new AcLauncherMain();
		boolean success = false;
		try {
			setupCore();
			System.out.println("Session is ready: "
					+ acCoreContext.getSession());
			success = true;
			Runtime.getRuntime().addShutdownHook(new Thread(){
				public void run() {
					try {
						HpscMain.this.destroy();
					} catch (Exception e) {
						System.err.println("shutdown get error: " + e);
					}
				}
			});
		} finally {
			if (!success) {
				destroy();
			}
		}
	}

	@PreDestroy
	public void destroy() throws Exception {
		AcLauncherMain main;
		if ((main = laucherMain) != null) {
			laucherMain = null;
			main.close();
		}
	}

	private void setupCore() throws Exception {
		laucherMain.setLaunchables(Collections
				.singletonList(AcCoreLauncher.class));
		String hostName = InetAddress.getLocalHost().getHostName();
		laucherMain.setHost(hostName);
		laucherMain.setPort(Integer.getInteger("ac.hpsc.laucher.port", 11090));
		laucherMain.launch();
		AcCoreLauncher coreLauncher = laucherMain
				.getLaunchable(AcCoreLauncher.class);
		AcProfile acProfile = new DefaultAcProfile(HpscDictionary.INSTANCE,
				PROFILE_ID);
		Properties sessionProp = new Properties();
		sessionProp.setProperty(AcSessionConstants.PREFERENCES_LOCATION, System
				.getProperty("ac.hpsc.preference",
						"src/main/data/test_ac_preferences.yaml"));
		AcRealtimeSession acSession = new AcRealtimeSession(acProfile,
				sessionProp);
		acSession.setSessionId(LIVE_SESSION);
		acSession.setServices(Arrays.asList("transfer", "persist", "transform",
				"dispatch", "chain", "spfchain", "hpscSearch", "loadclient",
				"hpscRealtime"));
		coreLauncher.activeSession(acSession);
		acCoreContext = coreLauncher.getCoreContext(acSession.getSessionId());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		HpscMain hpscAc = new HpscMain();
		System.out.println("init");
		try {
			hpscAc.init();
			System.out.println("done");
			hpscAc.onReady(args);
		} finally {
			System.out.println("destroy");
			hpscAc.destroy();
		}
	}

	public void onReady(String[] args) throws Exception {
		Thread.sleep(Long.MAX_VALUE);
	}
	
	public <T extends AcService> T getService(Class<T> serviceClass) {
		return acCoreContext.getService(serviceClass);
	}

}
