package com.hp.it.perf.ac.load.content;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

public class AcInputStreamContent implements AcReaderContent {

	private Reader reader;
	private AcContentMetadata metadata;

	public AcInputStreamContent(InputStream in) {
		this(new InputStreamReader(in));
	}

	public AcInputStreamContent(InputStream in, String enc) {
		this(toReader(in, enc));
	}

	public AcInputStreamContent(InputStream in, AcContentMetadata metadata) {
		this(in);
		this.metadata = metadata;
	}

	private static Reader toReader(InputStream in, String enc) {
		try {
			return new InputStreamReader(in, enc);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	protected AcInputStreamContent(Reader reader) {
		this.reader = reader;
		this.metadata = new AcContentMetadata();
		metadata.setBasename("");
		metadata.setLastModified(System.currentTimeMillis());
		try {
			metadata.setLocation(new URI("system:///fd/0"));
		} catch (URISyntaxException e) {
			throw new Error(e);
		}
	}

	@Override
	public Reader getContent() throws IOException {
		return reader;
	}

	@Override
	public AcContentMetadata getMetadata() {
		return metadata;
	}

}
