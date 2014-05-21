<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
response.setHeader("Pragma","No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires",-10);
%>
<html>
<head>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
</head>
<body>
		<table>
	<tr>
		<td><h1><a href="" style="text-decoration:none">Log View</a> </h1></td>
	</tr>
</table>
	<form method="post" action="./LoginServlet">
		<c:if test="${! empty validateFailed}"> 
			<span style="color:red">${validateFailed}</span>
		</c:if>
		
		<table>
			<tr>
				<td>Email:</td>
				<td> <input type="text" name="email"></td>
			</tr>
			<tr>
				<td>Password:</td>
				<td><input type="password" name="password"></td>
			</tr>
		</table>
		<input type="submit">
	</form>
</body>
</html>
