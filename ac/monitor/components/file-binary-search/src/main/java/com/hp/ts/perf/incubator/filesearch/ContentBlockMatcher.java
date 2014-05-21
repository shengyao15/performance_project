package com.hp.ts.perf.incubator.filesearch;

public interface ContentBlockMatcher {

	public enum MatchRelation {
		Less, Infimum, Inside, Supremum, Exceed
	}

	public MatchRelation match(ContentBlock block);

}
