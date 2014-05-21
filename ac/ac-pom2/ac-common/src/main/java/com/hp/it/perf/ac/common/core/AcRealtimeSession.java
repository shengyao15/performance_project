package com.hp.it.perf.ac.common.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class AcRealtimeSession implements AcSession {

	private static final long serialVersionUID = 1075334042051050342L;

	private final AcProfile profile;

	private int sessionId;

	private List<String> services;

	private Map<String, Properties> serviceProperties = new HashMap<String, Properties>();

	private Properties properties = new Properties();

	public AcRealtimeSession(AcProfile profile, Properties sessionProperties) {
		this.profile = profile;
		properties.putAll(sessionProperties);
		properties.put(SESSION_TYPE, SESSION_TYPE_REALTIME);
	}

	@Override
	public AcProfile getProfile() {
		return profile;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public int getSessionId() {
		return sessionId;
	}

	@Override
	public List<String> getServices() {
		return services;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}

	public Properties getServiceProperties(String serviceId) {
		return serviceProperties.get(serviceId);
	}

	public void setServiceProperties(String serviceId, Properties prop) {
		serviceProperties.put(serviceId, prop);
	}

	@Override
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

}
