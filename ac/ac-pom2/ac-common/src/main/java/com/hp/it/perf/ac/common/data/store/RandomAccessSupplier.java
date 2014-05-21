package com.hp.it.perf.ac.common.data.store;

import java.io.IOException;

public interface RandomAccessSupplier {
	RandomAccessStore getRandomAccess() throws IOException;
}
