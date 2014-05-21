package com.hp.hpsc.logview.client;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import net.sf.json.JSONArray;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import com.hp.hpsc.logview.po.Link;

public class LogviewClient {

	final static int BUFFER_SIZE = 4096;
	
	public static void main(String[] args) throws Exception {
		/*
		 * URL url = new URL(
		 * "http://c0007614.itcs.hp.com:50002/log-view/Directory4ClientServlet?id=hpsc-log-view-client&password=asdfQWER654321&path=/opt/cloudhost/casfw/sp4ts-portal-2.0.1-SNAPSHOT/var/log/vignette-portal"
		 * ); URLConnection con = url.openConnection(); con.connect();
		 * InputStream in = con.getInputStream(); List<Link> list =
		 * convertDirectory(in);
		 * 
		 * for(Link link : list){ System.out.println(link.getName()); }
		 */

		URL url = new URL(
				"http://c0007614.itcs.hp.com:50002/log-view/Download4ClientServlet?id=hpsc-log-view-client&password=asdfQWER654321&path=/opt/cloudhost/casfw/sp4ts-portal-2.0.1-SNAPSHOT/var/log/vignette-portal/tristan_mock.txt");
		URLConnection con = url.openConnection();
		con.connect();
		InputStream in = con.getInputStream();
		InputStreamReader isr = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(isr);
		String temp = "";
		while ((temp = br.readLine()) != null) {
			System.out.println(temp);
		}

	}

	public List<Link> readDirectory(String folder, String url) throws Exception {
		URL u = new URL(url + folder);
		URLConnection con = null;
		List<Link> list = null;
		con = u.openConnection();
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
		return new String(outStream.toByteArray(), "ISO-8859-1");
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
		HttpClient client = new HttpClient();
		GetMethod httpGet = new GetMethod(url + file);
		InputStream in = null;
		try {
			client.executeMethod(httpGet);
			in = httpGet.getResponseBodyAsStream();
			return in;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return in;
	}




}
