package com.hp.hpsc.logview.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hp.hpsc.logview.util.PropUtils;

public class HomeServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Map<String, String> map = new HashMap<String, String>();
		// Log Link
		Set<String> allLogLinklist = PropUtils.getLogPathSet();
		
		for (String logLink : allLogLinklist) {
			String logLinkURL = req.getContextPath()
					+ "/DirectoryServlet?path=" + logLink;
			map.put(logLink, logLinkURL);
		}


		req.setAttribute("logLinkMap", map);

		// Response
		RequestDispatcher dispatcher = req.getRequestDispatcher("/home.jsp");
		dispatcher.forward(req, resp);
	}


}
