package com.hp.it.perf.ac.load.set.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.it.perf.ac.load.common.AcBranchSupplier;
import com.hp.it.perf.ac.load.common.AcClosure;
import com.hp.it.perf.ac.load.common.AcKeyValue;
import com.hp.it.perf.ac.load.common.AcMapper;
import com.hp.it.perf.ac.load.common.AcMultipleMapper;
import com.hp.it.perf.ac.load.common.AcPredicate;

public class ProcessSteps {

	private static abstract class MergeProcessStep extends ChainedProcessStep {
		private Map<Object, ChainedProcessStep> map = new HashMap<Object, ChainedProcessStep>();
		private List<Object> mergedList = new ArrayList<Object>();

		final protected void merge(Object merged, AcKeyValue data,
				AcBranchSupplier branchProvider) {
			ChainedProcessStep branch = map.get(merged);
			if (branch == null) {
				final AcClosure closure = branchProvider.apply(merged);
				branch = new ChainedProcessStep() {

					@Override
					public void apply(AcKeyValue data) {
						closure.apply(data);
					}

				};
				branch.setNextStep(this.getNextStep());
				map.put(merged, branch);
				mergedList.add(merged);
			}
			branch.apply(data);
		}

		@Override
		public void onProcessEnd() {
			mergedEnd();
			super.onProcessEnd();
		}

		final protected void mergedEnd() {
			for (Object merged : mergedList) {
				ProcessStep branch = map.get(merged);
				branch.onProcessEnd();
			}
		}

	}

	public static ChainedProcessStep newKeyMapStep(final AcMapper mapper) {
		return new ChainedProcessStep() {

			@Override
			public void apply(AcKeyValue data) {
				super.apply(new AcKeyValue(mapper.apply(data.getKey()), data
						.getValue()));
			}

		};
	}

	public static ChainedProcessStep newValueMultipleMapStep(
			final AcMultipleMapper mapper) {
		return new ChainedProcessStep() {

			@Override
			public void apply(AcKeyValue data) {
				Iterator<Object> mapping = (Iterator<Object>) mapper.apply(data
						.getValue());
				while (mapping.hasNext()) {
					super.apply(new AcKeyValue(data.getKey(), mapping.next()));
				}
			}
		};
	}

	public static ChainedProcessStep newValueMapStep(final AcMapper mapper) {
		return new ChainedProcessStep() {

			@Override
			public void apply(AcKeyValue data) {
				super.apply(new AcKeyValue(data.getKey(), mapper.apply(data
						.getValue())));
			}
		};
	}

	public static ChainedProcessStep newValueFilterStep(final AcPredicate filter) {
		return new ChainedProcessStep() {

			@Override
			public void apply(AcKeyValue data) {
				if (filter.apply(data.getValue())) {
					super.apply(data);
				}
			}
		};
	}

	public static ChainedProcessStep newKeyFilterStep(final AcPredicate filter) {
		return new ChainedProcessStep() {

			@Override
			public void apply(AcKeyValue data) {
				if (filter.apply(data.getKey())) {
					super.apply((data));
				}
			}

		};
	}

	public static ChainedProcessStep newKeyMergeStep(
			final AcBranchSupplier branchProvider) {
		return new MergeProcessStep() {

			@Override
			public void apply(AcKeyValue data) {
				merge(data.getKey(), data, branchProvider);
			}

		};
	}

	public static ChainedProcessStep chainSteps(ChainedProcessStep... steps) {
		ChainedProcessStep rootStep = new ChainedProcessStep();
		ChainedProcessStep pStep = rootStep;
		for (ChainedProcessStep step : steps) {
			pStep.setNextStep(step);
			pStep = step;
		}
		return rootStep;
	}
}
