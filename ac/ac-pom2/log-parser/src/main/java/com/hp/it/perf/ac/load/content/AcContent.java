package com.hp.it.perf.ac.load.content;

import java.io.IOException;

public interface AcContent<T> {

	public T getContent() throws IOException;

	public AcContentMetadata getMetadata();
}
