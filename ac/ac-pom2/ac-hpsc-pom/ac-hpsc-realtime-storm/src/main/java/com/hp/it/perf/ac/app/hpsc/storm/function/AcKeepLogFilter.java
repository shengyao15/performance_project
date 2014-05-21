package com.hp.it.perf.ac.app.hpsc.storm.function;

import storm.trident.operation.BaseFilter;
import storm.trident.tuple.TridentTuple;

public class AcKeepLogFilter extends BaseFilter{
	private static final long serialVersionUID = 735160524666276393L;

	@Override
	public boolean isKeep(TridentTuple tuple) {
		// filter SPFPerformanceLog, SPFPerformanceDetailLog and PortalBizLog log
		// refer to ac-hpsc-core/src/main/resources/hpsc_category.txt
		int category = tuple.getInteger(0);
		if(category == 1 || category == 2 || category == 6) {
			return true;
		}
		return false;
	}

}
