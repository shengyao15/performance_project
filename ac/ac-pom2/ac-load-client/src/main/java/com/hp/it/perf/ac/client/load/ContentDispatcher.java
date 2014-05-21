package com.hp.it.perf.ac.client.load;

import java.io.Closeable;
import java.io.IOException;

import javax.management.remote.JMXServiceURL;

interface ContentDispatcher extends Closeable {

	public long getLineCount();

	public long getByteCount();

	public void close() throws IOException;

	public void monitor(JMXServiceURL jmxURL) throws IOException;
}
