package com.hp.it.perf.ac.load.set.process;

import com.hp.it.perf.ac.load.common.AcKeyValue;

public class ChainedProcessStep implements ProcessStep {
	private ProcessStep nextStep;

	public void onProcessEnd() {
		nextStep.onProcessEnd();
	}

	public void setNextStep(ProcessStep nextStep) {
		this.nextStep = nextStep;
	}
	
	public ProcessStep getNextStep() {
		return nextStep;
	}

	@Override
	public void apply(AcKeyValue data) {
		if (nextStep != null) {
			nextStep.apply(data);
		}
	}
}