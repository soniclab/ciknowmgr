<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="ciknowmgr.util.*"%>
<%@ page import="ciknowmgr.dao.*"%>
<%@ page import="ciknowmgr.domain.*"%>
<%@ page import="java.util.*"%>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String uri = (String)request.getParameter("uri");
	String[] parts = uri.split("/");
	String context = parts[1];	
	String msg = "";
	if (context.startsWith("_")){
		String projectName = context.substring(1);	
		Beans.init(request.getSession().getServletContext());
		ProjectDao projectDao = (ProjectDao)Beans.getBean("projectDao");
		ManagerUtil managerUtil = (ManagerUtil)Beans.getBean("managerUtil");
		Project project = projectDao.findByName(projectName);

		if (project == null) {
			msg = "Project: " + projectName + " does not exist in this server.";
		} else if (project.getEnabled()){
			msg = "Project: " + projectName + " has already been enabled.";
		} else {
			msg = "Project: " + projectName + " was disabled because it was not active for more than two weeks. We are now enabling this project, please wait...";
			managerUtil.processAction(project.getId(), "yao.gyao@gmail.com", Constants.ENABLE);
			
			String projectUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + "/_" + projectName;
			request.setAttribute("url", projectUrl);
			request.getRequestDispatcher("/forward.jsp").forward(request, response);
		}
	} else {
		msg = "Resource (" + uri + ") Not Found.";
	}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>404 - Page Not Found</title>
</head>
<body>
	<h2><%=msg %></h2>
</body>
</html>