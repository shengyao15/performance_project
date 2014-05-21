package com.hp.it.perf.ac.common.data;

import static com.hp.it.perf.ac.common.data.AcDataUtils.writeVInt;
import static com.hp.it.perf.ac.common.data.AcDataUtils.writeVLong;

import java.io.DataOutput;
import java.io.IOException;


public class AcDataOutput implements DataOutput {

	private DataOutput out;

	private AcDataTypeManager classManager = new AcDataTypeManager();

	public AcDataOutput(DataOutput out) {
		this.out = out;
	}

	public void writeBoolean(boolean v) throws IOException {
		out.writeBoolean(v);
	}

	public void writeByte(int v) throws IOException {
		out.writeByte(v);
	}

	public void writeShort(int v) throws IOException {
		writeVInt(out, v);
	}

	public void writeChar(int v) throws IOException {
		writeVInt(out, v);
	}

	public void writeInt(int v) throws IOException {
		writeVInt(out, v);
	}

	public void writeLong(long v) throws IOException {
		writeVLong(out, v);
	}

	public void writeFloat(float v) throws IOException {
		writeVInt(out, Float.floatToIntBits(v));
	}

	public void writeDouble(double v) throws IOException {
		writeVLong(out, Double.doubleToLongBits(v));
	}

	public void writeString(String s) throws IOException {
		AcDataUtils.writeString(out, s);
	}

	public void writeObject(Object obj) throws IOException {
		Class<?> objClass = (obj == null ? null : obj.getClass());
		AcDataType dataClass = classManager.getDataType(objClass);
		// write class block or class type id
		classManager.writeDataClass(this, dataClass);
		dataClass.writeObject(this, obj);
	}

	public void write(byte[] b) throws IOException {
		out.write(b);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}

	public void write(int b) throws IOException {
		out.write(b);
	}

	public void writeBytes(String s) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void writeChars(String s) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void writeUTF(String s) throws IOException {
		writeString(s);
	}

}
