package ciknowmgr.util;

import org.zkoss.zk.ui.Sessions;

import ciknowmgr.zk.ManagerController;

public class SessionUtil {
	
	public static ManagerController getController(){
		return (ManagerController)Sessions.getCurrent().getAttribute(Constants.SESSION_MANAGER_CONTROLLER);
	}
	
	public static void setController(ManagerController controller){
		Sessions.getCurrent().setAttribute(Constants.SESSION_MANAGER_CONTROLLER, controller);
	}
	
	public static Integer getDesktopWidth(){
		return (Integer)Sessions.getCurrent().getAttribute(Constants.SESSION_DESKTOP_WIDTH);
	}
	
	public static void setDesktopWidth(Integer width){
		Sessions.getCurrent().setAttribute(Constants.SESSION_DESKTOP_WIDTH, width);
	}
	
	public static Integer getDesktopHeight(){
		return (Integer)Sessions.getCurrent().getAttribute(Constants.SESSION_DESKTOP_HEIGHT);
	}
	
	public static void setDesktopHeight(Integer height){
		Sessions.getCurrent().setAttribute(Constants.SESSION_DESKTOP_HEIGHT, height);
	}
}
