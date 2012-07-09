<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<%@ page import="ciknowmgr.util.*"%>
<%@ page import="org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter" %>
<%@ page import="org.springframework.security.core.AuthenticationException" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page pageEncoding="UTF-8" %> 
<%
    PropsUtil props = new PropsUtil("ciknowmgr");
    String allowRegister = props.get("allow.register");
    System.out.println("allow.register=" + allowRegister);
    //if (!allowRegister.equalsIgnoreCase("true")) request.getRequestDispatcher("index.jsp").forward(request, response);
    
    String serverName = "C-IKNOW Public Server";
    String url = request.getServerName();
	if (url.contains("ciknow1") || url.contains("129.105.161.76")){
		serverName = "C-IKNOW Production Server";
	} else if (url.contains("ciknow2") || url.contains("129.105.161.77")){
		serverName = "C-IKNOW Development Server";
	} else if (url.contains("iknowinc")){
		serverName = "C-IKNOW @ Syndio Social";
	} else if (url.contains("localhost") || url.contains("127.0.0.1")){
		serverName = "C-IKNOW Local Server";
	}
	
    String username = request.getParameter("username");
    if (username == null) username = "";
    
	String msg = (String)request.getAttribute("msg");
	if (msg == null || msg.length() == 0) msg = "";
%>
<html>
	<head>
		<title><%=serverName %></title>
		<link rel=stylesheet type="text/css" href="ciknowmgr.css">
	</head>
	<body>
		<form method="POST" action='<%= response.encodeURL("j_spring_security_check") %>'>
			<table class="form">		
                <tr class="header">
                    <td colspan='2'><%=serverName %></td>
                </tr>					
				<tr>
					<td class="error" colspan='2'>
						<c:if test="${not empty param.login_error}">
			                    Your login attempt was not successful, try again.<BR><BR>
			                    Reason: <%= ((AuthenticationException) session.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY)).getMessage() %>
						</c:if> 
						<c:if test="${not empty param.logout}">
			                    You are logged out.
			            </c:if>			            
			            <c:if test="${not empty msg}">
			                    <%=msg %>
			            </c:if>
			        </td>
				</tr>
				 
                <tr>
                    <td class="right">username:</td>
                    <td class="left">
                            <input id="username" tabindex="1" type='text' name='j_username'                  
                              <c:choose>
                                  <c:when test="${not empty param.login_error}">value='<c:out value="${SPRING_SECURITY_LAST_USERNAME}"/>'</c:when>
                                  <c:otherwise>
                                      value='<%=username%>'
                                  </c:otherwise>
                              </c:choose>
                              />                                
                    </td>                    
                </tr>
                
                <tr>
                    <td class="right">password:</td>
                    <td class="left"><input tabindex="2" type='password' name='j_password'></td>
                </tr>
                
				<tr>
					<td class="right">&nbsp;</td>
					<td class="left">
						<input tabindex="3" type="submit" value="Log In">
						<% if (allowRegister.equals("true")){ %>
						<input tabindex="5" type="button" value="Register" onclick="window.location.href='register.jsp'">
						<%} %>
				</tr>
				<tr>
					<td class="right">&nbsp;</td>
					<td class="left">
						<a href="getusername.jsp">Forgot username?</a><br/>
						<a href="getpassword.jsp">Forgot password?</a>
				</tr>
			</table>
		</form>
	</body>
</html>
