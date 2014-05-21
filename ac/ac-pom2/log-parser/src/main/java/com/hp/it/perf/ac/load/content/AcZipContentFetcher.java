package com.hp.it.perf.ac.load.content;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.hp.it.perf.ac.load.common.AcPredicate;

public class AcZipContentFetcher implements AcContentFetcher {

	private ZipFile zipFile;
	private Enumeration<? extends ZipEntry> entries;
	private AcPredicate<String> nameFilter;

	public AcZipContentFetcher(ZipFile file) {
		this.zipFile = file;
	}

	public AcZipContentFetcher(ZipFile file, AcPredicate<String> filter) {
		this.zipFile = file;
		this.nameFilter = filter;
	}

	@Override
	public void close() throws IOException {
		entries = null;
		zipFile.close();
	}

	@Override
	public AcReaderContent next() throws IOException {
		if (entries == null) {
			entries = zipFile.entries();
		}
		while (entries.hasMoreElements()) {
			ZipEntry zipEntry = entries.nextElement();
			if (!zipEntry.isDirectory()
					&& (nameFilter == null || nameFilter.apply(zipEntry
							.getName()))) {
				return new AcInputStreamContent(
						zipFile.getInputStream(zipEntry));
			}
		}
		return null;
	}

}
