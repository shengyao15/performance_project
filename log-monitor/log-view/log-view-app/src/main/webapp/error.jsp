<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Log View</title>
<style>
table {
white-space: normal;
line-height: normal;
font-weight: normal;
font-size: medium;
font-variant: normal;
font-style: normal;
text-align: start;
border-collapse: separate;
border-spacing: 2px;
border-color: gray;
}
</style>

</head>
<body>

	<table>
	<tr>
		<td><h1><a href="./HomeServlet" style="text-decoration:none">Log View</a> </h1></td>
	</tr>
</table>
		
	<table>
			<tr>
				<td width="150">Status Code</td><td>${statusCode}</td>
			</tr>
			<tr>
				<td>Servlet Name</td><td>${servletName}</td>
			</tr>
			<tr>
				<td>Exception Type</td><td>${exceptionType}</td>
			</tr>
			<tr>
				<td>Exception Message</td><td>${exceptionMessage}</td>
			</tr>
			<tr>
				<td valign="top">Stack</td><td>${stack}</td>
			</tr>
			</table>	
		
</body>
</html>