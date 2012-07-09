package ciknowmgr.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JdbcUtil {
	
	private static final Log logger = LogFactory.getLog(JdbcUtil.class);
	
	public static final String CIKNOW_ADMIN = "CIKNOW AMDIN";
	public static final String PROJECT_CREATOR = "PROJECT CREATOR";
	public static final String PROJECT_OWNER = "PROJECT OWNER";
	
	public static void main(String[] args) throws Exception{
		Connection con = getConnection("jdbc:mysql://localhost:3306/_ciknow", "sonic", "sonic");
		if (con == null) return;
		insert(con, "username", CIKNOW_ADMIN);
		delete(con, "username");
	}
	
	public static Connection getConnection(String url, String username, String password){
		Connection con = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, username, password);
			logger.info("Get jdbc connection to " + url);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		
		return con;
	}
	
	public static Long getMaxNodeId(Connection con) throws SQLException{
		Long max = 0L;

		String sql = "SELECT MAX(node_id) FROM nodes";
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery(sql);
		rs.next();
		max = rs.getLong(1);
		
		return max;
	}
	
	public static Long getNodeId(Connection con, String username) throws SQLException{
		logger.info("Get Node ID by username: " + username);
		
		String sql = "SELECT node_id FROM nodes WHERE username=?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, username);
		ResultSet rs = ps.executeQuery();
		Long nodeId = null;
		if (rs.next()) nodeId = rs.getLong(1);
		return nodeId;
	}
	
	public static void insert(Connection con, String username, String label) throws SQLException{
		logger.info("Inserting " + label + ": " + username);		
		
		// check whether given username already exists
		Long nodeId = getNodeId(con, username);
		if (nodeId != null) {
			logger.debug("This username is already exist");
			return;
		}
		
		nodeId = getMaxNodeId(con) + 1;
		logger.info("New Node Id: " + nodeId);
		String sql = "INSERT INTO `nodes` VALUES (?,1,?,'user','',?,'doesnotmatter','-','-','','','','','','','','','','','','','','',true)";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setLong(1, nodeId);
		ps.setString(2, label);
		ps.setString(3, username);
		ps.executeUpdate();
		
		sql = "INSERT INTO `node_role` VALUES (?,1)";
		ps = con.prepareStatement(sql);
		ps.setLong(1, nodeId);
		ps.executeUpdate();
		
		logger.info("Inserted: " + username);
	}
	
	public static void delete(Connection con, String username) throws Exception{
		logger.info("Deleting " + username);
		
		// get Node Id
		Long nodeId = getNodeId(con, username);
		if (nodeId == null) {
			logger.debug("This username isn't exist");
			return;
		}
		
		// delete associations
		String sql = "DELETE FROM node_role WHERE node_id=?";
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setLong(1, nodeId);
		ps.executeUpdate();
		
		// delete node
		sql = "DELETE FROM nodes WHERE node_id=?";
		ps = con.prepareStatement(sql);
		ps.setLong(1, nodeId);
		ps.executeUpdate();
		
		logger.info("Deleted: " + username);
	}
}
