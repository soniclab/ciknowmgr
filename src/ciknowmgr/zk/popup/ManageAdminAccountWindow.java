package ciknowmgr.zk.popup;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ciknowmgr.dao.ProjectDao;
import ciknowmgr.dao.UserDao;
import ciknowmgr.domain.Project;
import ciknowmgr.domain.User;
import ciknowmgr.util.Beans;
import ciknowmgr.util.JdbcUtil;

public class ManageAdminAccountWindow extends Window {
	private static final long serialVersionUID = -1902950071713495757L;

	private static Log logger = LogFactory.getLog(ManageAdminAccountWindow.class);
	
	private UserDao userDao;
	private ProjectDao projectDao;
	
	private Listbox accountsBox;
	private Grid detailsBox;
	private Label usernameLabel;
	private Textbox passwordBox;
	private Textbox emailBox;
	private Checkbox enabledBox;
	private Listbox projectsBox;
	
	public ManageAdminAccountWindow(Component parent) throws Exception{
		this.setParent(parent);
		this.setClosable(true);
		this.setSclass("manageAdminAccountWindow");
		this.doModal();
		
		// create ui from template
		Executions.createComponents("/WEB-INF/composite/ManageAdminAccountWindow.zul", this, null);
		
		Components.wireVariables(this, this, '$', true, true);
		Components.addForwards(this, this, '$');
		
		userDao = (UserDao)Beans.getBean("userDao");
		projectDao = (ProjectDao)Beans.getBean("projectDao");
		
		// populate UI
		render(null);
	}
	
	public void render(String selectedUsername){
		logger.debug("rendering ...");
		// populate accounts list
		List<User> users = userDao.getAll();
		List<String> usernames = new ArrayList<String>();
		for (User user : users){
			// There is only one administrator, who can not be modified via interface for tighten security
			if (user.getUsername().equals("admin")) continue;
			
			usernames.add(user.getUsername());
		}
		Collections.sort(usernames);
		ListModelList userModel = new ListModelList(usernames);
		accountsBox.setModel(userModel);		
		
		// set selection if appropriate
		projectsBox.setMultiple(true);
		projectsBox.setCheckmark(true);
		if (usernames.isEmpty()) {
			detailsBox.setVisible(false);
			return;
		}
		if (selectedUsername == null) userModel.addSelection(usernames.get(0));
		else userModel.addSelection(selectedUsername);
		onSelect$accountsBox();
	}
	
	@SuppressWarnings("rawtypes")
	public void onSelect$accountsBox(){
		Set selections = ((ListModelList)accountsBox.getModel()).getSelection();
		if (selections == null || selections.isEmpty()) {
			detailsBox.setVisible(false);
		} else {
			detailsBox.setVisible(true);
			
			// populate user info
			String username = (String)selections.iterator().next();
			logger.info("Selected user: " + username);
			User user = userDao.loadByUsername(username);
			logger.debug(user);
			usernameLabel.setValue(user.getUsername());
			passwordBox.setValue(user.getPassword());
			String email = user.getEmail();
			if (email == null || email.isEmpty()) {
				email = "please-provide-email@required.com";
			}
			emailBox.setValue(email);
			enabledBox.setChecked(user.getEnabled());
			//isAdminBox.setChecked(user.isAdmin());
			
			// populate project list for selected user			
			logger.info("Get all projects...");
			List<String> projectNames = new ArrayList<String>();
			for (Project project : projectDao.getAll()){
				projectNames.add(project.getName());
				logger.debug("project: " + project.getName());
			}
			Collections.sort(projectNames);
			ListModelList projectModel = new ListModelList(projectNames);
			logger.info("Get projects created by or shared to selected user ...");
			for (Project project : user.getProjects()){
				projectModel.addSelection(project.getName());
				logger.debug("project: " + project.getName());
			}
			projectsBox.setModel(projectModel);
		}		
	}
	
