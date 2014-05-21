package com.hp.it.perf.ac.load.content;

import java.io.Closeable;
import java.io.IOException;

public interface AcContentFetcher extends Closeable {

	// return null if nothing provide
	public AcReaderContent next() throws IOException;

}
