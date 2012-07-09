package ciknowmgr.zk;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.api.Checkbox;

import ciknowmgr.dao.ProjectDao;
import ciknowmgr.domain.Project;
import ciknowmgr.domain.ProjectLog;
import ciknowmgr.domain.User;
import ciknowmgr.util.Beans;
import ciknowmgr.util.Constants;
import ciknowmgr.util.GeneralUtil;
import ciknowmgr.util.ManagerUtil;
import ciknowmgr.util.ProjectNameComparator;
import ciknowmgr.util.PropsUtil;
import ciknowmgr.util.SessionUtil;
import ciknowmgr.zk.popup.CopyProjectWindow;
import ciknowmgr.zk.popup.CreateProjectWindow;
import ciknowmgr.zk.popup.EditInstructionWindow;
import ciknowmgr.zk.popup.ManageAdminAccountWindow;
import ciknowmgr.zk.popup.ManageSelfAccountWindow;


public class ManagerController extends GenericForwardComposer {
	private static final long serialVersionUID = -9135576397887656457L;

	private static Log logger = LogFactory.getLog(ManagerController.class);
	
	// Spring Beans
	private ProjectDao projectDao;	
	private ManagerUtil managerUtil;
	
	// UI
	private Label usernameLabel;
	private Vbox centerBox;
	private Html instruction;
	private Listbox projectList;
	
	private Button editBtn;
	private Button restartBtn;
	private Button updateBtn;
	private Button copyBtn;
	private Button enableBtn;
	private Button disableBtn;
	private Button cleanBtn;
	private Button deleteBtn;
	private Checkbox batchBox;
	
	// private data
	private ListModelList projectModel;
	
	public void doAfterCompose(Component comp) throws Exception{	
		super.doAfterCompose(comp);		

		populate();
	}
	
	public void onClientInfo$stage(ForwardEvent event) throws Exception {
		ClientInfoEvent evt = (ClientInfoEvent) event.getOrigin();
		Integer width = evt.getDesktopWidth();
		Integer height = evt.getDesktopHeight();
		logger.debug("screen.width: " + evt.getScreenWidth());
		logger.debug("screen.height: " + evt.getScreenHeight());
		logger.debug("desktop.width: " + width);
		logger.debug("desktop.height: " + height);


		Integer currentWidth = (Integer) SessionUtil.getDesktopWidth();
		Integer currentHeight = (Integer) SessionUtil.getDesktopHeight();

		// first hit
		if (currentWidth == null || currentHeight == null) {
			logger.debug("First Hit");
			SessionUtil.setDesktopWidth(width);
			SessionUtil.setDesktopHeight(height);
			return;
		}

		// browser resize
		if (!width.equals(currentWidth) || !height.equals(currentHeight)) {
			logger.debug("Browser Resize");
			SessionUtil.setDesktopWidth(width);
			SessionUtil.setDesktopHeight(height);	
		}
	}
    
	private void populate() throws Exception{		
		// share controller in session
		SessionUtil.setController(this);
		
		// Initialize states
		projectDao = (ProjectDao)Beans.getBean("projectDao");	
		managerUtil = (ManagerUtil)Beans.getBean("managerUtil");

		// Render 
		logger.debug("Set instruction...");
		setInstruction(GeneralUtil.getInstruction());
		
		logger.debug("Get Login info...");
		User login = GeneralUtil.getLogin();	
		usernameLabel.setValue(login.getUsername());
		if (!login.getEmail().equals("yao.gyao@gmail.com")){
			editBtn.setParent(null);
		}		
		logger.debug("Rendering projects ...");
		renderProjects();
	}
	

	
	/**************************************************************/
	/**************** GENERAL USER FUNCTIONS **********************/
	/**************************************************************/
		
	public void onClick$logoutBtn() throws Exception{
		logger.info("logout: " + GeneralUtil.getLogin().getUsername());
		Executions.sendRedirect("/j_spring_security_logout");
	}
	
	public void onSelect$projectList(){
		updateBtns();
	}
	
	public void onClick$createBtn() throws InterruptedException{
		CreateProjectWindow win = new CreateProjectWindow(centerBox);
		win.doModal();
	}
	
