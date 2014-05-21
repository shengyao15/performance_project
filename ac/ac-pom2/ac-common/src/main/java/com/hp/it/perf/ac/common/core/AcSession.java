package com.hp.it.perf.ac.common.core;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

public interface AcSession extends AcSessionConstants, Serializable {

	// global session id cross profiles
	public int getSessionId();

	public AcProfile getProfile();

	public List<String> getServices();

	public Properties getServiceProperties(String serviceId);

	public String getProperty(String key);

}
