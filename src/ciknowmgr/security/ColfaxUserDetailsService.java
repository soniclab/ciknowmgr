package ciknowmgr.security;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ciknowmgr.dao.UserDao;
import ciknowmgr.domain.User;


public class ColfaxUserDetailsService implements UserDetailsService{
	private UserDao userDao;
	

	public UserDao getUserDao() {
		return userDao;
	}


	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}


	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		if (username == null || username.equals("")) throw new UsernameNotFoundException("User not found.");
		User user = userDao.findByUsername(username);
		if (user == null || !user.getEnabled()) throw new UsernameNotFoundException("User " + username + " not found or disabled.");
		
		ColfaxUserDetails userDetails = new ColfaxUserDetails(user);
		return userDetails;
	}

}
