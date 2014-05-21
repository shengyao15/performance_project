package com.hp.it.perf.ac.common.data.store;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccessFileStore implements RandomAccessStore {

	private RandomAccessFile file;

	private static final byte[] EMPTY = new byte[1024 * 16];

	public RandomAccessFileStore(RandomAccessFile file) {
		this.file = file;
	}

	@Override
	public int read() throws IOException {
		return file.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return file.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return file.read(b, off, len);
	}

	@Override
	public void setLength(long length) throws IOException {
		file.setLength(length);
	}

	@Override
	public long length() throws IOException {
		return file.length();
	}

	@Override
	public long position() throws IOException {
		return file.getFilePointer();
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		file.write(b, off, len);
	}

	@Override
	public void position(long position) throws IOException {
		file.seek(position);
	}

	@Override
	public void close() throws IOException {
		file.close();
	}

	@Override
	public int skip(int n) throws IOException {
		return file.skipBytes(n);
	}

	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		file.readFully(b, off, len);
	}

	@Override
	public int clear(int n) throws IOException {
		if (n < 0) {
			throw new IOException("nagitave size: " + n);
		}
		int total = 0;
		while (n > 0) {
			int size = Math.min(n, EMPTY.length);
			file.write(EMPTY, 0, size);
			n -= size;
			total += size;
		}
		return total;
	}

}
