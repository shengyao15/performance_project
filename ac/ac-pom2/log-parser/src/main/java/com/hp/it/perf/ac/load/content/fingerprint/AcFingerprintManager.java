package com.hp.it.perf.ac.load.content.fingerprint;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class AcFingerprintManager {

	private static class ChunkGeneator {

		private static final int[] CHUNK_BASE = { 1, 1 << 4, 1 << 8, 1 << 10,
				1 << 11 };

		private static final int CHUNK_SIZE = 1024;

		private int index = 0;

		private long chunkSize = 0L;

		public long next() {
			if (index < CHUNK_BASE.length) {
				chunkSize = CHUNK_SIZE * CHUNK_BASE[index];
			} else {
				chunkSize <<= 1;
				if (chunkSize < 0) {
					// overflowed
					chunkSize = Long.MAX_VALUE;
				}
			}
			index++;
			return chunkSize;
		}

	}

	private static class FingerprintGenerator {
		private long length;
		private long maxLength;
		private MessageDigest md;

		{
			try {
				md = MessageDigest.getInstance("SHA-1");
			} catch (NoSuchAlgorithmException e) {
				throw new IllegalArgumentException("sha-1 is not available", e);
			}
		}

		public void setMaxLength(long maxLength) {
			this.maxLength = maxLength;
		}

		public int update(byte[] data, int offset, int len) {
			if (offset < 0) {
				throw new ArrayIndexOutOfBoundsException("invalid offset: "
						+ offset);
			}
			if (len < 0 || data.length < len) {
				throw new ArrayIndexOutOfBoundsException("invalid len: " + len);
			}
			int updated;
			if (length < maxLength) {
				if (length + len >= maxLength) {
					// only a part of
					updated = (int) (maxLength - length);
				} else {
					updated = len;
				}
			} else {
				updated = 0;
			}
			if (updated > 0) {
				md.update(data, offset, updated);
			}
			length += updated;
			return updated;
		}

		public byte[] getDigestValue() {
			return md.digest();
		}

		public void setDigestValue(byte[] bs) {
			md.reset();
			md.update(bs);
		}

	}

	public static ContentFingerprint generate(InputStream input)
			throws IOException {
		ChunkGeneator chunkGen = new ChunkGeneator();
		FingerprintGenerator fpGen = new FingerprintGenerator();
		List<ContentFingerprint.FingerprintFragement> fragments = new ArrayList<ContentFingerprint.FingerprintFragement>();
		byte[] buf = new byte[8096];
		int len;
		long totalLen = 0;
		long pendingUpdated = 0;

		long maxLength = chunkGen.next();
		fpGen.setMaxLength(maxLength);
		fpGen.setDigestValue(new byte[0]);
		while ((len = input.read(buf)) != -1) {
			if (len == 0)
				continue;
			int updated = 0;
			int totalUpdated = 0;
			while ((updated = fpGen.update(buf, totalUpdated, len)) < len) {
				totalLen += updated;
				// no more updated as reach max length
				ContentFingerprint.FingerprintFragement fpf = new ContentFingerprint.FingerprintFragement();
				fpf.setLength(totalLen);
				fpf.setOffset(0);
				byte[] footprint = fpGen.getDigestValue();
				fpf.setFingerprint(footprint);
				fragments.add(fpf);

				pendingUpdated = 0;
				maxLength = chunkGen.next();
				fpGen.setMaxLength(maxLength);
				// keep last result to be considered
				fpGen.setDigestValue(footprint);
				len -= updated;
				totalUpdated += updated;
				updated = 0;
			}
			totalLen += updated;
			pendingUpdated += updated;
		}

		if (pendingUpdated > 0) {
			// last part
			ContentFingerprint.FingerprintFragement fpf = new ContentFingerprint.FingerprintFragement();
			fpf.setLength(totalLen);
			fpf.setOffset(0);
			fpf.setFingerprint(fpGen.getDigestValue());
			fragments.add(fpf);
		}

		ContentFingerprint cfp = new ContentFingerprint();
		cfp.setLength(totalLen);
		cfp.setFragements(fragments
				.toArray(new ContentFingerprint.FingerprintFragement[fragments
						.size()]));

		return cfp;
	}

}
