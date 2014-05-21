package com.hp.it.perf.ac.load.process.support;

import com.hp.it.perf.ac.load.common.AcMapper;
import com.hp.it.perf.ac.load.common.AcPredicate;
import com.hp.it.perf.ac.load.common.AcReduceCallback;
import com.hp.it.perf.ac.load.util.Calculator;
import com.hp.it.perf.ac.load.util.StatisticsUnit;
import com.hp.it.perf.ac.load.util.StatisticsUnits;

public class AcMathAggregators {

	private abstract static class LongAggregator implements
			AcReduceCallback<Object, Long> {

		@Override
		public Object createReduceContext() {
			return new long[1];
		}

		@Override
		public Long getResult(Object context) {
			return ((long[]) context)[0];
		}

	}

	private abstract static class DoubleAggregator implements
			AcReduceCallback<Object, Double> {

		@Override
		public Object createReduceContext() {
			return new double[1];
		}

		@Override
		public Double getResult(Object context) {
			return ((double[]) context)[0];
		}

	}

	public static AcReduceCallback<Object, Long> count() {
		return count(new AcPredicate<Object>() {

			@Override
			public boolean apply(Object data) {
				return true;
			}
		});
	}

	public static AcReduceCallback<Object, Long> count(
			final AcPredicate<Object> filter) {
		return new LongAggregator() {

			public void reduce(Object item, Object context) {
				if (filter.apply(item)) {
					((long[]) context)[0]++;
				}
			}

		};
	}

	public static AcReduceCallback<Object, Long> longSum(
			final AcMapper<Object, Number> mapper) {
		return new LongAggregator() {

			public void reduce(Object item, Object context) {
				((long[]) context)[0] += mapper.apply(item).longValue();
			}

		};
	}

	public static AcReduceCallback<Object, Long> longSum() {
		return longSum(new AcMapper<Object, Number>() {

			@Override
			public Number apply(Object object) {
				return (Number) object;
			}
		});

	}

	public static AcReduceCallback<Object, Double> doubleSum(
			final AcMapper<Object, Number> mapper) {
		return new DoubleAggregator() {

			public void reduce(Object item, Object context) {
				((double[]) context)[0] += mapper.apply(item).doubleValue();
			}

		};
	}

	public static AcReduceCallback<Object, Double> doubleSum() {
		return doubleSum(new AcMapper<Object, Number>() {

			@Override
			public Number apply(Object object) {
				return (Number) object;
			}
		});

	}

	public static AcReduceCallback<Object, Calculator> longCalculate(
			final AcMapper<Object, Number> mapper) {
		return new AcReduceCallback<Object, Calculator>() {

			@Override
			public Object createReduceContext() {
				return StatisticsUnits.newLongStatisticsUnit();
			}

			@Override
			public void reduce(Object item, Object context) {
				StatisticsUnit unit = (StatisticsUnit) context;
				unit.add();
				unit.setLong(mapper.apply(item).longValue());
			}

			@Override
			public Calculator getResult(Object context) {
				return Calculator.build(((StatisticsUnit) context)
						.toLongArray());
			}
		};

	}

	public static AcReduceCallback<Object, Calculator> longCalculate() {
		return longCalculate(new AcMapper<Object, Number>() {

			@Override
			public Number apply(Object object) {
				return (Number) object;
			}
		});

	}

}
