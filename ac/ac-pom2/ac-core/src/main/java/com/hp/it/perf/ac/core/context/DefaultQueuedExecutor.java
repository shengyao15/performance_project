package com.hp.it.perf.ac.core.context;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.hp.it.perf.ac.core.QueuedExecutor;

class DefaultQueuedExecutor implements QueuedExecutor {

	private ExecutorService executor;

	private int queueSize;

	private BlockingQueue<Runnable> blockingQueue;

	private static final AtomicInteger threadNumber = new AtomicInteger(1);

	private AtomicInteger blockingNumber = new AtomicInteger(0);

	public DefaultQueuedExecutor(final String name, int queueSize,
			int threadCount, RejectedExecutionHandler handler) {
		if (queueSize == 0) {
			this.blockingQueue = new SynchronousQueue<Runnable>(false) {

				private static final long serialVersionUID = -7898843924545524754L;

				@Override
				public boolean offer(Runnable runnable) {
					// enable blocked for our case
					try {
						blockingNumber.incrementAndGet();
						put(runnable);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						return false;
					} finally {
						blockingNumber.decrementAndGet();
					}
					return true;
				}

			};
		} else {
			this.blockingQueue = new ArrayBlockingQueue<Runnable>(queueSize,
					false) {

				private static final long serialVersionUID = 3363958349996575005L;

				@Override
				public boolean offer(Runnable runnable) {
					// enable blocked for our case
					try {
						blockingNumber.incrementAndGet();
						put(runnable);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						return false;
					} finally {
						blockingNumber.decrementAndGet();
					}
					return true;
				}
			};
		}
		this.queueSize = queueSize;
		this.executor = new ThreadPoolExecutor(threadCount, threadCount, 0L,
				TimeUnit.MILLISECONDS, blockingQueue, new ThreadFactory() {

					@Override
					public Thread newThread(Runnable r) {
						Thread t = Executors.defaultThreadFactory()
								.newThread(r);
						t.setName("ac-queued-executor-"
								+ threadNumber.getAndIncrement() + "-" + name);
						return t;
					}
				}, handler);
	}

	@Override
	public void execute(Runnable command) {
		this.executor.execute(command);
	}

	@Override
	public int getQueueSize() {
		return queueSize;
	}

	@Override
	public int getAvaialbeSize() {
		return this.blockingQueue.remainingCapacity();
	}

	// not accurate
	@Override
	public int getBlockedThreadCount() {
		return blockingNumber.get();
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

	public static void main(String[] args) {
		final DefaultQueuedExecutor executor = new DefaultQueuedExecutor(
				"test", 5, 5, new RejectedExecutionHandler() {

					@Override
					public void rejectedExecution(Runnable r,
							ThreadPoolExecutor executor) {
						System.err.println("rejected: " + r);
					}
				});
		final AtomicInteger running = new AtomicInteger();
		new Thread(new Runnable() {

			@Override
			public void run() {
				for (;;) {
					System.out.println("available: "
							+ executor.getAvaialbeSize());
					System.out.println("blocked: "
							+ executor.getBlockedThreadCount());
					System.out.println("running: " + running.get());
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}) {
			{
				setDaemon(true);
			}
		}.start();
		for (int i = 0; i < 10; i++) {
			final int j = i;
			Runnable run = new Runnable() {
				public void run() {
					System.out.println("Prepare: " + j);
					executor.execute(new Runnable() {

						@Override
						public void run() {
							System.out.println(Thread.currentThread() + "- "
									+ j + " - Running: "
									+ running.incrementAndGet());
							try {
								Thread.sleep(10000L);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							running.decrementAndGet();
							System.out.println("End");
						}
					});
				}
			};
			if (j % 2 == 0) {
				new Thread(run).start();
			} else {
				run.run();
			}
		}
		System.out.println("shutdown now...");
		try {
			executor.shutdownAndAwait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("shutdown finished...");
	}
}
