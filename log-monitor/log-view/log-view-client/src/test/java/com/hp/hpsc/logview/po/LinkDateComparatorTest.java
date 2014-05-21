package com.hp.hpsc.logview.po;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


import org.junit.Assert;
import org.junit.Test;

public class LinkDateComparatorTest {

	@Test
	public void testCompare() {
		Date now = new Date();
		Link link1 = new Link();
		link1.setLastModified(now);
		
		Link link2 = new Link();
		link2.setName("null");
		
		Link link3 = new Link();
		link3.setName("link3");
		
		Link link4 = new Link();
		link4.setName("new");
		link4.setLastModified(new Date(now.getTime() + 60000l));
		List<Link> collection = new ArrayList<Link>(4);
		collection.add(link1);
		collection.add(link2);
		collection.add(link4);
		collection.add(link3);
		Collections.sort(collection, new LinkDateComparator());
		for(Link l: collection){
			System.out.println(l);
		}
		Assert.assertEquals(collection.get(0), link4);
		Assert.assertEquals(collection.get(1), link1);
		Assert.assertEquals(collection.get(2), link3);
		Assert.assertEquals(collection.get(3), link2);
	}

}
