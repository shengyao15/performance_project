package com.hp.it.perf.ac.common.data.store;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RandomAccessBytesStore implements RandomAccessStore {

	private static class BytesBlock {
		private byte[] block;
		private int length;
		private final int blockSize;

		public BytesBlock(int blockSize) {
			this.length = 0;
			this.blockSize = blockSize;
		}

		public int get(int startIndex) {
			if (startIndex >= length) {
				return -1;
			}
			if (block == null) {
				// empty block
				return 0;
			}
			int b = 0xFF & block[startIndex];
			return b;
		}

		public void setLength(int length) {
			if (length > blockSize) {
				throw new IndexOutOfBoundsException(length + ">" + blockSize);
			}
			if (length < this.length) {
				// truncate, clear data
				if (block != null) {
					Arrays.fill(block, length, this.length, (byte) 0);
				}
			}
			this.length = length;
		}

		public int getLength() {
			return length;
		}

		public int get(int startIndex, byte[] b, int off, int len) {
			if (startIndex >= length) {
				return -1;
			}
			int size = Math.min(len, length - startIndex);
			if (block == null) {
				Arrays.fill(b, off, off + size, (byte) 0);
			} else {
				System.arraycopy(block, startIndex, b, off, size);
			}
			return size;
		}

		public int put(int startIndex, byte[] b, int off, int len) {
			if (startIndex > length) {
				throw new IndexOutOfBoundsException(startIndex + ">" + length);
			}
			if (startIndex >= blockSize) {
				throw new IndexOutOfBoundsException(startIndex + ">="
						+ blockSize);
			}
			int size = Math.min(len, blockSize - startIndex);
			if (block == null) {
				// check if data is full zero
				boolean empty = true;
				for (int i = off, n = off + size; i < n && empty; i++) {
					if (b[i] != 0) {
						empty = false;
					}
				}
				if (!empty) {
					block = new byte[blockSize];
				}
			}
			if (block != null) {
				System.arraycopy(b, off, block, startIndex, size);
			}
			this.length = startIndex + size;
			return size;
		}

		public int bytesCount() {
			return block == null ? 0 : block.length;
		}

		public int clear(int startIndex, int len) {
			if (startIndex > length) {
				throw new IndexOutOfBoundsException(startIndex + ">" + length);
			}
			if (startIndex >= blockSize) {
				throw new IndexOutOfBoundsException(startIndex + ">="
						+ blockSize);
			}
			int size = Math.min(len, blockSize - startIndex);
			if (block != null) {
				if (size < blockSize) {
					Arrays.fill(block, startIndex, startIndex + size, (byte) 0);
					// check if data is full zero
					boolean empty = true;
					for (int i = 0, n = block.length; i < n && empty; i++) {
						if (block[i] != 0) {
							empty = false;
						}
					}
					if (empty) {
						block = null;
					}
				} else {
					block = null;
				}
			}
			return size;
		}

	}

	private long position;

	private final int blockSize;

	private List<BytesBlock> blocks;

	public RandomAccessBytesStore(int blockSize) {
		if (blockSize <= 0) {
			throw new IllegalArgumentException("invalid block size: "
					+ blockSize);
		}
		this.blockSize = blockSize;
		blocks = new ArrayList<BytesBlock>();
		blocks.add(newBlock());
		position = 0;
	}

	public RandomAccessBytesStore() {
		// 8K
		this(1024 * 8);
	}

	private BytesBlock newBlock() {
		return new BytesBlock(blockSize);
	}

	@Override
	public synchronized int read() throws IOException {
		return read0();
	}

	@Override
	public synchronized int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public synchronized int read(byte[] b, int off, int len) throws IOException {
		int index = (int) (position / blockSize);
		int offset = (int) (position % blockSize);
		int total = 0;
		while (index < blocks.size() && len > 0) {
			BytesBlock block = blocks.get(index);
			int size = block.get(offset, b, off, len);
			if (size < 0) {
				if (total == 0) {
					return size;
				} else {
					return total;
				}
			}
			position += size;
			total += size;
			index++;
			offset = 0;
			len -= size;
			off += size;
		}
		return total;
	}

	private int read0() {
		int index = (int) (position / blockSize);
		int offset = (int) (position % blockSize);
		BytesBlock block = blocks.get(index);
		int b = block.get(offset);
		if (b >= 0) {
			position++;
		}
		return b;
	}

	@Override
	public synchronized void setLength(long length) throws IOException {
		int blockCount = (int) (length / blockSize);
		blockCount++;
		int blockOffset = (int) (length % blockSize);
		if (blockCount > blocks.size()) {
			// increase last one to full
			blocks.get(blocks.size() - 1).setLength(blockSize);
			while (blockCount > blocks.size()) {
				BytesBlock newBlock = new BytesBlock(blockSize);
				newBlock.setLength(blockSize);
				blocks.add(newBlock);
			}
		} else if (blockCount < blocks.size()) {
			// truncate data
			while (blockCount < blocks.size()) {
				blocks.remove(blocks.size() - 1);
			}
		}
		blocks.get(blocks.size() - 1).setLength(blockOffset);
		// reset position if large than new length
		long newLength = length();
		if (position > newLength) {
			position = newLength;
		}
	}

	@Override
	public synchronized long length() {
		return blocks.size() * blockSize
				+ blocks.get(blocks.size() - 1).getLength();
	}

	public synchronized long bytesCount() {
		long counts = 0;
		for (BytesBlock block : blocks) {
			counts += block.bytesCount();
		}
		return counts;
	}

	@Override
	public long position() {
		return position;
	}

	@Override
	public synchronized void write(byte[] b, int off, int len)
			throws IOException {
		int blockIndex = (int) (position / blockSize);
		int blockOffset = (int) (position % blockSize);
		while (len > 0) {
			BytesBlock block;
			if (blockIndex < blocks.size()) {
				block = blocks.get(blockIndex);
			} else {
				block = newBlock();
				blocks.add(block);
			}
			int size = block.put(blockOffset, b, off, len);
			len -= size;
			off += size;
			blockOffset = 0;
			position += size;
			blockIndex++;
		}
	}

	@Override
	public synchronized void position(long position) throws IOException {
		if (position < 0) {
			throw new IOException("invalid position: " + position);
		}
		if (position > this.position) {
			// update blocks
			int blockIndex = (int) (position / blockSize);
			blockIndex++;
			int blockOffset = (int) (position % blockSize);
			if (blockIndex > blocks.size()) {
				// increase last one to full
				blocks.get(blocks.size() - 1).setLength(blockSize);
				while (blockIndex > blocks.size()) {
					BytesBlock newBlock = new BytesBlock(blockSize);
					newBlock.setLength(blockSize);
					blocks.add(newBlock);
				}
			}
			BytesBlock lastBlock = blocks.get(blocks.size() - 1);
			if (blockOffset > lastBlock.getLength()) {
				lastBlock.setLength(blockOffset);
			}
		}
		this.position = position;
	}

	@Override
	public void close() {
		// no-op
	}

	@Override
	public synchronized int skip(int n) throws IOException {
		if (n <= 0) {
			return 0;
		}
		long pos = position();
		long newpos = Math.min(pos + n, length());
		position(newpos);
		return (int) (newpos - pos);
	}

	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		if (len < 0)
			throw new IndexOutOfBoundsException();
		int n = 0;
		while (n < len) {
			int count = read(b, off + n, len - n);
			if (count < 0)
				throw new EOFException();
			n += count;
		}
	}

	@Override
	public synchronized int clear(int n) throws IOException {
		int index = (int) (position / blockSize);
		int offset = (int) (position % blockSize);
		int total = 0;
		while (index < blocks.size() && n > 0) {
			BytesBlock block = blocks.get(index);
			int size = block.clear(offset, n);
			position += size;
			total += size;
			index++;
			offset = 0;
			n -= size;
		}
		// deallocate blocks
		// ignore first block
		for (int i = blocks.size() - 1; i > 0; i--) {
			BytesBlock block = blocks.get(i);
			if (block.bytesCount() > 0) {
				break;
			}
			blocks.remove(i);
		}
		return total;
	}

}
