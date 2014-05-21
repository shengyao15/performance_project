package com.hp.it.perf.ac.common.data.store;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.Random;

import org.junit.Test;

public class FixBlockStoreTest {

	@Test
	public void testBytesStore() throws IOException {
		// File file = new File("data/test-block.log");
		// RandomAccessFileStore store = new RandomAccessFileStore(
		// new RandomAccessFile(file, "rw"));
		RandomAccessBytesStore store = new RandomAccessBytesStore();
		FixBlockStore blocks = new FixBlockStore(store);
		long[] testKeys = new long[100];
		byte[][] testData = new byte[testKeys.length][];
		Random random = new Random();
		for (int i = 0; i < testKeys.length; i++) {
			testData[i] = new byte[random.nextInt(2048)];
			random.nextBytes(testData[i]);
			testKeys[i] = blocks.put(testData[i], 0, testData[i].length);
		}
		// System.out.println("========");
		// System.out.println(store.bytesCount());
		// System.out.println(blocks.blocksInUse().length);
		// System.out.println(store.length());
		for (int i = 0; i < testKeys.length; i++) {
			byte[] data = blocks.get(testKeys[i]);
			assertThat("Error on " + i, data, equalTo(testData[i]));
		}
		for (int i = 0; i < testKeys.length; i++) {
			byte[] data = blocks.remove(testKeys[i]);
			blocks.destroy(testKeys[i]);
			assertThat("Error on " + i, data, equalTo(testData[i]));
		}
		// System.out.println("========");
		// System.out.println(store.bytesCount());
		// System.out.println(blocks.blocksInUse().length);
		// System.out.println(store.length());
		//
		for (int i = 0; i < testKeys.length; i++) {
			testKeys[i] = blocks.put(testData[i], 0, testData[i].length);
		}
		// System.out.println("========");
		// System.out.println(store.bytesCount());
		// System.out.println(blocks.blocksInUse().length);
		// System.out.println(store.length());
		for (int i = 0; i < testKeys.length; i++) {
			byte[] data = blocks.get(testKeys[i]);
			assertThat("Error on " + i, data, equalTo(testData[i]));
		}
		for (int i = 0; i < testKeys.length; i++) {
			byte[] data = blocks.remove(testKeys[i]);
			blocks.destroy(testKeys[i]);
			assertThat("Error on " + i, data, equalTo(testData[i]));
		}
		// System.out.println("========");
		// System.out.println(store.bytesCount());
		// System.out.println(blocks.blocksInUse().length);
		// System.out.println(store.length());
		blocks.close();
	}
}
