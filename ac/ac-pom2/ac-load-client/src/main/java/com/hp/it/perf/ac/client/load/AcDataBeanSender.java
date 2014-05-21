package com.hp.it.perf.ac.client.load;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.hp.it.perf.ac.common.logging.AcLogger;
import com.hp.it.perf.ac.load.content.AcContentCounter;
import com.hp.it.perf.ac.load.content.AcContentHandler;
import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcContentLineInfo;
import com.hp.it.perf.ac.load.content.AcContentMetadata;
import com.hp.it.perf.ac.load.content.AcLoadException;
import com.hp.it.perf.ac.service.transfer.AcSender;
import com.hp.it.perf.ac.service.transfer.data.AcDataBeanBlock;
import com.hp.it.perf.ac.service.transfer.data.AcDataContentLine;
import com.hp.it.perf.ac.service.transfer.data.AcDataFileEntry;
import com.hp.it.perf.ac.service.transfer.data.AcDataFileSummary;

public class AcDataBeanSender extends AcContentCounter implements
		AcContentHandler {

	protected AcSender sender;
	protected int filteredCount;
	private String hostname = "unknown";
	private AcDataBeanBlock block;
	private int blockSize = 1000;
	private Map<Class<?>, AcClientLoadFactory> beanClasses;
	private static final AcLogger logger = AcLogger
			.getLogger(AcDataBeanSender.class);
	private static AtomicInteger sessionIdGen = new AtomicInteger();
	protected int dataSessionId;
	private String transformName;
	private AcClientBeanFilter beanFilter;

	public AcDataBeanSender(AcSender sender,
			Map<Class<?>, AcClientLoadFactory> beanClasses) {
		this.sender = sender;
		this.beanClasses = beanClasses;
	}

	public int getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	@Override
	public void init(AcContentMetadata metadata) {
		super.init(metadata);
		this.transformName = null;
		this.filteredCount = 0;
		dataSessionId = sessionIdGen.incrementAndGet();

		AcDataFileEntry fileHeader = new AcDataFileEntry();
		fileHeader.setDataSessionId(dataSessionId);
		fileHeader.setHostname(hostname);
		fileHeader.setLocation(String.valueOf(metadata.getLocation()));
		fileHeader.setBasename(metadata.getBasename());
		fileHeader.setFileSize(metadata.getSize());
		fileHeader.setLastModified(metadata.getLastModified());

		// send header
		try {
			sender.sendObject(fileHeader);
		} catch (IOException e) {
			logger.error("fail to send header", e);
		}

	}

	@Override
	public void handle(Object object, AcContentLine contentLine)
			throws AcLoadException {
		super.handle(object, contentLine);
		object = filterObject(object, contentLine);
		if (object == null) {
			filteredCount++;
			return;
		}
		AcContentLineInfo lineInfo = contentLine.getLineInfo();

		AcDataContentLine line = new AcDataContentLine();
		line.setLineNum(lineInfo.getLineNum());
		line.setOffset(lineInfo.getOffset());
		line.setLength(lineInfo.getLength());
		line.setContent(contentLine.getCurrentLines());

		processDataBean(object, line);
	}

	protected Object filterObject(Object object, AcContentLine contentLine) {
		if (beanFilter == null) {
			beanFilter = beanClasses.get(beanClass).getBeanFilter(beanClass,
					metadata);
			if (beanFilter == null) {
				beanFilter = new AcClientBeanFilter() {

					@Override
					public Object filter(Object beanInstance,
							AcContentLineInfo lineInfo) {
						return beanInstance;
					}
				};
			}
		}
		return beanFilter.filter(object, contentLine.getLineInfo());
	}

	protected String getTransformName() {
		if (transformName == null) {
			transformName = getTransformName(getBeanClass());
		}
		return transformName;
	}

	private String getTransformName(Class<?> beanClass) {
		return beanClasses.get(beanClass).getTransformName(beanClass, metadata);
	}

	@Override
	public void destroy() {
		super.destroy();

		processPending();

		// send summary as tailor
		AcDataFileSummary summary = new AcDataFileSummary();
		summary.setDataSessionId(dataSessionId);
		summary.setDuration(getTime());
		summary.setErrorCount(getErrorCount());
		summary.setSuccessCount(getSuccessCount() - filteredCount);
		summary.setIgnoredCount(filteredCount);
		summary.setFileSize(metadata.getSize());
		summary.setLastModified(metadata.getLastModified());

		try {
			sender.sendObject(summary);
		} catch (IOException e) {
			logger.error("fail to send file summary", e);
		}
		this.metadata = null;
		this.transformName = null;
	}

	protected void processDataBean(Object object, AcDataContentLine line) {
		// prepare block
		if (block == null) {
			block = new AcDataBeanBlock(getBeanClass(), blockSize);
			block.setDataSessionId(dataSessionId);
			// set transformName
			block.setTransformName(getTransformName());
		}
		int count = block.getCount();
		if (count < blockSize) {
			// add one if under size
			block.getBeans()[count] = object;
			block.getLines()[count] = line;
			block.setCount(count + 1);
		} else {
			// send if block is full
			try {
				sender.sendObject(block);
			} catch (IOException e) {
				logger.error("fail to send data block", e);
			}
			// reset block for next
			block = null;
		}
	}

	protected void processPending() {
		// check if there is last block
		if (block != null) {
			try {
				sender.sendObject(block);
			} catch (IOException e) {
				logger.error("fail to send final data block", e);
			}
			block = null;
		}
	}

}
