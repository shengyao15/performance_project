package com.hp.it.perf.ac.common.data;

import java.io.Serializable;

public interface AcIndexableDataObject extends AcData {

	public long getId();

	public Serializable getField(int fieldIndex)
			throws IndexOutOfBoundsException;

}
