package com.hp.it.perf.ac.load.content;

import java.io.IOException;
import java.io.Reader;

public class ReloadableReader extends Reader {

	protected Reader reader;
	private StringBuilder cBuffer;
	private int position = 0;
	private boolean cacheable = true;
	private boolean eof = false;
	private boolean notClose = false;

	public ReloadableReader(Reader reader, int initSize) {
		this.reader = reader;
		this.cBuffer = new StringBuilder(initSize);
	}

	public ReloadableReader(Reader reader) {
		this(reader, 8096);
	}

	public void setNotClose(boolean notClose) {
		this.notClose = notClose;
	}

	public void setCacheable(boolean cacheable) {
		this.cacheable = cacheable;
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int rlen;
		if (cBuffer != null && position < cBuffer.length()) {
			rlen = Math.min(len, cBuffer.length() - position);
			cBuffer.getChars(position, position + rlen, cbuf, off);
			position += rlen;
		} else if (eof) {
			return -1;
		} else {
			rlen = reader.read(cbuf, off, len);
			if (rlen != -1) {
				if (cacheable) {
					cBuffer.append(cbuf, off, rlen);
					position += rlen;
				} else {
					// discard buffer in non-cache mode
					cBuffer = null;
				}
			} else {
				eof = true;
			}
		}
		return rlen;
	}

	@Override
	public void close() throws IOException {
		if (!notClose || eof) {
			if (reader != null) {
				reader.close();
				reader = null;
			}
		}
		resetPosition();
	}

	protected void resetPosition() {
		if (cacheable) {
			position = 0;
		}
	}

	public void closeOrReset() throws IOException {
		if (reader instanceof ReloadableReader) {
			((ReloadableReader) reader).resetPosition();
		} else {
			close();
		}
	}

}