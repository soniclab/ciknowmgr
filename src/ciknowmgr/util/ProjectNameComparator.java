package ciknowmgr.util;

import java.util.Comparator;

import ciknowmgr.domain.Project;

public class ProjectNameComparator implements Comparator<Project>{

	@Override
	public int compare(Project p1, Project p2) {
		return p1.getName().compareTo(p2.getName());
	}

}
