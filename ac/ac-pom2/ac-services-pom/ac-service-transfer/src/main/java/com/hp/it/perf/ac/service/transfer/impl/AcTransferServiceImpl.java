package com.hp.it.perf.ac.service.transfer.impl;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.hp.it.perf.ac.common.data.AcData;
import com.hp.it.perf.ac.common.data.AcDataObject;
import com.hp.it.perf.ac.service.transfer.AcSenderId;
import com.hp.it.perf.ac.service.transfer.AcTransferCommand;
import com.hp.it.perf.ac.service.transfer.AcTransferService;

@Service
public class AcTransferServiceImpl implements AcTransferService {

	private static final int MAX_RECEIVER = 1024;

	private BitSet receiverIds = new BitSet();

	private int lastReceiveId = -1;

	private Map<Integer, AcTransferReceiverInfo> receiverInfos = new HashMap<Integer, AcTransferReceiverInfo>();

	private Map<Class<?>, AcTransferCommand<?>> commands = new HashMap<Class<?>, AcTransferCommand<?>>();

	@Resource
	private ApplicationContext context;

	private static final Logger log = LoggerFactory
			.getLogger(AcTransferServiceImpl.class);

	public AcTransferServiceImpl() {
		// TODO need persistent receive id list and send list?
		receiverIds.set(0, MAX_RECEIVER, true);
	}

	@PostConstruct
	public void initService() {
		for (AcTransferCommand<?> command : context.getBeansOfType(
				AcTransferCommand.class).values()) {
			commands.put(command.getSupportedDataType(), command);
		}
	}

	@Override
	public int startReceive(AcSenderId senderId, Properties properties) {
		int nextId;
		AcTransferReceiverInfo receiverInfo;
		synchronized (receiverIds) {
			// try to find next receiver id (from last one)
			// this simulate the socket port number assignment
			nextId = receiverIds.nextSetBit((lastReceiveId + 1)
					% receiverIds.size());
			if (nextId == -1) {
				throw new IllegalStateException("sender list is full: "
						+ receiverIds.cardinality());
			}
			// store receiver info
			receiverInfo = new AcTransferReceiverInfo(nextId, senderId,
					properties);
			receiverIds.set(nextId, false);
			receiverInfos.put(nextId, receiverInfo);
			lastReceiveId = nextId;
		}
		log.info("Start receiver: {}", receiverInfo);
		return nextId;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public AcData onReceive(int rid, AcData data) {
		AcTransferReceiverInfo receiverInfo = getReceiverInfo(rid);
		if (data == null || !(data instanceof AcDataObject)) {
			throw new IllegalArgumentException("invalid AcData recevied: "
					+ (data == null ? "null" : data.getClass()));
		}
		Object dataObject = ((AcDataObject) data).getObject();
		if (dataObject == null) {
			log.warn("null data object received: {}", receiverInfo);
			throw new IllegalArgumentException("null data object received");
		}
		Class<?> dataClass = dataObject.getClass();
		AcTransferCommand command = commands.get(dataClass);
		if (command != null) {
			return new AcDataObject(command.process(command
					.getSupportedDataType().cast(dataObject), receiverInfo));
		} else {
			return processUnknownData(receiverInfo, dataObject);
		}
	}

	private AcData processUnknownData(AcTransferReceiverInfo receiverInfo,
			Object dataObject) {
		log.warn("receive unknown data type '{}' from receiver '{}'",
				dataObject.getClass().getName(), receiverInfo);
		return null;
	}

	@Override
	public void endReceive(int rid) {
		AcTransferReceiverInfo receiverInfo;
		synchronized (receiverIds) {
			receiverInfo = getReceiverInfo(rid);
			receiverIds.set(rid, true);
			receiverInfos.remove(rid);
		}
		log.info("End receiver: {}", receiverInfo);
	}

	protected AcTransferReceiverInfo getReceiverInfo(int rid) {
		synchronized (receiverIds) {
			if (receiverIds.get(rid)) {
				throw new IllegalArgumentException("invalid receiver id: "
						+ rid);
			}
			return receiverInfos.get(rid);
		}
	}

	@Override
	public Properties getTransferInfo() {
		// TODO Auto-generated method stub
		return null;
	}

}
