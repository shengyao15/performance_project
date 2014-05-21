package com.hp.it.perf.ac.core;

import com.hp.it.perf.ac.common.core.AcStatusEvent;
import com.hp.it.perf.ac.common.core.AcStatusListener;

public interface AcStatusBoard {

	public void addStatusListener(AcStatusListener listener);

	public void removeStatusListener(AcStatusListener listener);

	public void sendStatusEvent(String status, AcStatusEvent event);

}
