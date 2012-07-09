<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.util.*"%>        
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel=stylesheet type="text/css" href="ciknowmgr.css">
<title>Show All CIKNOW Projects</title>
</head>
<body>
<p style="color:green; font-weight:bold">Standalone version of C-IKNOW visualizer is now available for <a href="install.htm">download</a>!</p>
<br/>

<%
	String[] hosts = {"ciknow.northwestern.edu", "ciknow1.northwestern.edu","ciknow2.northwestern.edu","www.iknowinc.com"};
	//String[] hosts = {"127.0.0.1"};
	Class.forName("com.mysql.jdbc.Driver");	
	for (String host : hosts){	
	    String serverName = "C-IKNOW Public Server";
		if (host.contains("ciknow1")){
			serverName = "C-IKNOW Production Server";
		} else if (host.contains("ciknow2")){
			serverName = "C-IKNOW Development Server";
		} else if (host.contains("iknowinc")){
			serverName = "C-IKNOW @ Syndio Social";
		}
		
		String url = "jdbc:mysql://" + host + ":3306/ciknowmgr";
		Connection con = DriverManager.getConnection(url, "ciknowmgr", "ciknowmgr");
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("SELECT * FROM projects");	
%>
	<p><a href="http://<%=host %>/ciknowmgr"><%=serverName %></a></p>
	<table>
	<% 
	boolean even = true;	 
	while (rs.next()){ 
		even = !even;
		String name = rs.getString("name");
		String description = rs.getString("description");
		boolean enabled = rs.getBoolean("enabled");
		String address = "http://" + host + "/_" + name;
	%>
		<tr class="<%=(even?"even":"odd") %>">
			<%if (enabled){ %>
			<td><a href="<%=address %>"><%=name %></a></td>
			<%} else { %>
			<td><%=name %></td>
			<%} %>
			<td><%=description %></td>
		</tr>
	<%} %>
	</table>
	<%
	con.close();
	}%>

</body>
</html>