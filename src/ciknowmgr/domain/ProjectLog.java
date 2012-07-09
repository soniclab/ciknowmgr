package ciknowmgr.domain;

import java.io.Serializable;
import java.util.Date;

public class ProjectLog implements Serializable{
	private static final long serialVersionUID = 1L;

	private String action;
	private String user;
	private String comment ="";
	private Date timeStamp;
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Date getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}	
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("ProjectLog[");
		sb.append("action=").append(action).append(",");
		sb.append("actor=").append(user).append(",");
		sb.append("comment=").append(comment).append(",");
		sb.append("timestamp=").append(timeStamp).append("]");
		return sb.toString();
	}
}
