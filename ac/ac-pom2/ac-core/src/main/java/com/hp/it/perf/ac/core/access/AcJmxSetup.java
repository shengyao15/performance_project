package com.hp.it.perf.ac.core.access;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import com.hp.it.perf.ac.core.AcCoreContext;
import com.hp.it.perf.ac.core.context.AcCoreContextListener;

public class AcJmxSetup implements AcCoreContextListener {

	private static final Logger log = LoggerFactory.getLogger(AcJmxSetup.class);

	@Resource
	private ApplicationContext context;

	private GenericXmlApplicationContext accessContext;

	@Override
	public void onCoreContextActive(AcCoreContext coreContext) {
		accessContext = new GenericXmlApplicationContext();
		accessContext.setParent(context);
		accessContext.load("classpath:/spring/ac-core-access.xml");
		//
		// finish context setup
		//
		log.debug("refresh spring app context for core-access");
		accessContext.refresh();
	}

	@Override
	public void onCoreContextDeactive(AcCoreContext coreContext) {
		accessContext.close();
	}

}
