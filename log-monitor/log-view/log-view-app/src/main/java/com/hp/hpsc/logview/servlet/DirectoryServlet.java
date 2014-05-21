package com.hp.hpsc.logview.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hp.hpsc.logview.po.Link;
import com.hp.hpsc.logview.service.DirectoryService;
import com.hp.hpsc.logview.util.PropUtils;

public class DirectoryServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String path = req.getParameter("path");
		
		if(path==null || "".equals(path)){
			resp.sendRedirect(req.getContextPath()+"/noAuthority.jsp");
			return;
		}
		
		path = path.replace("\\", "/");
		
		//check ..
		if(path.contains("..")){
			resp.sendRedirect(req.getContextPath()+"/noAuthority.jsp");
			return;
		}
		
		//check the path is under defined log path
		boolean parentPathCheck = false;
		
		Set<String> allLogLinklist = PropUtils.getLogPathSet();
		for(String s : allLogLinklist){
			if(path.startsWith(s)){
				parentPathCheck = true;
				break;
			}
		}
		
		if(!parentPathCheck){
			resp.sendRedirect(req.getContextPath()+"/noAuthority.jsp");
			return;
		}
		
		
		
		String returnPath = "";
		if(path.contains("\\")){
			returnPath = path.substring(0, path.lastIndexOf("\\"));
		}else if(path.contains("/")){
			returnPath = path.substring(0, path.lastIndexOf("/"));
		}
		if(allLogLinklist.contains(path)){
			returnPath = "";
		}
		
		
		req.setAttribute("returnPathLocation", returnPath);
		req.setAttribute("returnPath", req.getContextPath() + "/DirectoryServlet?path="+returnPath);
		
		DirectoryService rd = new DirectoryService();
		List<Link> linkList = rd.readDirectory(path, req.getContextPath());
		req.setAttribute("list", linkList);
		req.setAttribute("path", path);
		
		req.getRequestDispatcher("/directory.jsp").forward(req, resp);
	}

}
