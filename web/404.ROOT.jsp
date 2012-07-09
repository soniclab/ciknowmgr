<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String uri = (String)request.getAttribute("javax.servlet.forward.request_uri");
	String ciknowmgrUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + "/ciknowmgr/404.jsp?uri=" + uri;
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta http-equiv="refresh" content="1; url=<%=ciknowmgrUrl %>">
<title>404 - Page Not Found</title>
</head>
<body>

</body>
</html>