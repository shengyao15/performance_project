package com.hp.it.perf.ac.load.content;

import java.io.IOException;
import java.io.StringReader;

import com.hp.it.perf.ac.load.content.AcContentLine.NextLineStatus;
import com.hp.it.perf.ac.load.content.AcStringQueue.StringQueue;

public class AcContentLineStringQueue implements AcContentLineReadable {

	private StringQueue buffer;

	private AcContentLineReadable reader = new AcContentLineReader(
			new StringReader("")) {

		private int lineNumber = 0;

		private int newLineIndex = -1;

		private int lastModCount = 0;

		private String lastLine;

		@Override
		protected synchronized String internalReadLine() {
			lastModCount = buffer.getModCount();
			while (true) {
				if (lastLine == null) {
					lastLine = buffer.pollLine();
				}
				String line = lastLine;
				// check if EOF or EOB
				if (line == null) {
					if (buffer.isClosed()) {
						return null;
					}
					if (buffer.wasEOB()) {
						if (buffer.isEmpty()) {
							return null;
						} else {
							continue;
						}
					}
					// no data
					return null;
				}
				// check if line is multiple
				int lastNewLineIndex = newLineIndex + 1;
				newLineIndex = line.indexOf('\n', lastNewLineIndex);
				lineNumber++;
				if (newLineIndex >= 0) {
					return line.substring(lastNewLineIndex, newLineIndex);
				} else {
					lastLine = null;
					return line.substring(lastNewLineIndex);
				}
			}
		}

		@Override
		protected int internalgetLineNumber() {
			return lineNumber;
		}

		@Override
		public synchronized AcContentLine getContentLine() {
			AcContentLine contentLine = super.getContentLine();
			if (lastModCount != buffer.getModCount()) {
				if (contentLine != null && contentLine.getNextLine() == null) {
					// if next line was null (unknown, or eof, or eob)
					// check again
					try {
						super.retryNextLine();
					} catch (IOException never) {
					}
					contentLine = super.getContentLine();
				}
			}
			if (contentLine != null && contentLine.getNextLine() == null) {
				String line = lastLine == null ? buffer.peekLine() : lastLine;
				if (line == null) {
					if (buffer.wasEOB()) {
						contentLine
								.setNextLineStatus(NextLineStatus.StartOfBlock);
					} else if (!buffer.isClosed()) {
						contentLine.setNextLineStatus(NextLineStatus.Unknown);
					}
				}
			}
			return contentLine;
		}

	};

	public AcContentLineStringQueue(AcStringQueue buffer) throws IOException {
		this.buffer = buffer.getContent();
	}

	@Override
	public void close() throws IOException {
		buffer.close();
	}

	@Override
	public AcContentLine getContentLine() {
		return reader.getContentLine();
	}

	@Override
	public void readLines() {
		try {
			reader.readLines();
		} catch (IOException never) {
		}
	}

	@Override
	public void readMoreLines() {
		try {
			reader.readMoreLines();
		} catch (IOException never) {
		}
	}

}
