package com.hp.it.perf.ac.load.content;

import java.io.IOException;
import java.io.InputStream;

public interface AcStreamSupplier {

	public InputStream getInputStream() throws IOException;
}
