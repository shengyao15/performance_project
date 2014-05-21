package com.hp.it.perf.ac.app.hpsc.statistics;

import java.io.PrintStream;

import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcContentMetadata;

public interface StatisticProcessor2<T> extends StatisticProcessor<T> {

	public void onProcess(T bean, AcContentLine line, AcContentMetadata metadata);

	public void printTo(PrintStream out);

}
