package com.hp.it.perf.ac.core.hub;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.it.perf.ac.common.base.AcPredicate;
import com.hp.it.perf.ac.common.core.AcDataHub;
import com.hp.it.perf.ac.common.core.AcDataHubEndpoint;
import com.hp.it.perf.ac.common.core.AcDataListener;
import com.hp.it.perf.ac.common.core.AcStatusListener;

public class SimpleAcDataHubImpl<T> implements AcDataHub<T>, AcDataListener<T> {

	private Class<T> dataType;
	private int capacity;
	private T[] dataQueue;
	private long[] timeQueue;
	private int head;
	private int tail;
	private volatile long earlistSeq;
	private volatile long nextSeq;
	private Lock lock = new ReentrantLock();
	private Condition nodataCondition = lock.newCondition();
	private ExecutorService executor;

	private final static Logger log = LoggerFactory
			.getLogger(SimpleAcDataHubImpl.class);

	@SuppressWarnings("unchecked")
	public SimpleAcDataHubImpl(Class<T> dataType, int queueSize,
			ExecutorService executor) {
		this.dataType = dataType;
		this.capacity = queueSize + 1;
		this.dataQueue = (T[]) Array.newInstance(dataType, queueSize + 1);
		this.timeQueue = new long[queueSize + 1];
		this.head = 0;
		this.tail = 0;
		this.earlistSeq = 0;
		this.nextSeq = 0;
		this.executor = executor;
	}

	private class DataHubEndpoint implements AcDataHubEndpoint<T>, Runnable {

		private final AcDataListener<T> listener;
		private final int maxBatchSize;
		private final long maxWaitTime;
		private long clientSeq = 0;
		private long totalLost = 0;
		private volatile long totalProcess = 0;
		// dummy future for check in run method
		private volatile Future<?> future = new FutureTask<Object>(this, null);
		private String endpointName;
		private volatile transient Thread thread;

		DataHubEndpoint(AcDataListener<T> listener, int maxBatchSize,
				int maxWaitTime) {
			this.listener = listener;
			this.maxBatchSize = maxBatchSize;
			this.maxWaitTime = maxWaitTime;
			this.future = executor.submit(this);
		}

		@Override
		public AcDataListener<T> getDataListener() {
			return listener;
		}

		@Override
		public Class<T> getDataType() {
			return dataType;
		}

		@Override
		public long getReceived() {
			return nextSeq;
		}

		@Override
		public long getUnprocessed() {
			lock.lock();
			try {
				return nextSeq - Math.max(clientSeq, earlistSeq);
			} finally {
				lock.unlock();
			}
		}

		@Override
		public long getTotalLosted() {
			lock.lock();
			try {
				return totalLost + Math.max(earlistSeq - clientSeq, 0);
			} finally {
				lock.unlock();
			}
		}

		@Override
		public long getProcessed() {
			return totalProcess;
		}

		@Override
		public Date getEarliestOn() {
			long date = getTimeAt(earlistSeq);
			if (date == 0) {
				return null;
			} else {
				return new Date(date);
			}
		}

		@Override
		public Date getLatestOn() {
			long date = getTimeAt(nextSeq - 1);
			if (date == 0) {
				return null;
			} else {
				return new Date(date);
			}
		}

		@Override
		public boolean isClosed() {
			return future == null || future.isDone();
		}

		@Override
		public void close() {
			Future<?> state = future;
			if (state != null) {
				future = null;
				log.info("close hub endpoint");
				if (!state.isDone()) {
					state.cancel(true);
				}
			}
		}

		@Override
		public int getMaxBatchSize() {
			return maxBatchSize;
		}

		@Override
		public void run() {
			log.info("start hub endpoint");
			thread = Thread.currentThread();
			try {
				setThreadName();
				while (future != null) {
					try {
						T[] data = fetchNextBatch();
						if (future == null || Thread.interrupted()) {
							// closed
							break;
						}
						listener.onData(data);
						totalProcess += data.length;
					} catch (InterruptedException e) {
						// interrupted
						future = null;
						log.info("hub endpoint is interrupted, exit hub");
					} catch (Exception e) {
						log.error("hub endpoint process got exception", e);
					} catch (Error e) {
						log.error("hub endpoint process got ERROR", e);
					}
				}
			} catch (Throwable t) {
				log.error("stop hub endpoint caused by", t);
			} finally {
				thread = null;
				log.info("end hub endpoint");
			}
		}

		private void setThreadName() {
			Thread t = thread;
			if (t != null && endpointName != null) {
				String newName = "ac-data-hub [" + endpointName + "]";
				log.info("update endpoint name from '" + t.getName() + "' to '"
						+ newName + "'");
				t.setName(newName);
			}
		}

