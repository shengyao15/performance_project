package com.hp.it.perf.ac.service.transfer;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.UUID;

import com.hp.it.perf.ac.common.data.AcDataBean;

@AcDataBean
public class AcSenderId implements Serializable {
	private String hostname;
	private String hostIp;
	private String processUser;
	private String processId;
	private long startupTime;
	private String osName;
	private String startupArguments;
	private Properties systemProperties;
	private String uuid;

	public static AcSenderId createId() {
		AcSenderId senderId = new AcSenderId();
		senderId.uuid = UUID.randomUUID().toString();
		InetAddress localHost;
		try {
			localHost = InetAddress.getLocalHost();
			senderId.hostname = localHost.getHostName();
			senderId.hostIp = localHost.getHostAddress();
		} catch (UnknownHostException e) {
			senderId.hostname = "Unkonwn";
			senderId.hostIp = "127.0.0.1";
		}
		senderId.processUser = System.getProperty("user.name");
		senderId.processId = ManagementFactory.getRuntimeMXBean().getName();
		senderId.startupTime = ManagementFactory.getRuntimeMXBean()
				.getStartTime();
		senderId.osName = System.getProperty("os.name");
		// TODO list to string
		senderId.startupArguments = ManagementFactory.getRuntimeMXBean()
				.getInputArguments().toString();
		senderId.systemProperties = new Properties(System.getProperties());
		return senderId;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}

	public String getProcessUser() {
		return processUser;
	}

	public void setProcessUser(String processUser) {
		this.processUser = processUser;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public long getStartupTime() {
		return startupTime;
	}

	public void setStartupTime(long startupTime) {
		this.startupTime = startupTime;
	}

	public String getOsName() {
		return osName;
	}

	public void setOsName(String osName) {
		this.osName = osName;
	}

	public String getStartupArguments() {
		return startupArguments;
	}

	public void setStartupArguments(String startupArguments) {
		this.startupArguments = startupArguments;
	}

	public Properties getSystemProperties() {
		return systemProperties;
	}

	public void setSystemProperties(Properties systemProperties) {
		this.systemProperties = systemProperties;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String toString() {
		return String.format(
				"AcSenderId [hostname=%s, hostIp=%s, processUser=%s, uuid=%s]",
				hostname, hostIp, processUser, uuid);
	}

}
