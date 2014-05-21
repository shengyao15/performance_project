package com.hp.it.perf.ac.common.model.support;

import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.common.model.AcCommonException;
import com.hp.it.perf.ac.common.model.AcDictionary;

public interface ToAcCommonDataAdapter {

	public void setDictionary(AcDictionary dictionary);

	public void toCommonData(Object payload, AcCommonData commonData)
			throws AcCommonException;

	public void setPayloadAcid(Object payload, AcCommonData commonData)
			throws AcCommonException;

}
