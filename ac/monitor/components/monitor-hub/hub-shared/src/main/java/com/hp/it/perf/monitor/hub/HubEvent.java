package com.hp.it.perf.monitor.hub;

import java.util.EventObject;

public class HubEvent extends EventObject {

	private static final long serialVersionUID = 3900149530958960293L;

	public enum HubStatus {
		Connected, Disconnected, DataLost, EndpointBroadcast
	}

	private HubStatus status;
	private MonitorEndpoint endpoint;
	private Object data;

	public HubEvent(MonitorHub source, HubStatus status,
			MonitorEndpoint endpoint, Object data) {
		super(source);
		this.status = status;
		this.data = data;
	}

	public MonitorHub getHub() {
		return (MonitorHub) getSource();
	}

	public HubStatus getStatus() {
		return status;
	}

	public Object getData() {
		return data;
	}

	public MonitorEndpoint getEndpoint() {
		return endpoint;
	}

}
