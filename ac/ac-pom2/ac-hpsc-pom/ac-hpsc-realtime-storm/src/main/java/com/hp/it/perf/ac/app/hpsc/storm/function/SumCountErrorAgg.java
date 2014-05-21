package com.hp.it.perf.ac.app.hpsc.storm.function;

import clojure.lang.Numbers;

import com.hp.it.perf.ac.app.hpsc.storm.beans.SumCountErrorBean;

import storm.trident.operation.CombinerAggregator;
import storm.trident.tuple.TridentTuple;

public class SumCountErrorAgg implements CombinerAggregator<SumCountErrorBean> {
	private static final long serialVersionUID = -6610345074496875284L;

	@Override
	public SumCountErrorBean init(TridentTuple tuple) {
		Number score = (Number)tuple.get(0);
		boolean error = tuple.getBoolean(1);
		int scoreCount = 1;
		if(tuple.size() == 3) {
			boolean validatedScore = tuple.getBoolean(2);
			if(validatedScore) {
				scoreCount = 1;
			} else {
				scoreCount = 0;
				//score = 0.0;
			}
		}
		int errorCount = 0;
		if(error) {
			errorCount = 1;
		}
		SumCountErrorBean dataBean = new SumCountErrorBean(score, scoreCount, 1, errorCount);
		return dataBean;
	}

	@Override
	public SumCountErrorBean combine(SumCountErrorBean val1, SumCountErrorBean val2) {
		SumCountErrorBean dataBean = new SumCountErrorBean(Numbers.add(
				val1.getSum(), val2.getSum()), val1.getSumCount()
				+ val2.getSumCount(), val1.getCount() + val2.getCount(),
				val1.getErrorCount() + val2.getErrorCount());
		return dataBean;
	}


	@Override
	public SumCountErrorBean zero() {
		return new SumCountErrorBean(0, 0, 0, 0);
	}

}
