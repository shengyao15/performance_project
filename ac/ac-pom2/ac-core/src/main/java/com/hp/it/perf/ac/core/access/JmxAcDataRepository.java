package com.hp.it.perf.ac.core.access;

import javax.inject.Inject;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.core.AcDataRepository;

@ManagedResource
public class JmxAcDataRepository implements AcDataRepository {

	@Inject
	private AcDataRepository dataRepository;

	@ManagedOperation
	@ManagedOperationParameters({ @ManagedOperationParameter(name = "acid", description = "acid") })
	@Override
	public AcCommonData getCommonData(long acid) {
		return dataRepository.getCommonData(acid);
	}

	@ManagedOperation
	@Override
	public long count() {
		return dataRepository.count();
	}

//	@ManagedAttribute
//	@Override
//	public boolean isFullLoaded() {
//		return dataRepository.isFullLoaded();
//	}

}
