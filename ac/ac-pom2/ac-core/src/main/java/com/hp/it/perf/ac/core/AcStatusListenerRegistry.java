package com.hp.it.perf.ac.core;

import com.hp.it.perf.ac.common.core.AcStatusListener;

public interface AcStatusListenerRegistry {

	public void attachStatusListener(AcStatusListener listener);

	public AcStatusListener[] getStatusListeners();

	public void processStatusListener(Object obj);

}
