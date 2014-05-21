package com.hp.it.perf.ac.service.dispatch.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.springframework.jmx.export.annotation.ManagedResource;

import com.hp.it.perf.ac.core.context.BlockingAccumulativeRunner;
import com.hp.it.perf.ac.core.context.ListenerDataDispatcher;

@ManagedResource
class ContextDataDispatcher<T> extends BlockingAccumulativeRunner<T> {
	private ExecutorService executor;
	private List<ListenerDataDispatcher<T>> downstreams = new ArrayList<ListenerDataDispatcher<T>>();

	public ContextDataDispatcher(final int capacity) {
		super(capacity, 1);
		executor = Executors.newSingleThreadExecutor(new ThreadFactory() {

			private final ThreadFactory defaultFactory = Executors
					.defaultThreadFactory();

			@Override
			public Thread newThread(Runnable r) {
				Thread t = defaultFactory.newThread(r);
				t.setName("acContextDataDispatcher [" + capacity + "]");
				return t;
			}
		});
	}

	void addDownstreamDispatcher(ListenerDataDispatcher<T> downstream) {
		downstreams.add(downstream);
	}

	@Override
	protected void run(List<T> list) {
		for (T e : list) {
			for (ListenerDataDispatcher<T> downstream : downstreams) {
				try {
					downstream.add(e);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					return;
				}
			}
		}
	}

	@Override
	protected void onClosed() {
		for (ListenerDataDispatcher<T> downstream : downstreams) {
			try {
				downstream.closeDispatch();
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				break;
			}
		}
	}

	@Override
	protected void submit() {
		executor.execute(this);
	}

	public void closeDispatch() throws InterruptedException {
		close(true);
	}

	public void shutdown() {
		if (!executor.isShutdown()) {
			this.executor.shutdown();
		}
	}

	@PreDestroy
	public void destory() {
		this.shutdown();
		for (ListenerDataDispatcher<T> downstream : downstreams) {
			downstream.shutdown();
		}
	}

	public void shutdownAndAwait() throws InterruptedException {
		if (!executor.isShutdown()) {
			this.executor.shutdown();
		}
		while (!this.executor.awaitTermination(1L, TimeUnit.MINUTES)) {
		}
	}
}
