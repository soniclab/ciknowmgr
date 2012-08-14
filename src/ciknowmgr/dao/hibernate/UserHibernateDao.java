package ciknowmgr.dao.hibernate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import ciknowmgr.dao.UserDao;
import ciknowmgr.domain.Project;
import ciknowmgr.domain.User;
import ciknowmgr.util.Beans;

public class UserHibernateDao extends HibernateDaoSupport implements UserDao{
	private static Log logger = LogFactory.getLog(UserHibernateDao.class);
	
	public static void main(String[] args){
		Beans.init();
		UserDao userDao = (UserDao)Beans.getBean("userDao");
		//testFindByUsername(userDao);
		testLoadAll(userDao);
		System.exit(0);
	}
	
	private static void testLoadAll(UserDao userDao){
		List<User> users = userDao.loadAll();
		for (User user : users){
			logger.info(user.getUsername() + ":" + user.getProjects().size());
		}
	}
	
	@SuppressWarnings("unused")
	private static void testFindByUsername(UserDao userDao){
		User user = userDao.findByUsername("admin");
	}
	
	@SuppressWarnings("unused")
	private static void testSave(UserDao userDao){
		
	}
	
	public void delete(User user) {
		getHibernateTemplate().delete(user);
	}

	public void delete(Collection<User> users) {
		getHibernateTemplate().deleteAll(users);
	}

	public void deleteAll() {
		getHibernateTemplate().bulkUpdate("delete User");
	}

	public User findById(Long id) {
		return getHibernateTemplate().get(User.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public User loadById(Long id) {
		String query = "from User u " +
								"left join fetch u.attributes " +
								"left join fetch u.roles " +
								"left join fetch u.projects " +
								"where u.id = :id";
		List<User> users = getHibernateTemplate().findByNamedParam(query, "id", id);
		if (users.size() > 0) return users.get(0);
		else return null;
	}

	@SuppressWarnings("unchecked")
	public User findByUsername(String username) {
		String query = "from User u where u.username = :username";
		List<User> users = getHibernateTemplate().findByNamedParam(query, "username", username);
		if (users.size() > 0) return users.get(0);
		else return null;
	}

	@SuppressWarnings("unchecked")
	public User loadByUsername(String username) {
		String query = "from User u " +
								"left join fetch u.attributes " +
								"left join fetch u.roles " +
								"left join fetch u.projects " +
								"where u.username = :username";
		List<User> users = getHibernateTemplate().findByNamedParam(query, "username", username);
		if (users.size() > 0) return users.get(0);
		else return null;
	}

	@SuppressWarnings("unchecked")
	public User findByEmail(String email) {
		String query = "from User u where u.email = :email";
		List<User> users = getHibernateTemplate().findByNamedParam(query, "email", email);
		if (users.size() > 0) return users.get(0);
		else return null;
	}
	
	public User findProxyById(Long id) {
		return getHibernateTemplate().load(User.class, id);
	}


	public List<User> getAll() {
		return getHibernateTemplate().loadAll(User.class);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<User> loadAll() {
		/*
		String query = "select distinct u from User u " +
				"left join fetch u.attributes " +
				"left join fetch u.roles " +
				"left join fetch u.projects";
		return (List<User>) getHibernateTemplate().find(query);
		*/
		List<User> users = getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria c = session.createCriteria(User.class);
				c.setFetchMode("attributes", FetchMode.JOIN);
				c.setFetchMode("roles", FetchMode.JOIN);
				c.setFetchMode("projects", FetchMode.JOIN);
				c.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);				
				List<User> users = c.list();											
				return users;
			}
			
		});
		
		return users;
	}
	
	public void save(User user) {
		getHibernateTemplate().saveOrUpdate(user);
	}

	public void save(Collection<User> users) {
		getHibernateTemplate().saveOrUpdateAll(users);
	}

	@Override
	public List<User> getUsersByProject(Project project) {
		List<User> allUsers = getAll();
		List<User> users = new ArrayList<User>();
		for (User user : allUsers){
			if (user.getProjects().contains(project)) users.add(user);
		}
		return users;
	}

}
