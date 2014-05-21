package com.hp.hpsc.logview.retrievers;

import static org.junit.Assert.*;

import org.junit.Test;

public class HttpContentRetrieverTest {

	@Test
	public void testGetInputStream(){
		HttpContentRetriever retreiver_1 = new HttpContentRetriever("http://d6t0009g.atlanta.hp.com/files/logs-prod/web/w1/WHA-HPP-AUTH/ssl_error_log");
		HttpContentRetriever retreiver_2 = new HttpContentRetriever("http://d6t0009g.atlanta.hp.com/files/logs-prod/web/w1/WHA-HPP-AUTH/ssl_error_log-20140224.gz");
		
		final byte[] buffer = new byte[1024];
		int n = 0;
		
		try{
			while (-1 != (n = retreiver_1.getInputStream().read(buffer))) {
				System.out.println(new String(buffer, 0, n, "ISO-8859-1"));
			}
			
			System.out.println(" -------------------- another file ------------------------");

			while (-1 != (n = retreiver_2.getInputStream().read(buffer))) {
				System.out.println(new String(buffer, 0, n, "ISO-8859-1"));
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			retreiver_1.close();
			retreiver_2.close();
		}
	}

}
