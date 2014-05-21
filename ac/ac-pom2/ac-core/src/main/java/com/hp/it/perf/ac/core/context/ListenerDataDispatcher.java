package com.hp.it.perf.ac.core.context;

import java.lang.reflect.Array;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.hp.it.perf.ac.common.core.AcDataListener;

public class ListenerDataDispatcher<T> extends BlockingAccumulativeRunner<T> {
	private ExecutorService executor;
	private final AcDataListener<T> listener;
	private final String name;
	private final T[] emptyArray;

	@SuppressWarnings("unchecked")
	public ListenerDataDispatcher(final String name, final int capacity,
			int maxBatchSize, final int threadCount,
			AcDataListener<T> listener, Class<T> dataType) {
		super(capacity, maxBatchSize);
		if (threadCount < 1) {
			throw new IllegalArgumentException("invalid thread count: "
					+ threadCount);
		}
		this.name = name;
		this.listener = listener;
		emptyArray = (T[]) Array.newInstance(dataType, 0);
		executor = Executors.newFixedThreadPool(threadCount,
				new ThreadFactory() {

					private final ThreadFactory defaultFactory = Executors
							.defaultThreadFactory();

					private final AtomicInteger seq = new AtomicInteger();

					@Override
					public Thread newThread(Runnable r) {
						Thread t = defaultFactory.newThread(r);
						int id = seq.incrementAndGet();
						String idStr;
						if (threadCount > 1) {
							idStr = "#" + id;
						} else {
							idStr = "";
						}
						t.setName("acDataDispatcher-" + name + idStr + " ["
								+ capacity + "]");
						return t;
					}
				});
	}

	public String name() {
		return name;
	}

	@Override
	protected void run(List<T> list) {
		listener.onData(list.toArray(emptyArray));
	}

	@Override
	protected boolean tryMore() {
		submit();
		return true;
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

	public void shutdownAndAwait() throws InterruptedException {
		if (!executor.isShutdown()) {
			this.executor.shutdown();
		}
		while (!this.executor.awaitTermination(1L, TimeUnit.MINUTES)) {
		}
	}

	static Random random = new Random();
	static AtomicInteger count = new AtomicInteger();

	static AtomicInteger error = new AtomicInteger(3);

	static BitSet bitSet = new BitSet();

	public static void main(String[] args) {
		ListenerDataDispatcher<Integer> dispatcher = new ListenerDataDispatcher<Integer>(
				"test", 10000, 2000, 5, new AcDataListener<Integer>() {
					@Override
					public void onData(Integer... data) {
						try {
							Thread.sleep(random.nextInt(2000) + 1);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						synchronized (bitSet) {
							for (int d : data) {
								if (bitSet.get(d)) {
									throw new IllegalStateException("duplicate: "+d);
								}
								bitSet.set(d);
							}
						}
						count.addAndGet(data.length);
						System.out.println(count);
					}
				}, Integer.class);
		int i = 0;
		while (true) {
			try {
				dispatcher.add(i++);
				 Thread.sleep(random.nextInt(1) + 1);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
