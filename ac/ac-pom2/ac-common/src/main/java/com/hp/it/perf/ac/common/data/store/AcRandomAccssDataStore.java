package com.hp.it.perf.ac.common.data.store;

import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.RandomAccessFile;
import java.io.StreamCorruptedException;
import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.hp.it.perf.ac.common.data.AcDataInput;
import com.hp.it.perf.ac.common.data.AcDataOutput;
import com.hp.it.perf.ac.common.data.AcDataStore;

public class AcRandomAccssDataStore implements AcDataStore {

	private BufferedRandomAccess store;

	private AcDataInput input;

	private AcDataOutput output;

	private LongList indexList = new LongList(10);

	private File filePath;

	private static class LongList {

		private int offset = 0;
		private long[] store;

		public LongList(int size) {
			store = new long[size];
		}

		public void set(long value) {
			inc(++offset);
			store[offset - 1] = value;
		}

		protected void inc(int minSize) {
			if (store.length < minSize) {
				long[] newStore = new long[calNewSize(store.length, minSize)];
				System.arraycopy(store, 0, newStore, 0, store.length);
				store = newStore;
			}
		}

		protected int calNewSize(int length, int minSize) {
			int size = length + (length >> 1);
			if (size < minSize)
				size = minSize;
			// refer to ArrayList
			if (size > Integer.MAX_VALUE - 8) {
				size = Integer.MAX_VALUE - 8;
			}
			return size;
		}

		public int count() {
			return offset;
		}

		public long[] toLongArray() {
			long[] array = new long[count()];
			System.arraycopy(store, 0, array, 0, array.length);
			return array;
		}

		public long get(int index) {
			return store[index];
		}

		public int indexOf(long value) {
			return Arrays.binarySearch(store, 0, count(), value);
		}
	}

	public static enum Mode {
		READONLY, APPEND, OVERWRITE;
	}

	public AcRandomAccssDataStore(File f, Mode mode) throws IOException {
		this.filePath = f;
		String rMode = "r";
		if (mode != Mode.READONLY) {
			rMode = "rw";
		}
		RandomAccessFile file = new RandomAccessFile(f, rMode);
		this.store = new BufferedRandomAccess(new RandomAccessFileStore(file));
		this.input = new AcDataInput(store);
		this.output = new AcDataOutput(store);
		if (mode == Mode.OVERWRITE) {
			store.setLength(0);
		}
		preload(false);
	}

	private void preload(boolean checkContent) throws IOException {
		long length = store.length();
		for (long position = 0; position < length;) {
			loadData(position, checkContent);
			position = store.position();
		}
	}

	@Override
	public long add(Object data) throws IOException {
		long position = store.position();
		this.store.writeInt(0);// write size int holder
		this.output.writeObject(data);
		long newPostion = store.position();
		store.position(position);
		int len = (int) (newPostion - position - 4);
		this.store.writeInt(len);
		store.position(newPostion);
		indexList.set(position);
		return position;
	}

	@Override
	public int size() {
		return indexList.count();
	}

	@Override
	public URI toURI() {
		return filePath.toURI();
	}

	@Override
	public long[] addAll(AcDataStore other) throws IOException {
		LongList list = new LongList(10);
		for (Iterator<Object> iter = other.values(); iter.hasNext();) {
			list.set(add(iter.next()));
		}
		return list.toLongArray();
	}

	@Override
	public Object get(long key) throws IOException {
		// check key
		if (indexList.count() == 0) {
			throw new IndexOutOfBoundsException("no data in store: " + key);
		}
		if (indexList.indexOf(key) < 0) {
			throw new IndexOutOfBoundsException(key + " not in range: "
					+ indexList.get(0) + " - "
					+ indexList.get(indexList.count() - 1));
		}
		return locate(key);
	}

	private Object locate(long position) throws IOException {
		long oldPostion = store.position();
		try {
			return loadData(position, true);
		} finally {
			store.position(oldPostion);
		}
	}

	protected Object loadData(long position, boolean checkContent)
			throws IOException {
		store.position(position);
		int blockLength = this.store.readInt();
		if (blockLength < 0) {
			throw new StreamCorruptedException(blockLength
					+ " read at postion: " + position);
		}
		Object data = null;
		if (checkContent) {
			try {
				data = this.input.readObject();
			} catch (ClassNotFoundException e) {
				throw (InvalidClassException) new InvalidClassException(
						e.getMessage()).initCause(e);
			}
			int loadedSize = (int) (store.position() - 4 - position);
			if (loadedSize != blockLength) {
				throw new StreamCorruptedException(loadedSize + "!="
						+ blockLength);
			}
		} else {
			store.skipBytes(blockLength);
		}
		indexList.set(position);
		return data;
	}

	@Override
	public Iterator<Object> values() {
		final int currentSize = indexList.count();
		return new Iterator<Object>() {

			private int current = 0;

			@Override
			public boolean hasNext() {
				return current < currentSize;
			}

			@Override
			public Object next() {
				if (current >= currentSize) {
					throw new NoSuchElementException(current + ">="
							+ currentSize);
				}
				try {
					return locate(indexList.get(current++));
				} catch (IOException e) {
					throw (NoSuchElementException) new NoSuchElementException(
							"load data error").initCause(e);
				}
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException(
						"remove() is not supported");
			}
		};
	}

	@Override
	public Iterator<Long> keys() {
		final int currentSize = indexList.count();
		return new Iterator<Long>() {

			private int current = 0;

			@Override
			public boolean hasNext() {
				return current < currentSize;
			}

			@Override
			public Long next() {
				if (current >= currentSize) {
					throw new NoSuchElementException(current + ">="
							+ currentSize);
				}
				return indexList.get(current++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException(
						"remove() is not supported");
			}
		};
	}

	@Override
	public void close() throws IOException {
		this.store.close();
	}

}
