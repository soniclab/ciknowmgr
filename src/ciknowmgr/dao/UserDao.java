package ciknowmgr.dao;

import java.util.Collection;
import java.util.List;

import ciknowmgr.domain.Project;
import ciknowmgr.domain.User;

public interface UserDao {
	public void save(User user);
	public void save(Collection<User> users);
	public void delete(User user);
	public void delete(Collection<User> users);
	public void deleteAll();
	
	public User findById(Long id);
	public User loadById(Long id);
	public User findProxyById(Long id);
	public User findByUsername(String username);
	public User loadByUsername(String username);
	public User findByEmail(String email);
	public List<User> getUsersByProject(Project project);
	public List<User> getAll();
	public List<User> loadAll();
}
