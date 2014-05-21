package com.hp.hpsc.logview.retrievers;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import com.hp.hpsc.logview.retrievers.HttpRetriever;

public class HttpRetrieverTest {

	@Test
	public void testRetrieve() {
		IRetriever retriever = new HttpRetriever();
		String result = retriever.retrieve("http://d6t0009g.atlanta.hp.com/files/");
		System.out.println(result);
		Assert.assertNotNull(result);
	}

}
