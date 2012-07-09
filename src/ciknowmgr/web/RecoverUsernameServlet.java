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

import ciknowmgr.dao.UserDao;
import ciknowmgr.domain.User;
import ciknowmgr.util.Beans;
import ciknowmgr.util.Constants;
import ciknowmgr.util.GeneralUtil;
import ciknowmgr.util.PropsUtil;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

/**
 * @author gyao
 */
public class RecoverUsernameServlet extends HttpServlet {
    private static final long serialVersionUID = -2470876179879040968L;
    private Logger logger = Logger.getLogger(this.getClass());


    @SuppressWarnings({"rawtypes", "unchecked"})
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            PropsUtil props = new PropsUtil("ciknowmgr");
            String msg = "";
            ServletContext sc = getServletContext();
        	
        	String email = request.getParameter("email").trim();
        	logger.info("recovering user account for email=" + email);
        	
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
            	sc.getRequestDispatcher("/getusername.jsp").forward(request, response);
            	return;
            }        	
        	
        	// retrieve user account
        	UserDao userDao = (UserDao)Beans.getBean("userDao");
        	
        	User user = userDao.findByEmail(email);
        	if (user == null){
        		msg = "User with email=" + email + " does not exist.";
            	request.setAttribute("msg", msg);
            	sc.getRequestDispatcher("/getusername.jsp").forward(request, response);
            	return;	
        	}
    		
    		// email notification
        	ServletContext context = request.getSession().getServletContext();
        	String baseUrl = (String)context.getAttribute(Constants.APP_BASE_URL);
        	String username = user.getUsername();
    		Map data = new HashMap();
			data.put("url", baseUrl);
			data.put("username", username);
			data.put("date", new Date().toString());	
			
			if (!baseUrl.contains("localhost") && !baseUrl.contains("127.0.0.1")) {
        		logger.info("notifying adminstrator...");			
        		GeneralUtil.notifyAdmin("C-IKNOW: Username Recovery", data, "notification2admin4recoverusername.vm");
				
        		logger.info("notifying user...");
        		GeneralUtil.notify("C-IKNOW Username Recovery", email, data, "notification2user4recoverusername.vm");
			}
			
            logger.debug("account recovered: " + username + "(" + user.getEmail() + ").");
            request.setAttribute("m", "Your recovered username will be sent to your email address in a few seconds.");
            request.setAttribute("url", GeneralUtil.getLoginUrl());
            request.setAttribute("u", username);
            sc.getRequestDispatcher("/forward.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e.getMessage());
        }
    }    
    
}
