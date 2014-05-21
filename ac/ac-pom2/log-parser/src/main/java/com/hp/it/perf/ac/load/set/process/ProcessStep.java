package com.hp.it.perf.ac.load.set.process;

import com.hp.it.perf.ac.load.common.AcClosure;
import com.hp.it.perf.ac.load.common.AcKeyValue;

public interface ProcessStep extends AcClosure<AcKeyValue> {

	public void onProcessEnd();

}
