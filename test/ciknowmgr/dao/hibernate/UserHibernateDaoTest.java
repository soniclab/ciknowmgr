package ciknowmgr.dao.hibernate;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ciknowmgr.dao.ProjectDao;
import ciknowmgr.dao.UserDao;
import ciknowmgr.domain.Project;
import ciknowmgr.domain.User;

public class UserHibernateDaoTest extends AbstractHibernateDaoTest{
	@Autowired UserDao userDao;
	@Autowired ProjectDao projectDao;
	
	@Test
	public void getUsersByProject(){
		Project project = projectDao.findById(173L);
		User user = userDao.findById(130L);
		List<User> users = userDao.getUsersByProject(project);
		Assert.assertTrue(users.contains(user));
		Assert.assertEquals(1, users.size());
	}
}
