package com.hp.it.perf.ac.core.access;

import static com.hp.it.perf.ac.launch.AcLaunchConstants.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.it.perf.ac.common.core.AcSession;
import com.hp.it.perf.ac.common.core.AcSessionToken;
import com.hp.it.perf.ac.core.AcDataRepository;
import com.hp.it.perf.ac.core.AcService;
import com.hp.it.perf.ac.core.service.AcServiceException;

public class JmxAcCoreConnection implements AcCoreAccess {

	private static final Logger log = LoggerFactory
			.getLogger(JmxAcCoreConnection.class);

	private MBeanServerConnection mbeanServer;

	// service class name <-> service proxy
	private final Map<String, AcService> proxyServices = new HashMap<String, AcService>();

	private AcCoreAccess proxyConnection;

	private AcDataRepository proxyRepository;

	private AcSession session;
	
	private AcSessionToken sessionToken;

	private boolean isInitial;
	
	private int sessionId;

	@Inject
	public void setMbeanServer(MBeanServerConnection mbeanServer) {
		this.mbeanServer = mbeanServer;
	}

	public void setSession(AcSession session) {
		this.session = session;
	}
	
	@Inject
	public void setSessionToken(AcSessionToken sessionToken) {
		this.sessionToken = sessionToken;
	}

	// TODO look up from session manager
	protected void checkConnect() throws AcAccessException {
		if (isInitial) {
			return;
		}
		try {
			AcCoreLauncherMBean coreLauncher = MBeanServerInvocationHandler
					.newProxyInstance(mbeanServer, new ObjectName(
							LAUNCH_DOMAIN_NAME + ":name=core"),
							AcCoreLauncherMBean.class, false);
			if (session != null) {
				// FIXME: how to active session
				// coreLauncher.activeSession(session);
				sessionId = session.getSessionId();
			} else {
				sessionId = coreLauncher.getSession(sessionToken).getSessionId();
			}
			this.proxyConnection = MBeanServerInvocationHandler.newProxyInstance(
					mbeanServer,
					findObjectNameByType(sessionId, AcCoreAccess.class, null),
					AcCoreAccess.class, false);
			this.proxyRepository = MBeanServerInvocationHandler
					.newProxyInstance(
							mbeanServer,
							findObjectNameByType(sessionId,
									AcDataRepository.class, null),
							AcDataRepository.class, false);
		} catch (JMException e) {
			throw new AcAccessException(e);
		} catch (IOException e) {
			throw new AcAccessException(e);
		}
		isInitial = true;
	}

	private ObjectName findObjectNameByType(int sessionId, Class<?> clazz,
			String name) throws AcAccessException, JMException, IOException {
		if (name == null)
			name = clazz.getSimpleName();
		Set<ObjectName> set;
		String query = JmxAcCoreConnection.class.getPackage().getName()
				+ ":instance=session#" + sessionId + ",name=" + name + ",*";
		log.debug("try to find object name with: {}", query);
		set = mbeanServer.queryNames(ObjectName.getInstance(query),
				Query.isInstanceOf(Query.value(clazz.getName())));
		if (set.size() != 1) {
			throw new AcAccessException("object name set: " + set);
		}
		return set.iterator().next();
	}

	private ObjectName findServiceObjectNameByType(int sessionId,
			Class<?> clazz, String serviceId) throws AcServiceException,
			JMException, IOException {
		Set<ObjectName> set;
		String query = JmxAcCoreConnection.class.getPackage().getName()
				+ ":instance=session#" + sessionId + ",serviceId=" + serviceId
				+ ",*";
		log.debug("try to find object name with: {}", query);
		set = mbeanServer.queryNames(ObjectName.getInstance(query),
				Query.isInstanceOf(Query.value(clazz.getName())));
		if (set.size() != 1) {
			throw new AcServiceException("object name set: " + set);
		}
		return set.iterator().next();
	}

	@Override
	public AcDataRepository getDataRepository() {
		checkConnect();
		return proxyRepository;
	}

	@Override
	public <T extends AcService> T getService(final Class<T> serviceClass)
			throws AcServiceException {
		String serviceClassname = serviceClass.getName();
		AcService acService = proxyServices.get(serviceClassname);
		if (acService == null) {
			try {
				String serviceId = findServiceId(serviceClass);
				acService = MBeanServerInvocationHandler.newProxyInstance(
						mbeanServer,
						findServiceObjectNameByType(sessionId,
								serviceClass, serviceId), serviceClass, false);
				proxyServices.put(serviceClassname, acService);
			} catch (JMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return serviceClass.cast(acService);
	}

	private String findServiceId(Class<?> serviceClass) {
		for (String serviceId : getLoadedServiceIds()) {
			if (getServiceClassNameById(serviceId).equals(
					serviceClass.getName())) {
				return serviceId;
			}
		}
		throw new IllegalArgumentException(
				"service id not found for service class: "
						+ serviceClass.getName());
	}

	@Override
	public String getServiceClassNameById(String serviceId)
			throws AcServiceException {
		return getProxyConnection().getServiceClassNameById(serviceId);
	}

	@Override
	public String[] getLoadedServiceIds() {
		return getProxyConnection().getLoadedServiceIds();
	}

	@Override
	public AcSession getSession() {
		return getProxyConnection().getSession();
	}

	private AcCoreAccess getProxyConnection() {
		checkConnect();
		return proxyConnection;
	}

}
