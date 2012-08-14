package ciknowmgr.job;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ciknowmgr.dao.ProjectDao;
import ciknowmgr.dao.UserDao;
import ciknowmgr.domain.Project;
import ciknowmgr.domain.User;
import ciknowmgr.util.Constants;
import ciknowmgr.util.GeneralUtil;
import ciknowmgr.util.ManagerUtil;

/**
 * Detect whether projects are inactive. If so, disable them.
 * This is used in Spring cron job
 * @author gyao
 *
 */
public class CIKNOWDetector {
	private static final Log logger = LogFactory.getLog(CIKNOWDetector.class);
	
	private UserDao userDao;
	private ProjectDao projectDao;	
	private ManagerUtil managerUtil;
	
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

	public ManagerUtil getManagerUtil() {
		return managerUtil;
	}

	public void setManagerUtil(ManagerUtil managerUtil) {
		this.managerUtil = managerUtil;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void detect() throws ClassNotFoundException{
		logger.info("Detecting CIKNOW projects...");
		
		Class.forName("com.mysql.jdbc.Driver");	
		Connection con = null;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.WEEK_OF_MONTH, -2);
		List<User> allUsers = userDao.loadAll();
		
		List<Project> projects = projectDao.getAll();
		for (Project project : projects){
			// skip if project is already disabled
			if (!project.getEnabled()) continue; 
			
			try{
				String url = "jdbc:mysql://localhost:3306/_" + project.getName();
				con = DriverManager.getConnection(url, "sonic", "sonic");
				String sql;
				ResultSet rs;
				
				// check whether project has been logged in. If not, skip
				sql = "SELECT COUNT(*) FROM activities";
				Statement st = con.createStatement();
				rs = st.executeQuery(sql);
				rs.next();
				int count = rs.getInt(1);
				if (count == 0) {
					logger.info("Project: " + project.getName() + " has never been logined.");
					continue;
				}

				// check whether project is inactive (e.g. no activities in last two weeks)
				// if so, disable it.
				sql = "SELECT COUNT(*) FROM activities WHERE timestamp >= ?";
				PreparedStatement ps = con.prepareStatement(sql);	
				Date cutoff = new Date(cal.getTimeInMillis());
				ps.setDate(1, cutoff);
				rs = ps.executeQuery();
				rs.next();
				count = rs.getInt(1);
				logger.debug("Activity count after " + cutoff + ": " + count);
				if (count > 0) {
					logger.info("Project: " + project.getName() + " is active.");
				} else {
					logger.info("Disabling project: " + project.getName());
					managerUtil.processAction(project.getId(), "admin", Constants.DISABLE);
					
					logger.info("Notify project creator/owners as well as admin...");
					List<User> users = filterByProject(allUsers, project);
					Map mailData = new HashMap();
					String ciknowmgrUrl = GeneralUtil.getBaseUrl();
					mailData.put("projectName", project.getName());
					mailData.put("ciknowmgrUrl", ciknowmgrUrl);
					for (User user : users){
						String email = user.getEmail();
						if (email == null || email.isEmpty()){
							logger.warn("Email for project owner: " + user.getUsername() + " is not available.");
							continue;
						} else if (ciknowmgrUrl.contains("localhost") || ciknowmgrUrl.contains("127.0.0.1")){
							logger.info(user.getUsername() + " would have received notification about disabled project in production system.");
							continue;
						}
						GeneralUtil.notify("Your CIKNOW project: " + project.getName() + " has been disabled", 
								user.getEmail(), mailData, "notification2user4disable.vm");
					}
					
					if (ciknowmgrUrl.contains("localhost") || ciknowmgrUrl.contains("127.0.0.1")){
						logger.info("Administrator(s) would have received notification about disabled project in production system.");
					} else {
						GeneralUtil.notifyAdmin("CIKNOW project: " + project.getName() + " has been disabled", 
								mailData, "notification2admin4disable.vm");
					}
				}
			} catch (SQLException se){
				logger.warn(se.getMessage());
			} catch (Exception e) {
				logger.warn(e.getMessage());
				e.printStackTrace();
			} finally{
				if (con != null){
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}
	
	private List<User> filterByProject(List<User> allUsers, Project project){
		List<User> users = new ArrayList<User>();
		
		for (User user : allUsers){
			if (user.getProjects().contains(project)) users.add(user);
		}
		
		return users;
	}
}
