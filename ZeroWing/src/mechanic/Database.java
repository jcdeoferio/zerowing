package mechanic;

import java.io.PrintStream;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import util.DBUtility;
import util.Pair;
import util.ParenScanner;
import util.Utility;

public class Database {
	boolean debug = false;
	private enum TriggerOperation{
		INSERT, UPDATE, DELETE
	}
	
	static final String zwTempEntityIDTable = "zwtrigproc_entityid";
	static final String zwTempCUEntityIDTable = "zwtrigproc_cuentityid";
	public static final String zwNULLEntry = "ZWSUPERNULL";
	
	private DBConnection dbConn;
	private DBUtility dbUtil;
	private Clock clock;
	private String peerName;
	private Filter filter;
	private String syncPartner;
	public boolean newSystemTables;
	
	private static Set<String> zwSystemTables = new HashSet<String>();
	
	public Database(String subprotocol, String host, int port, String database,
			String user, String password, String peerName) throws SQLException {
		dbConn = DBConnection.getDBConnection(subprotocol, host, port, database, user, password);
		dbUtil = new DBUtility(dbConn);
		
		this.peerName = peerName;
		clock = new Clock(this);
		
		filter = new Filter(dbConn);
		
		syncPartner = null;
		
		//Unhandled SQL Exception, causes program termination.
		newSystemTables = initMetadata();
	}
	
	/**
	 * This method adds a set column, table into a particular change unit.
	 * Throws an IllegalArgumentException in case that column is already in a change unit.
	 * 
	 * @param attribute
	 * @param tablename
	 * @param cuname
	 * @throws IllegalArgumentException
	 * @throws SQLException 
	 */
	public void addToChangeUnit(String attribute, String tablename, String cuname) throws IllegalArgumentException, SQLException{
		PreparedStatement columnUsedPS = dbConn.getConnection().prepareStatement("SELECT cuname FROM changeunits WHERE tablename = ? AND attribute = ?");
		columnUsedPS.setString(1, tablename);
		columnUsedPS.setString(2, attribute);
		
		ResultSet columnUsedRS = columnUsedPS.executeQuery();
		if(columnUsedRS.next())
			throw new IllegalArgumentException("Column "+attribute+" in table "+tablename+" already used in a change unit '"+columnUsedRS.getString("cuname")+"'");
		
		PreparedStatement cuExistsPS = dbConn.getConnection().prepareStatement("SELECT count(*) AS count FROM changeunits WHERE cuname = ?");
		cuExistsPS.setString(1, cuname);
		
		if(dbUtil.getCount(cuExistsPS.executeQuery()) == 0){
			
		}

	}

	boolean assertChangeLogTable() throws SQLException{
		return tableAsserter("changelog", "(cuname varchar(160), cuentityid varchar(160), changedon timestamp)");
	}
	
	boolean assertChangeUnitEntitiesTable() throws SQLException {
		return tableAsserter("changeunitentities", "(entityid varchar(160), tablename varchar(160), attribute varchar(160), cuentityid varchar(160), cuname varchar(160))");
	}
	
	boolean assertChangeUnitJoinsTable() throws SQLException{
		return tableAsserter("changeunitjoins", "(cuname varchar(160), tablename varchar(160), joinstr varchar(160))");
	}
	
	boolean assertChangeUnitsTable() throws SQLException {
		return tableAsserter("changeunits", "(cuname varchar(160), tablename varchar(160), attribute varchar(160))");
	}
	
	boolean assertCUDefVersionsTable() throws SQLException {
		return tableAsserter("cudef_versions", "(cuname varchar(160), peername varchar(160), counter integer)");
	}

	public static final String zwDataVersionsTable = "data_versions";
	boolean assertDataVersionsTable() throws SQLException{
		return tableAsserter(zwDataVersionsTable, "(cuentityid varchar(160), peername varchar(160), counter integer)");
	}
	
	//TODO: FINISH
	public static final String zwDeleteLogTable = "zwdeletelog";
	boolean assertDeleteLogTable() throws SQLException{
		return tableAsserter(zwDeleteLogTable, "()");
	}
	
	private void assertEntityIDColumns() throws SQLException {
		ResultSet tableRS = dbUtil.getTables();
		
		while(tableRS.next()){
			String tablename = tableRS.getString("TABLE_NAME");
			
			if(isSystemTable(tablename))
				continue;
			
			if(!dbUtil.tableHasColumn(tablename, "entityid"))
				dbConn.executeUpdate("ALTER TABLE "+tablename+" ADD COLUMN entityid character(36)");
		}
	}
	
	boolean assertMappingsTable() throws SQLException{
		return tableAsserter("mappings", "(id character(36), mappedid character(36), mappedpeer character(160))");
	}
	
	boolean assertSchemaVersionsTable() throws SQLException{
		return tableAsserter("schema_versions", "(tablename varchar(160), peername varchar(160), counter integer)");
	}
	
	boolean assertTempTables() throws SQLException{
		Boolean ret = tableAsserter(zwTempEntityIDTable, "(entityid character(36))");
		ret &= tableAsserter(zwTempCUEntityIDTable, "(cuentityid character(36))");
		return ret;
	}
	
	boolean assertVariablesTable() throws SQLException{
		return tableAsserter("variables", "(varname varchar(20), value varchar(160))");
	}
	
	boolean assertVersionVectorTable() throws SQLException{
		return tableAsserter("versionvector", "(peername varchar(160), maxcounter integer)");
	}
	
	final static String zwForeignKeyTable = "zwforeignkeys";	
	boolean assertForeignKeyTable() throws SQLException{
		return tableAsserter(zwForeignKeyTable, "(tablename varchar(160), colname varchar(160), foreigntable varchar(160), foreignkey varchar(160))");
	}
	
