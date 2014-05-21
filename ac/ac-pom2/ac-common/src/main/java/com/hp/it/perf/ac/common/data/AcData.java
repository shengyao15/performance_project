package com.hp.it.perf.ac.common.data;

import java.io.IOException;
import java.io.Serializable;

public interface AcData extends Serializable {

	public void toOutput(AcDataOutput out) throws IOException;

	public void fromInput(AcDataInput in) throws IOException,
			ClassNotFoundException;

	public static abstract class Initializer {
		public abstract AcData newInstance();
	}
}
