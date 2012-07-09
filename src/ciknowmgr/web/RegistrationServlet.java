package ciknowmgr.web;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import ciknowmgr.dao.RoleDao;
import ciknowmgr.dao.UserDao;
import ciknowmgr.domain.User;
import ciknowmgr.util.Beans;
import ciknowmgr.util.Constants;
import ciknowmgr.util.GeneralUtil;
import ciknowmgr.util.PropsUtil;
import ciknowmgr.util.Validation;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

/**
 * @author gyao
 */

public class RegistrationServlet extends HttpServlet {
    private static final long serialVersionUID = -2470876179879040968L;
    private Logger logger = Logger.getLogger(this.getClass());


    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            PropsUtil props = new PropsUtil("ciknowmgr");
            String allowRegister = props.get("allow.register");
            String msg = "";
            ServletContext sc = getServletContext();
            if (!allowRegister.equalsIgnoreCase("true")){
            	logger.warn("user registration is disabled.");
            	msg = "User Registration Is Disabled.";
            	request.setAttribute("msg", msg);
            	sc.getRequestDispatcher("/register.jsp").forward(request, response);
            } else {
            	logger.info("registering new user.");
            	String username = request.getParameter("username").trim();
            	logger.debug("username: " + username);
            	String password = request.getParameter("password").trim();
            	String email = request.getParameter("email").trim();
            	
            	// protect by reCaptcha
                String remoteAddr = request.getRemoteAddr();
                ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
                reCaptcha.setPrivateKey(props.get("recaptcha_private_key"));

                String challenge = request.getParameter("recaptcha_challenge_field");
                String uresponse = request.getParameter("recaptcha_response_field");
                ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, uresponse);

                if (!reCaptchaResponse.isValid()) {
            		msg = "Show me you are not a robot by passing the reCaptcha:)";
                	request.setAttribute("msg", msg);
                	sc.getRequestDispatcher("/register.jsp").forward(request, response);
                	return;
                }

            	
                // validate username
            	if (username.length() == 0 || password.length() == 0){
            		msg = "Username and password cannot be empty.";
                	request.setAttribute("msg", msg);
                	sc.getRequestDispatcher("/register.jsp").forward(request, response);
                	return;
            	} else if (username.length() > 80){
            		msg = "Username is too long (>80): " + username;
                	request.setAttribute("msg", msg);
                	sc.getRequestDispatcher("/register.jsp").forward(request, response);
                	return;
            	} else if (username.contains(" ")
            		|| username.contains(",")
            		|| username.contains("`")
            		|| username.contains("/")
            		|| username.contains("\\")
            		|| username.contains("*")
            		|| username.contains("\"")
            		|| username.contains(">")
            		|| username.contains("<")
            		|| username.contains(":")
            		|| username.contains("|")
            		|| username.contains("?")){
            		msg = "Username cannot contain special characters or spaces.";
                	request.setAttribute("msg", msg);
                	sc.getRequestDispatcher("/register.jsp").forward(request, response);
                	return;
            	}
            	
            	// validate registered email address
            	if (!Validation.isEmailValid(email)){
            		msg = "Invalid Email Address";
                	request.setAttribute("msg", msg);
                	sc.getRequestDispatcher("/register.jsp").forward(request, response);
                	return;
            	}
            	
            	// create new user account
            	UserDao userDao = (UserDao)Beans.getBean("userDao");
            	RoleDao roleDao = (RoleDao)Beans.getBean("roleDao");
            	
            	User user = userDao.findByUsername(username);
            	if (user != null){
            		msg = "Username is already exist.";
                	request.setAttribute("msg", msg);
                	sc.getRequestDispatcher("/register.jsp").forward(request, response);
                	return;	
            	}
            	if (userDao.findByEmail(email) != null){
            		msg = "Email is already exist.";
                	request.setAttribute("msg", msg);
                	sc.getRequestDispatcher("/register.jsp").forward(request, response);
                	return;	
            	}
        		user = new User();
        		user.setUsername(username);
        		user.setPassword(password);
        		user.setEmail(email);
        		user.setEnabled(true);		
        		user.getRoles().add(roleDao.findByName("ROLE_USER"));
        		userDao.save(user);
        		
        		// email newly registered user as well as administrator
            	ServletContext context = request.getSession().getServletContext();
            	String baseUrl = (String)context.getAttribute(Constants.APP_BASE_URL);
        		Map data = new HashMap();
				data.put("url", baseUrl);
				data.put("username", username);
				data.put("password", password);
				data.put("date", new Date().toString());	
				
				if (!baseUrl.contains("localhost") && !baseUrl.contains("127.0.0.1")) {
	        		logger.info("notifying adminstrator...");			
	        		GeneralUtil.notifyAdmin("C-IKNOW: New User Registered", data, "notification2admin4registration.vm");
					
	        		logger.info("notifying new user...");
	        		GeneralUtil.notify("C-IKNOW Registration Confirmation", email, data, "notification2user4registration.vm");
				}
				
                logger.debug("new node (username=" + username + ") is created.");
                request.setAttribute("m", "Registration succeeded. Please login.");
                request.setAttribute("url", GeneralUtil.getLoginUrl());
                request.setAttribute("u", username);
                sc.getRequestDispatcher("/forward.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e.getMessage());
        }
    }    
    
}