	@SuppressWarnings("rawtypes")
	public void onClick$copyBtn() throws InterruptedException, ParseException{
		Set selections = projectModel.getSelection();
		if (selections.isEmpty()) {
			Messagebox.show("Please select a project first");
			return;
		}		
		if (!isUpdatable()) {
			Messagebox.show("Selected project was created before cutoff date and cannot be copied.");
			return;
		}
		
		Project selection = (Project)selections.iterator().next();
		logger.info("Copy project: " + selection.getName());
		
		CopyProjectWindow win = new CopyProjectWindow(centerBox, selection);
		win.doModal();
	}
	
	
	@SuppressWarnings("rawtypes")
	public void onClick$deleteBtn() throws InterruptedException{
		if (batchBox.isChecked()) {
			batchProcess(Constants.DELETE);
			return;
		}
		
		Set selections = projectModel.getSelection();
		if (selections.isEmpty()) {
			Messagebox.show("Please select a project first");
			return;
		}			
		
		final Project selection = (Project)selections.iterator().next();
		Messagebox.show("Are you sure to delete the project: " + selection.getName(), "", 
				Messagebox.YES|Messagebox.NO, Messagebox.QUESTION, 
				new EventListener(){

			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals(Messagebox.ON_YES)){
					try{
						managerUtil.delete(selection.getId(), GeneralUtil.getLogin());
						removeProject(selection);
					} catch(Exception e){
						logger.error(e.getMessage());
						e.printStackTrace();
						//Messagebox.show("Data out of sync. Click OK to refresh browser and try again.", "", Messagebox.OK, Messagebox.ERROR, new EventListener(){
						Messagebox.show(e.getMessage(), "", Messagebox.OK, Messagebox.ERROR, new EventListener(){
							@Override
							public void onEvent(Event evt) throws Exception {
								Executions.sendRedirect(null);
							}
							
						});
					}
				}
			}
			
		});
	}
	
	public void onClick$restartBtn() throws Exception{
		processAction(Constants.RESTART);
	}	
	public void onClick$updateBtn() throws Exception{
		if (!isUpdatable()) {
			Messagebox.show("Selected project was created before cutoff date and cannot be updated.");
			return;
		}
		processAction(Constants.UPDATE);
	}	
	public void onClick$enableBtn() throws Exception{
		processAction(Constants.ENABLE);
	}	
	public void onClick$disableBtn() throws Exception{
		processAction(Constants.DISABLE);
	}	
	public void onClick$cleanBtn() throws Exception{
		processAction(Constants.CLEAN);
	}	
	@SuppressWarnings("rawtypes")
	private void processAction(final String action) throws Exception{
		if (batchBox.isChecked()) {
			batchProcess(action);
			return;
		}
		
		Set selections = projectModel.getSelection();
		if (selections.isEmpty()) {
			Messagebox.show("Please select a project first");
			return;
		}				
		final Project selection = (Project)selections.iterator().next();
		Messagebox.show("Are you sure to " + action + " the project: " + selection.getName(), "", 
				Messagebox.YES|Messagebox.NO, Messagebox.QUESTION, 
				new EventListener(){

			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals(Messagebox.ON_YES)){
					try {
						User login = GeneralUtil.getLogin();
						logger.info("login: " + login.getUsername() + ", action: " + action + ", project: " + selection.getName());
						
						// process the requested operation
						managerUtil.processAction(selection.getId(), login.getUsername(), action);		
						
						// update interface
						updateProject(projectDao.findById(selection.getId()));
					} catch(Exception e){
						logger.error(e.getMessage());
						e.printStackTrace();
						Messagebox.show("Data out of sync. Click OK to refresh browser and try again.", "", Messagebox.OK, Messagebox.ERROR, new EventListener(){

							@Override
							public void onEvent(Event evt) throws Exception {
								Executions.sendRedirect(null);
							}
							
						});
					}
				}
			}
			
		});
	}
	
	
	/**************************************************************/
	/****************** ADMINISTRATIVE FUNCTIONS ******************/
	/**************************************************************/	
	public void onClick$manageBtn() throws Exception{
		User login = GeneralUtil.getLogin();
		if (login.isAdmin()) {
			new ManageAdminAccountWindow(centerBox);
		} else {
			new ManageSelfAccountWindow(centerBox);			
		}
	}

	public void onClick$editBtn() throws InterruptedException{
		EditInstructionWindow win = new EditInstructionWindow(centerBox);
		win.doModal();
	}
	
	public void onCheck$batchBox() throws InterruptedException{
		if (batchBox.isChecked()){
			projectList.setMultiple(true);
			projectList.setCheckmark(true);
			
			restartBtn.setDisabled(false);
			updateBtn.setDisabled(false);
			copyBtn.setDisabled(true);
			enableBtn.setDisabled(false);
			disableBtn.setDisabled(false);
			cleanBtn.setDisabled(false);
			deleteBtn.setDisabled(false);
		} else {
			projectList.setMultiple(false);
			projectList.setCheckmark(false);
			projectList.clearSelection();
			updateBtns();
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void batchProcess(final String action) throws InterruptedException{
		Set selections = projectModel.getSelection();
		if (selections.isEmpty()) {
			Messagebox.show("Please select a project first");
			return;
		}
		
		final List<Long> projectIds = new ArrayList<Long>();
		StringBuilder sb = new StringBuilder();
		sb.append("Are you sure to " + action + " these projects?").append("\n");
		for (Object o : selections){
			Project selection = (Project)o;
			projectIds.add(selection.getId());
			sb.append(selection.getName()).append("\n");
		}
		
		Messagebox.show(sb.toString(), "", 
				Messagebox.YES|Messagebox.NO, 
				Messagebox.QUESTION, 
				new EventListener(){

			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals(Messagebox.ON_YES)){
					try {
						String result = managerUtil.batchProcess(projectIds, GeneralUtil.getLogin(), action);
						Messagebox.show(result);
						renderProjects();
					} catch(Exception e){
						logger.error(e.getMessage());
						e.printStackTrace();
						//Messagebox.show("Data out of sync. Click OK to refresh browser and try again.", "", Messagebox.OK, Messagebox.ERROR, new EventListener(){
						Messagebox.show(e.getMessage(), "", Messagebox.OK, Messagebox.ERROR, new EventListener(){

							@Override
							public void onEvent(Event evt) throws Exception {
								Executions.sendRedirect(null);
							}
							
						});
					}
				}
			}
			
		});
	}
	
	
	
	/**************************************************************/
	/**************** UTILITIY FUNCTIONS **************************/
	/**************************************************************/	
	public void setInstruction(String content){
		StringBuilder sb = new StringBuilder();
		/*
		sb.append("<div " +
				"style='" +
				"-moz-border-radius: 0px;" +
				"-webkit-border-radius: 0px;" +
				"border-radius: 0px;" +
				"border: 0px solid #fff5ee;" +
				"background:#fff5ee'>");
		*/
		if (content != null && !content.isEmpty()) {
			if (content.startsWith("<TEXTFORMAT ")){
				content = content.replaceAll("SIZE=\"[0-9]+\"", "");
				logger.debug("htmlInstruction from flash version of C-IKNOW Manager");
			}			
		} else content = "[Click Edit buttton to insert your instruction here]";
		sb.append(content);
		//sb.append("</div>");
		instruction.setContent(sb.toString());
	}
	
	public void addProject(Project project){
		projectModel.add(project);		
		projectModel.clearSelection();
		projectModel.addSelection(project);
		updateBtns();
	}
	
	public void updateProject(Project project){		
		int index = projectModel.indexOf(project);
		projectModel.set(index, project);		
		projectModel.clearSelection();
		projectModel.addSelection(project);
		updateBtns();
	}
	
	public void removeProject(Project project){
		projectModel.remove(project);		
		projectModel.clearSelection();
		updateBtns();
	}
	
	
	@SuppressWarnings("rawtypes")
	private void updateBtns(){
		if (batchBox.isChecked()) return;
		
		Set selections = projectModel.getSelection();
		if (selections.isEmpty()) {
			logger.debug("No project is selected.");
			restartBtn.setDisabled(true);
			updateBtn.setDisabled(true);
			copyBtn.setDisabled(true);
			enableBtn.setDisabled(true);
			disableBtn.setDisabled(true);
			cleanBtn.setDisabled(true);
			deleteBtn.setDisabled(true);
		} else {
			logger.debug("selection count: " + selections.size());
			Project selection = (Project)selections.iterator().next();
			logger.debug("project: " + selection.getName() + ", eabled: " + selection.getEnabled());
			if (selection.getEnabled()){
				restartBtn.setDisabled(false);
				updateBtn.setDisabled(false);
				copyBtn.setDisabled(false);
				enableBtn.setDisabled(true);
				disableBtn.setDisabled(false);
				cleanBtn.setDisabled(false);
				deleteBtn.setDisabled(false);
			} else {
				restartBtn.setDisabled(true);
				updateBtn.setDisabled(true);
				copyBtn.setDisabled(true);
				enableBtn.setDisabled(false);
				disableBtn.setDisabled(true);
				cleanBtn.setDisabled(true);
				deleteBtn.setDisabled(true);
			}
		}
	}
	
	private void renderProjects() throws Exception{
		User login = GeneralUtil.getLogin();
		boolean isAdmin = login.isAdmin();
		
		logger.debug("Retrieve projects...");				
		List<Project> projects = null;
		if (isAdmin) projects = projectDao.getAll();	// administrators can access all projects
		else projects = new ArrayList<Project>(login.getProjects());
		Collections.sort(projects, new ProjectNameComparator());
			
		projectModel = new ListModelList(projects);
		projectList.setModel(projectModel);
		projectList.setItemRenderer(new ProjectItemRenderer());
	}
	
	/**
	 * Check whether selected project(s) is eligible for update (and copy)
	 * Project created before the cut-off date (set in ciknowmgr.properties) 
	 * cannot be updated because the database schema changed too much. 
	 * They can be manually updated though.
	 * @return true if selected project(s) is eligible for update
	 * @throws ParseException 
	 */
	@SuppressWarnings("rawtypes")
	private boolean isUpdatable() throws ParseException{
		Set selections = projectModel.getSelection();
		if (selections.isEmpty()) return true;
		
		PropsUtil props = new PropsUtil("ciknowmgr");
		String cutoff = props.get("cutoff");
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Date cutoffDate = formatter.parse(cutoff);
		for (Object o : selections){
			Project selection = (Project)o;
			Iterator<ProjectLog> itr = selection.getLogs().iterator();
			while (itr.hasNext()){
				ProjectLog log = itr.next();
				if (log.getAction().equals(Constants.CREATE)){
					if (log.getTimeStamp().before(cutoffDate)) return false;
				}
			}
		}
		return true;
	}
}