	public void onClick$createBtn() throws Exception{
		CreateUserWindow win = new CreateUserWindow(this);
		win.doModal();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onClick$updateBtn() throws Exception{
		logger.debug("on update");
		Set selections = ((ListModelList)accountsBox.getModel()).getSelection();
		if (selections == null || selections.isEmpty()) {
			Messagebox.show("Please select a user first.");
			return;
		} else {			
			// get selected user
			String username = (String)selections.iterator().next();			
			User user = userDao.loadByUsername(username);
			
			// update user info
			user.setPassword(passwordBox.getValue().trim());
			user.setEmail(emailBox.getValue().trim());
			user.setEnabled(enabledBox.isChecked());
			
			// update user projects list
			Set<Project> oldUserProjects = user.getProjects(); 
			Map<String, Project> projectMap = new HashMap<String, Project>();
			List<Project> allProjects = projectDao.getAll();
			List<Project> userProjects = new ArrayList<Project>();		// projects owned (created or assigned) by this user
			List<String> createdProjectNames = new ArrayList<String>(); // projects created by this user
			for (Project project : allProjects){
				projectMap.put(project.getName(), project);
				if (project.getCreator().equals(username)) {
					userProjects.add(project);
					createdProjectNames.add(project.getName());
				}
			}
			Set<String> selectedProjectNames = (Set<String>)((ListModelList)projectsBox.getModel()).getSelection();
			for (String projectName : selectedProjectNames){
				Project project = projectMap.get(projectName);
				userProjects.add(project);
				createdProjectNames.remove(projectName);
			}
			Set<Project> newUserProjects = new HashSet<Project>(userProjects);
			user.setProjects(newUserProjects);
			String warnMsg = "";
			if (!createdProjectNames.isEmpty()) {
				warnMsg = "Some project(s) cannot be removed because they are created by this user.";
			}
			
			// save
			userDao.save(user);
			
			// update UI
			render(username);
			
			// notify client
			Messagebox.show("User (username=" + username + ") is updated." + " " + warnMsg);
			
			// insert/delete project owners
			for (Project project : newUserProjects){
				if (!oldUserProjects.contains(project)) {
					Connection con = JdbcUtil.getConnection("jdbc:mysql://localhost:3306/_" + project.getName(), "sonic", "sonic");
					JdbcUtil.insert(con, username, JdbcUtil.PROJECT_OWNER);
				} else oldUserProjects.remove(project);
			}
			for (Project project : oldUserProjects){
				Connection con = JdbcUtil.getConnection("jdbc:mysql://localhost:3306/_" + project.getName(), "sonic", "sonic");
				JdbcUtil.delete(con, username);
			}
		}
	}
	
	/**
	 *  Avoid delete, but disable user instead. 
	 */
	@SuppressWarnings("rawtypes")
	public void onClick$deleteBtn() throws InterruptedException{
		Set selections = ((ListModelList)accountsBox.getModel()).getSelection();
		if (selections == null || selections.isEmpty()) {
			Messagebox.show("Please select a user first.");
			return;
		} else {			
			// get selected user
			String username = (String)selections.iterator().next();			
			final User user = userDao.loadByUsername(username);
			List<Project> allProjects = projectDao.getAll();
			List<Project> userCreatedProjects = new ArrayList<Project>();
			for (Project project : allProjects){
				if (project.getCreator().equals(username)) userCreatedProjects.add(project);
			}
			if (!userCreatedProjects.isEmpty()){
				Messagebox.show("The projects created by this user need to be deleted before deleting this user.");
				return;
			}
			
			Messagebox.show("Are you sure to delete this user: " + username, "", 
					Messagebox.YES|Messagebox.NO, Messagebox.QUESTION, 
					new EventListener(){

				@Override
				public void onEvent(Event event) throws Exception {
					if (event.getName().equals(Messagebox.ON_YES)){
						// remove user from user owned projects' databases
						// note: user created projects have been deleted
						Set<Project> userOwnedProjects = user.getProjects(); 
						for (Project project : userOwnedProjects){
							Connection con = JdbcUtil.getConnection("jdbc:mysql://localhost:3306/_" + project.getName(), "sonic", "sonic");
							JdbcUtil.delete(con, user.getUsername());
						}
						
						// remove user from ciknowmgr
						userDao.delete(user);
						
						// refresh UI
						render(null);	
					}
				}
			});
		}
	}

	
	public void onClick$closeBtn(){
		this.setParent(null);
	}
}
