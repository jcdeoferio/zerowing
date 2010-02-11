package util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mechanic.DBConnection;

public class DBUtility {
	private DBConnection dbConn;
	
	public DBUtility(DBConnection dbConn){
		this.dbConn = dbConn;
	}
	
	public String nQuestionMarks(int n){
		String str = "";
		
		for(int i = 0; i < n; i++)
			str += "?, ";
		
		return(str.substring(0, str.length()-2)); //2 length of dangling ", "
	}
	
	public String getVariable(String var) throws SQLException{
		final PreparedStatement ps = dbConn.getConnection().prepareStatement("SELECT value FROM variables WHERE varname = ?");
		ps.setString(1, var);
		ResultSet result = ps.executeQuery();
		
		if(!result.next())
			return(null);
		
		return(result.getString("value"));
	}
	
	public void putVariable(String var, String val) throws SQLException{
		PreparedStatement putVarPS = null;
		
		if(!variableExists(var))
			putVarPS = dbConn.getConnection().prepareStatement("INSERT INTO variables (value, varname) VALUES (?, ?)");
		else
			putVarPS = dbConn.getConnection().prepareStatement("UPDATE variables SET value = ? WHERE varname = ?");
		
		putVarPS.setString(1, val);
		putVarPS.setString(2, var);
		putVarPS.executeUpdate();
	}
	
	public boolean variableExists(String var) throws SQLException{
		return(selectCount("FROM variables WHERE varname = '"+var+"'") > 0);
	}

	
	public String getStringValue(ResultSet rs) throws SQLException{
		if(rs.next())
			return(rs.getString(1));
		else
			return(null);
	}
	
	public String craftInsertStatement(String tablename, List<String> columns,
			List<String> values, List<Boolean> quote){
		
		if (columns.size() != values.size() || columns.size() != quote.size())
			throw (new IllegalArgumentException());

		String insertStmt = "INSERT INTO " + tablename + " ("+Utility.commaSeparate(columns)+") VALUES ("+Utility.commaSeparate(values, quote)+")";
		
		return (insertStmt);
	}

	public String craftUpdateStatement(String table, List<String> columns,
			List<String> values, List<Boolean> quote, String condition)
			throws IllegalArgumentException {
		
		if (columns.size() != values.size() || columns.size() != quote.size())
			throw (new IllegalArgumentException());
		
		String updateStmt = "UPDATE " + table + " SET ";

		while (columns.size() > 0) {
			String value;
			
			if(quote.remove(0)){
				value = "'" + values.remove(0) + "'";
			}
			else{
				value = values.remove(0);
			}
			
			updateStmt += columns.remove(0) + " = " + value;

			if (columns.size() != 0)
				updateStmt += ", ";
		}
		
		updateStmt += " WHERE " + condition;

		return (updateStmt);
	}
	
	public String dumpResultSet(ResultSet results) throws SQLException{
		ResultSetMetaData metadata = results.getMetaData();
		int n_columns = metadata.getColumnCount();

		String dump = "";
		for (int i = 1; i <= n_columns; i++) {
			dump += metadata.getColumnName(i) + "\t";
		}
		dump += "\n";

		while (results.next()) {
			for (int i = 1; i <= n_columns; i++) {
				dump += results.getString(i) + "\t";
			}
			dump += "\n";
		}

		return (dump);
	}
	
	public String dumpTable(String table) throws SQLException {
		ResultSet results = selectAll(table);

		String dump = dumpResultSet(results); 

		results.close();

		return(dump);
	}

	public ResultSet getColumns(String tablename) throws SQLException{
		return(dbConn.getConnection().getMetaData().getColumns(null, "public", tablename, null));
	}
	
	public ResultSet getTables() throws SQLException{
		return(dbConn.getConnection().getMetaData().getTables(null, "public", null, new String[]{"TABLE"}));
	}
	
	public ResultSet getViews() throws SQLException{
		return(dbConn.getConnection().getMetaData().getTables(null, null, null, new String[]{"VIEW"}));
	}
	
	public int getColumnType(String tablename, String column) throws SQLException{
		ResultSet columnRS = dbConn.getConnection().getMetaData().getColumns(null, "public", tablename, column);
		
		if(!columnRS.next())
			throw new IllegalArgumentException(column + " does not exist in "+tablename);
		
		return(columnRS.getInt("DATA_TYPE"));
	}
	
