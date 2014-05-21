package com.hp.it.perf.ac.load.process;

import java.util.Comparator;

class PivotComparator<T extends Comparable<T>> implements Comparator<T> {

	@Override
	public int compare(T o1, T o2) {
		if (o1 == null) {
			if (o2 == null) {
				return 0;
			} else {
				return -o2.compareTo(o1);
			}
		} else {
			return o1.compareTo(o2);
		}
	}

}
