package com.hp.it.perf.ac.load.content;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import com.hp.it.perf.ac.common.logging.AcLogger;
import com.hp.it.perf.ac.load.common.AcPredicate;

public class AcArchiveContentFetcher implements AcContentFetcher {

	private static final AcLogger logger = AcLogger
			.getLogger(AcArchiveContentFetcher.class);

	private AcPredicate<String> nameFilter;
	private ArchiveInputStream archiveInputStream;
	private boolean archiveMode = true;
	private InputStream in;
	private AcStreamSupplier inSupplier;
	private File file;
	private int sizeLimitWithBuffer = 0;

	private static class NoCloseInputStream extends FilterInputStream {

		public NoCloseInputStream(InputStream in) {
			super(in);
		}

		public void close() {
			// do not close inner stream
		}

	}

	public AcArchiveContentFetcher(InputStream input) {
		this(AcStreamSuppliers.createStreamSupplier(input));
	}

	public AcArchiveContentFetcher(AcStreamSupplier inSupplier) {
		this.inSupplier = inSupplier;
	}

	public AcArchiveContentFetcher(File file) {
		this(AcStreamSuppliers.createFileSupplier(file));
		this.file = file;
	}

	public AcArchiveContentFetcher(InputStream input, AcPredicate<String> filter) {
		this(AcStreamSuppliers.createStreamSupplier(input));
		this.nameFilter = filter;
	}

	public AcArchiveContentFetcher(AcStreamSupplier inSupplier,
			AcPredicate<String> filter) {
		this(inSupplier);
		this.nameFilter = filter;
	}

	public AcArchiveContentFetcher(File file, AcPredicate<String> filter) {
		this(AcStreamSuppliers.createFileSupplier(file), filter);
		this.file = file;
	}

	public int getSizeLimitWithBuffer() {
		return sizeLimitWithBuffer;
	}

	public void setSizeLimitWithBuffer(int sizeLimitWithBuffer) {
		this.sizeLimitWithBuffer = sizeLimitWithBuffer;
	}

	@Override
	public void close() throws IOException {
		if (archiveInputStream != null) {
			archiveInputStream.close();
		}
		if (in != null) {
			in.close();
		}
	}

	@Override
	public AcReaderContent next() throws IOException {
		if (archiveInputStream == null && archiveMode) {
			in = inSupplier.getInputStream();
			InputStream inputStream = new BufferedInputStream(in);
			boolean compressed = false;
			InputStream compressStream;
			try {
				compressStream = new BufferedInputStream(
						new CompressorStreamFactory()
								.createCompressorInputStream(inputStream));
				compressed = true;
			} catch (CompressorException e) {
				// not compress stream, ignore it
				compressStream = inputStream;
			}
			inputStream = compressStream;
			try {
				archiveInputStream = new ArchiveStreamFactory()
						.createArchiveInputStream(inputStream);
				in = null;
			} catch (ArchiveException e) {
				archiveMode = false;
				// not archive stream, ignore it
				if (file != null && !compressed) {
					inputStream.close();
					in = null;
					// try to use File based content (random access possible)
					return new AcFileContent(file);
				} else {
					return new AcInputStreamContent(inputStream);
				}
			}
			if (logger.isInfoEnabled()) {
				logger.info("Process archive: {}", file != null ? file.toURI()
						: "[stream]");
			}
		}
		if (archiveInputStream == null) {
			return null;
		}
		ArchiveEntry archiveEntry;
		while ((archiveEntry = archiveInputStream.getNextEntry()) != null) {
			if (!archiveEntry.isDirectory()
					&& (nameFilter == null || nameFilter.apply(archiveEntry
							.getName()))) {
				AcContentMetadata metadata = new AcContentMetadata();
				metadata.setBasename(archiveEntry.getName());
				try {
					String prefix = file == null ? "" : (file.toURI()
							.toString() + "?");
					metadata.setLocation(new URI(prefix
							+ archiveEntry.getName()));
				} catch (URISyntaxException ignored) {
				}
				metadata.setReloadable(false);
				metadata.setSize(archiveEntry.getSize());
				metadata.setLastModified(archiveEntry.getLastModifiedDate()
						.getTime());
				if (acceptEntry(metadata)) {
					AcReaderContent content = new AcInputStreamContent(
							new NoCloseInputStream(archiveInputStream),
							metadata);
					if (metadata.getSize() < sizeLimitWithBuffer) {
						content = new AcCachedContent(content);
					}
					if (logger.isDebugEnabled()) {
						logger.debug("Processing {}", metadata);
					}
					return content;
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Ignore archive entry: {}", archiveEntry.getName());
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("Finish archive: {}", file != null ? file.toURI()
					: "[stream]");
		}
		return null;
	}

	protected boolean acceptEntry(AcContentMetadata entryMetadata) {
		return true;
	}

}
