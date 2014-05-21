package com.hp.it.perf.ac.core.access;

import static com.hp.it.perf.ac.launch.AcLaunchConstants.LAUNCH_DOMAIN_NAME;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.hp.it.perf.ac.common.core.AcSession;
import com.hp.it.perf.ac.common.core.AcSessionToken;
import com.hp.it.perf.ac.core.AcCoreContext;
import com.hp.it.perf.ac.core.context.AcCoreContextInitializer;
import com.hp.it.perf.ac.core.context.AcCoreRuntimeManager;
import com.hp.it.perf.ac.core.service.AcServiceManager;
import com.hp.it.perf.ac.launch.AcLaunchable;

@ManagedResource(objectName = LAUNCH_DOMAIN_NAME + ":name=core")
public class AcCoreLauncher implements AcLaunchable, AcCoreLauncherMBean {

	private static final Logger log = LoggerFactory
			.getLogger(AcCoreLauncher.class);

	private GenericXmlApplicationContext coreAppContext;

	private AcCoreContextInitializer coreContextInitializer;

	private AcCoreRuntimeManager coreRuntimeManager;

	@Override
	public void launch() {
		log.info("load spring configuration for core launcher");
		coreAppContext = new GenericXmlApplicationContext();
		coreAppContext.load("classpath:/spring/ac-core.xml");
		// inject properties
		coreAppContext.getBeanFactory().registerSingleton("coreLauncher", this);
		// perform context refresh
		coreAppContext.refresh();
		log.info("core launcher spring context is start up");
		coreContextInitializer = coreAppContext
				.getBean(AcCoreContextInitializer.class);
		log.info("core context initializer is ready");
		AcServiceManager serviceManager = coreAppContext
				.getBean(AcServiceManager.class);
		log.info("loaded service list is '{}'",
				Arrays.toString(serviceManager.getServiceIdList()));
		coreRuntimeManager = coreAppContext.getBean(AcCoreRuntimeManager.class);
	}

	@Override
	public boolean isSessionExist(AcSessionToken sessionToken) {
		validateToken(sessionToken);
		return coreRuntimeManager.lookupCoreContextBySessionId(sessionToken.getSessionId()) != null;
	}

	private void validateToken(AcSessionToken sessionToken) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AcSession getSession(AcSessionToken sessionToken) {
		validateToken(sessionToken);
		AcCoreContext coreContext = getCoreContext(sessionToken.getSessionId());
		return coreContext.getSession();
	}

	@Override
	public void activeSession(AcSession session) {
		if (coreRuntimeManager.lookupCoreContextBySessionId(session.getSessionId()) == null) {
			coreContextInitializer.initialize(session);
		}
	}

	@Override
	public void deactiveSession(AcSessionToken sessionToken) {
		validateToken(sessionToken);
		AcCoreContext coreContext = getCoreContext(sessionToken.getSessionId());
		coreContext.close();
	}

	@Override
	public void close() {
	    if (coreAppContext!=null) {
    		log.info("Closing spring context for core launcher");
    		coreAppContext.close();
    		log.info("core launcher spring context is closed");
	    }
	}

	public AcCoreContext getCoreContext(int sessionId) {
		AcCoreContext coreContext = coreRuntimeManager
				.lookupCoreContextBySessionId(sessionId);
		if (coreContext != null) {
			return coreContext;
		} else {
			throw new IllegalArgumentException("session not found");
		}
	}

}
