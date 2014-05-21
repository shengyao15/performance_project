package com.hp.hpsc.logview.parsers;


import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.hp.hpsc.logview.parsers.RegrexParser;
import com.hp.hpsc.logview.po.Link;
import com.hp.hpsc.logview.po.ParserParameters;
import com.hp.hpsc.logview.retrievers.HttpRetriever;
import com.hp.hpsc.logview.retrievers.IRetriever;

public class RegrexParserTest {

	@Test
	public void testResolve() {
		String url = "http://d6t0009g.atlanta.hp.com/files/logs-perf2/producer/logs/producer1/itrc/sp4tssearch/";
		IRetriever retriever = new HttpRetriever();
		IParser parser = new RegrexParser();
		
		ParserParameters params = new ParserParameters();
		params.setUrl(url);
		params.setContent(retriever.retrieve(url));
		
		List<Link> files = parser.resolve(params);
		Assert.assertNotNull(files);
		Assert.assertTrue(files.size() > 0);
		for(Link n: files){
			System.out.println(n.toString());
		}
		
		params.setLastDate(new Date().getTime());
		List<Link> nofiles = parser.resolve(params);
		for(Link l: nofiles){
			List<Link> subfiles = l.getSubLinks();
			Assert.assertNull(subfiles);
		}
	}

}
