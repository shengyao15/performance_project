package com.hp.it.perf.ac.load.common;

public class AcMatchers {

	public static AcMatcher<String> newStringMatcher(final String text) {
		return new AcMatcher<String>() {

			@Override
			public AcMatchResult match(String data) {
				if (data != null && data.contains(text)) {
					return new AcMatchResult(text);
				} else {
					return AcMatchResult.NOT_MATCH;
				}
			}
		};
	}

	public static <T> AcMatcher<T> newStringMatcher(final String text,
			final AcMapper<T, String> mapper) {
		return new AcMatcher<T>() {

			@Override
			public AcMatchResult match(T data) {
				String str = mapper.apply(data);
				if (str != null && str.contains(text)) {
					return new AcMatchResult(text);
				} else {
					return AcMatchResult.NOT_MATCH;
				}
			}
		};
	}
}
