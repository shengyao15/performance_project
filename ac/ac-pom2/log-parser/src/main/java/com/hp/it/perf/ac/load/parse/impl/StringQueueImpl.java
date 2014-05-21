package com.hp.it.perf.ac.load.parse.impl;

import java.util.LinkedList;
import java.util.Queue;

import com.hp.it.perf.ac.load.content.AcStringQueue.StringQueue;

class StringQueueImpl implements StringQueue {

	// use new string instance as mark
	private final String MARK_EOB = new String("<EOB>");
	// use new string instance as mark
	private final String MARK_EOF = new String("<EOF>");
	private int modCount = 0;
	private Queue<String> queue = new LinkedList<String>();
	private boolean closed;
	private boolean wasEOB;

	@Override
	public boolean wasEOB() {
		return wasEOB;
	}

	@Override
	public void putLine(String line) {
		if (line == null) {
			throw new NullPointerException("null line");
		}
		queue.offer(line);
		modCount++;
	}

	@Override
	public String pollLine() {
		wasEOB = false;
		String line = queue.poll();
		if (line == MARK_EOB) {
			wasEOB = true;
			return null;
		}
		if (line == MARK_EOF) {
			closed = true;
			return null;
		}
		return line;
	}

	@Override
	public String peekLine() {
		String line = queue.peek();
		if (line == MARK_EOB) {
			wasEOB = true;
			return null;
		}
		if (line == MARK_EOF) {
			closed = true;
			return null;
		}
		return line;
	}

	@Override
	public void markEOB() {
		queue.offer(MARK_EOB);
		modCount++;
	}

	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public int getModCount() {
		return modCount;
	}

	@Override
	public void close() {
		queue.offer(MARK_EOF);
		modCount++;
	}
}