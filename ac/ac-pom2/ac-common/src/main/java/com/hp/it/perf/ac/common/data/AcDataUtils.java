package com.hp.it.perf.ac.common.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class AcDataUtils {

	public static final void writeString(DataOutput out, String str)
			throws IOException {
		if (str == null) {
			writeVInt(out, 0);
		} else {
			byte[] bytes = UTF16toUTF8(str);
			// keep length + 1 (0 means null)
			writeVInt(out, bytes.length + 1);
			out.write(bytes);
		}
	}

	// refer to Lucense IndexInput (revised to handle null value)
	public static final String readString(DataInput in) throws IOException {
		int length = readVInt(in);
		if (length == 0) {
			return null;
		} else {
			byte[] bytes = new byte[length - 1];
			in.readFully(bytes);
			return new String(bytes, "UTF-8");
		}
	}

	// refer to Lucense IndexOutput
	public static final void writeVInt(DataOutput out, int i)
			throws IOException {
		for (; (i & 0xFFFFFF80) != 0; i >>>= 7) {
			out.writeByte((byte) (i & 0x7F | 0x80));
		}
		out.writeByte((byte) i);
	}

	public static final void writeVLong(DataOutput out, long l)
			throws IOException {
		for (; (l & 0xFFFFFFFFFFFFFF80L) != 0; l >>>= 7) {
			out.writeByte((byte) (l & 0x7F | 0x80));
		}
		out.writeByte((byte) l);
	}

	// refer to Lucense IndexInput
	public static final int readVInt(DataInput in) throws IOException {
		byte b = in.readByte();
		int i = b & 0x7F;
		for (int shift = 7; (b & 0x80) != 0; shift += 7) {
			b = in.readByte();
			i |= (b & 0x7F) << shift;
		}
		return i;
	}

	public static final long readVLong(DataInput in) throws IOException {
		byte b = in.readByte();
		long l = b & 0x7F;
		for (int shift = 7; (b & 0x80) != 0; shift += 7) {
			b = in.readByte();
			l |= ((long) (b & 0x7F)) << shift;
		}
		return l;
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

	public static void writePrimitiveOrObject(AcDataOutput out, Object value,
			Class<?> type) throws IOException {
		if (type.isPrimitive()) {
			if (type == int.class) {
				out.writeInt((Integer) value);
			} else if (type == long.class) {
				out.writeLong((Long) value);
			} else if (type == double.class) {
				out.writeDouble((Double) value);
			} else if (type == byte.class) {
				out.writeByte((Byte) value);
			} else if (type == short.class) {
				out.writeShort((Short) value);
			} else if (type == float.class) {
				out.writeFloat((Float) value);
			} else if (type == char.class) {
				out.writeChar((Character) value);
			} else if (type == boolean.class) {
				out.writeBoolean((Boolean) value);
			}
		} else if (type == String.class) {
			out.writeString((String) value);
		} else {
			out.writeObject(value);
		}
	}

	public static Object readPrimitiveOrObject(AcDataInput in, Class<?> type)
			throws IOException, ClassNotFoundException {
		Object value;
		if (type.isPrimitive()) {
			if (type == int.class) {
				value = in.readInt();
			} else if (type == long.class) {
				value = in.readLong();
			} else if (type == double.class) {
				value = in.readDouble();
			} else if (type == byte.class) {
				value = in.readByte();
			} else if (type == short.class) {
				value = in.readShort();
			} else if (type == float.class) {
				value = in.readFloat();
			} else if (type == char.class) {
				value = in.readChar();
			} else if (type == boolean.class) {
				value = in.readBoolean();
			} else {
				value = null;
			}
		} else if (type == String.class) {
			value = in.readString();
		} else {
			value = in.readObject();
		}
		return value;
	}

}
