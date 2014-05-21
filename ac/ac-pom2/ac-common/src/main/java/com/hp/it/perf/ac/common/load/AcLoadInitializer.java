package com.hp.it.perf.ac.common.load;

import static com.hp.it.perf.ac.common.load.AcLoadDataUtils.readString;
import static com.hp.it.perf.ac.common.load.AcLoadDataUtils.writeString;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class AcLoadInitializer implements AcLoadData {

	private static final long serialVersionUID = 1L;

	private static ConcurrentMap<Integer, Initializer> initializers = new ConcurrentHashMap<Integer, Initializer>();

	public static final int TYPE = 1;

	static {
		register(TYPE, new Initializer() {

			@Override
			public AcLoadData newInstance() {
				return new AcLoadInitializer();
			}
		});
	}

	private String loadDataClassName;

	private String initializerClassName;

	private boolean standard;

	@Override
	public int getDataType() {
		return TYPE;
	}

	public String getLoadDataClassName() {
		return loadDataClassName;
	}

	public void setLoadDataClassName(String loadDataClassName) {
		this.loadDataClassName = loadDataClassName;
	}

	public String getInitializerClassName() {
		return initializerClassName;
	}

	public void setInitializerClassName(String initializerClassName) {
		this.initializerClassName = initializerClassName;
	}

	public boolean isStandard() {
		return standard;
	}

	public void setStandard(boolean standard) {
		this.standard = standard;
	}

	@Override
	public void toOutput(DataOutput output) throws IOException {
		writeString(output, loadDataClassName);
		writeString(output, initializerClassName);
		output.writeBoolean(standard);
	}

	@Override
	public void fromInput(DataInput input) throws IOException {
		loadDataClassName = readString(input);
		initializerClassName = readString(input);
		standard = input.readBoolean();
	}

	public static void register(int dataType, Initializer initializer) {
		if (initializer == null) {
			throw new NullPointerException("null initializer");
		}
		if (initializers.putIfAbsent(dataType, initializer) != null) {
			throw new IllegalArgumentException(
					"existing initializer for data type: " + dataType);
		}
	}

	public static Initializer getInitializer(int dataType) {
		Initializer initializer = initializers.get(dataType);
		if (initializer == null) {
			throw new IllegalArgumentException(
					"no initializer defined for data type: " + dataType);
		}
		return initializer;
	}

}
