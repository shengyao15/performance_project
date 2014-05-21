package com.hp.it.perf.ac.app.hpsc.statistics;

import java.io.PrintStream;

public interface StatisticProcessor<T> {

	public void onProcess(T bean);

	public void printTo(PrintStream out);
	
	public String getName();

}