	final static String zwGraveyardTable = "zwgraveyard";
	boolean assertGraveyardTable() throws SQLException{
		return tableAsserter(zwGraveyardTable, "(entityid varchar(36), peername varchar(160), counter integer)");
	}
	
	void attachInsertTrigger(String tablename) throws SQLException{
		if(!dbUtil.tableExists(tablename))
			throw new IllegalArgumentException("Table "+tablename+" does not exist!");
		
		final String subprotocol = dbConn.getSubprotocol();
		
		String trigBody = "";
	
		trigBody += "DELETE FROM "+zwTempEntityIDTable+";";
		
		trigBody += "INSERT INTO "+zwTempEntityIDTable+" (entityid) VALUES ("+dbConn.getUUIDFun()+");";
		
		if(subprotocol.equals("postgresql")){ //TODO: not tested probably will not work
			trigBody += "UPDATE "+tablename+" SET entityid = (SELECT entityid FROM "+zwTempEntityIDTable+" LIMIT 1) WHERE entityid IS NULL;";
		}
		else if(subprotocol.equals("mysql")){
			trigBody += "SET NEW.entityid = (SELECT entityid FROM "+zwTempEntityIDTable+" LIMIT 1);";
		}
		
		trigBody += "DELETE FROM "+zwTempCUEntityIDTable+";";
		
		PreparedStatement columnsPS = dbConn.getConnection().prepareStatement("SELECT cuname, attribute FROM changeunits WHERE tablename = ? ORDER BY cuname, attribute");
		columnsPS.setString(1, tablename);
		
		ResultSet columnsRS = columnsPS.executeQuery();
		
		boolean hasCU = false;
		String prevcuname = "";
		while(columnsRS.next()){
			hasCU = true;
			
			String cuname = columnsRS.getString("cuname");
			String attribute = columnsRS.getString("attribute");
			
			if(!cuname.equals(prevcuname)){
				trigBody += "DELETE FROM "+zwTempCUEntityIDTable+";";
				
				trigBody += "INSERT INTO "+zwTempCUEntityIDTable+" (cuentityid) VALUES ("+dbConn.getUUIDFun()+");";
				trigBody += "INSERT INTO changelog (cuentityid, cuname) VALUES ((SELECT cuentityid FROM "+zwTempCUEntityIDTable+" LIMIT 1), '"+cuname+"');";
			}
			
			trigBody += "INSERT INTO changeunitentities (entityid, tablename, attribute, cuentityid, cuname) VALUES ((SELECT entityid FROM "+zwTempEntityIDTable+" LIMIT 1), '"+tablename+"', '"+attribute+"', (SELECT cuentityid FROM "+zwTempCUEntityIDTable+" LIMIT 1), '"+cuname+"');";
			
			prevcuname = cuname;
		}
		if(!hasCU)
			return;
		
		trigBody += "DELETE FROM "+zwTempEntityIDTable+";";
		trigBody += "DELETE FROM "+zwTempCUEntityIDTable+";";
		
		if(subprotocol.equals("postgresql"))
			trigBody += "RETURN NEW;";
		
		detachTrigger(TriggerOperation.INSERT, tablename);
		
		dbConn.execute(generateTriggerCreationSQL(TriggerOperation.INSERT, tablename, trigBody));		
	}
	
	void attachTriggers(String tablename) throws SQLException{
		attachInsertTrigger(tablename);
		attachUpdateTrigger(tablename);
	}
		
	void attachUpdateTrigger(String tablename) throws SQLException{
		if(!dbUtil.tableExists(tablename))
			throw new IllegalArgumentException("Table "+tablename+" does not exist!");
		
		String trigBody = "";
		
		PreparedStatement columnsPS = dbConn.getConnection().prepareStatement("SELECT cuname, attribute FROM changeunits WHERE tablename = ? ORDER BY cuname, attribute");
		columnsPS.setString(1, tablename);
		
		ResultSet columnsRS = columnsPS.executeQuery();
		
		boolean hasCU = false;
		String prevcuname = "";
		String prevattr = "";
		while(columnsRS.next()){
			hasCU = true;
			
			String cuname = columnsRS.getString("cuname");
			String attribute = columnsRS.getString("attribute");
			
			if(!cuname.equals(prevcuname)){
				if(!prevcuname.equals(""))
					trigBody += " THEN (SELECT cuentityid FROM changeunitentities WHERE entityid = old.entityid AND attribute = '"+prevattr+"' LIMIT 1) ELSE NULL END);";
				
				trigBody += "INSERT INTO changelog (cuname, cuentityid) " +
					"VALUES ('"+cuname+"', CASE WHEN old." +
					attribute + " != new." + attribute;
			}
			else{
				trigBody += " OR old."+attribute+" != new."+attribute;
			}
			
			prevcuname = cuname;
			prevattr = attribute;
		}
		
		if(!hasCU)
			return;
		
		trigBody += " THEN (SELECT cuentityid FROM changeunitentities WHERE entityid = old.entityid AND attribute = '"+prevattr+"' LIMIT 1) ELSE NULL END);";
		trigBody += "DELETE FROM changelog WHERE cuentityid IS NULL;";
		
		final String subprotocol = dbConn.getSubprotocol();
		if(subprotocol.equals("postgresql"))
			trigBody += "RETURN NEW;";
		
		detachTrigger(TriggerOperation.UPDATE, tablename);
		
		dbConn.execute(generateTriggerCreationSQL(TriggerOperation.UPDATE, tablename, trigBody));
	}
	
	void attachDeleteTrigger(String tablename) throws SQLException{
		if(!dbUtil.tableExists(tablename))
			throw new IllegalArgumentException("Table "+tablename+" does not exist!");

		String trigBody = "INSERT INTO "+zwGraveyardTable+" (entityid) VALUES (old.entityid)";
		
		detachTrigger(TriggerOperation.DELETE, tablename);
		
		dbConn.execute(generateTriggerCreationSQL(TriggerOperation.DELETE, tablename, trigBody));
	}
	
