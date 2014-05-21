package com.hp.hpsc.logview.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.hp.hpsc.logview.po.Link;
import com.hp.hpsc.logview.service.DirectoryService;
import com.hp.hpsc.logview.service.LDAPService;

public class Directory4ClientServlet extends HttpServlet {


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		
		// verify
		String id = req.getHeader("x-logview-username");
		String password = req.getHeader("x-logview-password");
		boolean validateFlag = false;
		validateFlag = LDAPService.verifyApp(id, password);

		if (validateFlag) {
			String path = req.getParameter("path");

			DirectoryService rd = new DirectoryService();
			List<Link> linkList = rd.readDirectory4Client(path);

			if (linkList == null) {
				resp.getWriter().write("NULL");
				resp.getWriter().close();
				return;
			}

			String s = convertLink2String(linkList);

			resp.getWriter().write(s);
			resp.getWriter().close();
		} else {
			PrintWriter wr = resp.getWriter();
			wr.write("validate failed");
			wr.close();
			return;
		}

	}

	private String convertLink2String(List<Link> linkList) {

		// In order to reduce the project size, remove the json-lib.jar
		// JSONArray jsonArray = JSONArray.fromObject( linkList );
		// String s = jsonArray.toString();

		StringBuilder sb = new StringBuilder();
		sb.append("[");

		for (Link link : linkList) {
			sb.append("{");
			sb.append("\"folderFlag\":" + link.isFolderFlag());
			sb.append(",\"lastModifiedDate\":\"" + link.getLastModifiedDate()
					+ "\"");
			sb.append(",\"name\":\"" + link.getName() + "\"");
			sb.append(",\"size\":\"" + link.getSize() + "\"");
			sb.append(",\"uri\":\"" + link.getUri() + "\"");
			sb.append("},");
		}
		String s = sb.toString();
		if (!s.equals("[")) {
			s = s.substring(0, s.length() - 1);
		}
		s = s + "]";
		return s;
	}

}
