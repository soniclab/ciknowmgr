package ciknowmgr.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.VelocityEngine;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zul.Messagebox;

import ciknowmgr.dao.ProjectDao;
import ciknowmgr.dao.UserDao;
import ciknowmgr.domain.Project;
import ciknowmgr.domain.User;
import ciknowmgr.security.ColfaxUserDetails;
import ciknowmgr.util.Beans;

public class GeneralUtil {
	private static Log logger = LogFactory.getLog(GeneralUtil.class);
	
	public static void main(String[] args){
		try {
			setDefaultColors();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static User getLogin(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) {
			logger.warn("Cannot find login user!!!");
			return null;	
		}
		ColfaxUserDetails userDetails = (ColfaxUserDetails)auth.getPrincipal();
		return userDetails.getUser();
	}
	
	// e.g. http://ciknow.northwestern.edu/ciknowmgr
	public static String getBaseUrl(){
		WebApp app = WebApps.getCurrent();
		String baseUrl = (String)app.getAttribute(Constants.APP_BASE_URL);
		return baseUrl;
	}
	
	// e.g. http://ciknow.northwestern.edu
	public static String getServerUrl(){
		String baseUrl = getBaseUrl();
		return baseUrl.substring(0, baseUrl.lastIndexOf("/"));	
	}
	
	public static String getLoginUrl(){
		PropsUtil props = new PropsUtil("ciknowmgr");
		return props.get("cas.login.url");
	}
	
	public static String getRealPath(){
		WebApp app = WebApps.getCurrent();
		String realPath = (String)app.getAttribute(Constants.APP_REAL_PATH);
		return realPath;
	}
	
	public static String getInstruction(){
		StringBuilder sb = new StringBuilder();
		try{
			String filename = getRealPath() + "WEB-INF/classes/instruction.txt";
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line = null;
			while ((line = reader.readLine()) != null){
				sb.append(line).append("\n");
			}			
			reader.close();
		} catch (Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
		}

		return sb.toString();
	}
	
	public static void saveInstruction(String instruction) throws Exception{
		String filename = getRealPath() + "WEB-INF/classes/instruction.txt";
		PrintWriter writer = new PrintWriter(new File(filename));
		writer.write(instruction);
		writer.close();
	}
	
	private static void setDefaultColors() throws DocumentException, IOException{
		String path = "C:\\Users\\gyao\\apache-tomcat-6.0.16\\webapps";
		String defaultNodeColors = "0xFF8C69,0xA4D3EE,0xFFFF66,0x8FBC8F,0xFF69B4,0x17BECF,0xDAC4E5,0xEE9A49,0x6B8E23,0x666699,0xCCFF66,0xFFE4E1,0x8DB6CD,0xCC3300,0xCD8500,0xC7C7C7,0xBCBD22,0xB452CD,0xDAA520,0x4876FF";
		String defaultEdgeColors = "0x1F77B4,0x990066,0x66CC99,0x666666,0xFFFF33,0x9370D8,0xFF7F0E,0x99CCCC,0x0000CC,0xCCCC99,0x669933,0xC71585,0x999999,0x99FFFF,0xFF3399,0xCCFF00,0xFFE4E1,0x98FB98,0x9370D8,0xCC3300";
		Beans.init();
		ProjectDao projectDao = (ProjectDao) Beans.getBean("projectDao");
		List<Project> projects = projectDao.getAll();
		for (Project project : projects){
			if (project.getName().equals("ciknow")) continue;
			String filename = path + "\\_" + project.getName() + "\\WEB-INF\\classes\\ciknow.xml";
			logger.info("file: " + filename);
			InputStream is = new FileInputStream(filename);
			Document doc = readXML(is);
			is.close();
			
			// modify ciknow.xml
			Element config = doc.getRootElement();
			config.element("nodeColors").addAttribute("default", defaultNodeColors);
			config.element("nodeColors").addAttribute("value", defaultNodeColors);
			
			config.element("edgeColors").addAttribute("default", defaultEdgeColors);
			config.element("edgeColors").addAttribute("value", defaultEdgeColors);
			
			OutputStream os = new FileOutputStream(filename);
			writeXML(doc, os);
			os.close();
		}
	}
	
	public static Document readXML(InputStream is) throws DocumentException{
    	SAXReader reader = new SAXReader();
    	Document doc = reader.read(is);
    	return doc;
	}

	
	public static void writeXML(Document doc, OutputStream out) throws IOException{
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(new OutputStreamWriter(out, "UTF-8"), format);
		writer.write(doc);
		writer.flush();
	}
	
	@SuppressWarnings("rawtypes")
	public static void notify(final String subject, final String toEmail, final Map data, final String templateFileName){
		if (!Validation.isEmailValid(toEmail)){
			logger.error("invalid email:" + toEmail);
			return;
		}
		
		MimeMessagePreparator preprarator = new MimeMessagePreparator(){

			public void prepare(MimeMessage mimeMessage) throws Exception {				
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
				message.setFrom("no-reply@sonic.northwestern.edu");
				message.addTo(toEmail);		
				message.setSubject(subject);
				VelocityEngine velocityEngine = (VelocityEngine) Beans.getBean("velocityEngine");
				
				String text = VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine,
						templateFileName, 
						data);
				message.setText(text);								
			}
			
		};
		
		JavaMailSender mailSender = (JavaMailSender) Beans.getBean("mailSender");
		
		// set mail host
		PropsUtil props = new PropsUtil("ciknowmgr");
		String host = props.get("smtp.host");
		if (host == null || host.length() == 0) host = "localhost";
		((org.springframework.mail.javamail.JavaMailSenderImpl)mailSender).setHost(host);
		logger.debug("SMTP host: " + host);
		
		mailSender.send(preprarator);
		logger.info("Notification sent to " + toEmail);
    }
    
