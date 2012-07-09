package ciknowmgr.dao;

import java.util.Collection;
import java.util.List;

import ciknowmgr.domain.Role;

public interface RoleDao {
	public void save(Role role);
	public void save(Collection<Role> roles);
	public void delete(Role role);
	public void delete(Collection<Role> roles);
	public void deleteAll();
	
	public Role findById(Long id);
	public Role findProxyById(Long id);
	public Role findByName(String name);
	public List<Role> getAll();
}
