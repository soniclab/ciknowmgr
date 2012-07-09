package ciknowmgr.zk.popup;

import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ciknowmgr.dao.ProjectDao;
import ciknowmgr.domain.Project;
import ciknowmgr.domain.ProjectLog;
import ciknowmgr.util.Beans;
import ciknowmgr.util.Constants;
import ciknowmgr.util.GeneralUtil;
import ciknowmgr.zk.ManagerController;

public class ProjectDetailsWindow extends Window {
	private static final long serialVersionUID = -3902957071713495757L;

	private static Log logger = LogFactory.getLog(ProjectDetailsWindow.class);
	
	private Project project;
	
	private Label nameLabel;
	private Label creatorLabel;
	
	private Row descriptionRow;
	private Label descriptionLabel;
	private Hlayout hbox;
	private Textbox descriptionBox;
	private Button updateBtn;
	
	private Rows logRows;
	
	public ProjectDetailsWindow(Component parent, Project project){
		this.setParent(parent);
		this.project = project;
		
		// create ui from template
		Executions.createComponents("/WEB-INF/composite/ProjectDetailsWindow.zul", this, null);
		this.setClosable(true);
		
		Components.wireVariables(this, this, '$', true, true);
		Components.addForwards(this, this, '$');
		
		nameLabel.setValue(project.getName());
		creatorLabel.setValue(project.getCreator());
		descriptionLabel.setValue(project.getDescription());
		descriptionLabel.addEventListener("onDoubleClick", new EventListener(){

			@Override
			public void onEvent(Event event) throws Exception {
				descriptionLabel.setParent(null);
				if (hbox == null){
					hbox = new Hlayout();					
					
					descriptionBox = new Textbox();	
					descriptionBox.setParent(hbox);
					descriptionBox.setConstraint("no empty");
					
					updateBtn = new Button("Update");
					updateBtn.setParent(hbox);
					updateBtn.addEventListener("onClick", new UpdateListener());
				}
				hbox.setParent(descriptionRow);
				descriptionBox.setValue(descriptionLabel.getValue());
			}
			
		});
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		for (ProjectLog log : project.getLogs()){
			Row row = new Row();
			row.setParent(logRows);
			
			new Label(log.getAction()).setParent(row);
			new Label(log.getUser()).setParent(row);
			new Label(df.format(log.getTimeStamp())).setParent(row);
		}
	}
	
	private class UpdateListener implements EventListener{

		@Override
		public void onEvent(Event event) throws Exception {
			// update project
			ProjectDao projectDao = (ProjectDao)Beans.getBean("projectDao");	
			String description = descriptionBox.getValue().trim();
			if (!GeneralUtil.isValidLabel(description)) return;
			project.setDescription(description);			
			projectDao.save(project);
			logger.debug("Project description updated.");
			
			// update interface
			Session session = Sessions.getCurrent();
			ManagerController controller = (ManagerController)session.getAttribute(Constants.SESSION_MANAGER_CONTROLLER);
			hbox.setParent(null);
			descriptionLabel.setValue(project.getDescription());
			descriptionLabel.setParent(descriptionRow);
			controller.updateProject(project);
		}
		
	}

}
