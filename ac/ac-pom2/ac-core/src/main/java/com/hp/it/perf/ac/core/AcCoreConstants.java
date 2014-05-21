package com.hp.it.perf.ac.core;

public final class AcCoreConstants {

	public static final String CORE_DOMAIN_NAME = "com.hp.it.perf.ac.core";

	static {
		// validate
		if (!AcCoreConstants.class.getPackage().getName()
				.equals(CORE_DOMAIN_NAME)) {
			throw new IllegalArgumentException("incorrect domain name: "
					+ CORE_DOMAIN_NAME);
		}
	}

	private AcCoreConstants() {
	}

}
