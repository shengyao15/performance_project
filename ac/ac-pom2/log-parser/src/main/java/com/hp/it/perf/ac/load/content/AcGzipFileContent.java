package com.hp.it.perf.ac.load.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

public class AcGzipFileContent extends AcFileContent {

	public AcGzipFileContent(File file) {
		super(file);
	}

	@Override
	public Reader getContent() throws IOException {
		return new InputStreamReader(new GZIPInputStream(new FileInputStream(
				file)));
	}

}
