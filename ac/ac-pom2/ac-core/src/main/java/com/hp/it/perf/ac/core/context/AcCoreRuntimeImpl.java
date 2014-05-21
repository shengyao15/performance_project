package com.hp.it.perf.ac.core.context;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.hp.it.perf.ac.common.core.AcSession;
import com.hp.it.perf.ac.core.AcCoreRuntime;

@ManagedResource
public class AcCoreRuntimeImpl implements AcCoreRuntime {

	private AtomicInteger size = new AtomicInteger();

	@Inject
	private AcSession session;

	// Refer to acid utils sid bits
	private static long MAX_MASK = (1L << (38 - 1)) - 1;

	private static AtomicLong CURRENT_SID = new AtomicLong(
			(System.currentTimeMillis() << 3) & MAX_MASK);

	@Override
	@ManagedOperation
	public long nextSid() {
		long sid = CURRENT_SID.incrementAndGet();
		size.incrementAndGet();
		return sid & MAX_MASK;
	}

	@Override
	@ManagedAttribute
	public int size() {
		return size.get();
	}

	@Override
	public AcSession getSession() {
		return session;
	}

	@Override
	public int getProfileId() {
		return session.getProfile().getProfileId();
	}

}
