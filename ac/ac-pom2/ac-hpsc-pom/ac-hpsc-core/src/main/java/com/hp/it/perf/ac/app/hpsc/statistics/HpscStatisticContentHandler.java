package com.hp.it.perf.ac.app.hpsc.statistics;

import java.io.PrintStream;

import com.hp.it.perf.ac.load.content.AcContentCounter;
import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcContentMetadata;
import com.hp.it.perf.ac.load.content.AcLoadException;

public class HpscStatisticContentHandler extends AcContentCounter {

	private StatisticProcessorChain processorChain = new StatisticProcessorChain();

	private boolean verbose;

	private boolean debug;

	private AcContentMetadata current;

	public <T> void addProcessor(Class<T> type, StatisticProcessor<T> processor) {
		processorChain.addProcessor(type, processor);
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	@Override
	public void init(AcContentMetadata metadata) {
		this.current = metadata;
		if (verbose || debug) {
			super.init(metadata);
		}
		processorChain.onStart(current);
	}

	@Override
	public void handle(Object object, AcContentLine contentLine)
			throws AcLoadException {
		if (verbose || debug) {
			super.handle(object, contentLine);
		}
		if (current == null) {
			Thread.dumpStack();
		}
		processorChain.onProcess(object, contentLine, current);
	}

	@Override
	public void handleLoadError(AcLoadException error, AcContentLine contentLine)
			throws AcLoadException {
		if (verbose || debug) {
			super.handleLoadError(error, contentLine);
		}
		if (debug) {
			System.err.println("==================\n"
					+ "Error on following lines (line number: "
					+ contentLine.getLineInfo().getLineNum() + ")\n"
					+ "==================\n" + contentLine.getCurrentLines());
			throw error;
		}
	}

	@Override
	public void destroy() {
		processorChain.onEnd(current);
		if (verbose || debug) {
			super.destroy();
		}
		current = null;
	}

	public void printStatistics(PrintStream out) {
		this.processorChain.printTo(out);
	}

}
