package com.hp.it.perf.ac.launch;

import static com.hp.it.perf.ac.launch.AcLaunchConstants.LAUNCH_DOMAIN_NAME;

import java.io.Closeable;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(objectName = LAUNCH_DOMAIN_NAME + ":name=jmxNaming")
public class AcJmxNamingLauncher implements AcNamingLauncher, Closeable {

	private static final Logger log = LoggerFactory
			.getLogger(AcJmxNamingLauncher.class);

	private String host;

	private int port;

	private String location;

	private boolean server;

	private GenericXmlApplicationContext coreAppContext;

	private AcLaunchable parentLauncher;

	public AcJmxNamingLauncher(AcLaunchable parentLauncher) {
		this.parentLauncher = parentLauncher;
	}

	@Override
	public void launch() {
		String theHost = host;
		if (theHost == null) {
			theHost = "localhost";
		}
		String theLocation = location;
		if (location == null) {
			theLocation = "root";
		}
		String serviceUrl = "service:jmx:rmi://" + theHost + ":" + port
				+ "/jndi/rmi://" + theHost + ":" + port + "/" + theLocation;
		log.info("Target JMX Service URL is {}", serviceUrl);
		log.info("load spring configuration for jmx naming launcher");
		coreAppContext = new GenericXmlApplicationContext();
		coreAppContext.load("classpath:/spring/ac-launch-jmx.xml");
		// inject properties
		Properties prop = new Properties();
		prop.setProperty("serviceUrl", serviceUrl);
		prop.setProperty("jmxRmiPort", String.valueOf(port));
		prop.setProperty("createRegistry", String.valueOf(server));
		prop.setProperty("threaded", String.valueOf(server));
		coreAppContext.getBeanFactory().registerSingleton("prop", prop);
		coreAppContext.getBeanFactory().registerSingleton("namingLauncher",
				this);
		// perform context refresh
		coreAppContext.refresh();
		log.info("jmx naming launcher spring context is start up");
		log.info("Core server is ready on URL: '{}'", serviceUrl);
		if (server) {
			coreAppContext.registerShutdownHook();
		}
	}

	@Override
	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public void setServer(boolean server) {
		this.server = server;
	}

	@Override
	public void close() {
		log.info("Closing spring context for jmx naming launcher");
		coreAppContext.close();
		log.info("jmx naming launcher spring context is closed");
	}

	@ManagedOperation
	@ManagedOperationParameters({ @ManagedOperationParameter(name = "async", description = "async to stop naming launcher") })
	public void stop(boolean async) throws Exception {
		log.info("Request to stop spring context for jmx naming launcher");
		Runnable shutdown = new Runnable() {

			@Override
			public void run() {
				parentLauncher.close();
			}
		};
		if (async) {
			new Thread(shutdown, "AcJmxNaming Shutdown Command").start();
		} else {
			shutdown.run();
		}
	}
}
