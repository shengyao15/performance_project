package com.hp.it.perf.ac.common.core;

import com.hp.it.perf.ac.common.model.AcDictionary;

public class DefaultAcProfile implements AcProfile {
	private static final long serialVersionUID = -3134956342164714987L;

	private final AcDictionary dictionary;

	private final int profileId;

	public DefaultAcProfile(AcDictionary dictionary, int profileId) {
		this.dictionary = dictionary;
		this.profileId = profileId;
	}

	@Override
	public int getProfileId() {
		return profileId;
	}

	@Override
	public AcDictionary getDictionary() {
		return dictionary;
	}

}
