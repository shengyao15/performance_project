package com.hp.it.perf.ac.load.content;

import java.io.IOException;
import java.io.Reader;

public interface AcReaderContent extends AcContent<Reader> {

	public Reader getContent() throws IOException;

	public AcContentMetadata getMetadata();

}
