package mechanic;

import java.sql.*;

import util.DBUtility;

public class Clock {
	Database db;
	DBConnection dbConn;
	DBUtility dbUtil;
	
	Clock(Database db) throws SQLException{
		this.db = db;
		this.dbConn = db.getDBConnection();
		this.dbUtil = new DBUtility(dbConn);
		
		initDBClock();
	}
	
	private void initDBClock() throws SQLException{
		db.assertVariablesTable();
		
		if(dbUtil.getVariable("clock") == null)
			dbUtil.putVariable("clock", "0");
	}
	
	public int getValue() throws SQLException{
		return(Integer.valueOf(dbUtil.getVariable("clock")));
	}
	
	public void setValue(int clockValue) throws SQLException{
		dbUtil.putVariable("clock", Integer.toString(clockValue));
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
