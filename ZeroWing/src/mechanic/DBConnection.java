package mechanic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection{
	private Connection db_conn;
	private String UUIDFun;
	private String subprotocol;
	
	private DBConnection(String driver, String subprotocol, String host, int port, String database,
			String user, String password) throws SQLException{
		String url = "jdbc:" + subprotocol + "://" + host + ":" + port + "/" + database;
		
		if(subprotocol.equals("postgresql"))
			UUIDFun = "uuid_generate_v4()";
		else if(subprotocol.equals("mysql"))
			UUIDFun = "uuid()";
		else
			throw new IllegalArgumentException("Unsupported database " + subprotocol);
		
		this.subprotocol = subprotocol;

		try {  
			Class.forName(driver);  
		} catch(Exception E) {  
			System.out.println("Unable to load JDBC driver: " + driver);  
		}  

		try {
			db_conn = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Database error: unable to connect to database");
			throw(e);
		}
	}
	
	public static DBConnection getDBConnection(String subprotocol, String host, int port, String database,
			String user, String password) throws SQLException{
		String driver = null;
		
		if(subprotocol.equals("postgresql"))
			driver = "org.postgresql.Driver";
		else if(subprotocol.equals("mysql"))
			driver = "com.mysql.jdbc.Driver";
		else
			throw new IllegalArgumentException("Unsupported database " + subprotocol);

		return new DBConnection(driver, subprotocol, host, port, database, user, password);
	}
	
	public boolean execute(String query) throws SQLException{
		Statement st = db_conn.createStatement();
		return(st.execute(query));
	}
	
	public ResultSet executeQuery(String query) throws SQLException {
		Statement st = db_conn.createStatement();
		return (st.executeQuery(query));
	}
	
	public ResultSet executeQueryUpdatable(String query) throws SQLException{
		Statement st = db_conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
		return (st.executeQuery(query));
	}

	public int executeUpdate(String update) throws SQLException {
		Statement st = db_conn.createStatement();
		return (st.executeUpdate(update));
	}
	
	public Connection getConnection(){
		return(db_conn);
	}
	
	public String getSubprotocol() {
		return(subprotocol);
	}

	public String getUUIDFun(){
		return(UUIDFun);
	}
	
	public PreparedStatement prepareUpdatableStatement(String sql) throws SQLException{
		return(db_conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE));
	}
}
