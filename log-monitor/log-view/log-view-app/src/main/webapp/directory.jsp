<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
	<jsp:include page="header.jsp"></jsp:include>

<h2> Index of ${path }</h2>
	<table>
		<tbody>
			<tr>
				<th width="50">
				</th>
				<th width="300" align="left">Name
				</th>
				<th width="300" >Last modified <img align="bottom" src="./icons/down.png"/>
				
				<th width="100">Size
				</th>
			</tr>
			<tr>
				<th colspan="5"><hr>
				</th>
			</tr>
			<tr>
				<c:if test="${! empty returnPathLocation }">
				<td>
					<img src="./icons/back.png"/>
				</td>
				<td>
					<a href="${returnPath }">Parent Directory</a>
				</td>
				</c:if>
			</tr>
            <c:forEach items="${list}" var="link" varStatus="status">     
             	<tr>
             	<c:if test="${link.folderFlag}">
             		<td valign="top"><img src="./icons/folder.png"/></td>
             	</c:if>
             	<c:if test="${!link.folderFlag}">
             		<td valign="top"><img src="./icons/tex.png"/></td>
             	</c:if>
				
				<td align="left">
				<a href="${link.uri }">${link.name}</a>
				</td>
				<td align="center">${link.lastModifiedDate }</td>
				<td align="right">${link.size }</td>
			</tr>
            </c:forEach> 
            <tr>
				<th colspan="5"><hr>
				</th>
			</tr>
		</tbody>
	</table>

</body>
</html>