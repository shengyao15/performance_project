package com.hp.hpsc.logservice.client;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import net.sf.json.JSONArray;


public class LogviewClient {

	final static int BUFFER_SIZE = 4096;

	private final String USERNAME = "hpsc-log-view-client";
	private final String PASSWORD = "asdfQWER654321";
	
	public List<Link> readDirectory(String folder, String url) throws Exception {
		URL u = new URL(url + folder);
		URLConnection con = null;
		List<Link> list = null;
		con = u.openConnection();
		con.addRequestProperty("x-logview-username", USERNAME);
		con.addRequestProperty("x-logview-password", PASSWORD);
		
		con.connect();
		InputStream in = con.getInputStream();
		list = convertDirectory(in);
		in.close();
		return list;
	}

	public static String InputStreamTOString(InputStream in) throws Exception {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[BUFFER_SIZE];
		int count = -1;
		while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
			outStream.write(data, 0, count);

		data = null;
		return new String(outStream.toByteArray());
	}

	private static List<Link> convertDirectory(InputStream in) throws Exception {

		String s = InputStreamTOString(in);

		if ("NULL".equals(s)) {
			return null;
		}

		JSONArray array = JSONArray.fromObject(s);
		List<Link> collection = (List<Link>) JSONArray.toCollection(array,
				Link.class);
		List<Link> list2 = collection;
		return list2;
	}

	public InputStream loadInputStream(String file, String url)
			throws Exception {
		URL u = new URL(url + file);
		URLConnection con = u.openConnection();
		con.addRequestProperty("x-logview-username", USERNAME);
		con.addRequestProperty("x-logview-password", PASSWORD);
		
		con.connect();
		InputStream in = con.getInputStream();
		return in;
	}

}
