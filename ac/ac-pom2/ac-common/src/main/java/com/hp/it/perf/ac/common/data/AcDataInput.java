package com.hp.it.perf.ac.common.data;

import java.io.DataInput;
import java.io.IOException;


public class AcDataInput implements DataInput {

	private DataInput in;
	private AcDataTypeManager dataTypeManager = new AcDataTypeManager();

	public AcDataInput(DataInput in) {
		this.in = in;
	}

	public void readFully(byte[] b) throws IOException {
		in.readFully(b);
	}

	public void readFully(byte[] b, int off, int len) throws IOException {
		in.readFully(b, off, len);
	}

	public int skipBytes(int n) throws IOException {
		return in.skipBytes(n);
	}

	public boolean readBoolean() throws IOException {
		return in.readBoolean();
	}

	public byte readByte() throws IOException {
		return in.readByte();
	}

	public short readShort() throws IOException {
		return (short) readInt();
	}

	public char readChar() throws IOException {
		return (char) readInt();
	}

	public int readInt() throws IOException {
		return AcDataUtils.readVInt(in);
	}

	public long readLong() throws IOException {
		return AcDataUtils.readVLong(in);
	}

	public float readFloat() throws IOException {
		return Float.intBitsToFloat(AcDataUtils.readVInt(in));
	}

	public double readDouble() throws IOException {
		return Double.longBitsToDouble(AcDataUtils.readVLong(in));
	}

	public String readString() throws IOException {
		return AcDataUtils.readString(in);
	}

	public Object readObject() throws ClassNotFoundException, IOException {
		AcDataType dataType = dataTypeManager.readDataType(this);
		return dataType.readObject(this);
	}

	@Override
	public int readUnsignedByte() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int readUnsignedShort() throws IOException {
		throw new UnsupportedOperationException();
	}

	public String readLine() throws IOException {
		throw new UnsupportedOperationException();
	}

	public String readUTF() throws IOException {
		return readString();
	}

}
