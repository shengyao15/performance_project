package com.hp.hpsc.logview.parsers;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

public class CompressTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		BufferedInputStream in = new BufferedInputStream(new FileInputStream("C:/Temp/error_log-20140220.gz"));
		
		StringBuffer out = new StringBuffer();
		
		GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);
		final byte[] buffer = new byte[1024];
		int n = 0;
		while (-1 != (n = gzIn.read(buffer))) {
			out.append(new String(buffer, 0, n, "ISO-8859-1"));
		}
		System.out.println(out.toString());
		gzIn.close();	
	}

}
