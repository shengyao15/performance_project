package com.hp.it.perf.ac.common.model.support;

import java.util.Iterator;

import com.hp.it.perf.ac.common.model.AcCommonException;

public interface AcRelatedFieldExtractor {

	public Iterator<?> extract(Object payload) throws AcCommonException;

}
