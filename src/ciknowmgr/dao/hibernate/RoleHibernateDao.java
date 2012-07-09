package ciknowmgr.dao.hibernate;

import java.util.Collection;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import ciknowmgr.dao.RoleDao;
import ciknowmgr.domain.Role;

public class RoleHibernateDao extends HibernateDaoSupport implements RoleDao{

	public void delete(Role role) {
		getHibernateTemplate().delete(role);
	}

	public void delete(Collection<Role> roles) {
		getHibernateTemplate().deleteAll(roles);
	}

	public void deleteAll() {
		getHibernateTemplate().bulkUpdate("delete Role");
	}

	public Role findById(Long id) {
		return getHibernateTemplate().get(Role.class, id);
	}

	@SuppressWarnings("unchecked")
	public Role findByName(String name) {
		String query = "from Role r where r.name = :name";
		List<Role> roles = getHibernateTemplate().findByNamedParam(query, "name", name);
		if (roles.size() > 0) return roles.get(0);
		else return null;
	}

	public Role findProxyById(Long id) {
		return getHibernateTemplate().load(Role.class, id);
	}

	public List<Role> getAll() {
		return getHibernateTemplate().loadAll(Role.class);
	}

	public void save(Role role) {
		getHibernateTemplate().saveOrUpdate(role);
	}

	public void save(Collection<Role> roles) {
		getHibernateTemplate().saveOrUpdateAll(roles);
	}
	
}
