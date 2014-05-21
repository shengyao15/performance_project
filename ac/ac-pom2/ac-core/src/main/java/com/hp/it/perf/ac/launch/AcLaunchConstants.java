package com.hp.it.perf.ac.launch;

public final class AcLaunchConstants {

	public static final String LAUNCH_DOMAIN_NAME = "com.hp.it.perf.ac.launch";

	static {
		// validate
		if (!AcLaunchConstants.class.getPackage().getName()
				.equals(LAUNCH_DOMAIN_NAME)) {
			throw new IllegalArgumentException("incorrect domain name: "
					+ LAUNCH_DOMAIN_NAME);
		}
	}

	private AcLaunchConstants() {
	}

}
