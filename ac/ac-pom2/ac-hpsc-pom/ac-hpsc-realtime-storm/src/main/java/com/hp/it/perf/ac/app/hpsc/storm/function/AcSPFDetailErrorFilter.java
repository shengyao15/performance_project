package com.hp.it.perf.ac.app.hpsc.storm.function;

import storm.trident.operation.BaseFilter;
import storm.trident.tuple.TridentTuple;

public class AcSPFDetailErrorFilter extends BaseFilter{
	private static final long serialVersionUID = 735160524666276393L;

	@Override
	public boolean isKeep(TridentTuple tuple) {
		int category = tuple.getInteger(0);
		int level = tuple.getInteger(1);
		// filter SPFPerformanceDetailLog error log
		// refer to ac-hpsc-core/src/main/resources/hpsc_category.txt
		if(category == 6 && (level == 2 || level == 3)) {
			return true;
		}
		return false;
	}

}
