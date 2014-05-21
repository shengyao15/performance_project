package com.hp.it.perf.ac.core;

import com.hp.it.perf.ac.common.core.AcSession;

public interface AcCoreRuntime {

	public long nextSid();

	public int size();

	public AcSession getSession();
	
	public int getProfileId();

}
