package com.hp.it.perf.ac.common.data.store;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

class BufferedRandomAccess implements DataInput, DataOutput {

	private RandomAccessStore store;

	private DataOutputStream output;

	private ByteArrayOutputStream outBuffer;

	private DataInputStream input;

	private BufferedInputStream inBuffer;

	private CountInputStream inCount;

	private long inPrePosition = 0;

	private static class CountInputStream extends FilterInputStream {

		private int count;

		public CountInputStream(InputStream in) {
			super(in);
		}

		@Override
		public int read() throws IOException {
			int v = super.read();
			if (v != -1) {
				count++;
			}
			return v;
		}

		@Override
		public int read(byte[] b) throws IOException {
			int rlen = super.read(b);
			if (rlen != -1) {
				count += rlen;
			}
			return rlen;
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			int rlen = super.read(b, off, len);
			if (rlen != -1) {
				count += rlen;
			}
			return rlen;
		}

		@Override
		public long skip(long n) throws IOException {
			long skipped = super.skip(n);
			count += skipped;
			return skipped;
		}

		public int count() {
			return count;
		}

	}

	public BufferedRandomAccess(RandomAccessStore store) {
		this.store = store;
		resetBuffer();
	}

	private void resetBuffer() {
		outBuffer = new ByteArrayOutputStream();
		output = new DataOutputStream(outBuffer);
		inBuffer = new BufferedInputStream(new InputStream() {

			@Override
			public int read() throws IOException {
				return store.read();
			}

			@Override
			public int read(byte[] b) throws IOException {
				return store.read(b);
			}

			@Override
			public int read(byte[] b, int off, int len) throws IOException {
				return store.read(b, off, len);
			}

		});
		inCount = new CountInputStream(inBuffer);
		input = new DataInputStream(inCount);
	}

	public void setLength(long length) throws IOException {
		flush();
		store.setLength(length);
	}

	public long length() throws IOException {
		flush();
		return store.length();
	}

	public long position() throws IOException {
		flush();
		return store.position();
	}

	public void flush() throws IOException {
		boolean dirty = false;
		if (outBuffer.size() > 0) {
			output.close();
			byte[] bs = outBuffer.toByteArray();
			store.write(bs, 0, bs.length);
			dirty = true;
		}
		if (inCount.count() > 0) {
			store.position(inPrePosition + inCount.count());
			dirty = true;
		}
		if (dirty) {
			resetBuffer();
			inPrePosition = store.position();
		}
	}

	public void position(long position) throws IOException {
		flush();
		store.position(position);
		inPrePosition = position;
	}

	public void close() throws IOException {
		flush();
		store.close();
	}

	@Override
	public void write(int b) throws IOException {
		output.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		output.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		output.write(b, off, len);
	}

	@Override
	public void writeBoolean(boolean v) throws IOException {
		output.writeBoolean(v);
	}

	@Override
	public void writeByte(int v) throws IOException {
		output.writeByte(v);
	}

	@Override
	public void writeShort(int v) throws IOException {
		output.writeShort(v);
	}

	@Override
	public void writeChar(int v) throws IOException {
		output.writeChar(v);
	}

	@Override
	public void writeInt(int v) throws IOException {
		output.writeInt(v);
	}

	@Override
	public void writeLong(long v) throws IOException {
		output.writeLong(v);
	}

	@Override
	public void writeFloat(float v) throws IOException {
		output.writeFloat(v);
	}

	@Override
	public void writeDouble(double v) throws IOException {
		output.writeDouble(v);
	}

	@Override
	public void writeBytes(String s) throws IOException {
		output.writeBytes(s);
	}

	@Override
	public void writeChars(String s) throws IOException {
		output.writeChars(s);
	}

	@Override
	public void writeUTF(String s) throws IOException {
		output.writeUTF(s);
	}

	@Override
	public void readFully(byte[] b) throws IOException {
		input.readFully(b);
	}

	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		input.readFully(b, off, len);
	}

	@Override
	public int skipBytes(int n) throws IOException {
		flush();
		return store.skip(n);
	}

	@Override
	public boolean readBoolean() throws IOException {
		return input.readBoolean();
	}

	@Override
	public byte readByte() throws IOException {
		return input.readByte();
	}

	@Override
	public int readUnsignedByte() throws IOException {
		return input.readUnsignedByte();
	}

	@Override
	public short readShort() throws IOException {
		return input.readShort();
	}

	@Override
	public int readUnsignedShort() throws IOException {
		return input.readUnsignedShort();
	}

	@Override
	public char readChar() throws IOException {
		return input.readChar();
	}

	@Override
	public int readInt() throws IOException {
		return input.readInt();
	}

	@Override
	public long readLong() throws IOException {
		return input.readLong();
	}

	@Override
	public float readFloat() throws IOException {
		return input.readFloat();
	}

	@Override
	public double readDouble() throws IOException {
		return input.readDouble();
	}

	@Override
	public String readLine() throws IOException {
		return input.readUTF();
	}

	@Override
	public String readUTF() throws IOException {
		return input.readUTF();
	}

}
