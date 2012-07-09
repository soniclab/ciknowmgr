package ciknowmgr.util;

import java.io.File;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class AntUtil {	
	public static void executeTarget(String buildFile, String target){
		Project ant = new Project();
		ant.init();
		ProjectHelper.configureProject(ant,new File(buildFile));
		
        DefaultLogger log = new DefaultLogger();
        log.setErrorPrintStream(System.err);
        log.setOutputPrintStream(System.out);
        log.setMessageOutputLevel(Project.MSG_INFO);
        ant.addBuildListener(log);	
		ant.executeTarget(target);
	}
}
