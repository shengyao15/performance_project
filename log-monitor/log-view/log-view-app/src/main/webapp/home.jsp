<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Log View</title>
</head>
<body>

	<jsp:include page="header.jsp"></jsp:include>
  
	
	<table>
		<tbody>
			<tr>
				<th width="100"><img src="./icons/blank.png" >
				</th>
				<th width="300" align="left"><a>Log Link</a>
				</th>
			</tr>
			<tr>
				<th colspan="5"><hr>
				</th>
			</tr>
			<c:forEach items="${logLinkMap}" var="entry">  
			<tr>
				<td valign="top"><img src="./icons/folder.png"/></td>
				<td><a href="${entry.value}">${entry.key}</a></td>
			</tr>
			</c:forEach>  
			</tbody>
	</table>	
			<br><br><br>
			
			
</body>
</html>