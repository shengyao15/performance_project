package com.hp.it.perf.ac.core.service;

import com.hp.it.perf.ac.core.AcCoreContext;
import com.hp.it.perf.ac.core.AcDispatchRegistry;
import com.hp.it.perf.ac.core.AcPreferences;
import com.hp.it.perf.ac.core.AcStatusListenerRegistry;

public interface AcServiceConfig {

	public String SERVICE_CONFIG_BEAN_NAME = "acServiceConfig";
	public String CORE_CONTEXT_BEAN_NAME = "acCoreContext";
	public String SESSION_BEAN_NAME = "acSession";
	public String PROFILE_BEAN_NAME = "acProfile";
	public String REPOSITORY_BEAN_NAME = "acDataRepository";
	public String PREFERENCES_BEAN_NAME = "acPreferences";
	public String DICTIONARY_BEAN_NAME = "acDictionary";

	// Preferences key and default property key
	public String[] getKeys();

	// Try to get preferences value first, then default property
	public String getProperty(String key);
	
	public AcCoreContext getCoreContext();

	public AcStatusListenerRegistry getStatusListenerRegistry();

	public AcDispatchRegistry getDispatchRegistry();

	public AcPreferences getServicePreferences();
}
