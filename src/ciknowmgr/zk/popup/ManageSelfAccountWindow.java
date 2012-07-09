package ciknowmgr.zk.popup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ciknowmgr.dao.UserDao;
import ciknowmgr.domain.User;
import ciknowmgr.util.Beans;
import ciknowmgr.util.GeneralUtil;

public class ManageSelfAccountWindow extends Window {
	private static final long serialVersionUID = -3902950071713495757L;

	private static Log logger = LogFactory.getLog(ManageSelfAccountWindow.class);
	
	private Label usernameLabel;
	private Textbox oldPasswordBox;
	private Textbox newPasswordBox;
	private Textbox confirmPasswordBox;	
	private Textbox emailBox;
	
	private User login;
	
	public ManageSelfAccountWindow(Component parent) throws Exception{
		this.setParent(parent);
		this.setClosable(true);
		this.setSclass("manageSelfAccountWindow");
		this.doModal();
		
		// create ui from template
		Executions.createComponents("/WEB-INF/composite/ManageSelfAccountWindow.zul", this, null);
		
		Components.wireVariables(this, this, '$', true, true);
		Components.addForwards(this, this, '$');
		
		login = GeneralUtil.getLogin();
		usernameLabel.setValue(login.getUsername());
		emailBox.setValue(login.getEmail());
		oldPasswordBox.setValue("");
		newPasswordBox.setValue("");
		confirmPasswordBox.setValue("");
	}
	
	public void onClick$updateBtn() throws InterruptedException{
		logger.debug("on update");
		String oldPassword = oldPasswordBox.getValue().trim();
		String newPassword = newPasswordBox.getValue().trim();
		String confirmPassword = confirmPasswordBox.getValue().trim();
		String email = emailBox.getValue().trim();
		if (!oldPassword.equals(login.getPassword())) {
			Messagebox.show("Old Password Invalid");			
		} else if (!newPassword.equals(confirmPassword)){
			Messagebox.show("New Password Mismatched");
		} else {
			login.setPassword(confirmPassword);
			login.setEmail(email);
			UserDao userDao = (UserDao)Beans.getBean("userDao");
			userDao.save(login);
			Messagebox.show("Account information updated.");
			this.setParent(null);
		}		
	}
	
	public void onClick$cancelBtn(){
		logger.debug("on cancel");
		this.setParent(null);
	}
}
