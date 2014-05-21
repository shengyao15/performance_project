package com.hp.it.perf.ac.common.core;

import java.io.Closeable;
import java.util.Date;

public interface AcDataHubEndpoint<T> extends Closeable {

	public class AcDataLostEvent extends AcStatusEvent {

		private static final long serialVersionUID = 1L;

		private final long lost;

		public static String EventType = "DataLost";

		public AcDataLostEvent(AcDataHub<?> source, long lost) {
			super(source);
			this.lost = lost;
		}

		public long getLost() {
			return lost;
		}

	}

	public AcDataListener<T> getDataListener();

	public Class<T> getDataType();

	public int getMaxBatchSize();

	public long getReceived();

	public long getUnprocessed();

	public long getProcessed();

	public long getTotalLosted();

	public Date getEarliestOn();

	public Date getLatestOn();

	// control operation
	public boolean isClosed();

	public void close();

	public void setName(String name);
	
	public String getName();

}
