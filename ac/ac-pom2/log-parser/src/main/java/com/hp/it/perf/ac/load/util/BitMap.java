package com.hp.it.perf.ac.load.util;

import java.util.BitSet;

import javaewah.EWAHCompressedBitmap;
import javaewah.IntIterator;

interface BitMap {

	void set(int index);

	boolean get(int index);

	void or(BitMap another);

	void and(BitMap another);

	int cardinality();

	int[] toArray();

}

class BitSetMap implements BitMap {

	private BitSet storage;

	public BitSetMap() {
		this.storage = new BitSet();
	}

	@Override
	public void set(int index) {
		storage.set(index);
	}

	@Override
	public boolean get(int index) {
		return storage.get(index);
	}

	@Override
	public void or(BitMap another) {
		storage.or(((BitSetMap) another).storage);
	}

	@Override
	public void and(BitMap another) {
		storage.and(((BitSetMap) another).storage);
	}

	@Override
	public int cardinality() {
		return storage.cardinality();
	}

	@Override
	public int[] toArray() {
		int[] indexes = new int[storage.cardinality()];
		int j = 0;
		for (int i = storage.nextSetBit(0); i >= 0; i = storage
				.nextSetBit(i + 1)) {
			indexes[j++] = i;
		}
		return indexes;
	}

}

class CompressedBitMap implements BitMap {

	private EWAHCompressedBitmap storage;

	public CompressedBitMap() {
		this.storage = new EWAHCompressedBitmap();
	}

	@Override
	public void set(int index) {
		if (!storage.set(index)) {
			throw new IllegalStateException(index + "<" + storage.sizeInBits());
		}
	}

	@Override
	public boolean get(int index) {
		int size = storage.sizeInBits();
		if (index >= size) {
			return false;
		}
		IntIterator iter = storage.intIterator();
		while (iter.hasNext()) {
			int next = iter.next();
			if (next == index) {
				return true;
			} else if (next > index) {
				return false;
			}
		}
		return false;
	}

	@Override
	public void or(BitMap another) {
		storage = storage.or(((CompressedBitMap) another).storage);
	}

	@Override
	public void and(BitMap another) {
		storage = storage.and(((CompressedBitMap) another).storage);
	}

	@Override
	public int cardinality() {
		return storage.cardinality();
	}

	@Override
	public int[] toArray() {
		return storage.toArray();
	}

}