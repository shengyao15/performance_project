package com.hp.it.perf.ac.common.core;

import java.io.Serializable;

import com.hp.it.perf.ac.common.model.AcDictionary;

public interface AcProfile extends Serializable {

	public int getProfileId();

	public AcDictionary getDictionary();

}
