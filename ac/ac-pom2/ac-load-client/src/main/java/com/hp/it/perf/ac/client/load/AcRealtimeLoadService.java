package com.hp.it.perf.ac.client.load;

import java.io.IOException;

import com.hp.it.perf.ac.core.AcService;

public interface AcRealtimeLoadService extends AcService {
	public void setMonitorURL(String monitorURL);

	public String getMonitorURL();

	public void start() throws IOException;

	public void stop();

	public boolean isRunning();

	public long getLineCount();

	public long getByteCount();

	public void setStartOnActive(boolean startOnActive);

	public boolean isStartOnActive();
	
	public void setUseHub(boolean useHub);
	
	public boolean isUseHub(); 

}
