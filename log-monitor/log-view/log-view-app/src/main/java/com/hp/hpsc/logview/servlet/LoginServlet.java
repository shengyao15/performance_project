package com.hp.hpsc.logview.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hp.hpsc.logview.service.LDAPService;

public class LoginServlet extends HttpServlet {

	private static String validateFailedMsg = "Validate Failed!  Email or Password are not correct";
	private static String validateFailedMsg2 = "Validate Failed!  Email or Password cannot be empty";
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		
		if("".equals(email)  || "".equals(password)){
			req.getSession().setAttribute("validateFailed", validateFailedMsg2);
			resp.sendRedirect("./HomeServlet");
			return;
		}
		
		
		boolean autheticateFlag = false;
		try {
			autheticateFlag = LDAPService.verifyUser(email,
					password);
		} catch (Exception e) {
		}
		
		if(autheticateFlag){
			System.out.println("LogView " +email + " successful logged in ");
			//req.getSession().removeAttribute("validateFailed");
		}else{
			req.getSession().setAttribute("validateFailed", validateFailedMsg);
		}
		//Response
		resp.sendRedirect("./HomeServlet");
	}

}
