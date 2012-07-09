package ciknowmgr.dao.hibernate;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import ciknowmgr.dao.ProjectDao;
import ciknowmgr.domain.Project;
import ciknowmgr.util.Beans;

public class ProjectHibernateDao extends HibernateDaoSupport implements ProjectDao{
	private static Log log = LogFactory.getLog(ProjectHibernateDao.class);
	
	public static void main(String[] args){
		Beans.init();
		ProjectDao dao = (ProjectDao)Beans.getBean("projectDao");
		Project project = dao.findById(1L);
		log.info(project);
	}
	
	public void delete(Project project) {
		getHibernateTemplate().delete(project);
	}

	public void delete(Collection<Project> projects) {
		getHibernateTemplate().deleteAll(projects);
	}

	public Project findById(Long id) {
		return (Project)getHibernateTemplate().get(Project.class, id);
	}
	
	/*
	public Project findProxyById(Long id) {
		return (Project)getHibernateTemplate().load(Project.class, id);
	}
	*/

	@SuppressWarnings("unchecked")
	public Project findByName(String name) {
		String query = "from Project p where p.name = :name";
		List<Project> projects = getHibernateTemplate().findByNamedParam(query, "name", name); 
		if (projects == null || projects.isEmpty()) return null;
		else return projects.get(0);
	}

	public List<Project> getAll() {
		logger.info("get all projects...");
		return getHibernateTemplate().loadAll(Project.class);
	}

	public void save(Project project) {
		getHibernateTemplate().saveOrUpdate(project);
	}

	public void save(Collection<Project> projects) {
		getHibernateTemplate().saveOrUpdateAll(projects);
	}

}
