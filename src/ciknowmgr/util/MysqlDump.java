package ciknowmgr.util;

import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MysqlDump {
	private Log logger = LogFactory.getLog(this.getClass());
	
	public static void main(String args[]) throws IOException {
		new MysqlDump("c:\\users\\gyao\\", "_ciknow");
	}
	
	public MysqlDump(String path, String dbname) throws IOException {
			Runtime rt = Runtime.getRuntime();
			String command = "mysqldump -usonic -psonic " + dbname + " -r " + path + dbname + ".sql";
			rt.exec(command);
			logger.info(command);
	}
}
