package test.netfree.modules;

import java.sql.SQLException;

import mechanic.DBConnection;
import mechanic.Database;

/**
 * Each instance of <code>Node</code> represents a device that uses the Zero Wing system. For testing purposes,
 * <code>Nodes</code> do not use <code>Sockets</code> to interact with one another. 
 * <p>
 * Each <code>Node</code> has a unique name. By prefixing this name with the managing <code>TestControl</code>
 * instance, individual <code>Node</code> names are guaranteed unique.
 * <p>
 * A <code>Node</code> requires a database which uses a predefined name <code>dbName</code>. 
 * 
 * @author kevinzana
 *
 */
public class Node {
	String peerName; //This Node's name (for reference)
	String ip;
	int dbPort;
	String dbType;
	String dbName;
	String dbUser;
	String dbPassword;
	
	Database db;
	// ============================= INSTANTIATION METHODS
	/**
	 * <code>getNode</code> creates a Node object and instantiates the database using the
	 * given database information.
	 */
	public static Node getNode(
			String peerName,
			String ip,
			int dbPort,
			String dbType,
			String dbName,
			String dbUser,
			String dbPassword) throws SQLException{
		
		Node newNode = new Node(peerName, ip, dbPort, dbType, 
								dbName, dbUser, dbPassword);
		// this one starts the DB on creation.
		newNode.startDB();
		return newNode;
	}
	/**
	 * <code>getCleanNode</code> creates a Node object. This does not start the database, and
	 * the user must manually start it by calling <code>node.startDB()</code>.
	 * 
	 * @param peerName
	 * @param ip
	 * @param dbPort
	 * @param dbType
	 * @param dbName
	 * @param dbUser
	 * @param dbPassword
	 * @return
	 */
	public static Node getCleanNode(
			String peerName,
			String ip,
			int dbPort,
			String dbType,
			String dbName,
			String dbUser,
			String dbPassword){
		
		Node newNode = new Node(peerName, ip, dbPort, dbType,
								dbName, dbUser, dbPassword);
		
		return newNode;
	}
	
	private Node(
			String peerName,
			String ip,
			int dbPort,
			String dbType,
			String dbName,
			String dbUser,
			String dbPassword
			){
		this.peerName = peerName;
		this.ip = ip;
		this.dbPort = dbPort;
		this.dbType = dbType;
		this.dbUser = dbUser;
		this.dbName = dbName;
		this.dbPassword = dbPassword;
		this.ip = ip;
	}
	public void startDB() throws SQLException{
		db = new Database(dbType, ip, dbPort, dbName, dbUser, dbPassword, peerName);
	}
	/**
	 * Returns a brand-spanking new <code>DBConnection</code> using this <code>Node</code>'s
	 * Database connection information. Why is this necessary? Because while instantiating
	 * this <code>Node</code>'s Database using <code>startDB()</code> may sound simple and useful,
	 * it will also trigger a lot of side effects, most of which may or may not be helpful. So, this.
	 * @return
	 * @throws SQLException
	 */
	public DBConnection getConnection() throws SQLException{
		return DBConnection.getDBConnection(dbType, ip, dbPort, dbName, dbUser, dbPassword);
	}
	public String getPeerName(){
		return peerName;
	}
	
	// ============================= OPERATION METHODS
	/**
	 * This <code>Node</code> synchronizes with <code>peerNode</code>.
	 * ERRATA: 
	 * @param peerNode
	 */
	public void syncWith(Node peerNode){
		
	}
	
	// ============================= OPERATION METHODS
	public String toString(){
		return "[Node]:"+peerName;		
	}
}
