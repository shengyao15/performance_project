package com.hp.it.perf.ac.app.hpsc.storm.function;

import storm.trident.operation.BaseFilter;
import storm.trident.tuple.TridentTuple;

public class AcPortalBizFilter extends BaseFilter{
	private static final long serialVersionUID = 735160524666276393L;

	@Override
	public boolean isKeep(TridentTuple tuple) {
		int category = tuple.getInteger(0);
		// filter PortalBizLog log
		// refer to ac-hpsc-core/src/main/resources/hpsc_category.txt
		if(category == 2) {
			return true;
		}
		return false;
	}

}
