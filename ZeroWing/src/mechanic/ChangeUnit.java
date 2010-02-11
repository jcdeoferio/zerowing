package mechanic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.DBUtility;
import util.Pair;

public class ChangeUnit {
	private String name;
	private Set<String> tables;
	private List<Pair<String, String>> attributes;
	private Map<String, String> joinStrs;
	private DBConnection dbConn;
	
	private static final String NO_DBCONN_MSG = "Database Connection not set";
	private static final String TABLE_SIZE_MSG = "Table size exception";
	private static final String TABLE_EQ_MSG = "Table equality exception";
	
	public ChangeUnit(String name){
		this(name, new HashSet<String>(), new LinkedList<Pair<String, String>>(), null, null);
	}
	
	public ChangeUnit(String name, List<Pair<String, String>> attributes){
		this(name, attributes, null);
	}
	
	public ChangeUnit(String name, List<Pair<String, String>> attributes, DBConnection dbConn){
		Set<String> tables = Pair.setOfFirst(attributes); 
		
		if(tables.size() > 1)
			throw new IllegalArgumentException(TABLE_SIZE_MSG);
		
		this.name = name;
		this.tables = tables;
		this.attributes = attributes;
		this.joinStrs = new HashMap<String, String>();
		this.dbConn = dbConn;		
	}
	
	public ChangeUnit(String name, Set<String> tables, List<Pair<String, String>> attributes, Map<String, String> joinStrs, DBConnection dbConn){
		Set<String> attributeTables = Pair.setOfFirst(attributes);
		
		if(tables.size() != attributeTables.size() || tables.size() != joinStrs.size() + 1) //+1 because there are join expressions for all tables except the first
			throw new IllegalArgumentException(TABLE_SIZE_MSG);
		
		if(!tables.equals(attributeTables) || !tables.containsAll(joinStrs.keySet()))
			throw new IllegalArgumentException(TABLE_EQ_MSG);
		
		this.name = name;
		this.tables = tables;
		this.attributes = attributes;
		this.joinStrs = joinStrs;
		this.dbConn = dbConn;
	}
	
	//Sample view definition:
	//CREATE VIEW cu_students AS SELECT DISTINCT cuentityid, classlists.entityid AS classlists_entityid, students.entityid AS students_entityid, students.studentid AS students_studentid, students.name AS students_name, classlists.studentid AS classlists_studentid, classlists.class AS classlists_class FROM classlists FULL OUTER JOIN students ON (classlists.studentid = students.studentid) INNER JOIN (SELECT entityid, cuentityid FROM changeunitentities) AS cuentities ON (classlists.entityid = cuentities.entityid OR students.entityid = cuentities.entityid);
	public String viewDefinition(){
		String viewStr = "SELECT DISTINCT cuentityid";
		
		String firstCol = null;
		
		for(String tablename : tables){
			viewStr += ", "+tablename+".entityid"+" AS "+tablename+"_entityid";
			
			if(!joinStrs.containsKey(tablename)) //find column with no join condition, this is the first column in the FROM list
				firstCol = tablename;
		}
		
		for(Pair<String, String> attribute : attributes){
			String tablename = attribute.first;
			String column = attribute.second;
			
			if(column.equals("entityid"))
				continue;
			
			viewStr += ", "+tablename+"."+column+" AS "+tablename+"_"+column;
		}
		
		viewStr += " FROM "+firstCol;
		
		for(Map.Entry<String, String> joinEntry : joinStrs.entrySet())
			viewStr += " FULL OUTER JOIN "+joinEntry.getKey()+" ON ("+joinEntry.getValue()+")";
		
		viewStr += " INNER JOIN changeunitentities ON (";
		
		for(String tablename : tables)
			viewStr += tablename+".entityid = changeunitentities.entityid OR ";
		
		viewStr = viewStr.substring(0, viewStr.length()-4); //4 length of dangling " OR "
		
		viewStr += ")";
		
		return(viewStr);
	}
	
	public void setDBConnection(DBConnection dbConn){
		this.dbConn = dbConn;
	}
	
	public void saveToDB() throws SQLException{
		if(dbConn == null)
			throw new IllegalArgumentException(NO_DBCONN_MSG);
		
		DBUtility dbUtil = new DBUtility(dbConn);
		
		//Disallows edits
		if(dbUtil.tableExists(name))
			return;
		
		dbConn.getConnection().setAutoCommit(false);
		
		dbConn.execute("CREATE VIEW "+name+" AS "+viewDefinition());
		
		final PreparedStatement cuPS = dbConn.getConnection().prepareStatement("INSERT INTO changeunits (cuname, tablename, attribute) VALUES (?, ?, ?)");
		cuPS.setString(1, name);
		
		for(Pair<String, String> attrEntry : attributes){
			cuPS.setString(2, attrEntry.first);
			cuPS.setString(3, attrEntry.second);
			cuPS.executeUpdate();
		}
		
		final PreparedStatement joinPS = dbConn.getConnection().prepareStatement("INSERT INTO changeunitjoins (cuname, tablename, joinstr) VALUES (?, ?, ?)");
		joinPS.setString(1, name);
		
		for(Map.Entry<String, String> joinEntry : joinStrs.entrySet()){
			joinPS.setString(2, joinEntry.getKey());
			joinPS.setString(3, joinEntry.getValue());
			joinPS.executeUpdate();
		}
		
		dbConn.getConnection().setAutoCommit(true);
	}
	
	public void loadFromDB() throws SQLException{
		if(dbConn == null)
			throw new IllegalArgumentException(NO_DBCONN_MSG);
		
		if(name == null)
			throw new IllegalArgumentException("Name not set");
		
		PreparedStatement loadPS = dbConn.getConnection().prepareStatement("SELECT tablename, attribute FROM changeunits WHERE cuname = ? ORDER BY tablename, attribute");
		loadPS.setString(1, name);
		
		String prevTablename = "";
		ResultSet loadRS = loadPS.executeQuery();
		while(loadRS.next()){
			String tablename = loadRS.getString("tablename");
			String attribute = loadRS.getString("attribute");
			
			if(!tablename.equals(prevTablename))
				tables.add(tablename);
			
			attributes.add(new Pair<String, String>(tablename, attribute));
			
			prevTablename = tablename;
		}
	}

	public String getName() {
		return name;
	}

	public List<String> getTables() {
		return(new LinkedList<String>(tables));
	}
	
	public List<Pair<String, String>> getAttributes() {
		return(new LinkedList<Pair<String, String>>(attributes));
	}
}
