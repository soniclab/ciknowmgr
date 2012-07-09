<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.net.URLEncoder"%>
<%
	String url = (String)request.getAttribute("url");
	
	// for registration and username/password recovery servlet
	String username = (String)request.getAttribute("u");	
	if (username != null && !username.isEmpty()) {
		url += "?";
		url += ("u=" + URLEncoder.encode(username, "utf-8"));
		
		String msg = (String)request.getAttribute("m");
		if (msg != null && !msg.isEmpty()) {
			url += ("&m=" + URLEncoder.encode(msg, "utf-8"));
		}
	}	

	
	System.out.println("Forwarding to: " + url);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta http-equiv="refresh" content="0; url=<%=url %>">
<title></title>
</head>
<body>

</body>
</html>