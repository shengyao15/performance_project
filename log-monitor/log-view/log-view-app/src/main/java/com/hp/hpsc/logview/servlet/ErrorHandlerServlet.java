package com.hp.hpsc.logview.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public class ErrorHandlerServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Throwable throwable = (Throwable) request
				.getAttribute("javax.servlet.error.exception");
		Integer statusCode = (Integer) request
				.getAttribute("javax.servlet.error.status_code");
		String servletName = (String) request
				.getAttribute("javax.servlet.error.servlet_name");
		if (servletName == null) {
			servletName = "Unknown";
		}
		 String requestUri = (String)
	      request.getAttribute("javax.servlet.error.request_uri");
	      if (requestUri == null){
	         requestUri = "Unknown";
	      }
	      
		if (statusCode == 404) {
			
			request.setAttribute("statusCode", statusCode);
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/error.jsp");
			dispatcher.forward(request, response);
			return;
		}

		request.setAttribute("statusCode", statusCode);
		request.setAttribute("servletName", servletName);
		request.setAttribute("exceptionType", throwable.getClass().getName());
		request.setAttribute("exceptionMessage", throwable.getMessage());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		throwable.printStackTrace(new PrintStream(out));
		out.close();
		
		request.setAttribute("stack", out.toString());
		
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("/error.jsp");
		dispatcher.forward(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}