	public String getColumnTypeIS(String tablename, String column) throws SQLException{
		PreparedStatement columnPS = dbConn.getConnection().prepareStatement("SELECT column_type FROM information_schema.columns WHERE c.table_name = ? AND c.column_name = ?");
		columnPS.setString(1, tablename);
		columnPS.setString(2, column);
		
		ResultSet columnRS = columnPS.executeQuery();
		
		if(!columnRS.next())
			throw new IllegalArgumentException("Table "+tablename+" or column "+column+" does not exist");
		
		return(columnRS.getString("column_type"));
	}
	
	public boolean isBoolean(int type){
		return(type == Types.BOOLEAN);
	}
	
	public boolean isIntegral(int type){
		switch(type){
		case Types.INTEGER:
		case Types.SMALLINT:
			return(true);
		default:
			return(false);
		}
	}
	
	public boolean isFloatingPoint(int type){
		switch(type){
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.NUMERIC:
		case Types.REAL:
			return(true);
		default:
			return(false);
		}
	}
	
	public boolean isString(int type){
		switch(type){
		case Types.CHAR:
		case Types.DATE:
		case Types.SQLXML:
		case Types.TIME:
		case Types.TIMESTAMP:
		case Types.VARCHAR:
			return(true);
		default:
			return(false);
		}
	}

	public ResultSet selectAll(String table) throws SQLException {
		return (dbConn.executeQuery("select * from " + table));
	}
	
	public int selectCount(String sql) throws SQLException{
		ResultSet countRS = dbConn.executeQuery("SELECT count(*) " + sql);
		countRS.next();
		
		return(countRS.getInt(1));
	}
	
	public int getCount(ResultSet rs) throws SQLException{
		if(!rs.next())
			throw new IllegalArgumentException("Improper ResultSet");
		
		return(rs.getInt("count"));
	}
	
	public boolean tableExists(String tablename) throws SQLException{
		ResultSet result = dbConn.getConnection().getMetaData().getTables(null, null, tablename, null);
		
		return(result.next());
	}
	
	public boolean tableHasColumn(String tableName, String columnName) throws SQLException{
		ResultSet results = dbConn.getConnection().getMetaData().getColumns(null, "public", tableName, columnName);
		
		return(results.next());
	}
	
	public static List<Object[]> resultSetToList(ResultSet rs) throws SQLException{
		List<Object[]> list = new ArrayList<Object[]>(rs.getFetchSize());
		
		int nColumns = rs.getMetaData().getColumnCount();
		while(rs.next()){
			Object[] row = new Object[nColumns];
			
			for(int i = 1; i <= nColumns; i++)
				row[i-1] = rs.getObject(i);
			
			list.add(row);
		}
		
		return(list);
	}
	
	public Set<String> tablesByChangeUnit(String cuname) throws SQLException{
		PreparedStatement ps = dbConn.getConnection().prepareStatement("SELECT tablename FROM changeunits WHERE cuname = ?");
		ps.setString(1, cuname);
		
		ResultSet results = ps.executeQuery();
		
		Set<String> tables = new HashSet<String>();
		while(results.next())
			tables.add(results.getString("tablename"));
		
		return(tables);
	}
	
	public PreparedStatement prepareInsertStatement(String tablename, List<String> columns, String key) throws SQLException{
		//insertQuery = INSERT INTO tablename (column1, column2, ..., key) VALUES (?, ?, ..., ?)
		String insertQuery = "INSERT INTO "+tablename+" ("+Utility.commaSeparate(columns)+", "+key+") VALUES ("+nQuestionMarks(columns.size() + 1)+")";
		
		PreparedStatement insertPS = dbConn.getConnection().prepareStatement(insertQuery);
		
		return(insertPS);
	}
	
	public PreparedStatement prepareUpdateStatement(String tablename, List<String> columns, String key) throws SQLException{
		//updateQuery = UPDATE tablename SET column1 = ?, column2 = ?, ... WHERE key = ?
		String updateQuery = "UPDATE "+tablename+" SET "+Utility.separatorSeparate(columns, " = ?, ")+" = ? WHERE "+key+" = ?";
		
		PreparedStatement updatePS = dbConn.getConnection().prepareStatement(updateQuery);
		
		return(updatePS);
	}
}
