package ciknowmgr.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpUtil {
	private static Log logger = LogFactory.getLog(HttpUtil.class);
	
	public static void main(String[] args) throws HttpException, IOException{
		getListInfo("localhost", 8080, "admin", "northwestern", "list", null);
		
		/* My implementation is equivalent to catalina Ant tasks
		 * except that Ant tasks print result to log/console while 
		 * my code allow me to process ManagerServlet result directly
		 * Both depend on ManagerServlet
		ListTask lt = new ListTask();
		lt.setUrl("http://localhost:8080/manager");
		lt.setUsername("admin");
		lt.setPassword("northwestern");
		lt.execute();
		*/
	}
	
	public static Map<String, Map<String, String>> getListInfo(String host, Integer port, String username, String password, String operation, String path) throws HttpException, IOException{
		logger.info("getting tomcat contexts list info...");
		Map<String, Map<String, String>> contextMap = new HashMap<String, Map<String, String>>();
		if (host == null) host = "localhost";
		if (port == null) port = 8080;
		HttpClient client = new HttpClient();
	    
	    // pass our credentials to HttpClient, they will only be used for
	    // authenticating to servers with realm "realm" on the host
	    // "www.verisign.com", to authenticate against an arbitrary realm 
	    // or host change the appropriate argument to null.
	    client.getState().setCredentials(
	            new AuthScope(host, port),
	            new UsernamePasswordCredentials(username, password)
	            );
	    
	    // create a GET method that reads a file over HTTPS, 
	    // we're assuming that this file requires basic 
	    // authentication using the realm above.
	    String url = "http://" + host + ":" + port + "/manager/" + operation;
	    if (path != null) url += ("?path=" + path);
	    logger.info("url: " + url);
	    GetMethod get = new GetMethod(url);
	    
	    // Tell the GET method to automatically handle authentication. The
	    // method will use any appropriate credentials to handle basic
	    // authentication requests.  Setting this value to false will cause
	    // any request for authentication to return with a status of 401.
	    // It will then be up to the client to handle the authentication.
	    get.setDoAuthentication( true );
	       
	    try {
	        // execute the GET
	        client.executeMethod( get );	
	        
	        // process response
	        String r = get.getResponseBodyAsString();
	        String[] lines = r.split("\n");
	        for (String line : lines){
	        	if (line.contains(":")){
	        		String[] parts = line.trim().split(":");
	        		Map<String, String> m = new HashMap<String, String>();
	        		m.put("status", parts[1]);
	        		m.put("sessions", parts[2]);
	        		contextMap.put(parts[3], m);
	        	}
	        }
	    } finally {
	        // release any connection resources used by the method
	        get.releaseConnection();
	    }
	    
	    logger.debug("context info: \n" + contextMap);
	    
	    return contextMap;
	}
}
