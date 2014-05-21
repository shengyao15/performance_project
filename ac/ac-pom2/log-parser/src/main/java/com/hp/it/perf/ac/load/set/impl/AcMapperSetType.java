package com.hp.it.perf.ac.load.set.impl;

import com.hp.it.perf.ac.load.common.AcMapper;
import com.hp.it.perf.ac.load.set.AcSetType;

public class AcMapperSetType<T> implements AcSetType {

	private AcMapper<T, ?> mapper;
	private Class<T> mappingType;

	public AcMapperSetType(AcMapper<T, ?> mapper, Class<T> mappingType) {
		this.mapper = mapper;
		this.mappingType = mappingType;
	}

	public AcMapper<T, ?> getMapper() {
		return this.mapper;
	}

	public Class<T> getMappingType() {
		return mappingType;
	}

	Object safeMap(Object key) {
		if (mappingType.isInstance(key)) {
			return this.mapper.apply(mappingType.cast(key));
		} else {
			return null;
		}
	}

}
