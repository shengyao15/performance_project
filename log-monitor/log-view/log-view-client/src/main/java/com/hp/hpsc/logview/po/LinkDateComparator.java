package com.hp.hpsc.logview.po;

import java.util.Comparator;

public class LinkDateComparator implements Comparator<Link> {

	@Override
	public int compare(Link o1, Link o2) {
		if(o1 == null || o1.getLastModified() == null){
			return 1;
		}
		if(o2 ==null || o2.getLastModified() == null){
			return -1;
		}
		return o2.getLastModified().compareTo(o1.getLastModified());
	}

}
