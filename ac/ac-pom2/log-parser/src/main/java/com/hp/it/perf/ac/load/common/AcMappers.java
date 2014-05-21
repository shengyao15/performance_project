package com.hp.it.perf.ac.load.common;

public class AcMappers {

	public static <T> AcMapper<T, String> newToStringMapper() {
		return new AcMapper<T, String>() {

			@Override
			public String apply(T object) {
				return object == null ? null : object.toString();
			}
		};
	}
}
