package com.hp.it.perf.ac.common.load;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class AcLoadLocation implements AcLoadData {

	private static final long serialVersionUID = 1L;

	public static final int TYPE = 3;

	static {
		AcLoadInitializer.register(TYPE, new Initializer() {

			@Override
			public AcLoadData newInstance() {
				return new AcLoadLocation();
			}
		});
	}

	@Override
	public int getDataType() {
		return TYPE;
	}

	@Override
	public void toOutput(DataOutput output) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void fromInput(DataInput input) throws IOException {
		// TODO Auto-generated method stub

	}

}
