package ciknowmgr.zk.popup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ciknowmgr.dao.RoleDao;
import ciknowmgr.dao.UserDao;
import ciknowmgr.domain.Role;
import ciknowmgr.domain.User;
import ciknowmgr.util.Beans;
import ciknowmgr.util.GeneralUtil;

public class CreateUserWindow extends Window {
	private static final long serialVersionUID = -3902950071713495757L;

	private static Log logger = LogFactory.getLog(CreateUserWindow.class);
	
	private UserDao userDao;
	private RoleDao roleDao;
	
	private Textbox usernameBox;
	private Textbox passwordBox;
	private Textbox confirmPasswordBox;	
	private Textbox emailBox;
	
	public CreateUserWindow(Component parent) throws Exception{
		this.setParent(parent);
		this.setClosable(true);
		
		// create ui from template
		Executions.createComponents("/WEB-INF/composite/CreateUserWindow.zul", this, null);
		
		Components.wireVariables(this, this, '$', true, true);
		Components.addForwards(this, this, '$');
		
		userDao = (UserDao)Beans.getBean("userDao");
		roleDao = (RoleDao)Beans.getBean("roleDao");
	}
	
	public void onClick$saveBtn() throws InterruptedException{
		logger.debug("on update");
		
		// server side validation in addition to client side validation
		String username = usernameBox.getValue().trim();
		String password = passwordBox.getValue().trim();
		String confirmPassword = confirmPasswordBox.getValue().trim();
		String email = emailBox.getValue().trim();				
		if (!GeneralUtil.isValidName(username)) return;
		if (!password.equals(confirmPassword)) {
			Messagebox.show("Password Mismatched");
			return;
		}
		
		// create new user
		User user = new User();
		user.setUsername(username);
		user.setPassword(confirmPassword);
		user.setEmail(email);
		Role role = roleDao.findByName("ROLE_USER");
		user.getRoles().add(role);
		
		// save
		userDao.save(user);
		
		// update parent
		ManageAdminAccountWindow win = (ManageAdminAccountWindow)this.getParent();
		win.render(username);
		
		this.setParent(null);
	}
	
	public void onClick$cancelBtn(){
		logger.debug("on cancel");
		this.setParent(null);
	}
}