	//updateString: encodedCUName cuentityid encodedCUVersion (encodedTablename entityid encodedAttribute:encodedValue:dataType)(...)
	public int compareToLocalCU(String updateString) throws SQLException{
		String[] cuMsg = updateString.split(" ", 4);
		String cuentityid = cuMsg[1];
		VersionVector cuVV = new VersionVector(Utility.decode(cuMsg[2]));
		
		VersionVector vv = new VersionVector(dbConn);
		vv.loadByCUEntityID(cuentityid);
		
		return(vv.compareTo(cuVV));
	}
	
	public String[] getTables() throws SQLException{
		ResultSet tableRS = dbUtil.getTables();
		LinkedList<String> list = new LinkedList<String>();
		while(tableRS.next()){
			String tablename = tableRS.getString("TABLE_NAME");
			
			if(isSystemTable(tablename) || dbUtil.selectCount("FROM changeunits WHERE tablename = '"+tablename+"'") > 0)
				continue;
			list.add(tablename);
		}
		String[] array = new String[list.size()];
		list.toArray(array);
		return array;
	}
	
	public String[] getColumns(String tablename) throws SQLException{
		ResultSet columnRS = dbUtil.getColumns(tablename);
		LinkedList<String> list = new LinkedList<String>();
		while(columnRS.next()){
			String attribute = columnRS.getString("COLUMN_NAME");
			
			if(attribute.equals("entityid"))
				continue;
			
			//let autoincrementing columns assign their own IDs 
			if(columnRS.getString("IS_AUTOINCREMENT").equals("YES"))
				continue;
			
			list.add(attribute);
		}
		String[] array = new String[list.size()];
		list.toArray(array);
		return array;
	}
	
	public ChangeUnit newChangeUnit(String tablename, List<Pair<String, String>> attrEntries) throws SQLException{
		return new ChangeUnit("cu_"+tablename, attrEntries, dbConn);
	}
	
	public void createChangeUnitsPerTable() throws SQLException {
		ResultSet tableRS = dbUtil.getTables();
		
		while(tableRS.next()){
			String tablename = tableRS.getString("TABLE_NAME");
			
			if(isSystemTable(tablename) || dbUtil.selectCount("FROM changeunits WHERE tablename = '"+tablename+"'") > 0)
				continue;
			
			List<Pair<String, String>> attrEntries = new LinkedList<Pair<String, String>>();
			ResultSet columnRS = dbUtil.getColumns(tablename);
			while(columnRS.next()){
				String attribute = columnRS.getString("COLUMN_NAME");
				
				if(attribute.equals("entityid"))
					continue;
				
				
				//let autoincrementing columns assign their own IDs 
				if(columnRS.getString("IS_AUTOINCREMENT").equals("YES"))
					continue;
				
				
				attrEntries.add(new Pair<String, String>(tablename, attribute));
			}
			
			new ChangeUnit("cu_"+tablename, attrEntries, dbConn).saveToDB();
		}
	}

	void detachTrigger(TriggerOperation op, String tablename) throws SQLException{
		final String zwTrigName = getZWTriggerName(op, tablename);
		final String zwTrigProcName = getZWTrigProcName(op, tablename);
		final String subprotocol = dbConn.getSubprotocol();
		
		if(subprotocol.equals("postgresql")){
			dbConn.execute("DROP TRIGGER IF EXISTS "+zwTrigName+" ON "+tablename);
			dbConn.execute("DROP FUNCTION IF EXISTS "+zwTrigProcName+"()");
		}
		else if(subprotocol.equals("mysql"))
			dbConn.execute("DROP TRIGGER IF EXISTS "+zwTrigName);		
	}
	
	void detachTriggers(String tablename) throws SQLException{
		for(TriggerOperation op : TriggerOperation.values())
			detachTrigger(op, tablename);
	}
	
	public String filterString() throws SQLException{
		return(filter.toString());
	}
	
	/**
	 * Goes through all entries in the change log table and applies
	 * the necessary versioning. The change log table is then cleared
	 * (all entries deleted).
	 */
	public void flushChangeLogTable() throws SQLException{
		ResultSet changelogRS = dbConn.executeQuery("SELECT cuentityid FROM changelog ORDER BY changedon");
		
		while(changelogRS.next())
			versionChangeUnit(changelogRS.getString("cuentityid"));
		
		dbConn.executeUpdate("DELETE FROM changelog");
	}
	
