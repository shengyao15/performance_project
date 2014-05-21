package com.hp.it.perf.ac.common.load;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

public interface AcLoadData extends Serializable {

	public int getDataType();

	public void toOutput(DataOutput output) throws IOException;

	public void fromInput(DataInput input) throws IOException;

	public static abstract class Initializer {
		public abstract AcLoadData newInstance();

		protected boolean isStandard() {
			return true;
		}
	}
}
