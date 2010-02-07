package mechanic;

import java.sql.*;

public class DBCounter {
	Connection db_conn;
	String varname;

	DBCounter(Connection db_conn, String varname) throws SQLException{
		this.db_conn = db_conn;
		this.varname = varname;
		
		initDBCounter();
	}
	
	private void initDBCounter() throws SQLException{
		DatabaseMetaData dmd = db_conn.getMetaData();
		
		ResultSet result = dmd.getTables(null, null, "variables", null);
		
		if(!result.next()){
			//create variables table
			db_conn.createStatement().execute("create table variables (varname character varying(20), value text)");
		}
		
		result = db_conn.createStatement().executeQuery("select value from variables where varname = '" + varname + "'");
		if(!result.next()){
			//insert clock entry
			db_conn.createStatement().executeUpdate("insert into variables (varname, value) values ('" + varname + "', 0)");
		}
	}

	public int getValue() throws SQLException{
		Statement st = db_conn.createStatement();
		ResultSet result = st.executeQuery("select value from variables where varname = '" + varname + "'");
		result.next();
		int clockValue = result.getInt(1);
		result.close();
		
		return(clockValue);
	}
	
	public void setValue(int clockValue) throws SQLException{
		Statement st = db_conn.createStatement();
		st.executeUpdate("update variables set value = " + clockValue + " where varname = '" + varname + "'");
		st.close();
	}
	
	public void incValue() throws SQLException{
		setValue(getValue() + 1);
	}
	
	public void reset() throws SQLException{
		setValue(0);
	}	

}
