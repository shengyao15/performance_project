package com.hp.it.perf.ac.load.common;

public interface AcMatcher<T> {

	public AcMatchResult match(T data);

	public static class AcMatchResult {
		public static final AcMatchResult NOT_MATCH = new AcMatchResult();
		private final boolean matched;
		private final Object result;

		public AcMatchResult(Object result) {
			this.matched = true;
			this.result = result;
		}

		private AcMatchResult() {
			this.matched = false;
			this.result = null;
		}

		public boolean isMatched() {
			return matched;
		}

		public Object getResult() {
			return result;
		}

	}

}
