package com.hp.it.perf.ac.load.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class AcStreamSuppliers {

	public static AcStreamSupplier createFileSupplier(final File file) {
		return new AcStreamSupplier() {

			@Override
			public InputStream getInputStream() throws IOException {
				return new FileInputStream(file);
			}
		};
	}

	public static AcStreamSupplier createURLSupplier(final URL url) {
		return new AcStreamSupplier() {

			@Override
			public InputStream getInputStream() throws IOException {
				return url.openStream();
			}
		};
	}

	public static AcStreamSupplier createStreamSupplier(final InputStream in) {
		return new AcStreamSupplier() {

			@Override
			public InputStream getInputStream() throws IOException {
				return in;
			}
		};
	}
}
