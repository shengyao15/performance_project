package com.hp.it.perf.ac.load.content;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;

public class AcStringsContent implements AcReaderContent {

	private String[] lines;

	private AcContentMetadata metadata;

	public AcStringsContent(String... lines) {
		this.lines = lines;
		this.metadata = new AcContentMetadata();
		this.metadata.setBasename("memory");
		this.metadata.setLastModified(System.currentTimeMillis());
		long size = 0;
		for (String s : lines) {
			size += s.length() + 1;
		}
		this.metadata.setSize(size);
		this.metadata.setReloadable(true);
		String etag = "test";
		this.metadata.setSignature(etag);
		try {
			this.metadata.setLocation(new URI("ac://memory/" + etag));
		} catch (URISyntaxException e) {
			throw new Error(e);
		}
	}

	@Override
	public Reader getContent() throws IOException {
		return new InputStreamReader(new InputStream() {

			private int index = 0;
			private int position = 0;

			@Override
			public int read() throws IOException {
				while (index < lines.length) {
					String line = lines[index];
					if (position > line.length()) {
						position = 0;
						index++;
					} else if (position == line.length()) {
						position++;
						if (index < lines.length - 1) {
							// not last line
							return '\n';
						}
					} else {
						return line.charAt(position++);
					}
				}
				return -1;
			}
		});
	}

	@Override
	public AcContentMetadata getMetadata() {
		return metadata;
	}

}
