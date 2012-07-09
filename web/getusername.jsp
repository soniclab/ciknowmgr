<%@ page import="ciknowmgr.util.*"%>
<%@ page import="net.tanesha.recaptcha.ReCaptcha" %>
<%@ page import="net.tanesha.recaptcha.ReCaptchaFactory" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page pageEncoding="UTF-8" %> 
<%
    PropsUtil props = new PropsUtil("ciknowmgr");
	String msg = (String)request.getAttribute("msg");
	if (msg == null || msg.length() == 0) msg = "";
    String email = request.getParameter("email");
    if (email == null) email = "";
    ReCaptcha c = ReCaptchaFactory.newReCaptcha(
    		props.get("recaptcha_public_key"), 
		    props.get("recaptcha_private_key"), false);
    
    String loginUrl = props.get("cas.login.url");
%>
<html>
<head>
<title>Username Recovery</title>
<link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />
<link rel="icon" href="favicon.ico" type="image/x-icon" />
<link rel=stylesheet type="text/css" href="ciknowmgr.css">

<script language="JavaScript">	
	function validate(form) {
		var e = form.elements, m = '';
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

	<form action="recoverUsername" method="POST" onsubmit="return validate(this)">
		<table class="form">
           <tr class="header">
               <td colspan='2'>Username Recovery</td>
           </tr>				
		
			<tr>
				<td class="right">email:</td>
				<td class="left">
					<input tabindex="1" type='text' name="email" value="<%=email %>" />
				</td>
			</tr>
			<tr>
	             	<td class="right">&nbsp;</td>
	               <td class="left"><% out.print(c.createRecaptchaHtml(null, "white", 2)); %></td>
	           </tr>
		    <tr class="error">
	               <td colspan='2'><%=msg %></td>
	           </tr>			
			<tr>
				<td class="right">&nbsp;</td>
				<td class="left">
					<input tabindex="3" name="submit" type="submit" value="Retrieve Username">
					<input tabindex="4" type="button" value="Back" onclick="window.location.href='<%=loginUrl %>'">
				</td>
			</tr>
		</table>
	</form>
</body>
</html>
