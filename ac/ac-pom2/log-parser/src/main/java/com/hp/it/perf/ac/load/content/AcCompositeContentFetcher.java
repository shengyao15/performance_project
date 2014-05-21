package com.hp.it.perf.ac.load.content;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

public class AcCompositeContentFetcher implements AcContentFetcher {

	private Iterator<AcContentFetcher> fetchers;
	private AcContentFetcher current;

	public AcCompositeContentFetcher(AcContentFetcher... contentFetchers) {
		fetchers = Arrays.asList(contentFetchers).iterator();
	}

	@Override
	public void close() throws IOException {
		while (current != null) {
			current.close();
			current = null;
			if (fetchers.hasNext()) {
				current = fetchers.next();
			}
		}
	}

	@Override
	public AcReaderContent next() throws IOException {
		while (true) {
			if (current == null) {
				if (fetchers.hasNext()) {
					current = fetchers.next();
				} else {
					return null;
				}
			}
			AcReaderContent content = current.next();
			if (content == null) {
				current.close();
				current = null;
			} else {
				return content;
			}
		}
	}

}