	/**
	 * Adds an entityid column to all non-system tables.
	 * Generates random UUID for all entries with NULL entityids.
	 * Generates cuentityid for all affected change units.
	 * Versions generated cuentityid.
	 */
	private void generateEntityMetadata() throws SQLException{
		ResultSet results = dbUtil.getTables();
		
		final String tempNullColumnName = "zwtmp_nullentityid";
		
		//final PreparedStatement changeUnitPS = dbConn.getConnection().prepareStatement("SELECT entityid, tablename, attribute, cuentityid FROM (SELECT entityid, tablename, attribute, cuentityid FROM changeunitentities RIGHT JOIN changeunits USING (tablename, attribute) WHERE tablename = ?) AS cuquery WHERE cuentityid IS NULL");
		final PreparedStatement changeUnitPS = dbConn.getConnection().prepareStatement("SELECT DISTINCT cuname FROM changeunits WHERE tablename = ?");
		final PreparedStatement cuDefPS = dbConn.getConnection().prepareStatement("SELECT attribute FROM changeunits WHERE tablename = ? and cuname = ?");
		final PreparedStatement insertPS = dbConn.getConnection().prepareStatement("INSERT INTO changeunitentities (entityid, tablename, attribute, cuentityid, cuname) VALUES (?, ?, ?, ?, ?)");
		final PreparedStatement insertCUEntityIDPS = dbConn.getConnection().prepareStatement("INSERT INTO "+zwTempCUEntityIDTable+" (cuentityid) VALUES ("+dbConn.getUUIDFun()+")");
		while(results.next()){
			String tableName = results.getString(3);
			
			if(isSystemTable(tableName))
				continue;
			
			//count entities with null entityids
			int nNullEntityIDs = dbUtil.selectCount("FROM "+tableName+" WHERE entityid IS NULL");
			
			if(nNullEntityIDs > 0){
				//add temp column to flag entities that have null entityids
				if(!dbUtil.tableHasColumn(tableName, tempNullColumnName))
					dbConn.execute("ALTER TABLE "+tableName+" ADD COLUMN "+tempNullColumnName+" boolean");
				
				//flag all entities with null entityids
				dbConn.executeUpdate("UPDATE "+tableName+" SET "+tempNullColumnName+" = TRUE WHERE entityid IS NULL");
				
				//assign entityids to those entities
				dbConn.executeUpdate("UPDATE "+tableName+" SET entityid = "+dbConn.getUUIDFun()+" WHERE entityid IS NULL");
				
				//foreach newly assigned entityid, insert into changeunitentities a row
				//for each attribute in a change unit in the table
				ResultSet entityidRS = dbConn.executeQuery("SELECT entityid FROM "+tableName+" WHERE "+tempNullColumnName);
				changeUnitPS.setString(1, tableName);
				List<Object[]> changeUnits = DBUtility.resultSetToList(changeUnitPS.executeQuery());
				while(entityidRS.next()){
					String entityid = entityidRS.getString(1);
					
					//foreach change unit
					Iterator<Object[]> iter = changeUnits.iterator();
					while(iter.hasNext()){
						Object[] rowObj = iter.next();
						String cuname = (String)rowObj[0];
						
						String cuentityid = null;

						final PreparedStatement cuentityidPS = dbConn.getConnection().prepareStatement("SELECT cuentityid FROM "+cuname+" WHERE "+tableName+"_entityid = ?");						
						cuentityidPS.setString(1, entityid);
						ResultSet cuentityidRS = null;
						//ResultSet cuentityidRS = cuentityidPS.executeQuery();
//						if(cuentityidRS.next()){ //change unit already has change unit entityid
//							cuentityid = cuentityidRS.getString("cuentityid");
//							System.out.println("Has change unit: "+cuentityid);
//						}
//						else{ //need to generate cuentityid
							dbConn.execute("DELETE FROM "+zwTempCUEntityIDTable);
							
							insertCUEntityIDPS.executeUpdate();
							
							cuentityidRS = dbConn.executeQuery("SELECT cuentityid FROM "+zwTempCUEntityIDTable+" LIMIT 1");
							cuentityidRS.next();
							cuentityid = cuentityidRS.getString("cuentityid");
							
							dbConn.execute("DELETE FROM "+zwTempCUEntityIDTable);
//						}
						
						cuDefPS.setString(1, tableName);
						cuDefPS.setString(2, cuname);
						ResultSet cuDefRS = cuDefPS.executeQuery();
						//foreach attribute
						while(cuDefRS.next()){
							String attribute = cuDefRS.getString("attribute");
							
							insertPS.setString(1, entityid);
							insertPS.setString(2, tableName);
							insertPS.setString(3, attribute);
							insertPS.setString(4, cuentityid);
							insertPS.setString(5, cuname);
							insertPS.executeUpdate();
						}
						
						versionChangeUnit(cuentityid);
					}
				}
				
				dbConn.execute("ALTER TABLE "+tableName+" DROP COLUMN "+tempNullColumnName);
			}

			System.out.println("Attaching triggers...");
			attachTriggers(tableName);
		}
	}
	
	public String generateTriggerCreationSQL(TriggerOperation op, String tablename, String trigBody){
		final String opOp;
		
		switch(op){
		case INSERT:
			opOp = "INSERT";
			break;
		case UPDATE:
			opOp = "UPDATE";
			break;
		case DELETE:
			opOp = "DELETE";
			break;
		default:
			opOp = "O_O";
		}
		
		final String subprotocol = dbConn.getSubprotocol();
		
		final String zwTrigName = getZWTriggerName(op, tablename);
		final String zwTrigProcName = getZWTrigProcName(op, tablename);
		
		String sql = "";
		
		if(subprotocol.equals("postgresql"))
			sql += "CREATE FUNCTION "+zwTrigProcName+"() RETURNS trigger AS $$ BEGIN ";
		else if(subprotocol.equals("mysql"))
			sql += "CREATE TRIGGER "+zwTrigName+" BEFORE "+opOp+" ON "+tablename+" FOR EACH ROW BEGIN ";
		
		sql += trigBody;
		
		if(subprotocol.equals("postgresql")){
			sql += " END; $$ LANGUAGE plpgsql; CREATE TRIGGER "+zwTrigName+" AFTER "+opOp+" ON "+tablename+" FOR EACH ROW EXECUTE PROCEDURE "+zwTrigProcName+"();";
		}
		else if(subprotocol.equals("mysql")){
			sql += " END;";
		}
		else{
			throw new IllegalArgumentException("Unsupported database " + subprotocol);
		}
		
		return(sql);
	}
	
	public DBConnection getDBConnection(){
		return(dbConn);
	}
	
	public DBUtility getDBUtility(){
		return(dbUtil);
	}
	
	String getMapping(String id, String peername) throws SQLException{
		final PreparedStatement mappedPS = dbConn.getConnection().prepareStatement("SELECT mappedid FROM mappings WHERE id = ? AND mappedpeer = ?");
		mappedPS.setString(1, id);
		mappedPS.setString(2, peername);
		
		ResultSet mappedRS = mappedPS.executeQuery();
		if(mappedRS.next())
			return(mappedRS.getString("mappedid"));
		else
			return(id);
	}
	
