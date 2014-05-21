package com.hp.it.perf.ac.common.data;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;

public interface AcDataStore extends Closeable{

	public long add(Object data) throws IOException;

	public int size();

	public URI toURI();

	public long[] addAll(AcDataStore other) throws IOException;

	public Object get(long key) throws IOException;

	public Iterator<Object> values();

	public Iterator<Long> keys();
	
}
