package com.hp.hpsc.logview.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hp.hpsc.logview.service.LDAPService;

public class Download4ClientServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// verify
		String id = req.getHeader("x-logview-username");
		String password = req.getHeader("x-logview-password");
		
		boolean validateFlag = false;
		validateFlag = LDAPService.verifyApp(id, password);

		if (validateFlag) {
			String path = (String) req.getParameter("path");
			File file = new File(path);
			String downFilename = file.getName();
			resp.setHeader("Content-Disposition", "attachment; filename="
					+ URLEncoder.encode(downFilename, "UTF-8"));
			
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
			

		} else {
			PrintWriter wr = resp.getWriter();
			wr.write("validate failed");
			wr.close();
			return;
		}

	}
}
