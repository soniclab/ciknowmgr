package ciknowmgr.zk.popup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ciknowmgr.dao.ProjectDao;
import ciknowmgr.domain.Project;
import ciknowmgr.domain.User;
import ciknowmgr.util.Beans;
import ciknowmgr.util.Constants;
import ciknowmgr.util.GeneralUtil;
import ciknowmgr.util.ManagerUtil;
import ciknowmgr.zk.ManagerController;

public class CopyProjectWindow extends Window {
	private static final long serialVersionUID = -3902957071713495757L;

	private static Log logger = LogFactory.getLog(CopyProjectWindow.class);
	
	private Project oldProject;
	
	private Label nameLabel;
	private Textbox newNameBox;
	
	public CopyProjectWindow(Component parent, Project oldProject){
		this.setParent(parent);
		this.oldProject = oldProject;
		
		// create ui from template
		Executions.createComponents("/WEB-INF/composite/CopyProjectWindow.zul", this, null);
		
		Components.wireVariables(this, this, '$', true, true);
		Components.addForwards(this, this, '$');
		
		nameLabel.setValue(oldProject.getName());
		newNameBox.setValue("Copy_" + oldProject.getName());
	}
	
	public void onClick$copyBtn() throws InterruptedException{
		logger.debug("on copy");
		
		// validate input
		String newName = newNameBox.getValue().trim();
		if (!GeneralUtil.isValidName(newName)) return;
		if (newName.equalsIgnoreCase(oldProject.getName())){
			Messagebox.show("Copied project name: " + newName + " must be different from original project.");
			return;
		}

		// copy project
		Session session = Sessions.getCurrent();
		ManagerController controller = (ManagerController)session.getAttribute(Constants.SESSION_MANAGER_CONTROLLER);
		User login = GeneralUtil.getLogin();
		ManagerUtil managerUtil = (ManagerUtil)Beans.getBean("managerUtil");
		ProjectDao projectDao = (ProjectDao)Beans.getBean("projectDao");		
		try{
			managerUtil.copy(login, oldProject.getName(), newName);
			
			// update interface
			Project project = projectDao.findByName(newName);
			controller.addProject(project);
			this.setParent(null);
		} catch(Exception e){
			logger.error(e.getMessage());
			e.printStackTrace();
			Messagebox.show(e.getMessage() + ". Press OK to refresh browser and try again.", "", Messagebox.OK, Messagebox.ERROR, new EventListener(){

				@Override
				public void onEvent(Event evt) throws Exception {
					Executions.sendRedirect(null);
				}
				
			});
		}
	}
	
	public void onClick$cancelBtn(){
		logger.debug("on cancel");
		this.setParent(null);
	}
}