		@SuppressWarnings("unchecked")
		private T[] fetchNextBatch() throws InterruptedException {
			int count = maxBatchSize;
			long nextIndex = clientSeq;
			long lost;
			List<T> data = new ArrayList<T>();

			long waitTime = TimeUnit.MILLISECONDS.toNanos(maxWaitTime);
			long startTime = System.nanoTime();
			lock.lock();
			try {
				while (nextIndex >= nextSeq) {
					nodataCondition.await();
				}
				lost = earlistSeq - clientSeq;
				if (lost < 0) {
					lost = 0;
				}
				if (nextIndex < earlistSeq) {
					nextIndex = earlistSeq;
				}
				waitTime -= System.nanoTime() - startTime;
				WAIT_LOOP: do {
					startTime = System.nanoTime();
					// need load more data
					while (count > 0 && nextIndex < nextSeq) {
						if (nextIndex >= earlistSeq) {
							data.add(getDataAt(nextIndex));
							--count;
							++nextIndex;
						} else {
							// some data are lost during wait loop
							break WAIT_LOOP;
						}
					}
					waitTime -= System.nanoTime() - startTime;
					if (count <= 0) {
						break;
					} else if (waitTime > 0) {
						waitTime = nodataCondition.awaitNanos(waitTime);
					}
				} while (waitTime > 0);
				clientSeq = nextIndex;
				totalLost += lost;
			} finally {
				lock.unlock();
			}

			if (lost > 0) {
				triggerLost(lost);
			}

			return (T[]) data.toArray((T[]) Array.newInstance(dataType,
					data.size()));
		}

		private void triggerLost(long lost) {
			if (listener instanceof AcStatusListener) {
				((AcStatusListener) listener).onStatus(
						AcDataLostEvent.EventType, new AcDataLostEvent(
								SimpleAcDataHubImpl.this, lost));
			}
		}

		@Override
		public void setName(String name) {
			this.endpointName = name;
			if (this.endpointName != null) {
				setThreadName();
			}
		}

		@Override
		public String getName() {
			return endpointName;
		}

	}

	private int size() {
		int diff = tail - head;
		if (diff < 0)
			diff += capacity;
		return diff;
	}

	private void add(T o, long time) {
		dataQueue[tail] = o;
		timeQueue[tail] = time;
		int newtail = (tail + 1) % capacity;
		if (newtail == head)
			throw new IndexOutOfBoundsException("Queue full");
		tail = newtail;
	}

	private void removeHead() {
		if (head == tail)
			throw new IndexOutOfBoundsException("Queue empty");
		dataQueue[head] = null;
		timeQueue[head] = 0;
		head = (head + 1) % capacity;
	}

	private T getDataAt(long seqNo) {
		int index = toIndex(seqNo);
		return dataQueue[index];
	}

	protected int toIndex(long seqNo) {
		long index = seqNo - earlistSeq;
		if (index < 0 || index > Integer.MAX_VALUE) {
			final String msg = "Bad sequence number: " + seqNo + " (earliest "
					+ earlistSeq + ")";
			throw new IllegalArgumentException(msg);
		}
		int i = (int) index;
		int size = size();
		if (i < 0 || i >= size) {
			final String msg = "Index " + i + ", queue size " + size;
			throw new IndexOutOfBoundsException(msg);
		}
		int index1 = (head + i) % capacity;
		return index1;
	}

	private long getTimeAt(long seqNo) {
		int i = (int) (seqNo - earlistSeq);
		int size = size();
		if (i < 0 || i >= size) {
			return 0L;
		}
		int index = (head + i) % capacity;
		return timeQueue[index];
	}

	@Override
	public void onData(T... data) {
		// TODO use small chunk
		lock.lock();
		try {
			long now = System.currentTimeMillis();
			for (int i = 0; i < data.length; i++) {
				T t = data[i];
				while (size() >= capacity - 1) {
					removeHead();
					earlistSeq++;
				}
				add(t, now);
				nextSeq++;
				nodataCondition.signalAll();
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public AcDataHubEndpoint<T> createDataEndpoint(AcDataListener<T> listener,
			int maxBatchSize, int maxWaitTime) {
		return new DataHubEndpoint(listener, maxBatchSize, maxWaitTime);
	}

	@Override
	public AcDataHubEndpoint<T> createDataEndpoint(AcDataListener<T> listener,
			int maxBatchSize, int maxWaitTime, AcPredicate<T> filter) {
		// TODO Auto-generated method stub
		return null;
	}

	public void shutdown(long timeout, TimeUnit unit)
			throws InterruptedException {
		executor.shutdownNow();
		if (!executor.awaitTermination(timeout, unit)) {
			// TODO force trigger lost event
		}
	}

}
