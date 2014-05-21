package com.hp.it.perf.ac.common.load;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class AcLoadDataUtils {

	public static final void writeString(DataOutput output, String str)
			throws IOException {
		byte[] bytes = UTF16toUTF8(str);
		writeVInt(output, bytes.length);
		output.write(bytes);
	}

	// refer to Lucense IndexInput
	public static final String readString(DataInput input) throws IOException {
		int length = readVInt(input);
		byte[] bytes = new byte[length];
		input.readFully(bytes);
		return new String(bytes, "UTF-8");
	}

	// refer to Lucense IndexOutput
	public static final void writeVInt(DataOutput output, int i)
			throws IOException {
		for (; (i & 0xFFFFFF80) != 0; i >>>= 7) {
			output.writeByte((byte) (i & 0x7F | 0x80));
		}
		output.writeByte((byte) i);
	}

	// refer to Lucense IndexInput
	public static final int readVInt(DataInput input) throws IOException {
		byte b = input.readByte();
		int i = b & 0x7F;
		for (int shift = 7; (b & 0x80) != 0; shift += 7) {
			b = input.readByte();
			i |= (b & 0x7F) << shift;
		}
		return i;
	}

	// refer from Lucense UnicodeUtil
	private static byte[] UTF16toUTF8(String s) {
		int offset = 0;
		int length = s.length();
		int end = offset + length;
		byte out[] = new byte[length];
		int upto = 0;
		for (int i = offset; i < end; i++) {
			int code = s.charAt(i);
			if (upto + 4 > out.length) {
				byte newOut[] = new byte[2 * out.length];
				System.arraycopy(out, 0, newOut, 0, upto);
				out = newOut;
			}
			if (code < 128) {
				out[upto++] = (byte) code;
				continue;
			}
			if (code < 2048) {
				out[upto++] = (byte) (192 | code >> 6);
				out[upto++] = (byte) (128 | code & 63);
				continue;
			}
			if (code < 55296 || code > 57343) {
				out[upto++] = (byte) (224 | code >> 12);
				out[upto++] = (byte) (128 | code >> 6 & 63);
				out[upto++] = (byte) (128 | code & 63);
				continue;
			}
			if (code < 56320 && i < end - 1) {
				int utf32 = s.charAt(i + 1);
				if (utf32 >= 56320 && utf32 <= 57343) {
					utf32 = (code - 55232 << 10) + (utf32 & 1023);
					i++;
					out[upto++] = (byte) (240 | utf32 >> 18);
					out[upto++] = (byte) (128 | utf32 >> 12 & 63);
					out[upto++] = (byte) (128 | utf32 >> 6 & 63);
					out[upto++] = (byte) (128 | utf32 & 63);
					continue;
				}
			}
			out[upto++] = -17;
			out[upto++] = -65;
			out[upto++] = -67;
		}

		byte[] newout = new byte[upto];
		System.arraycopy(out, 0, newout, 0, upto);

		return newout;
	}

}
