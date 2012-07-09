package ciknowmgr.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Project implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private Long version;
	private String name;
	private String description;
	private String url;
	private String creator;
	private Boolean enabled = true;
	private Set<ProjectLog> logs = new HashSet<ProjectLog>();
	
	public void update(Project p){
		this.id = p.id;
		this.version = p.version;
		this.name = p.name;
		this.description = p.description;
		this.url = p.url;
		this.creator = p.creator;
		this.enabled = p.enabled;
		this.logs = new HashSet<ProjectLog>(p.getLogs());
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}	
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}	
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	public Set<ProjectLog> getLogs() {
		return logs;
	}
	public void setLogs(Set<ProjectLog> logs) {
		this.logs = logs;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Project other = (Project) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Project[");
		sb.append("id=").append(id).append(",");
		sb.append("version=").append(version).append(",");
		sb.append("name=").append(name).append(",");
		sb.append("description=").append(description).append(",");
		sb.append("creator=").append(creator).append(",");
		sb.append("logCount=").append(logs.size()).append("]");
		return sb.toString();
	}
}
