package com.hp.it.perf.ac.core.context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.jmx.export.annotation.ManagedAttribute;

// new function based on sun.swing.AccumulativeRunnable
public abstract class BlockingAccumulativeRunner<E> implements Runnable {

	private final BlockingQueue<E> queue;
	private final int queueSize;
	private final int maxBatchSize;
	private int runInProgress = 0;
	private boolean plannedRun = false;
	private volatile boolean closed = false;
	private AtomicBoolean onClosedDone = new AtomicBoolean();
	private AtomicInteger addBlocked = new AtomicInteger();
	private AtomicInteger totalAdd = new AtomicInteger();
	private AtomicInteger totalProcessing = new AtomicInteger();
	private AtomicInteger totalProcessed = new AtomicInteger();
	private AtomicInteger totalFailed = new AtomicInteger();
	private AtomicInteger totalPassed = new AtomicInteger();

	public BlockingAccumulativeRunner(int queueSize, int maxBatchSize) {
		this.maxBatchSize = maxBatchSize;
		this.queueSize = queueSize;
		this.queue = new LinkedBlockingQueue<E>(queueSize);
	}

	protected void submit() {
		this.run();
	}

	protected abstract void run(List<E> list);

	protected void onClosed() {
	}

	@Override
	public final void run() {
		synchronized (this) {
			runInProgress++;
			plannedRun = false;
		}
		boolean normalExit = false;
		try {
			List<E> batchList;
			while (true) {
				batchList = flush();
				if (batchList == null) {
					// get closed and nothing to process
					try {
						onClosed();
					} finally {
						synchronized (onClosedDone) {
							onClosedDone.set(true);
							onClosedDone.notifyAll();
						}
					}
					break;
				} else if (batchList.isEmpty()) {
					break;
				} else {
					int dataSize = batchList.size();
					synchronized (this) {
						if (!plannedRun && !queue.isEmpty()) {
							plannedRun = tryMore();
						}
					}
					totalProcessing.addAndGet(dataSize);
					boolean success = false;
					try {
						run(batchList);
						success = true;
					} finally {
						if (success) {
							totalPassed.addAndGet(dataSize);
						} else {
							totalFailed.addAndGet(dataSize);
						}
						totalProcessed.addAndGet(dataSize);
					}
				}
			}
			normalExit = true;
		} finally {
			// in case some critical error cause data blocked without notify
			synchronized (this) {
				if (!normalExit) {
					runInProgress--;
				}
				if (!queue.isEmpty()) {
					plannedRun = false;
					retryLater();
				}
			}
		}
	}

	protected void retryLater() {
		submit();
	}

	public final void add(E e) throws InterruptedException {
		if (closed) {
			throw new IllegalStateException("closed");
		}
		if (!queue.offer(e)) {
			addBlocked.incrementAndGet();
			try {
				queue.put(e);
			} finally {
				addBlocked.decrementAndGet();
			}
		}
		totalAdd.incrementAndGet();
		synchronized (this) {
			if (!queue.isEmpty() && runInProgress == 0 && !plannedRun) {
				// queue.isEmpty() =>
				// some are consumed before go into sync block
				plannedRun = true;
				submit();
			}
		}
	}

	protected boolean tryMore() {
		return false;
	}

	protected final void close(boolean blockOnClosed)
			throws InterruptedException {
		synchronized (this) {
			closed = true;
			while (!queue.isEmpty()) {
				this.wait();
			}
		}
		if (blockOnClosed) {
			if (!onClosedDone.get()) {
				submit();
			}
			synchronized (onClosedDone) {
				if (!onClosedDone.get()) {
					onClosedDone.wait();
				}
			}
		}
	}

	private final synchronized List<E> flush() {
		List<E> list = new ArrayList<E>(Math.min(maxBatchSize, queue.size()));
		for (int i = 0; i < maxBatchSize; i++) {
			E e = queue.poll();
			if (e != null) {
				list.add(e);
			} else {
				if (closed && queue.isEmpty()) {
					this.notifyAll();
				}
				break;
			}
		}
		if (list.isEmpty()) {
			runInProgress--;
		}
		if (closed && queue.isEmpty() && list.isEmpty()) {
			// nothing found and is marked as closed
			return null;
		} else {
			return list;
		}
	}

	@ManagedAttribute
	public int getBlockedAddCount() {
		return addBlocked.get();
	}

	@ManagedAttribute
	public int getCapacity() {
		return this.queueSize;
	}

	@ManagedAttribute
	public int getRemainingCapacity() {
		return queue.remainingCapacity();
	}

	@ManagedAttribute
	public int getSize() {
		return queue.size();
	}

	@ManagedAttribute
	public int getTotoalAddCount() {
		return totalAdd.get();
	}

	@ManagedAttribute
	public int getTotalProcessingCount() {
		return totalProcessing.get();
	}

	@ManagedAttribute
	public int getTotalProcessedCount() {
		return totalProcessed.get();
	}

	@ManagedAttribute
	public int getTotalFailed() {
		return totalFailed.get();
	}

	@ManagedAttribute
	public int getTotalPassed() {
		return totalPassed.get();
	}

}