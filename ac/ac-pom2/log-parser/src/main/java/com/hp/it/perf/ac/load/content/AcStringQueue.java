package com.hp.it.perf.ac.load.content;

import java.io.IOException;

import com.hp.it.perf.ac.load.content.AcStringQueue.StringQueue;

public class AcStringQueue implements AcContent<StringQueue> {

	public static interface StringQueue {

		public void putLine(String line);
		
		public void markEOB();
		
		public void close();

		public String pollLine();

		public String peekLine();

		public boolean isClosed();

		public boolean wasEOB();

		public boolean isEmpty();
		
		public int getModCount();
	}

	private AcContentMetadata metadata;
	private StringQueue queue;

	public AcStringQueue(AcContentMetadata metadata, StringQueue queue) {
		this.metadata = metadata;
		this.queue = queue;
	}

	@Override
	public StringQueue getContent() throws IOException {
		return queue;
	}

	@Override
	public AcContentMetadata getMetadata() {
		return metadata;
	}

}
