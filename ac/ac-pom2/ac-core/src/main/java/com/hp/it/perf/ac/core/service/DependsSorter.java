package com.hp.it.perf.ac.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DependsSorter<T> {

	private static class Node<T> {
		final T id;
		List<Edge<T>> outgoing = new ArrayList<DependsSorter.Edge<T>>(3);
		List<Edge<T>> incoming = new ArrayList<DependsSorter.Edge<T>>(3);

		public Node(T id) {
			this.id = id;
		}

		public boolean hasOutgoingEdges() {
			return !outgoing.isEmpty();
		}

		public void remove() {
			for (Edge<T> edge : new ArrayList<Edge<T>>(outgoing)) {
				edge.remove();
			}
			for (Edge<T> edge : new ArrayList<Edge<T>>(incoming)) {
				edge.remove();
			}
		}

	}

	private static class Edge<T> {
		final Node<T> fromNode;
		final Node<T> toNode;

		public void remove() {
			fromNode.outgoing.remove(this);
			toNode.incoming.remove(this);
		}

		public Edge(Node<T> fromNode, Node<T> toNode) {
			this.fromNode = fromNode;
			this.toNode = toNode;
			fromNode.outgoing.add(this);
			toNode.incoming.add(this);
		}
	}

	private Map<T, Node<T>> nodes = new HashMap<T, Node<T>>();

	private Map<T, Integer> sortedIdList = null;

	public void addNode(T id) {
		if (!nodes.containsKey(id)) {
			nodes.put(id, new Node<T>(id));
		}
	}

	public void addDependency(T from, T to) {
		Node<T> fromNode = getNode(from);
		Node<T> toNode = getNode(to);
		new Edge<T>(fromNode, toNode);
	}

	private Node<T> getNode(T id) {
		Node<T> node = nodes.get(id);
		if (node == null) {
			throw new IllegalArgumentException("node not added before: " + id);
		}
		return node;
	}

	public void sort() throws IllegalStateException {
		Map<T, Node<T>> workingMap = new HashMap<T, Node<T>>(nodes);
		Map<T, Integer> list = new HashMap<T, Integer>(workingMap.size());
		int prevSize;
		do {
			prevSize = workingMap.size();
			for (Node<T> node : new ArrayList<Node<T>>(workingMap.values())) {
				if (!node.hasOutgoingEdges()) {
					list.put(node.id, list.size());
					node.remove();
					workingMap.remove(node.id);
				}
			}
		} while (workingMap.size() < prevSize);
		if (!workingMap.isEmpty()) {
			throw new IllegalStateException(
					"dependent detect error (circle or self dependency): "
							+ workingMap.keySet().iterator().next());
		}
		sortedIdList = list;
	}

	public List<T> resolveSortedList(List<T> sourceList)
			throws IllegalArgumentException {
		if (sortedIdList == null) {
			throw new IllegalStateException(
					"the dependent sort() is not performed");
		}
		List<T> sortedList = new ArrayList<T>();
		for (T s : sourceList) {
			if (!sortedIdList.containsKey(s)) {
				throw new IllegalArgumentException("Service [" + s
						+ "] is not found. Only these services are loaded: "
						+ sortedIdList.keySet());
			}
			sortedList.add(s);
		}
		Comparator<T> comparator = new Comparator<T>() {

			@Override
			public int compare(T t1, T t2) {
				return sortedIdList.get(t1).compareTo(sortedIdList.get(t2));
			}
		};
		Collections.sort(sortedList, comparator);
		return sortedList;
	}
}
