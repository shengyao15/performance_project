package com.hp.it.perf.ac.app.hpsc.storm.function;

import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

/**
 * This class convert the level to status
 * 
 * @author Qiu, Li-hong
 *
 */
public class ConvertLevelToErrorStatus extends BaseFunction{
	private static final long serialVersionUID = 2661018399958525879L;

	@Override
	public void execute(TridentTuple tuple,
			TridentCollector collector) {
		int category = tuple.getInteger(0);
		int level = tuple.getInteger(1);
		boolean erorrStatus = false;
		// refer to ac-hpsc-core/src/main/resources/hpsc_category.txt
		if(category == 2) {
			erorrStatus = (level == 5);
		} else if (category == 6) {
			erorrStatus = (level == 2 || level == 3);
		}
        collector.emit(new Values(erorrStatus));
	}

}
