package com.hp.it.perf.ac.core.access;

import java.io.Closeable;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.support.GenericXmlApplicationContext;

import com.hp.it.perf.ac.common.core.AcSessionToken;

public class JmxAcCoreAccessProvider extends AbstractFactoryBean<AcCoreAccess>
		implements Closeable {

	private static final Logger log = LoggerFactory
			.getLogger(JmxAcCoreAccessProvider.class);

	private String host;

	private int port = 1099;

	private String location;

	private AcSessionToken sessionToken;

	private GenericXmlApplicationContext coreAppContext;
	
	private String jmxUrl;

	JMXServiceURL jmxServiceURL;

	private MBeanServerConnectionInitializer serverConnTargetSource;
	
	@Inject
	public void setSessionToken(AcSessionToken sessionToken) {
		this.sessionToken = sessionToken;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public void setJmxUrl(String jmxUrl) {
		this.jmxUrl = jmxUrl;
	}

	@PreDestroy
	public void close() {
		coreAppContext.close();
	}

	@Override
	public Class<?> getObjectType() {
		return AcCoreAccess.class;
	}

	// synchronized for connectorTargetSource
	@Override
	protected synchronized void destroyInstance(AcCoreAccess instance)
			throws Exception {
		serverConnTargetSource.destroy();
	}

	@Override
	public void setSingleton(boolean singleton) {
		// only supports singleton
		super.setSingleton(true);
	}

	@Override
	protected AcCoreAccess createInstance() throws Exception {
		String serviceUrl;
		if (jmxUrl != null) {
			serviceUrl = jmxUrl;
		} else {
			String theHost = host;
			if (theHost == null) {
				theHost = "localhost";
			}
			String theLocation = location;
			if (location == null) {
				theLocation = "root";
			}
			serviceUrl = "service:jmx:rmi:///jndi/rmi://" + theHost + ":"
					+ port + "/" + theLocation;
		}
		jmxServiceURL = new JMXServiceURL(serviceUrl);
		log.info("Target JMX Service URL is {}", jmxServiceURL);
		serverConnTargetSource = new MBeanServerConnectionInitializer(
				jmxServiceURL);
		MBeanServerConnection serverConnection = (MBeanServerConnection) new ProxyFactory(
				MBeanServerConnection.class, serverConnTargetSource).getProxy();
		log.info("load spring configuration for ac core client");
		return createAcCoreAccess(serverConnection);
	}

	private AcCoreAccess createAcCoreAccess(
			MBeanServerConnection serverConnection) {
		coreAppContext = new GenericXmlApplicationContext();
		coreAppContext.load("classpath:/spring/ac-core-client.xml");
		// inject properties
		coreAppContext.getBeanFactory().registerResolvableDependency(
				AcSessionToken.class, sessionToken);
		coreAppContext.getBeanFactory().registerResolvableDependency(
				MBeanServerConnection.class, serverConnection);
		// perform context refresh
		coreAppContext.refresh();
		log.info("ac core client spring context is start up");
		return coreAppContext.getBean(AcCoreAccess.class);
	}

}
