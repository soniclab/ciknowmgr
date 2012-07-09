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
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.InputEvent;
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

public class CreateProjectWindow extends Window {
	private static final long serialVersionUID = -3902957071713495757L;

	private static Log logger = LogFactory.getLog(CreateProjectWindow.class);
	
	private Textbox descriptionBox;
	private Textbox nameBox;
	
	public CreateProjectWindow(Component parent){
		this.setParent(parent);
		
		// create ui from template
		Executions.createComponents("/WEB-INF/composite/CreateProjectWindow.zul", this, null);
		
		Components.wireVariables(this, this, '$', true, true);
		Components.addForwards(this, this, '$');
	}
	
	public void onChanging$nameBox(ForwardEvent event){
		InputEvent e = (InputEvent)event.getOrigin();
		String value = e.getValue();
		nameBox.setValue(value);
		descriptionBox.setValue(value);
		
		nameBox.setFocus(true);
		nameBox.setSelectionRange(value.length(), value.length());
	}
	
	public void onClick$createBtn() throws InterruptedException{
		logger.debug("on create");
		
		// validate input
		String name = nameBox.getValue().trim();
		String description = descriptionBox.getValue().trim();
		if (!GeneralUtil.isValidName(name)) return;
		if (!GeneralUtil.isValidLabel(description)) return;
		
		// create project
		Session session = Sessions.getCurrent();
		ManagerController controller = (ManagerController)session.getAttribute(Constants.SESSION_MANAGER_CONTROLLER);
		ManagerUtil managerUtil = (ManagerUtil)Beans.getBean("managerUtil");
		ProjectDao projectDao = (ProjectDao)Beans.getBean("projectDao");
		User login = GeneralUtil.getLogin();	
		try {		
			managerUtil.create(name, description, "", login);
			
			// update interface
			Project project = projectDao.findByName(name);
			controller.addProject(project);
			this.setParent(null);
		} catch(Exception e){
			logger.error(e.getMessage());
			e.printStackTrace();
			Messagebox.show(e.getMessage() + ". Click OK to refresh browser and try again.", "", Messagebox.OK, Messagebox.ERROR, new EventListener(){

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
