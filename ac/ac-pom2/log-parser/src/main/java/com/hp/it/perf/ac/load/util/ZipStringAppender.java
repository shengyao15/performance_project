package com.hp.it.perf.ac.load.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class ZipStringAppender implements Appendable {

	private DataOutputStream out;
	private ByteArrayOutputStream baOut;
	private byte[] compressedData = null;
	private int length;

	public ZipStringAppender() {
		initOutputStreams();
	}

	private void initOutputStreams() {
		this.baOut = new ByteArrayOutputStream();
		this.out = new DataOutputStream(new BufferedOutputStream(
				new DeflaterOutputStream(baOut), 512));
	}

	@Override
	public synchronized ZipStringAppender append(CharSequence csq) {
		return append(csq, 0, csq.length());
	}

	@Override
	public synchronized ZipStringAppender append(CharSequence csq, int start,
			int end) {
		char[] chars = new char[end - start];
		for (int i = start; i < end; i++) {
			chars[i - start] = csq.charAt(i);
		}
		for (char c : chars) {
			append0(c);
		}
		return this;
	}

	@Override
	public synchronized ZipStringAppender append(char c) {
		append0(c);
		return this;
	}

	public ZipStringAppender append(Object obj) {
		return append(String.valueOf(obj));
	}

	private final void append0(char c) {
		// check if has previous compressed data
		if (compressedData != null) {
			byte[] bytes = compressedData;
			String previous = decompress(bytes, length);
			// mark compress data is empty
			compressedData = null;
			length = 0;
			// call back again
			this.append(previous);
		}
		try {
			out.writeChar(c);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private byte[] toCompressedBytes() {
		if (compressedData == null) {
			try {
				out.close();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
			byte[] bytes = baOut.toByteArray();
			initOutputStreams();
			return bytes;
		} else {
			return compressedData;
		}
	}

	public synchronized String toString() {
		if (out.size() > 0) {
			length = out.size() / 2;
			compressedData = toCompressedBytes();
			return decompress(compressedData, length);
		} else {
			return "";
		}
	}

	private String decompress(byte[] bytes, int len) {
		try {
			DataInputStream input = new DataInputStream(
					new BufferedInputStream(new InflaterInputStream(
							new ByteArrayInputStream(bytes))));
			char[] chars = new char[len];
			for (int i = 0; i < len; i++) {
				chars[i] = input.readChar();
			}
			input.close();
			return new String(chars);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

}
