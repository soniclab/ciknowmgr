package ciknowmgr.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ciknowmgr.dao.ProjectDao;
import ciknowmgr.dao.UserDao;
import ciknowmgr.domain.Project;
import ciknowmgr.domain.ProjectLog;
import ciknowmgr.domain.User;

public class ManagerUtil {
	private static Log logger = LogFactory.getLog(ManagerUtil.class);
	
	private ProjectDao projectDao;
	private UserDao userDao;
	
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public ProjectDao getProjectDao() {
		return projectDao;
	}

	public void setProjectDao(ProjectDao projectDao) {
		this.projectDao = projectDao;
	}


	public synchronized String batchProcess(List<Long> projectIds, User login, String action){
		StringBuilder sb = new StringBuilder();

		if (action.equals(Constants.CREATE) || action.equals(Constants.COPY)){
			return "Batch " + action + " is not supported.";
		}
		
		for (Long projectId : projectIds){
			Project project = projectDao.findById(projectId);
			if (project == null) {
				String warning = "Cannot find project with id: " + projectId;
				logger.warn(warning);
				sb.append(warning).append("\n");
				continue;
			}
			
			logger.info("Project: " + project.getName());
			if (project.getEnabled() && action.equals(Constants.ENABLE)) {
				sb.append("Project: " + project.getName() + " is already enabled.").append("\n");
				continue;
			}
			if (!project.getEnabled() && !action.equals(Constants.ENABLE)) {
				sb.append("Project: " + project.getName() + " is not enabled.").append("\n");
				continue;
			}
			
			try{
				if (action.equals(Constants.DELETE)){
					delete(projectId, login);
				} else {
					processAction(projectId, login.getUsername(), action);
				}
				sb.append(action + " " + project.getName() + " successful.").append("\n");
			} catch (Exception e){
				logger.warn(e.getMessage());
				e.printStackTrace();
				sb.append(action + " " + project.getName() + " failed: " + e.getMessage()).append("\n");
			}
		}

		return sb.toString();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized void create(String name, String description, String url, User login) throws Exception{
		if (projectDao.findByName(name) != null) {
			throw new Exception("There is already a project named: " + name);
		}
		
		logger.info("creating new project " + name);
		invokeAnt(Constants.CREATE, name, null);
		
		// update project
		Project project = new Project();
		project.setName(name);
		project.setDescription(description);
		project.setUrl(url);
		project.setCreator(login.getUsername());
		project.setEnabled(true);
		
		ProjectLog log = new ProjectLog();
		log.setAction(Constants.CREATE);
		log.setUser(login.getUsername());
		log.setComment("");
		log.setTimeStamp(new Date());		
		project.getLogs().add(log);
		
		projectDao.save(project);
		
		// update user
		login.update(userDao.findById(login.getId()));
		login.getProjects().add(project);
		userDao.save(login);
		
		// insert project creator into newly created project (with administrator role)		
		Connection con = JdbcUtil.getConnection("jdbc:mysql://localhost:3306/_" + name, "sonic", "sonic");
		JdbcUtil.insert(con, login.getUsername(), JdbcUtil.PROJECT_CREATOR);
		
		// send out notifications for user and administrator
		Map data = new HashMap();
		String serverUrl = GeneralUtil.getServerUrl();
		if (!serverUrl.contains("localhost") && !serverUrl.contains("127.0.0.1")) {
			String projectUrl = serverUrl + "/_" + name;
			data.put("url", projectUrl);
			data.put("username", login.getUsername());
			data.put("date", new Date().toString());
			GeneralUtil.notify("You have created new C-IKNOW project: " + name, login.getEmail(), data, "notification2user4create.vm");
			GeneralUtil.notifyAdmin("New C-IKNOW Project Created: " + name, data, "notification2admin4create.vm");
		}
		
		logger.info("created project: " + project);
	}
	
	public synchronized void copy(User login, String name, String newName) throws Exception{
		if (projectDao.findByName(newName) != null) {
			throw new Exception("There is already a project named: " + newName);
		}
		
		logger.info("copying project " + name + " to " + newName);		
		
		invokeAnt(Constants.COPY, name, newName);
		
		Project p = new Project();
		p.setName(newName);
		p.setDescription("A copy of project: " + name);
		p.setUrl("");
		p.setCreator(login.getUsername());
		p.setEnabled(true);
		
		ProjectLog log = new ProjectLog();
		log.setAction(Constants.COPY);
		log.setUser(login.getUsername());
		log.setComment("");
		log.setTimeStamp(new Date());				
		p.getLogs().add(log);				
		projectDao.save(p);
		
		login.update(userDao.findById(login.getId()));
		login.getProjects().add(p);
		userDao.save(login);
	}
	
	/**
	 * Delete a project only if the actor is the only owner, or the actor is admin
	 * @param projectId
	 * @param actor
	 * @throws Exception
	 */
	public synchronized void delete(Long projectId, User login) throws Exception{
		Project project = projectDao.findById(projectId);
		if (project == null) throw new Exception("The project to be deleted does not exist.");
		
		// check if project is shared
		List<User> users = userDao.getAll();
		StringBuilder sb = new StringBuilder();
		for (User user : users){
			if (user.getUsername().equals(login.getUsername())) continue;
			if (user.getProjects().contains(project)){
				sb.append(">> " + user.getUsername());
			}
		}
		String msg = sb.toString();
		if (!login.isAdmin() && msg.length() > 0){
			throw new Exception("You cannot delete project (name=" + project.getName() + ") because it is shared by other users:\n" + msg + "\n\nOnly Administrator can help you to delete this project permanently.");
		}
		
		logger.info("deleting project " + project.getName());
		invokeAnt(Constants.DELETE, project.getName(), null);


		if (msg.length() > 0){
			for (User user : users){
				user.getProjects().remove(project);
			}
			userDao.save(users);
			login.update(userDao.findById(login.getId()));
		} else {
			login.update(userDao.findById(login.getId()));
			login.getProjects().remove(project);
			userDao.save(login);
		}
		
		projectDao.delete(projectDao.findById(projectId));
		
		logger.info("deleted project: " + project);
	}
	
	/**
	 * update/enable/disable/clean/restart
	 */
	public synchronized void processAction(Long projectId, String actor, String action) throws Exception{		
		Project project = projectDao.findById(projectId);
		if (project == null) throw new Exception("The project does not exist.");
		logger.info(action + " project (name=" + project.getName() + ") by " + actor);
		invokeAnt(action, project.getName(), null);		
		
		project = projectDao.findById(projectId);
		if (action.equals(Constants.DISABLE)) project.setEnabled(false);
		else project.setEnabled(true);		
		ProjectLog log = new ProjectLog();
		log.setAction(action);
		log.setUser(actor);
		log.setComment("");
		log.setTimeStamp(new Date());				
		project.getLogs().add(log);				
		projectDao.save(project);
	}

	private synchronized void invokeAnt(String action, String name, String newName) throws Exception{
		String context = null;
		String oldContext = null;
		if (action.equals(Constants.COPY)){
			oldContext = "_" + name;
			context = "_" + newName;
		} else {
			context = "_" + name;
		}
		
		String pathPrefix = GeneralUtil.getRealPath();
		String buildFile = pathPrefix + "/WEB-INF/build.xml";
		String buildCfgFile = pathPrefix + "/WEB-INF/build.properties";		
		String clientBuildCfgFile = pathPrefix + "/WEB-INF/template/client/build.properties";
		
		// update ant build properties
		Properties prop = new Properties();
		InputStream is = new FileInputStream(new File(buildCfgFile));
		prop.load(is);
		is.close();
		prop.list(System.out);
		
		OutputStream os = new FileOutputStream(new File(buildCfgFile));
		prop.setProperty("context", context);
		if (action.equals(Constants.COPY)){
			prop.setProperty("oldContext", oldContext);
			String path = pathPrefix + "WEB-INF/sql/";
			new MysqlDump(path, oldContext);
		}
		prop.store(os, "last updated: " + new Date());
		os.close();
		prop.list(System.out);
		
		// update client ant build properties
		prop = new Properties();
		is = new FileInputStream(new File(clientBuildCfgFile));
		prop.load(is);
		is.close();
		prop.list(System.out);
		
		os = new FileOutputStream(new File(clientBuildCfgFile));
		prop.setProperty("context", context);
		prop.store(os, "last updated: " + new Date());
		os.close();
		prop.list(System.out);
		
		// invoke ant
		AntUtil.executeTarget(buildFile, action);
	}
}
