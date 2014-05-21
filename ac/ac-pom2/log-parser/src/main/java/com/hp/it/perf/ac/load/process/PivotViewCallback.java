package com.hp.it.perf.ac.load.process;

import com.hp.it.perf.ac.load.common.AcClosure;

public interface PivotViewCallback extends AcClosure<Object> {

	public PivotView createView();

}
