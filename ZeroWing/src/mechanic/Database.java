package mechanic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import util.DBUtility;
import util.Pair;
import util.ParenScanner;
import util.Utility;

public class Database {
	private enum TriggerOperation{
		INSERT, UPDATE
	}
	
	static final String zwTempEntityIDTable = "zwtrigproc_entityid";
	static final String zwTempCUEntityIDTable = "zwtrigproc_cuentityid";
	public static final String zwNULLEntry = "ZWSUPERNULL";
	
	DBConnection dbConn;
	DBUtility dbUtil;
	Clock clock;
	String peerName;
	Filter filter;
	
	private static Set<String> zwSystemTables = new HashSet<String>();
	
	public Database(String subprotocol, String host, int port, String database,
			String user, String password, String peerName) throws SQLException {
		dbConn = DBConnection.getDBConnection(subprotocol, host, port, database, user, password);
		dbUtil = new DBUtility(dbConn);
		
		this.peerName = peerName;
		clock = new Clock(this);
		
		filter = new Filter(dbConn);
		
		//Unhandled SQL Exception, causes program termination.
		initMetadata();
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

	void assertChangeLogTable() throws SQLException{
		tableAsserter("changelog", "(cuname varchar(160), cuentityid varchar(160), changedon timestamp)");
	}
	
	void assertChangeUnitEntitiesTable() throws SQLException {
		tableAsserter("changeunitentities", "(entityid varchar(160), tablename varchar(160), attribute varchar(160), cuentityid varchar(160), cuname varchar(160))");
	}
	
	void assertChangeUnitJoinsTable() throws SQLException{
		tableAsserter("changeunitjoins", "(cuname varchar(160), tablename varchar(160), joinstr varchar(160))");
	}
	
	void assertChangeUnitsTable() throws SQLException {
		tableAsserter("changeunits", "(cuname varchar(160), tablename varchar(160), attribute varchar(160))");
	}
	
	void assertCUDefVersionsTable() throws SQLException {
		tableAsserter("cudef_versions", "(cuname varchar(160), peername varchar(160), counter integer)");
	}

	void assertDataVersionsTable() throws SQLException{
		tableAsserter("data_versions", "(cuentityid varchar(160), peername varchar(160), counter integer)");
	}
	
	void assertSchemaVersionsTable() throws SQLException{
		tableAsserter("schema_versions", "(tablename varchar(160), peername varchar(160), counter integer)");
	}
	
	void assertTempTables() throws SQLException{
		tableAsserter(zwTempEntityIDTable, "(entityid character(36))");
		tableAsserter(zwTempCUEntityIDTable, "(cuentityid character(36))");
	}
	
	void assertVariablesTable() throws SQLException{
		tableAsserter("variables", "(varname varchar(20), value varchar(160))");
	}
	
	void assertVersionVectorTable() throws SQLException{
		tableAsserter("versionvector", "(peername varchar(160), maxcounter integer)");
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
	
	//updateString: encodedCUName cuentityid encodedCUVersion (encodedTablename entityid encodedAttribute:encodedValue:dataType)(...)
	public int compareToLocalCU(String updateString) throws SQLException{
		String[] cuMsg = updateString.split(" ", 4);
		String cuentityid = cuMsg[1];
		VersionVector cuVV = new VersionVector(Utility.decode(cuMsg[2]));
		
		VersionVector vv = new VersionVector(dbConn);
		vv.loadByCUEntityID(cuentityid);
		
		return(vv.compareTo(cuVV));
	}

	private void createChangeUnitsPerTable() throws SQLException {
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
		final String opOp = op == TriggerOperation.INSERT?"INSERT":"UPDATE";
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
	
	public String getPeerName(){
		return peerName;
	}

	public List<String> getUpdates(Filter filter, VersionVector vv) throws SQLException {
		flushChangeLogTable();
		System.out.println("GETTING UPDATES FOR: " + vv);
		System.out.println("WITH FILTOR:"+filter);
		List<String> updates = new LinkedList<String>();
		
		ResultSet cuentityidRS = dbConn.executeQuery("SELECT DISTINCT cuentityid FROM data_versions WHERE "+vv.toWhereClause());
		
		//TODO:REMOVE
		PrintStream ps = null;
		try {
			ps = new PrintStream(new File("before_encoding_for_send.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//TODO:REMOVE
		PrintStream ups = null;
		try {
			ups = new PrintStream(new File("no_russian.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		while(cuentityidRS.next()){
			String cuentityid = cuentityidRS.getString("cuentityid");
			
			//for each table in cuentityid's change unit
			String prevtablename = "";
			String tabledata = "";
			String cuname = null;
			ResultSet valueRS = null;
		
			//for each cuentityid with version newer than the one in vv
			PreparedStatement dataPS = dbConn.getConnection().prepareStatement("SELECT entityid, tablename, attribute, cuname FROM changeunitentities WHERE cuentityid = ? AND "+filter.getForCU(cuname)+" ORDER BY tablename"); //TODO:TIEM 4 FILTORS
			dataPS.setString(1, cuentityid);
			System.out.println("STATEMENT:"+dataPS);
			ResultSet dataRS = dataPS.executeQuery();
			while(dataRS.next()){
				String entityid = dataRS.getString("entityid");
				String tablename = dataRS.getString("tablename");
				String attribute = dataRS.getString("attribute");
				cuname = dataRS.getString("cuname");
				
				if(!tablename.equals(prevtablename)){
					if(!prevtablename.equals(""))
						tabledata += ")";
					
					valueRS = dbConn.executeQuery("SELECT * FROM "+tablename+" WHERE entityid = '"+entityid+"'");
					
					if(!valueRS.next())
						continue;
			
					ups.println("TABLENAME:"+tablename);
					ups.println("ENTITYID:"+entityid);
					tabledata += "("+Utility.encode(tablename)+" "+entityid;
				}
				
				ups.println("ATTRIBUTE:"+attribute);
				ups.println("VALUE:"+valueRS.getString(attribute));
				ups.println("TYPE:"+dbUtil.getColumnType(tablename, attribute));
				ups.println();
				//get data per attribute	
				tabledata += " "+Utility.encode(attribute)+":"+Utility.encode(valueRS.getString(attribute))+":"+dbUtil.getColumnType(tablename, attribute);
				
				prevtablename = tablename;
			}
			
			if(tabledata == "")
				continue;
			
			tabledata += ")";
			
			VersionVector cuVV = new VersionVector(dbConn);
			cuVV.loadByCUEntityID(cuentityid);

			ups.println("CUNAME:"+cuname);
			ups.println("CUENTITYID:"+cuentityid);
			ups.println("CUVV:"+cuVV.toString());
			ups.println("------------------");
			String cudata = "("+Utility.encode(cuname)+" "+cuentityid+" "+Utility.encode(cuVV.toString())+" "+tabledata+")";
			updates.add(cudata);
			
			ps.println(cudata);
		}
		
		return(updates);
	}
	
	private String getZWTriggerName(TriggerOperation op, String tablename){
		String opName = null;
		
		if(op == TriggerOperation.INSERT)
			opName = "insert";
		else if(op == TriggerOperation.UPDATE)
			opName = "update";
		
		return("zwtrig_"+opName+"_"+tablename);
	}

	private String getZWTrigProcName(TriggerOperation op, String tablename){
		String opName = null;
		
		if(op == TriggerOperation.INSERT)
			opName = "insert";
		else if(op == TriggerOperation.UPDATE)
			opName = "update";
		
		return("zwtrigproc_"+opName+"_"+tablename);
	}

	private void initMetadata() throws SQLException {
		System.out.println("Creating system tables...");
		assertChangeLogTable();
		assertChangeUnitsTable();
		assertChangeUnitEntitiesTable();
		assertCUDefVersionsTable();
		assertDataVersionsTable();
		assertSchemaVersionsTable();
		assertVariablesTable();
		assertVersionVectorTable();
		assertTempTables();
		
		assertEntityIDColumns();
		
		//TODO:way for users to specify their own change units
		System.out.println("Creating change units per table...");
		createChangeUnitsPerTable();
		
		//TODO:way for users to specify their own filter
		if(!dbUtil.variableExists(Filter.FILTER_VAR_NAME)){
			filter.setForCU("cu_objectcache", "objectcache:objectcache_keyname = 'zerowikidba:messages:en'");
			filter.saveToDB();
		}
		
		System.out.println("FILTER!:"+filter);
		
		System.out.println("Flushing ChangeLog...");
		flushChangeLogTable();
		
		System.out.println("Generating entity metadata...");
		generateEntityMetadata();
		
		System.out.println("Init done!");
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

	//updateString: encodedCUName cuentityid encodedCUVersion (encodedTablename entityid encodedAttribute:encodedValue:dataType)(...)
	public void insertUpdate(String updateString) throws SQLException {
		dbConn.getConnection().setAutoCommit(false);
		String[] cuMsg = updateString.split(" ", 4);
		String cuname = Utility.decode(cuMsg[0]);
		String cuentityid = cuMsg[1];
		VersionVector cuVV = new VersionVector(Utility.decode(cuMsg[2]), dbConn);
		String tabledata = cuMsg[3];
		
		ParenScanner pscTab = new ParenScanner(tabledata);
		while(pscTab.hasNext()){
			String[] tabMsg = pscTab.next().split(" ");
			String tablename = Utility.decode(tabMsg[0]);
			String entityid = tabMsg[1];
			
			List<String> attributes = new LinkedList<String>();
			List<String> values = new LinkedList<String>();
			List<Integer> types = new LinkedList<Integer>();
			for(int i = 2; i < tabMsg.length; i++){
				String[] valMsg = tabMsg[i].split(":");
				
				if(valMsg.length != 3)
					throw new RuntimeException("WTF");
				
				String attribute = Utility.decode(valMsg[0]);
				String value = Utility.decode(valMsg[1]);
				int type = Integer.parseInt(valMsg[2]);
				
				attributes.add(attribute);
				values.add(value);
				types.add(type);
			}
			
			PreparedStatement updatePS = null;
			if(dbUtil.selectCount("FROM "+tablename+" WHERE entityid = '"+entityid+"'") == 0)
				updatePS = dbUtil.prepareInsertStatement(tablename, attributes, "entityid");					
			else
				updatePS = dbUtil.prepareUpdateStatement(tablename, attributes, "entityid");
			
			int valDex = 1;
			Iterator<String> valIter = values.iterator();
			Iterator<Integer> typeIter = types.iterator();
			while(valIter.hasNext()){
				String value = valIter.next();
				int type = typeIter.next();
				
				if(dbUtil.isString(type))
					updatePS.setString(valDex, value);
				else
					updatePS.setObject(valDex, value);
				
				valDex++;
			}
			
			updatePS.setString(valDex, entityid);
			System.out.println("EXECUTING: "+updatePS);
			detachTriggers(tablename);
			updatePS.executeUpdate();
			attachTriggers(tablename);
			
			cuVV.versionChangeUnit(cuentityid);
		}
		
		dbConn.getConnection().setAutoCommit(true);
	}

	public boolean isSystemTable(String tablename){
		return(zwSystemTables.contains(tablename));
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
	private void tableAsserter(String tablename, String columnList) throws SQLException {
		if(!dbUtil.tableExists(tablename)){
			dbConn.execute("CREATE TABLE "+tablename+" "+columnList);
		}
		
		zwSystemTables.add(tablename);
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
	
	public String filterString() throws SQLException{
		return(filter.toString());
	}
}
