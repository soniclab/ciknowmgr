package ciknowmgr.dao;

import java.util.Collection;
import java.util.List;

import ciknowmgr.domain.Project;

public interface ProjectDao {
	public void save(Project project);
	public void save(Collection<Project> projects);
	public void delete(Project project);
	public void delete(Collection<Project> projects);
	
	public Project findById(Long id);
	//public Project findProxyById(Long id);
	public Project findByName(String name);
	public List<Project> getAll();
}
