package ciknowmgr.zk;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Vbox;

import ciknowmgr.domain.Project;
import ciknowmgr.util.GeneralUtil;
import ciknowmgr.zk.popup.ProjectDetailsWindow;

public class ProjectItemRenderer implements ListitemRenderer {
	private static final Log logger = LogFactory.getLog(ProjectItemRenderer.class);
	
	private DoubleClickEventListener doubleClickListener = new DoubleClickEventListener();
	private DetailsClickEventListener detailsClickListener = new DetailsClickEventListener();
	
	@Override
	public void render(Listitem item, Object data) throws Exception {
		Project project = (Project)data;
		item.setValue(project);
		
		Listcell cell = new Listcell(project.getName());
		cell.setParent(item);
		setStyle(project, cell);
		
		cell = new Listcell(project.getDescription());
		cell.setParent(item);
		setStyle(project, cell);
		
		cell = new Listcell(project.getCreator());
		cell.setParent(item);
		setStyle(project, cell);
		
		cell = new Listcell();		
		cell.setParent(item);
		Button detailsBtn = new Button("Details");
		if (!project.getEnabled()) detailsBtn.setDisabled(true);
		else detailsBtn.setDisabled(false);
		detailsBtn.addEventListener("onClick", detailsClickListener);
		detailsBtn.setParent(cell);
		setStyle(project, cell);
		
		item.addEventListener("onDoubleClick", doubleClickListener);
	}

	private void setStyle(Project project, Listcell cell) {
		if (project.getEnabled()) cell.setStyle("color:navy; font-weight: bold; font-style: normal");
		else cell.setStyle("color: #666666; font-weight:normal; font-style: italic");
	}
	
	private class DoubleClickEventListener implements EventListener{
		
		@Override
		public void onEvent(Event e) throws Exception {
			Listitem item = (Listitem) e.getTarget();
			Project project = (Project)item.getValue();
			if (!project.getEnabled()) {
				Messagebox.show("This project (" + project.getName() + ") is disabled.");
				return;
			}
			
			// construct URL and redirect user to double clicked project
			String projectUrl = GeneralUtil.getServerUrl() + "/_" + project.getName();
			Executions.getCurrent().sendRedirect(projectUrl);
			logger.info("Redirecting user to project: " + project.getName());
		}
	}

	private class DetailsClickEventListener implements EventListener{

		@Override
		public void onEvent(Event e) throws Exception {
			Listitem item = (Listitem) e.getTarget().getParent().getParent();
			Project project = (Project)item.getValue();
			if (!project.getEnabled()) {
				Messagebox.show("This project (" + project.getName() + ") is disabled.");
				return;
			}
			logger.info("Getting details for project: " + project.getName());
			
			Listbox lb = item.getListbox();
			Vbox centerBox = (Vbox)lb.getParent();
			ProjectDetailsWindow win = new ProjectDetailsWindow(centerBox, project);
			win.doModal();
		}
		
	}
}
