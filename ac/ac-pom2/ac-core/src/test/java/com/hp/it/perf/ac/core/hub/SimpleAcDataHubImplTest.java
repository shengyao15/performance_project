package com.hp.it.perf.ac.core.hub;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hp.it.perf.ac.common.core.AcDataHubEndpoint;
import com.hp.it.perf.ac.common.core.AcDataHubEndpoint.AcDataLostEvent;
import com.hp.it.perf.ac.common.core.AcDataListener;
import com.hp.it.perf.ac.common.core.AcStatusEvent;
import com.hp.it.perf.ac.common.core.AcStatusListener;

public class SimpleAcDataHubImplTest {

	private SimpleAcDataHubImpl<Integer> hub;
	private int capacity;
	private ExecutorService executor;
	private BitSet board = new BitSet();

	@Before
	public void setUp() throws Exception {
		capacity = 20;
		executor = Executors.newCachedThreadPool();
		hub = new SimpleAcDataHubImpl<Integer>(Integer.class, capacity,
				executor);
	}

	@After
	public void tearDown() throws Exception {
		executor.shutdownNow();
	}

	private class DataListener implements AcDataListener<Integer>,
			AcStatusListener {

		long totalLost;

		@Override
		public void onData(Integer... data) {
			for (Integer i : data) {
				board.set(i);
				try {
					TimeUnit.MILLISECONDS.sleep(5);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}

		@Override
		public void onActive(AcStatusEvent event) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDeactive(AcStatusEvent event) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatus(String status, AcStatusEvent event) {
			if (AcDataLostEvent.EventType.equals(status)) {
				AcDataLostEvent lostEvent = (AcDataLostEvent) event;
				totalLost += lostEvent.getLost();
			}
		}
	};

	@Test
	public void testCreateDataEndpoint() {
		DataListener listener = new DataListener();
		AcDataHubEndpoint<Integer> endpoint = hub.createDataEndpoint(listener,
				5, 1000);
		int max = 100;
		Random random = new Random();
		for (int i = 0; i < max;) {
			int generated = Math.min(random.nextInt(capacity), max - i);
			if (generated > 0) {
				Integer[] data = new Integer[generated];
				for (int j = 0; j < generated; j++) {
					data[j] = i + j;
				}
				i += generated;
				hub.onData(data);
			}
			try {
				TimeUnit.MILLISECONDS.sleep(5 + random.nextInt(50));
			} catch (InterruptedException e) {
			}
		}
		try {
			TimeUnit.MILLISECONDS.sleep(50);
		} catch (InterruptedException e) {
		}
		try {
			hub.shutdown(1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
		}
		System.out.println("lost: " + endpoint.getTotalLosted());
		System.out.println("unprocssed: " + endpoint.getUnprocessed());
		long processed = endpoint.getProcessed();
		System.out.println("processed: " + processed);
		assertThat("processed", processed, is(greaterThan(0L)));
		assertThat("processed", board.cardinality(),
				is(equalTo((int) processed)));
		assertThat("received", (int) endpoint.getReceived(), is(equalTo(max)));
		assertThat("lost", endpoint.getTotalLosted(),
				is(equalTo(listener.totalLost)));
	}
}
