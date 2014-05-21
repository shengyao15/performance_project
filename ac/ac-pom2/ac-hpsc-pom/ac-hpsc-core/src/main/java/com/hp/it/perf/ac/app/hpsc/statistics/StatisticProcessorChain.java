package com.hp.it.perf.ac.app.hpsc.statistics;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcContentMetadata;

public class StatisticProcessorChain implements StatisticProcessor2<Object>,
		StatisticProcessorLifecycle {

	private List<Mapping<?>> chain = new ArrayList<Mapping<?>>();

	private BitSet activedProcessors = new BitSet();

	private static class Mapping<T> {
		private StatisticProcessor<T> processor;
		private StatisticProcessor2<T> processor2;
		private Class<T> type;

		Mapping(Class<T> type, StatisticProcessor<T> processor) {
			this.type = type;
			this.processor = processor;
			if (processor instanceof StatisticProcessor2) {
				this.processor2 = (StatisticProcessor2<T>) processor;
			}
		}

		public boolean isInstance(Object bean) {
			return type.isInstance(bean);
		}

		public void onProcess(Object bean, AcContentLine currentLine,
				AcContentMetadata metadata) {
			if (processor2 != null) {
				processor2.onProcess(type.cast(bean), currentLine, metadata);
			} else {
				processor.onProcess(type.cast(bean));
			}
		}

	}

	public <T> void addProcessor(Class<T> type, StatisticProcessor<T> processor) {
		if (processor == null || processor == this) {
			throw new IllegalArgumentException("invalid processor");
		}
		chain.add(new Mapping<T>(type, processor));
	}

	@Override
	public void onProcess(Object bean, AcContentLine contentLine,
			AcContentMetadata metadata) {
		for (int i = 0, n = chain.size(); i < n; i++) {
			Mapping<?> mapping = chain.get(i);
			if (mapping.isInstance(bean)) {
				if (!activedProcessors.get(i)) {
					activedProcessors.set(i);
					if (mapping.processor instanceof StatisticProcessorLifecycle) {
						((StatisticProcessorLifecycle) mapping.processor)
								.onStart(metadata);
					}
				}
				mapping.onProcess(bean, contentLine, metadata);
			}
		}
	}

	@Override
	public void printTo(PrintStream out) {
		for (int i = activedProcessors.nextSetBit(0); i >= 0; i = activedProcessors
				.nextSetBit(i + 1)) {
			chain.get(i).processor.printTo(out);
		}
	}

	@Override
	public void onStart(AcContentMetadata metadata) {
		for (int i = activedProcessors.nextSetBit(0); i >= 0; i = activedProcessors
				.nextSetBit(i + 1)) {
			if (chain.get(i).processor instanceof StatisticProcessorLifecycle) {
				((StatisticProcessorLifecycle) chain.get(i).processor)
						.onStart(metadata);
			}
		}
	}

	@Override
	public void onEnd(AcContentMetadata metadata) {
		for (int i = activedProcessors.nextSetBit(0); i >= 0; i = activedProcessors
				.nextSetBit(i + 1)) {
			if (chain.get(i).processor instanceof StatisticProcessorLifecycle) {
				((StatisticProcessorLifecycle) chain.get(i).processor)
						.onEnd(metadata);
			}
		}
	}

	@Override
	public void onProcess(Object bean) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException();
	}

}