	String getLocalMapping(String mappedid, String peername) throws SQLException{
		final PreparedStatement idPS = dbConn.getConnection().prepareStatement("SELECT id FROM mappings WHERE mappedid = ? AND mappedpeer = ?");
		idPS.setString(1, mappedid);
		idPS.setString(2, peername);
		
		ResultSet mappedRS = idPS.executeQuery();
		if(mappedRS.next())
			return(mappedRS.getString("id"));
		else
			return(mappedid);		
	}

	public String getPeerName(){
		return peerName;
	}
	
	private ResultSet getForeignKey(String tablename, String column) throws SQLException{
		final PreparedStatement foreignKeyPS = dbConn.getConnection().prepareStatement("SELECT foreigntable, foreignkey FROM "+zwForeignKeyTable+" WHERE tablename = ? AND colname = ?");
		foreignKeyPS.setString(1, tablename);
		foreignKeyPS.setString(2, column);
		
		ResultSet foreignKeyRS = foreignKeyPS.executeQuery();
		
		if(foreignKeyRS.next())
			return(foreignKeyRS);
		else
			return(null);
	}
	
	private String getForeignEntity(String tablename, String column, String key) throws SQLException{
		PreparedStatement foreignEntityPS = dbConn.getConnection().prepareStatement("SELECT entityid FROM "+tablename+" WHERE "+column+" = ?");
		foreignEntityPS.setString(1, key);
		ResultSet foreignEntityRS = foreignEntityPS.executeQuery();
		
		if(foreignEntityRS.next())
			return(foreignEntityRS.getString("entityid"));
		else
			return(null);
	}
	
	private String getLocalKey(String tablename, String column, String entityid) throws SQLException{
		PreparedStatement localKeyPS = dbConn.getConnection().prepareStatement("SELECT "+column+" FROM "+tablename+" WHERE entityid = ?");
		localKeyPS.setString(1, entityid);
		ResultSet localKeyRS = localKeyPS.executeQuery();
		
		if(localKeyRS.next())
			return(localKeyRS.getString(column));
		else
			return(null);
	}
 
