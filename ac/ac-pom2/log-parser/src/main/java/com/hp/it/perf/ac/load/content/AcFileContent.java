package com.hp.it.perf.ac.load.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;

import com.hp.it.perf.ac.common.data.store.RandomAccessFileStore;
import com.hp.it.perf.ac.common.data.store.RandomAccessStore;
import com.hp.it.perf.ac.common.data.store.RandomAccessSupplier;

public class AcFileContent implements AcReaderContent, RandomAccessSupplier {

	protected File file;
	protected String enc;
	protected AcContentMetadata metadata;

	public AcFileContent(File file) {
		this.file = file;
		metadata = createMetadata();
	}

	public AcFileContent(File file, String enc) {
		this.file = file;
		this.enc = enc;
		metadata = createMetadata();
	}

	@Override
	public Reader getContent() throws IOException {
		if (enc == null) {
			return new FileReader(file);
		} else {
			return new InputStreamReader(new FileInputStream(file), enc);
		}
	}

	public RandomAccessStore getRandomAccess() throws IOException {
		return new RandomAccessFileStore(new RandomAccessFile(file, "r"));
	}

	@Override
	public AcContentMetadata getMetadata() {
		return metadata;
	}

	protected AcContentMetadata createMetadata() {
		AcContentMetadata metadata = new AcContentMetadata();
		metadata.setBasename(file.getName());
		metadata.setLastModified(file.lastModified());
		metadata.setLocation(file.toURI());
		metadata.setSize(file.length());
		metadata.setSignature(null);
		metadata.setReloadable(true);
		return metadata;
	}

}
