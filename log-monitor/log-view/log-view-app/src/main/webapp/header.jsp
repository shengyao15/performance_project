<%@ page language="java" pageEncoding="UTF-8"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<base href="<%=basePath%>">

	<table>
	<tr>
		<td><h1><a href="" style="text-decoration:none">Log View</a> </h1></td>
		<td>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="./LogoutServlet" style="text-decoration:none">logout</a>
		</td>
	</tr>
</table>
  
	
