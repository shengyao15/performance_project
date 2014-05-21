package com.hp.it.perf.ac.common.data;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class AcDataInitializer implements AcData {

	private static final long serialVersionUID = 1L;

	private static ConcurrentMap<Integer, Initializer> initializers = new ConcurrentHashMap<Integer, Initializer>();

	private String loadDataClassName;

	private String initializerClassName;

	private boolean standard;

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
	public void toOutput(AcDataOutput output) throws IOException {
		output.writeString(loadDataClassName);
		output.writeString(initializerClassName);
		output.writeBoolean(standard);
	}

	@Override
	public void fromInput(AcDataInput input) throws IOException {
		loadDataClassName = input.readString();
		initializerClassName = input.readString();
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
