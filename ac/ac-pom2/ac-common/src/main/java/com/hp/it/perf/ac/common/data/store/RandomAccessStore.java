package com.hp.it.perf.ac.common.data.store;

import java.io.Closeable;
import java.io.IOException;

public interface RandomAccessStore extends Closeable {

	int read() throws IOException;

	int read(byte[] b) throws IOException;

	int read(byte[] b, int off, int len) throws IOException;
	
	void readFully(byte[] b, int off, int len) throws IOException;

	void setLength(long length) throws IOException;

	long length() throws IOException;

	long position() throws IOException;

	void write(byte[] b, int off, int tlen) throws IOException;

	void position(long position) throws IOException;

	int skip(int n) throws IOException;

	int clear(int n) throws IOException;

}
