package com.hp.it.perf.ac.load.parse;

import java.io.Closeable;

import com.hp.it.perf.ac.load.content.AcContentMetadata;
import com.hp.it.perf.ac.load.content.AcLoadException;

public interface AcTextPipeline extends Closeable {

	public void prepare(AcContentMetadata metadata) throws AcLoadException,
			AcStopParseException;

	public void putLine(String line) throws AcLoadException,
			AcStopParseException;

	// mark end of block (EOB)
	public void markEOB() throws AcLoadException, AcStopParseException;

	// mark end of file (EOF)
	public void close();

}
