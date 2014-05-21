package com.hp.it.perf.ac.common.data.store;

import java.io.Closeable;
import java.io.IOException;

public interface BufferedStore extends Closeable {

	// buffer data with return key
	public long put(byte[] data, int offset, int len) throws IOException;

	public byte[] get(long key) throws IOException;
	
	public byte[] remove(long key) throws IOException;

	public long init(int initSize) throws IOException;

	// return new key (if possible)
	public long append(long key, byte[] data, int offset, int len)
			throws IOException;

	public int get(long key, int position, byte[] data) throws IOException;

	// deallocate it
	public void destroy(long key) throws IOException;

}
