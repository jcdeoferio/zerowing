package mechanic;

import java.sql.*;

public class Clock {
	Connection db_conn;
	Database db;
	
	Clock(Database db) throws SQLException{
		this.db = db;
		
		initDBClock();
	}
	
	private void initDBClock() throws SQLException{
		db.assertVariablesTable();
		
		if(db.getVariable("clock") == null)
			db.putVariable("clock", "0");
	}
	
	public int getValue() throws SQLException{
		return(Integer.valueOf(db.getVariable("clock")));
	}
	
	public void setValue(int clockValue) throws SQLException{
		db.putVariable("clock", Integer.toString(clockValue));
	}
	
	public void incValue() throws SQLException{
		setValue(getValue() + 1);
	}
	
	public void advanceValue(int clockValue) throws SQLException{
		setValue(Math.max(getValue(), clockValue));
	}
	
	public void reset() throws SQLException{
		setValue(0);
	}	
}
