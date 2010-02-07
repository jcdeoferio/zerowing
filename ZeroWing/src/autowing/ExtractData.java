package autowing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import settings.SystemSettings;
import util.DBUtility;

import mechanic.DBConnection;
import mechanic.Database;

public class ExtractData {
	Database db;
	DBConnection dbConn;
	DBUtility dbUtil;
	
	public static void main(String args[]) throws SQLException {
		ExtractData extractor = new 
		ExtractData("mysql", 
				"127.0.0.1", 3306, //change port 
				"zerowikidba",   // <-- change db name
				"root", 
				"password", 
				"JC");   // <--- change peer name
		
		extractor.extractAndSave(5);
	}
	
	public void extractAndSave(int interval) throws SQLException{
		LinkedList<String> extracted = extractData();
		storeData(db.getPeerName()+"DBSource.txt",extracted, interval );
	}
	
	public ExtractData(String subprotocol, String host, int port,
			String database, String user, String password, String peerName) throws SQLException {
		db = new Database(subprotocol, host, port, database, user, password, peerName);
		dbConn = db.getDBConnection();
		dbUtil = new DBUtility(dbConn);
	}
	
	public void storeData(String fileName, LinkedList<String> arr, int interval){
		int max = arr.size();
		for(int i=0;i<max;i++){
			String s = arr.removeFirst();
			s = interval + " "+ s;
			arr.addLast(s);
		}
		SystemSettings.insertSystemSettings(arr, fileName);
	}
	public void storeData(String fileName, LinkedList<String> arr, LinkedList<Integer> intArr){
		LinkedList<String> toOut = new LinkedList<String>();
		if(arr.size()!=intArr.size()){
			System.out.println("[ExtractData.storeData]: Command size is not equal to duration size.");
			return;
		}
		while(!arr.isEmpty()){
			toOut.addLast(intArr.removeFirst()+" " +arr.removeFirst());
		}
		
		SystemSettings.insertSystemSettings(toOut, fileName);
	}
	
	public LinkedList<String> extractData() throws SQLException{
		LinkedList<String> data = new LinkedList<String>();
		
		ResultSet tableRS = dbUtil.getTables();
		while(tableRS.next()){
			String tablename = tableRS.getString("TABLE_NAME");
			
			if(db.isSystemTable(tablename))
				continue;
			
			ResultSet entryRS = dbUtil.selectAll(tablename);
			while(entryRS.next()){
				LinkedList<String> columns = new LinkedList<String>();
				LinkedList<String> values = new LinkedList<String>();
				LinkedList<Boolean> quote = new LinkedList<Boolean>();
				
				ResultSet columnRS = dbUtil.getColumns(tablename);
				while(columnRS.next()){
					String column = columnRS.getString("COLUMN_NAME");
					String value = entryRS.getString(column);
					int type = columnRS.getInt("DATA_TYPE");
					
					columns.add(column);
					values.add(value);
					quote.add(dbUtil.isString(type));
				}
				
				data.add(dbUtil.craftInsertStatement(tablename, columns, values, quote));
			}
		}
		
		return(data);
	}

	
}
