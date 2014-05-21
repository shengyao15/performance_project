package com.hp.it.perf.ac.core.access;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;

import com.hp.it.perf.ac.common.core.AcSession;
import com.hp.it.perf.ac.common.core.AcSessionToken;

public interface AcCoreLauncherMBean {

	@ManagedOperation
	@ManagedOperationParameters(@ManagedOperationParameter(name = "sessionToken", description = "session token"))
	public abstract boolean isSessionExist(AcSessionToken sessionToken);

	@ManagedOperation
	@ManagedOperationParameters(@ManagedOperationParameter(name = "sessionToken", description = "session token"))
	public abstract AcSession getSession(AcSessionToken sessionToken);

	@ManagedOperation
	@ManagedOperationParameters(@ManagedOperationParameter(name = "session", description = "session data"))
	public abstract void activeSession(AcSession session);

	@ManagedOperation
	@ManagedOperationParameters(@ManagedOperationParameter(name = "sessionId", description = "session id"))
	public abstract void deactiveSession(AcSessionToken sessionToken);

}