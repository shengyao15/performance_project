package com.hp.it.perf.ac.common.data.store;

import java.io.IOException;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class FixBlockStore implements BufferedStore {

	// * File Header: (8 bytes) -- in first block
	// * - MAGIC WORD: 0xACFB => 2 bytes
	// * - VERSION: 0x01 => 1 byte
	// * - CHECKSUM FLAG: 0x00/0x01 => 1 byte (disable or enable)
	// * - SEED: 0xFFFF => 2 bytes
	// * - BLOCK SIZE: (16-based = 2^4) 1 ~ 65536 (2^16) [16B ~ 1MB] => 2 bytes

	// /*
	// * Block structure: // Block is 1 based
	// * - PRE BLOCK NO: 0 ~ 2G (Integer.MAX) => 4 bytes // 0 - first block
	// * - OTHERS: DATA => BLOCK SIZE - 8 (or 4)
	// * - CHECKSUM-32: CRC-32 (All except this in block)=> 4 bytes
	// */
	private int blockSize;
	private int dataBlockSize;

	private BitSet blocks = new BitSet();
	private byte[] blockBuffer;

	private RandomAccessStore store;
	private Checksum checksum;
	private int storeSeed = new Random().nextInt(0x0FFFF);

	public FixBlockStore(RandomAccessStore store, int blockSize,
			boolean checksum) {
		if (blockSize > 0xFFFF) {
			// len is encoded as 2 bytes in key
			// so must keep blocksize less than 2 bytes (65535)
			throw new IllegalArgumentException(
					"block size should less than: " + 0xFFFF);
		}
		this.store = store;
		this.blockSize = blockSize;
		this.checksum = checksum ? new CRC32() : null;
		this.dataBlockSize = blockSize - (checksum ? 8 : 4);
		if (dataBlockSize <= 0) {
			throw new IllegalArgumentException("block size is too small: "
					+ blockSize);
		}
		this.blockBuffer = new byte[blockSize];
		blocks.set(0); // set first occurred, because of 1 based
	}

	public FixBlockStore(RandomAccessStore store) {
		this(store, 512, true);
	}

	@Override
	public void close() throws IOException {
		store.close();
	}

	@Override
	public long put(byte[] data, int offset, int len) throws IOException {
		// pure new block
		return putNew(data, offset, len, 0);
	}

	private long putNew(byte[] data, int offset, int len, int preBlockNo)
			throws IOException {
		// pure new block
		int savedLen;
		int blockNo;
		do {
			blockNo = getAvailableBlock();
			savedLen = saveBlock(data, offset, len, blockNo, preBlockNo);
			offset += savedLen;
			len -= savedLen;
			preBlockNo = blockNo;
		} while (len > 0);
		return encodeKey(blockNo, savedLen);
	}

	private long encodeKey(int blockNo, int len) {
		// 4-bytes: blockNo
		// 2-bytes: len (2-bytes)
		// 2-bytes: 1-2 bytes ^ 3-4 bytes ^ 5-6 bytes ^ seed
		int b12 = blockNo >>> 16;
		int b34 = blockNo & 0x0FFFF;
		int b56 = len & 0x0FFFF;
		int b78 = (b12 ^ b34 ^ b56 ^ storeSeed) & 0x0FFFF;
		return ((long) blockNo << 32)
				| (long) ((b56 << 16 | b78) & 0x0FFFFFFFF);
	}

	private void decodeKey(long key, int[] decodedValue) throws IOException {
		int blockNo = (int) (key >>> 32);
		int b12 = blockNo >>> 16;
		int b34 = blockNo & 0x0FFFF;
		int b56 = (int) (key << 32 >>> 48);
		int b78 = (int) (key & 0x0FFFF);
		if ((b12 ^ b34 ^ b56 ^ b78 ^ storeSeed) != 0) {
			throw new IOException("invalid key (not match seed): " + key);
		}
		int len = b56;
		if (len < 0) {
			throw new IOException("invalid key (len<0): " + key);
		}
		if (len > dataBlockSize) {
			throw new IOException("invalid key (len>block size): " + key);
		}
		decodedValue[0] = blockNo;
		decodedValue[1] = len;
	}

	private int saveBlock(byte[] data, int offset, int len, int blockNo,
			int preBlockNo) throws IOException {
		int tlen = 0;
		// pre blockNo
		writeInt(blockBuffer, 0, preBlockNo);
		tlen += 4;
		int vlen = Math.min(len, dataBlockSize);
		// block can be full
		System.arraycopy(data, offset, blockBuffer, 4, vlen);
		tlen += vlen;
		// write check sum if enable
		if (checksum != null) {
			checksum.reset();
			checksum.update(blockBuffer, 0, tlen);
			writeInt(blockBuffer, tlen, (int) checksum.getValue());
			// add checksum 4 bytes
			tlen += 4;
		}
		// get position based on block no
		store.position(blockNo * blockSize);
		store.write(blockBuffer, 0, tlen);
		return vlen;
	}

	private static void writeInt(byte[] data, int offset, int v) {
		data[offset++] = (byte) ((v >>> 24) & 0xFF);
		data[offset++] = (byte) ((v >>> 16) & 0xFF);
		data[offset++] = (byte) ((v >>> 8) & 0xFF);
		data[offset++] = (byte) ((v >>> 0) & 0xFF);
	}

	private int readInt(byte[] data, int offset) {
		int ch1 = 0xFF & data[offset++];
		int ch2 = 0xFF & data[offset++];
		int ch3 = 0xFF & data[offset++];
		int ch4 = 0xFF & data[offset++];
		return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
	}

	private int getAvailableBlock() {
		int blockNo = blocks.nextClearBit(0);
		blocks.set(blockNo);
		return blockNo;
	}

	@Override
	public byte[] get(long key) throws IOException {
		return get(key, new BitSet());
	}

	private int loadBlock(int blockNo, byte[] data, int offset, int len)
			throws IOException {
		int tlen = len + 4; // block no + data
		if (checksum != null) {
			tlen += 4;
		}
		store.position(blockNo * blockSize);
		store.readFully(blockBuffer, 0, tlen);
		int preBlockNo = readInt(blockBuffer, 0);
		System.arraycopy(blockBuffer, 4, data, offset, len);
		if (checksum != null) {
			int checksumValue = readInt(blockBuffer, 4 + len);
			// checksum
			checksum.reset();
			checksum.update(blockBuffer, 0, 4 + len);
			if (((int) checksum.getValue()) != checksumValue) {
				throw new IOException("incorrect checksum: "
						+ (int) checksum.getValue() + " - " + checksumValue);
			}
		}
		return preBlockNo;
	}

	private int destroyPreBlockNo(int blockNo) throws IOException {
		int offset = blockNo * blockSize;
		store.position(offset);
		byte[] intBytes = new byte[4];
		store.readFully(intBytes, 0, intBytes.length);
		int preBlockNo = readInt(intBytes, 0);
		store.position(offset);
		store.clear(blockSize);
		return preBlockNo;
	}

	@Override
	public long init(int initSize) throws IOException {
		return put(new byte[0], 0, 0);
	}

	@Override
	public long append(long key, byte[] newData, int offset, int len)
			throws IOException {
		int[] decodedValue = new int[2];
		decodeKey(key, decodedValue);
		int blockNo = decodedValue[0];
		int lenInBlock = decodedValue[1];
		// if checksum enable, need read back its value
		int preBlockNo = blockNo;
		if (lenInBlock < dataBlockSize) {
			// load back block (with enough data)
			byte[] data;
			data = new byte[Math.min(len + lenInBlock, dataBlockSize)];
			preBlockNo = loadBlock(blockNo, data, 0, lenInBlock);
			System.arraycopy(newData, offset, data, lenInBlock, data.length
					- lenInBlock);
			saveBlock(data, 0, data.length, blockNo, preBlockNo);
			preBlockNo = blockNo;
			offset += data.length - lenInBlock;
			len -= data.length - lenInBlock;
			if (len == 0) {
				return encodeKey(blockNo, data.length);
			}
		}
		// otherwise, more new blocks need to created
		return putNew(newData, offset, len, preBlockNo);
	}

	@Override
	public int get(long key, int position, byte[] data) throws IOException {
		// quick implementation
		if (position < 0) {
			throw new IndexOutOfBoundsException("position less than 0: "
					+ position);
		}
		byte[] allData = get(key);
		if (position > allData.length) {
			throw new IndexOutOfBoundsException(
					"position great than data size: " + position + ">"
							+ allData.length);
		}
		int len = Math.min(data.length, allData.length - position);
		System.arraycopy(allData, position, data, 0, len);
		return len;
	}

	@Override
	public void destroy(long key) throws IOException {
		BitSet changeSet = (BitSet) blocks.clone();

		int[] decodedValue = new int[2];
		decodeKey(key, decodedValue);
		int blockNo = decodedValue[0];
		do {
			changeSet.clear(blockNo);
			blockNo = destroyPreBlockNo(blockNo);
		} while (blockNo != 0);

		blocks = changeSet;
	}

	@Override
	public byte[] remove(long key) throws IOException {
		BitSet changeSet = (BitSet) blocks.clone();
		byte[] data = get(key, changeSet);
		blocks = changeSet;
		return data;
	}

	private byte[] get(long key, BitSet changeSet) throws IOException {
		int[] decodedValue = new int[2];
		decodeKey(key, decodedValue);
		int blockNo = decodedValue[0];
		int lenInBlock = decodedValue[1];
		byte[] data;
		LinkedList<byte[]> dataBuffer = new LinkedList<byte[]>();
		int tlen = 0;
		do {
			data = new byte[lenInBlock];
			changeSet.clear(blockNo);
			blockNo = loadBlock(blockNo, data, 0, data.length);
			dataBuffer.offerFirst(data);
			tlen += data.length;
			lenInBlock = dataBlockSize;
		} while (blockNo != 0);
		byte[] tBytes = new byte[tlen];
		int index = 0;
		while ((data = dataBuffer.pollFirst()) != null) {
			System.arraycopy(data, 0, tBytes, index, data.length);
			index += data.length;
		}
		return tBytes;
	}

	public int[] blocksInUse() throws IOException {
		int[] inUse = new int[blocks.cardinality()];
		int j = 0;
		for (int i = blocks.nextSetBit(0); i >= 0; i = blocks.nextSetBit(i + 1)) {
			inUse[j++] = i;
		}
		return inUse;
	}

}
