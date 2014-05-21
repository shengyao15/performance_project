package com.hp.it.perf.ac.app.hpsc.storm.function;

import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;



/**
 * This class add a field of score based on duration
 * 
 * @author Qiu, Li-hong
 * 
 */
public class ConvertDurationToScore extends BaseFunction{
	private static final long serialVersionUID = 2661018399958525879L;

	@Override
	public void execute(TridentTuple tuple,
			TridentCollector collector) {
		int duration = tuple.getInteger(0);
		boolean hasNoWSRP = tuple.getBoolean(1);
		double score = 0.0;
		boolean validatedScore = !hasNoWSRP;
		if(validatedScore || (hasNoWSRP && duration >= 1000)) {
			validatedScore = true;
			if(duration <= 1000) {
				score = 100.0;
			} else if(duration <= 6000){
				score = (3000.0 - duration) / 1000 * 20 + 60;
			} else {
				score = 0.0;
			}
		}
        collector.emit(new Values(score, validatedScore));
	}

}
