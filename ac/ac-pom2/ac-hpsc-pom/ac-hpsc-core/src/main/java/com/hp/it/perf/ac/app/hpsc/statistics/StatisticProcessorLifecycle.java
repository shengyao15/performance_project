package com.hp.it.perf.ac.app.hpsc.statistics;

import com.hp.it.perf.ac.load.content.AcContentMetadata;

interface StatisticProcessorLifecycle {

	public void onStart(AcContentMetadata metadata);

	public void onEnd(AcContentMetadata metadata);

}
