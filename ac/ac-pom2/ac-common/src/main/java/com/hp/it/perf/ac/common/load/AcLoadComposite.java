package com.hp.it.perf.ac.common.load;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class AcLoadComposite implements AcLoadData {

	private static final long serialVersionUID = 1L;

	public static final byte TYPE = (byte) 2;

	static {
		AcLoadInitializer.register(TYPE, new Initializer() {

			@Override
			public AcLoadData newInstance() {
				return new AcLoadComposite();
			}
		});
	}

	@Override
	public int getDataType() {
		// TODO Auto-generated method stub
		return 0;
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
