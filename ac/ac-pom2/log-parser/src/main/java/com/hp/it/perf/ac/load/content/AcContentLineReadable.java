package com.hp.it.perf.ac.load.content;

import java.io.Closeable;
import java.io.IOException;

public interface AcContentLineReadable extends Closeable {

	public AcContentLine getContentLine();

	public void readLines() throws IOException;

	public void readMoreLines() throws IOException;

}
