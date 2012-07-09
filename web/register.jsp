<%@ page import="ciknowmgr.util.*"%>
<%@ page import="net.tanesha.recaptcha.ReCaptcha" %>
<%@ page import="net.tanesha.recaptcha.ReCaptchaFactory" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page pageEncoding="UTF-8" %> 
<%
    PropsUtil props = new PropsUtil("ciknowmgr");
	String msg = (String)request.getAttribute("msg");
	if (msg == null || msg.length() == 0) msg = "";
    String allowRegister = props.get("allow.register");
    //if (!allowRegister.equalsIgnoreCase("true")) request.getRequestDispatcher("index.jsp").forward(request, response);
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String email = request.getParameter("email");
    System.out.println("username: " + username);
    if (username == null) username = "";
    if (password == null) password = "";
    if (email == null) email = "";
    
    ReCaptcha c = ReCaptchaFactory.newReCaptcha(
    		props.get("recaptcha_public_key"), 
		    props.get("recaptcha_private_key"), false);
    
    String loginUrl = props.get("cas.login.url");
%>
<html>
<head>
<title>Registration</title>
<link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />
<link rel="icon" href="favicon.ico" type="image/x-icon" />
<link rel=stylesheet type="text/css" href="ciknowmgr.css">

<script language="JavaScript">	
	function validate(form) {
		var e = form.elements, m = '';
		if(!e['username'].value) {m += '- Username is required.\n';}	
		if(!e['password'].value) {m += '- Password is required.\n';}
		if(e['password'].value != e['confirm'].value) {
			m += '- Your password and confirmation password do not match.\n';
		}
		if(!/.+@[^.]+(\.[^.]+)+/.test(e['email'].value)) {
			m += '- E-mail requires a valid e-mail address.\n';
		}
		if(m) {
			alert('The following error(s) occurred:\n\n' + m);
			return false;
		}
		return true;
	}
</script>
</head>

<body onload="document.forms[0].elements[0].focus()">
	

	<%if (allowRegister.equalsIgnoreCase("true")){%>
	<form action="register" method="POST" onsubmit="return validate(this)">
		<table class="form">
            <tr class="header">
                <td colspan='2'>Registration</td>
            </tr>			
			<tr>
				<td class="right">username:</td>
				<td class="left">
					<input tabindex="1" type='text' name="username" value="<%=username %>" />
				</td>
			</tr>
									
			<tr>
				<td class="right">password:</td>
				<td class="left">
					<input tabindex="2" type='password' name="password" value="<%=password %>" />
				</td>
			</tr>
			
			<tr>
				<td class="right">confirm:</td>
				<td class="left">
					<input tabindex="3" type='password' name="confirm">
				</td>
			</tr>	
		
			<tr>
				<td class="right">email:</td>
				<td class="left">
					<input tabindex="4" type='text' name="email" value="<%=email %>" />
				</td>
			</tr>
			<tr>
              	<td class="right">&nbsp;</td>
                <td class="left"><% out.print(c.createRecaptchaHtml(null, "white", 5)); %></td>
            </tr>
		    <tr class="error">
                <td colspan='2'><%=msg %></td>
            </tr>			
			<tr>
				<td class="right">&nbsp;</td>
				<td class="left">
					<input tabindex="6" name="submit" type="submit" value="Register">
					<input tabindex="7" type="button" value="Back" onclick="window.location.href='<%=loginUrl %>'">
				</td>
			</tr>
		</table>
	</form>

<%} %>
</body>
</html>