	@SuppressWarnings("rawtypes")
	public static void notifyAdmin(final String subject, final Map data, final String templateFileName){
    	UserDao userDao = (UserDao)Beans.getBean("userDao");
		List<User> users = userDao.loadAll();		
		for (User u : users){
			if (u.isAdmin()){
				String adminEmail = u.getEmail();
				GeneralUtil.notify(subject, adminEmail, data, templateFileName);
			}
		}
    }
	
	/****************************************************
	 * Character handling, escape
	 * @throws InterruptedException 
	 * **************************************************/	
	public static boolean isValidName(String name) throws InterruptedException{
        if (name == null || name.isEmpty()){
        	Messagebox.show("Name is required.");
        	return false;
        }
        if (name.length() > 80){
        	Messagebox.show("Name cannot be longer than 80 characters");
        	return false;
        }
        if (GeneralUtil.containSpecialCharacter(name) 
        		|| name.contains(" ") 
        		|| name.contains("`")){
        	Messagebox.show("Name cannot contain special characters and spaces");
        	return false;
        }
        return true;
	}
	
	public static boolean isValidLabel(String label) throws InterruptedException{
        if (label.isEmpty()){
        	Messagebox.show("Description is required.");
        	return false;
        }
        if (label.length() > 255){
        	Messagebox.show("Description cannot be longer than 255 characters");
        	return false;
        }
        return true;
	}
	
	/*
	 * these characters cannot exist in file/directory names:
	 * 		/ \ * " < > : | ?
	 */
	private static String[] specialCharacters = {"/", "\\", "*", "\"", "<", ">", ":", "|", "?", ","};

	public static boolean containSpecialCharacter(String s){
		for (String c : specialCharacters){
			if (s.indexOf(c) >= 0) return true;
		}
		return false;
	}
	
	public static String replaceSpecialCharacter(String s, String r){
		for (String c : specialCharacters){
			s = s.replace(c, r);
		}
		return s;
	}
	
	public static String capitalizeInitial(String input){
		String head = input.substring(0, 1);
		String tail = input.substring(1);
		return head.toUpperCase() + tail;			
	}
}
