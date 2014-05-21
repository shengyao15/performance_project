package com.hp.it.perf.ac.core.access;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnectionNotification;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.target.AbstractLazyCreationTargetSource;

class MBeanServerConnectionInitializer extends AbstractLazyCreationTargetSource
		implements NotificationListener, InvocationHandler {

	private static final Logger log = LoggerFactory
			.getLogger(MBeanServerConnectionInitializer.class);

	private JMXServiceURL jmxServiceURL;
	private JMXConnector jmxConnector;
	private volatile MBeanServerConnection serverConnection;
	private volatile MBeanServerConnection serverConnectionInUse;

	MBeanServerConnectionInitializer(JMXServiceURL jmxServiceURL) {
		this.jmxServiceURL = jmxServiceURL;
	}

	@Override
	protected Object createObject() throws Exception {
		// serverConnectionInUse = connect();
		return createRetryableProxy();
	}

	private synchronized MBeanServerConnection connect() throws IOException {
		jmxConnector = JMXConnectorFactory.newJMXConnector(this.jmxServiceURL,
				new HashMap<String, Object>());
		jmxConnector
				.addConnectionNotificationListener(this, null, jmxConnector);
		jmxConnector.connect();
		serverConnection = jmxConnector.getMBeanServerConnection();
		log.info("Success to connect JMX URL: {}", this.jmxServiceURL);
		return serverConnection;
	}

	@Override
	public Class<?> getTargetClass() {
		return MBeanServerConnection.class;
	}

	public void destroy() throws Exception {
		if (isInitialized()) {
			jmxConnector.close();
		}
	}

	private MBeanServerConnection createRetryableProxy() {
		return (MBeanServerConnection) Proxy.newProxyInstance(getClass()
				.getClassLoader(), new Class[] { MBeanServerConnection.class },
				this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if (method.getDeclaringClass() == Object.class) {
			// Object level class
			if (method.getName().equals("toString")) {
				return toString();
			}
		}
		return invokeInternal(proxy, method, args, true);
	}

	private Object invokeInternal(Object proxy, Method method, Object[] args,
			boolean retry) throws Throwable {
		MBeanServerConnection oCon = serverConnectionInUse;
		MBeanServerConnection connection = checkServerConnection(oCon);
		serverConnectionInUse = connection;
		boolean failRetry = retry && (oCon == connection);
		try {
			return method.invoke(connection, args);
		} catch (InvocationTargetException e) {
			if (failRetry && e.getTargetException() instanceof IOException) {
				log.debug("retry for IO failure");
				try {
					return invokeInternal(proxy, method, args, false);
				} catch (IOException ioe) {
					// ignore retry ioe
				}
			}
			throw e.getTargetException();
		}
	}

	private MBeanServerConnection checkServerConnection(
			MBeanServerConnection connection) throws IOException {
		if (connection != null && connection == serverConnection) {
			return connection;
		}
		synchronized (this) {
			try {
				log.debug("try to connect to jmx server");
				return connect();
			} catch (IOException e) {
				log.debug("check server connection error: {}" + e.getMessage());
				if (connection == null) {
					// init call
					throw e;
				} else {
					return connection;
				}
			}
		}
	}

	@Override
	public void handleNotification(Notification notification, Object handback) {
		JMXConnectionNotification jmxNotfy = (JMXConnectionNotification) notification;
		if (JMXConnectionNotification.CLOSED.equals(jmxNotfy.getType())) {
			log.info("jmx connection is closed ...");
			try {
				serverConnection = null;
				JMXConnector connector = (JMXConnector) handback;
				connector.removeConnectionNotificationListener(this);
			} catch (ListenerNotFoundException e) {
				log.debug(e.getMessage(), e);
			}
		} else if (JMXConnectionNotification.OPENED.equals(jmxNotfy.getType())) {
			log.info("jmx connection is opened ...");
		} else if (JMXConnectionNotification.FAILED.equals(jmxNotfy.getType())) {
			log.warn("jmx connection failed...");
		}
	}

}