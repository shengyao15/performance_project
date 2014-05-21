package com.hp.hpsc.logview.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class DownloadServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String path = (String) req.getParameter("path");
		
		System.out.println("LogView DownloadServlet file: " + path);
		
		File file = new File(path);
		String downFilename = file.getName();
		resp.setHeader("Content-Disposition", "inline; filename="
				+ URLEncoder.encode(downFilename, "UTF-8"));
		resp.setHeader("Content-Length", String.valueOf(file.length()));
		
		OutputStream out = resp.getOutputStream();
		BufferedOutputStream bout = new BufferedOutputStream(out);
		
		InputStream in = new FileInputStream(file);
		BufferedInputStream bin = new BufferedInputStream(in);
		
		byte[] buffer = new byte[16 * 1024];
		int i = -1;
		try {
			while ((i = in.read(buffer)) != -1) {
				bout.write(buffer, 0, i);
			}
		} catch (Exception e) {
			
		}finally{
			try {
				bin.close();
			} catch (Exception ignore) {
			}
			
			try {
				bout.close();
			} catch (Exception ignore) {
			}
		}

	}
}
