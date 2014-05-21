package com.hp.it.perf.ac.load.set;

public interface AcSetProcessHandler {

	public void onStart(AcSet set);

	public void handle(AcSetItem item, AcSet set, Object origin);

	public void onEnd(AcSet set);

}