	public List<String> getUpdates(Filter filter, VersionVector vv) throws SQLException {
		flushChangeLogTable();
		System.out.println(Utility.now()+" GETTING UPDATES FOR: " + vv);
		System.out.println("WITH FILTOR:"+filter);
		List<String> updates = new LinkedList<String>();
		
		StringBuilder withMetadata = new StringBuilder();//TODO:FOR TESTING
		StringBuilder withoutMetadata = new StringBuilder();//TODO:FOR TESTING
		
		ResultSet cuentityidRS = dbConn.executeQuery("SELECT DISTINCT cuname, cuentityid FROM "+zwDataVersionsTable+" INNER JOIN changeunitentities USING (cuentityid) WHERE "+vv.toWhereClause()+" ORDER BY peername, counter");
//		String query = "SELECT DISTINCT cuname, cuentityid FROM "+zwDataVersionsTable+" INNER JOIN changeunitentities USING (cuentityid) WHERE "+vv.toWhereClause()+" ORDER BY peername, counter";
//		debugPrint(">>> GETTING UPDATES: "+query);
			
		//for each cuentityid with version newer than the one in vv
		while(cuentityidRS.next()){
			String cuentityid = cuentityidRS.getString("cuentityid");
			String cuname = cuentityidRS.getString("cuname");
			
			//for each table in cuentityid's change unit
			String prevtablename = "";
			StringBuilder tabledata = new StringBuilder();
			ResultSet valueRS = null;
			
			PreparedStatement dataPS = dbConn.getConnection().prepareStatement("SELECT entityid, tablename, attribute FROM changeunitentities cue WHERE cuentityid = ? AND EXISTS(SELECT cuentityid FROM "+cuname+" WHERE cuentityid = cue.cuentityid AND "+filter.getForCU(cuname)+") ORDER BY tablename");
			dataPS.setString(1, cuentityid);
//			System.out.println("STATEMENT:"+dataPS);
			ResultSet dataRS = dataPS.executeQuery();
			while(dataRS.next()){
				String entityid = dataRS.getString("entityid");
				String tablename = dataRS.getString("tablename");
				String attribute = dataRS.getString("attribute");
				
				if(!tablename.equals(prevtablename)){
					if(!prevtablename.equals(""))
						tabledata.append(")");
					
					valueRS = dbConn.executeQuery("SELECT * FROM "+tablename+" WHERE entityid = '"+entityid+"'");
					
					if(!valueRS.next())
						continue;
			
					tabledata.append("("+Utility.encode(tablename)+" "+entityid);
				}
				
				//get data per attribute
				String value = valueRS.getString(attribute);
				
				ResultSet foreignKeyRS = getForeignKey(tablename, attribute);
				
				if(foreignKeyRS != null){ //attribute is a foreign key
					String foreigntable = foreignKeyRS.getString("foreigntable");
					String foreignkey = foreignKeyRS.getString("foreignkey");
					
					value = getForeignEntity(foreigntable, foreignkey, value);
				}
				
				tabledata.append(" "+Utility.encode(attribute)+":"+Utility.encode(value)+":"+dbUtil.getColumnType(tablename, attribute));
				
				withoutMetadata.append(valueRS.getString(attribute));
				
				prevtablename = tablename;
			}
			
			if(tabledata.length() == 0)
				continue;
			
			tabledata.append(")");
			
			VersionVector cuVV = new VersionVector(dbConn);
			cuVV.loadByCUEntityID(cuentityid);

			String cudata = "("+Utility.encode(cuname)+" "+cuentityid+" "+Utility.encode(cuVV.toString())+" "+tabledata.toString()+")";
			updates.add(cudata);
			
			withMetadata.append(cudata);
		}
		for(int i=0;i<updates.size();i++)
			debugPrint("updateSource: "+updates.get(i));
		System.out.println(Utility.now()+" Done getting updates");
		System.out.println("WITH METADATA:"+withMetadata.length());
		System.out.println("WITHOUT METADATA:"+withoutMetadata.length());
		
		return(updates);
	}
	/////////////////////////////////////////////////////////////////////////////////////////////
	public List<String> getUpdates(Filter filter, VersionVector vv, PrintStream dataGetter) throws SQLException {
		flushChangeLogTable();
		System.out.println(Utility.now()+" GETTING UPDATES FOR: " + vv);
		System.out.println("WITH FILTOR:"+filter);
		List<String> updates = new LinkedList<String>();
		
		StringBuilder withMetadata = new StringBuilder();//TODO:FOR TESTING
		StringBuilder withoutMetadata = new StringBuilder();//TODO:FOR TESTING
		
		ResultSet cuentityidRS = dbConn.executeQuery("SELECT DISTINCT cuname, cuentityid FROM "+zwDataVersionsTable+" INNER JOIN changeunitentities USING (cuentityid) WHERE "+vv.toWhereClause()+" ORDER BY peername, counter");
//		String query = "SELECT DISTINCT cuname, cuentityid FROM "+zwDataVersionsTable+" INNER JOIN changeunitentities USING (cuentityid) WHERE "+vv.toWhereClause()+" ORDER BY peername, counter";
//		debugPrint(">>> GETTING UPDATES: "+query);
			
		//for each cuentityid with version newer than the one in vv
		while(cuentityidRS.next()){
			String cuentityid = cuentityidRS.getString("cuentityid");
			String cuname = cuentityidRS.getString("cuname");
			
			//for each table in cuentityid's change unit
			String prevtablename = "";
			StringBuilder tabledata = new StringBuilder();
			ResultSet valueRS = null;
			
			PreparedStatement dataPS = dbConn.getConnection().prepareStatement("SELECT entityid, tablename, attribute FROM changeunitentities cue WHERE cuentityid = ? AND EXISTS(SELECT cuentityid FROM "+cuname+" WHERE cuentityid = cue.cuentityid AND "+filter.getForCU(cuname)+") ORDER BY tablename");
			dataPS.setString(1, cuentityid);
//			System.out.println("STATEMENT:"+dataPS);
			ResultSet dataRS = dataPS.executeQuery();
			while(dataRS.next()){
				String entityid = dataRS.getString("entityid");
				String tablename = dataRS.getString("tablename");
				String attribute = dataRS.getString("attribute");
				
				if(!tablename.equals(prevtablename)){
					if(!prevtablename.equals(""))
						tabledata.append(")");
					
					valueRS = dbConn.executeQuery("SELECT * FROM "+tablename+" WHERE entityid = '"+entityid+"'");
					
					if(!valueRS.next())
						continue;
			
					tabledata.append("("+Utility.encode(tablename)+" "+entityid);
				}
				
				//get data per attribute
				String value = valueRS.getString(attribute);
				
				ResultSet foreignKeyRS = getForeignKey(tablename, attribute);
				
				if(foreignKeyRS != null){ //attribute is a foreign key
					String foreigntable = foreignKeyRS.getString("foreigntable");
					String foreignkey = foreignKeyRS.getString("foreignkey");
					
					value = getForeignEntity(foreigntable, foreignkey, value);
				}
				
				tabledata.append(" "+Utility.encode(attribute)+":"+Utility.encode(value)+":"+dbUtil.getColumnType(tablename, attribute));
				
				withoutMetadata.append(valueRS.getString(attribute));
				
				prevtablename = tablename;
			}
			
			if(tabledata.length() == 0)
				continue;
			
			tabledata.append(")");
			
			VersionVector cuVV = new VersionVector(dbConn);
			cuVV.loadByCUEntityID(cuentityid);

			String cudata = "("+Utility.encode(cuname)+" "+cuentityid+" "+Utility.encode(cuVV.toString())+" "+tabledata.toString()+")";
			updates.add(cudata);
			
			withMetadata.append(cudata);
		}
		for(int i=0;i<updates.size();i++)
			debugPrint("updateSource: "+updates.get(i));
		System.out.println(Utility.now()+" Done getting updates");
		System.out.println("WITH METADATA:"+withMetadata.length());
		System.out.println("WITHOUT METADATA:"+withoutMetadata.length());
		dataGetter.println(withMetadata.length()+"\t"+withoutMetadata.length());
		
		return(updates);
	}
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////

	private String getZWTriggerName(TriggerOperation op, String tablename){
		String opName = null;
		
		if(op == TriggerOperation.INSERT)
			opName = "insert";
		else if(op == TriggerOperation.UPDATE)
			opName = "update";
		else if(op == TriggerOperation.DELETE)
			opName = "delete";
		
		return("zwtrig_"+opName+"_"+tablename);
	}

	private String getZWTrigProcName(TriggerOperation op, String tablename){
		String opName = null;
		
		if(op == TriggerOperation.INSERT)
			opName = "insert";
		else if(op == TriggerOperation.UPDATE)
			opName = "update";
		else if(op == TriggerOperation.DELETE)
			opName = "delete";
		
		return("zwtrigproc_"+opName+"_"+tablename);
	}
	
