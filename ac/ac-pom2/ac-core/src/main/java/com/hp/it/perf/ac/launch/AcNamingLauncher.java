package com.hp.it.perf.ac.launch;


public interface AcNamingLauncher extends AcLaunchable {

	public void setHost(String host);

	public void setPort(int port);

	public void setLocation(String location);

	public void setServer(boolean server);

}
