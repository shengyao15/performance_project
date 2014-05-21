package com.hp.it.perf.ac.common.core;

import java.util.EventListener;

public interface AcStatusListener extends EventListener {

	public void onActive(AcStatusEvent event);

	public void onDeactive(AcStatusEvent event);

	public void onStatus(String status, AcStatusEvent event);

}