	private boolean initMetadata() throws SQLException {
		System.out.println(Utility.now()+"Creating system tables...");
		boolean newSystemTables = assertChangeLogTable();
		newSystemTables &= assertChangeUnitsTable();
		newSystemTables &= assertChangeUnitEntitiesTable();
		newSystemTables &= assertChangeUnitJoinsTable();
		newSystemTables &= assertCUDefVersionsTable();
		newSystemTables &= assertDataVersionsTable();
		newSystemTables &= assertMappingsTable();
		newSystemTables &= assertSchemaVersionsTable();
		newSystemTables &= assertVariablesTable();
		newSystemTables &= assertVersionVectorTable();
		newSystemTables &= assertTempTables();
		newSystemTables &= assertForeignKeyTable();
		newSystemTables &= assertGraveyardTable();
		
		assertEntityIDColumns();
		
		//TODO:way for users to specify their own change units
		System.out.println("Creating change units per table...");
		createChangeUnitsPerTable();
		
//		//TODO:way for users to specify their own filter
//		if(!dbUtil.variableExists(Filter.FILTER_VAR_NAME)){
//			filter.setForCU("cu_objectcache", "objectcache_keyname = 'zerowikidba:messages:en'");
//			filter.saveToDB();
//		}
		
		System.out.println("FILTER!:"+filter);
		
		System.out.println(Utility.now()+"Flushing ChangeLog...");
		flushChangeLogTable();
		
		System.out.println(Utility.now()+"Generating entity metadata...");
		generateEntityMetadata();
		
		System.out.println(Utility.now()+"Init done!");
		return newSystemTables; //specifies if the system tables are new tables
	}
	public void debugPrint(String print){
		if(debug)
			System.out.println("DEBUG ===========================  "+print);
	}
	//updateString: encodedCUName cuentityid encodedCUVersion (encodedTablename entityid encodedAttribute:encodedValue:dataType)(...)
	public void insertUpdate(String updateString, String syncPartner) throws SQLException {
		debugPrint(" =================== INSERT START");
		if(syncPartner == null)
			throw new IllegalStateException("No sync partner set");
		debugPrint("updateString: "+updateString);
		dbConn.getConnection().setAutoCommit(false);
		String[] cuMsg = updateString.split(" ", 4);
		String cuname = Utility.decode(cuMsg[0]);
		String cuentityid = cuMsg[1];
		VersionVector cuVV = new VersionVector(Utility.decode(cuMsg[2]), dbConn);
		String tabledata = cuMsg[3];

		//TODO: switch back if not working
		cuname = cuname.substring(1);
		tabledata = tabledata.substring(0, tabledata.length()-1);
		debugPrint("cuname: "+cuname);
		debugPrint("cuentityid: "+cuentityid);

		debugPrint("cuVV: "+Utility.decode(cuMsg[2]));
		debugPrint("tabledata: "+tabledata);
		ParenScanner pscTab = new ParenScanner(tabledata);
		while(pscTab.hasNext()){
			String pscTabNext = pscTab.next();
			debugPrint("\ttableentry: "+pscTabNext);
			String[] tabMsg = pscTabNext.split(" ");
			String tablename = Utility.decode(tabMsg[0]);
			String entityid = getLocalMapping(tabMsg[1], syncPartner);
			
			List<String> attributes = new LinkedList<String>();
			Map<String, String> values = new HashMap<String, String>();
			Map<String, Integer> types = new HashMap<String, Integer>();
			for(int i = 2; i < tabMsg.length; i++){
				String[] valMsg = tabMsg[i].split(":");
				
				if(valMsg.length != 3)
					throw new RuntimeException("WTF");
				
				String attribute = Utility.decode(valMsg[0]);
				String value = Utility.decode(valMsg[1]);
				int type = Integer.parseInt(valMsg[2]);
				
				ResultSet foreignKeyRS = getForeignKey(tablename, attribute);
				
				if(foreignKeyRS != null){
					String foreigntable = foreignKeyRS.getString("foreigntable");
					String foreignkey = foreignKeyRS.getString("foreignkey");

					value = getLocalKey(foreigntable, foreignkey, value);
				}
				
				attributes.add(attribute);
				values.put(attribute, value);
				types.put(attribute, type);
			}
			
			PreparedStatement updatePS = null;
			if(dbUtil.selectCount("FROM "+tablename+" WHERE entityid = '"+entityid+"'") == 0)
				updatePS = dbUtil.prepareInsertStatement(tablename, attributes, "entityid");					
			else
				updatePS = dbUtil.prepareUpdateStatement(tablename, attributes, "entityid");
			
			int valDex = 1;
			Iterator<String> attrIter = attributes.iterator();
			while(attrIter.hasNext()){
				String attribute = attrIter.next();
				String value = values.get(attribute);
				int type = types.get(attribute);
				
				if(dbUtil.isString(type))
					updatePS.setString(valDex, value);
				else
					updatePS.setObject(valDex, value);
				
				valDex++;
			}
			
			updatePS.setString(valDex, entityid);
//			System.out.println("EXECUTING: "+updatePS);
			detachTriggers(tablename);
			// =============== new kevin code
			
			ResultSet columnsRS = getChangeUnitColumns(dbConn, tablename);
			
			// =============== end
			try{
				PreparedStatement insertPS = 
					dbConn.prepareUpdatableStatement("INSERT INTO changeunitentities " +
								"(entityid, tablename, attribute, cuentityid, cuname) VALUES " +
								"(?, ?, ?, ?, ?)");
				
				while(columnsRS.next()){
					String cunameRS = columnsRS.getString("cuname");
					String attributeRS = columnsRS.getString("attribute");
					debugPrint(">> "+ cunameRS + " // "+attributeRS);
					insertPS.setObject(1, entityid);
					insertPS.setObject(2, tablename);
					insertPS.setObject(3, attributeRS);
					insertPS.setObject(4, cuentityid);
					insertPS.setObject(5, cunameRS);
					insertPS.execute();
					debugPrint("\tchangeunitentities:: " + insertPS.toString());
					
				}
				
				debugPrint("update " + updatePS.toString());
				
				updatePS.executeUpdate();
				debugPrint("success");
			}
			catch(MySQLIntegrityConstraintViolationException e){ //TODO:Cross-site mapping patch
				
				final Pattern p = Pattern.compile(".*Duplicate entry '(.*)' for key '(.*)'");
				final Matcher m = p.matcher(e.toString());
				
				if(m.matches()){
					System.err.println("EXCEPTION:"+e.toString());
					String entry = m.group(1);
					String key = m.group(2);
					
					List<String> keyColumns = dbUtil.getColumnsOfIndex(tablename, key);

					final PreparedStatement ps = dbUtil.prepareSelectStatement(tablename, Arrays.asList("entityid"), keyColumns); //dbConn.getConnection().prepareStatement("SELECT entityid FROM "+tablename+" WHERE "+key+" = ?");
					
					int keyColDex = 1;
					for(String keyColumn : keyColumns){
						if(dbUtil.isString(types.get(keyColumn)))
							ps.setString(keyColDex, values.get(keyColumn));
						else
							ps.setObject(keyColDex, values.get(keyColumn));
						
						keyColDex++;
					}
					System.out.println("EXECUTING FOR MAPPING:"+ps);
					ResultSet rs = ps.executeQuery();
					rs.next();
					
					String id = rs.getString("entityid");
					setMapping(id, entityid, syncPartner);
					
					updatePS = dbUtil.prepareUpdateStatement(tablename, attributes, "entityid");
					
					valDex = 1;
					attrIter = attributes.iterator();
					while(attrIter.hasNext()){
						String attribute = attrIter.next();
						String value = values.get(attribute);
						int type = types.get(attribute);
						
						if(dbUtil.isString(type))
							updatePS.setString(valDex, value);
						else
							updatePS.setObject(valDex, value);
						
						valDex++;
					}
					updatePS.setString(valDex, id);
					System.out.println("EXECUTING AFTER MAPPING: "+updatePS);
					updatePS.executeUpdate();
				}
			}
			
			attachTriggers(tablename); //this is really slow tho :\
			debugPrint("cuvv DOING!");
			cuVV.versionChangeUnit(cuentityid);
			debugPrint("================================");
		}
		
		dbConn.getConnection().setAutoCommit(true);
	}

