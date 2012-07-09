package ciknowmgr.zk.popup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkforge.ckez.CKeditor;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import ciknowmgr.util.Constants;
import ciknowmgr.util.GeneralUtil;
import ciknowmgr.zk.ManagerController;

public class EditInstructionWindow extends Window {
	private static final long serialVersionUID = -3902957071713495754L;

	private static Log logger = LogFactory.getLog(EditInstructionWindow.class);
	
	private CKeditor editorBox;
	
	public EditInstructionWindow(Component parent){
		this.setParent(parent);
		this.setClosable(true);
        this.setSclass("editInstructionWindow");
                
		// create ui from template
		Executions.createComponents("/WEB-INF/composite/EditInstructionWindow.zul", this, null);
		
		// wiring
		Components.wireVariables(this, this, '$', true, true);
		Components.addForwards(this, this, '$');
		
		// get current instruction
		editorBox.setValue(GeneralUtil.getInstruction());
	}
	
	public void onClick$saveBtn() throws InterruptedException{
		logger.debug("on save");

		try {
			String content = editorBox.getValue().trim();
			GeneralUtil.saveInstruction(content);
	
			// update UI
			Session session = Sessions.getCurrent();		
			ManagerController controller = (ManagerController)session.getAttribute(Constants.SESSION_MANAGER_CONTROLLER);
			controller.setInstruction(content);
			this.setParent(null);	
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
	
	public void onClick$cancelBtn(){
		logger.debug("on cancel");
		this.setParent(null);
	}
	
}
