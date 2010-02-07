package mechanic;

import java.sql.ResultSet;
import java.sql.SQLException;

import util.DBUtility;
import util.Utility;

public class TableSchema {
	String tablename;
	String[] columns;
	String[] types;
	VersionVector version;
	
	public TableSchema(){
		columns = new String[0];
		types = new String[0];
	}
	
	public TableSchema(String tablename, String[] columns, String[] types, VersionVector version){
		if(columns.length != types.length)
			throw new IllegalArgumentException("column array and type array must be of same size");
		
		this.tablename = tablename;
		this.columns = columns;
		this.types = types;
		this.version = version;
	}
	
	public static TableSchema getByTablename(String tablename, DBConnection dbConn) throws SQLException{
		DBUtility dbUtil = new DBUtility(dbConn);
		ResultSet columnsRS = dbUtil.getColumns(tablename);
		
		while(columnsRS.next()){
			String column = columnsRS.getString("COLUMN_NAME");
			
		}
		
		return(null);
	}
	
	public static TableSchema getByString(String str){
		return(null);
	}
	
	//encodedTablename encodedVersion column:type ...
	public String toString(){
		return(Utility.encode(tablename) + " " + Utility.encode(version.toString()) + " " + Utility.jointSeparate(":", " ", columns, types));
	}
}
