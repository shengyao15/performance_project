package com.hp.it.perf.ac.client.load;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.it.perf.ac.load.content.AcContentHandler;
import com.hp.it.perf.ac.service.transfer.AcSender;
import com.hp.it.perf.ac.service.transfer.data.AcDataContentLine;
import com.hp.it.perf.ac.service.transfer.data.AcMixDataBeanBlock;

class AcDataBeanMixAgent implements Runnable {

	private static final Logger logger = LoggerFactory
			.getLogger(AcDataBeanMixAgent.class);

	private AcSender sender;
	private Map<Class<?>, AcClientLoadFactory> beanClasses;
	private int blockSize;
	private AcMixDataBeanBlock block;
	private static AtomicInteger seq = new AtomicInteger();
	private ExecutorService executor = Executors
			.newSingleThreadExecutor(new ThreadFactory() {

				private ThreadFactory defaultThreadFactory = Executors
						.defaultThreadFactory();

				@Override
				public Thread newThread(Runnable r) {
					Thread thread = defaultThreadFactory.newThread(r);
					thread.setName("ac-load-client-agent-"
							+ seq.incrementAndGet());
					return thread;
				}
			});
	private volatile BlockingQueue<AcMixDataBeanBlock> queue;

	public AcDataBeanMixAgent(AcSender sender,
			Map<Class<?>, AcClientLoadFactory> beanClasses) {
		this.sender = sender;
		this.beanClasses = beanClasses;
		queue = new LinkedBlockingQueue<AcMixDataBeanBlock>(Integer.getInteger(
				"ac.load.client.queuesize", 20));
		executor.submit(this);
	}

	public int getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	public AcContentHandler createContentHandler() {
		return new AcDataBeanSender(sender, beanClasses) {

			@Override
			protected void processDataBean(Object object, AcDataContentLine line) {
				addDataBean(dataSessionId, getBeanClass(), getTransformName(),
						object, line);
			}

			@Override
			protected void processPending() {
				flushDataBeans(dataSessionId);
			}

		};
	}

	private synchronized void flushDataBeans(int dataSessionId) {
		if (block != null && block.contains(dataSessionId)) {
			sendDataBlock();
		}
	}

	protected void sendDataBlock() {
		BlockingQueue<AcMixDataBeanBlock> theQueue = queue;
		if (block != null) {
			if (theQueue != null) {
				try {
					theQueue.put(block);
				} catch (InterruptedException e) {
					executor.shutdownNow();
					queue = null;
				}
			} else {
				logger.warn("executor is shutdown, the data block is thrown...");
			}
		}
		block = null;
	}

	private synchronized void addDataBean(int dataSessionId,
			Class<?> beanClass, String transformName, Object object,
			AcDataContentLine line) {
		if (block == null) {
			block = new AcMixDataBeanBlock();
		}
		block.add(dataSessionId, beanClass, transformName, object, line);
		if (block.getCount() >= blockSize) {
			sendDataBlock();
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				AcMixDataBeanBlock dataBlock;
				try {
					dataBlock = queue.take();
				} catch (InterruptedException e) {
					logger.info("exit sending executor...");
					break;
				}
				try {
					sender.sendObject(dataBlock);
				} catch (IOException e) {
					logger.error("fail to send data block", e);
				} catch (RuntimeException e) {
					logger.error("cache runtime exception in data sending", e);
				}
			}
		} catch (Error e) {
			logger.error("uncauched error in data sending", e);
			throw e;
		}
	}

}