	public boolean isSystemTable(String tablename){
		return(zwSystemTables.contains(tablename));
	}

	/**
	 * @param id
	 * @param mappedid
	 * @param peername
	 * @return True when a new mapping was inserted, false when an existing mapping was updated 
	 * @throws SQLException
	 */
	boolean setMapping(String id, String mappedid, String peername) throws SQLException{
		final List<String> columns = Arrays.asList("mappedid", "mappedpeer");
		
		boolean newMapping = false;
		PreparedStatement ps = null;
		if(getMapping(id, peername).equals(id)){
			ps = dbUtil.prepareInsertStatement("mappings", columns, "id");
			newMapping = true;
		}
		else{
			ps = dbUtil.prepareUpdateStatement("mappings", columns, "id");
			newMapping = false;
		}
		
		ps.setString(1, mappedid);
		ps.setString(2, peername);
		ps.setString(3, id);
		ps.executeUpdate();
		
		return(newMapping);
	}

	public List<String> queryToStringList(String query) throws SQLException {
		return (resultSetToStringList(dbConn.executeQuery(query)));
	}
	
	private List<String> resultSetToStringList(ResultSet results)
			throws SQLException {
		List<String> stringList = new LinkedList<String>();

		int n_columns = results.getMetaData().getColumnCount();

		while (results.next()) {
			String row = "";
			for (int i = 1; i <= n_columns; i++) {
				row += results.getString(i);
				if (i != n_columns) {
					row += ":";
				}
			}

			stringList.add(row);
		}

		return (stringList);
	}
	
	/**
	 * Creates system table if not already extant.
	 */
	private boolean tableAsserter(String tablename, String columnList) throws SQLException {
		boolean ret = false;
		if(!dbUtil.tableExists(tablename)){
			dbConn.execute("CREATE TABLE "+tablename+" "+columnList);
			ret = true;
		}
		
		zwSystemTables.add(tablename);
		return ret;
	}
	/**
	 * Given a change unit entityid, updates the change unit's version
	 * to this site's current clock value and then increments the clock
	 */
	private void versionChangeUnit(String cuentityid) throws SQLException {
		int clockVal = clock.getValue();
		
		VersionVector vv = new VersionVector(dbConn);
		
		vv.setVersion(cuentityid, peerName, clockVal);
		
		vv.setDBVersionVectorEntry(peerName, clockVal);
		
		clock.incValue();
	}
	
	public String versionString() throws SQLException {
		flushChangeLogTable();
		
		String versionString = "";

		ResultSet results = dbConn.executeQuery("SELECT peername, maxcounter "
				+ "FROM versionvector ORDER BY peername");

		while (results.next())
			versionString += results.getString("peername") + ":" + results.getString("maxcounter") + " ";

		results.close();

		return (versionString.trim());
	}
	
	public void setSyncPartner(String syncPartner){
		this.syncPartner = syncPartner;
	}
	
	public void unsetSyncPartner(){
		this.syncPartner = null;
	}
	
	public void setFilterForCU(String cuname, String filterStr) throws SQLException{
		filter.setForCU(cuname, filterStr);
		filter.saveToDB();
	}
	// ================ kevin code
	private ResultSet getChangeUnitColumns(DBConnection dbConnection, String tableName ) throws SQLException{
		PreparedStatement columnsPS = 
			dbConn.getConnection().prepareStatement(
				"SELECT cuname, attribute FROM changeunits WHERE tablename = ? ORDER BY cuname, attribute");
		columnsPS.setString(1, tableName);
		ResultSet columnsRS = columnsPS.executeQuery();
		return columnsRS;
	}
}
