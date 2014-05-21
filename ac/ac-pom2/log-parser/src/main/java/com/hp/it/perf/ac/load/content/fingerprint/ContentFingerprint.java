package com.hp.it.perf.ac.load.content.fingerprint;

import java.util.Arrays;

public class ContentFingerprint {

	private long length;

	private FingerprintFragement[] fragements;

	public static class FingerprintFragement {

		private long offset;

		private long length;

		// for final confirm
		private byte[] fingerprint;

		public long getLength() {
			return length;
		}

		public void setLength(long length) {
			this.length = length;
		}

		public byte[] getFingerprint() {
			return fingerprint;
		}

		public void setFingerprint(byte[] fingerprint) {
			this.fingerprint = fingerprint;
		}

		public long getOffset() {
			return offset;
		}

		public void setOffset(long offset) {
			this.offset = offset;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (offset ^ (offset >>> 32));
			result = prime * result + (int) (length ^ (length >>> 32));
			result = prime * result + Arrays.hashCode(fingerprint);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof FingerprintFragement))
				return false;
			FingerprintFragement other = (FingerprintFragement) obj;
			if (length != other.length)
				return false;
			if (!Arrays.equals(fingerprint, other.fingerprint))
				return false;
			if (offset != other.offset)
				return false;
			return true;
		}

		public String getStringFingerprint() {
			byte[] fp = getFingerprint();
			char[] ca = new char[fp.length * 2];
			for (int i = 0; i < fp.length; i++) {
				ca[i << 1] = Character.forDigit((fp[i] & 0xF0) >> 4, 16);
				ca[(i << 1) + 1] = Character.forDigit(fp[i] & 0xF, 16);
			}
			return new String(ca);
		}

		public void setStringFingerprint(String s)
				throws IllegalArgumentException {
			char[] ca = s.toCharArray();
			if (ca.length % 2 != 0) {
				throw new IllegalArgumentException("string is not hex-format: "
						+ s);
			}
			byte[] ba = new byte[ca.length / 2];
			for (int i = 0; i < ba.length; i++) {
				int hc = Character.digit(ca[i << 1], 16);
				if (hc < 0) {
					throw new IllegalArgumentException("invalid hex char: " + s);
				}
				int lc = Character.digit(ca[(i << 1) + 1], 16);
				if (lc < 0) {
					throw new IllegalArgumentException("invalid hex char: " + s);
				}
				ba[i] = (byte) ((hc << 4) + lc);
			}
			setFingerprint(ba);
		}

		@Override
		public String toString() {
			return String
					.format("FootprintFragement [offset=%s, length=%s, fingerprint=%s]",
							offset, length, getStringFingerprint());
		}

	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public FingerprintFragement[] getFragements() {
		return fragements;
	}

	public void setFragements(FingerprintFragement[] fragements) {
		this.fragements = fragements;
	}

}
