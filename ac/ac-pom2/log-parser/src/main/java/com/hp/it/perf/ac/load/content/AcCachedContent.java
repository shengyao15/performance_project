package com.hp.it.perf.ac.load.content;

import java.io.IOException;
import java.io.Reader;

public class AcCachedContent implements AcReaderContent {

	private AcReaderContent content;
	private ReloadableReader reader;

	public AcCachedContent(AcReaderContent content) {
		this.content = content;
	}

	@Override
	public Reader getContent() throws IOException {
		if (reader == null) {
			long eSize = content.getMetadata().getSize();
			reader = new ReloadableReader(content.getContent(), Math.min(
					1024 * 1024 * 1, Math.max(8096, (int) eSize)));
		}
		reader.resetPosition();
		return reader;
	}

	@Override
	public AcContentMetadata getMetadata() {
		AcContentMetadata metadata = content.getMetadata();
		metadata = new AcContentMetadata(metadata);
		metadata.setReloadable(true);
		return metadata;
	}

}
