package mechanic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import util.DBUtility;

public class VersionVector implements Comparable<VersionVector>{
	private TreeMap<String, Integer> versionVector;
	private DBConnection dbConn;
	
	public VersionVector(){
		this("", null);
	}
	
	public VersionVector(DBConnection dbConn){
		this("", dbConn);
	}
	
	public VersionVector(ResultSet versionRS) throws SQLException{
		this(versionRS, null);
	}
	
	public VersionVector(ResultSet versionRS, DBConnection dbConn) throws SQLException {
		loadByResultSet(versionRS);
		
		this.dbConn = dbConn;
	}
	
	public VersionVector(String versionString){
		this(versionString, null);
	}
	
	public VersionVector(String versionString, DBConnection dbConn){
		this.versionVector = new TreeMap<String, Integer>();
		String[] versionStringParts = versionString.split("\\s");
		
		for(String str : versionStringParts){
			String[] arr = str.split(":");
			
			if(arr.length != 2)
				continue;
			
			String peername = arr[0];
			int counter = Integer.parseInt(arr[1]);
			
			addVersion(peername, counter);
		}
		
		this.dbConn = dbConn;
	}
	
	public void addVersion(String peername, int counter){
		if(!versionVector.containsKey(peername)){
			versionVector.put(peername, counter);
		}
		else{
			int prevCounter = versionVector.get(peername);
			
			if(prevCounter < counter)
				versionVector.put(peername, counter);
		}
	}
	
	public void addVersionVector(VersionVector v) {
		for(String site : v.siteSet())
			this.addVersion(site, v.getCounter(site));
	}
	
	public void addVersionVectorFromVersionTable(String tablename) throws SQLException{
		if(dbConn == null)
			throw new IllegalStateException("Database Connection not set");
		
		DBUtility dbUtil = new DBUtility(dbConn);
		
		if(!dbUtil.tableHasColumn(tablename, "version"))
			throw new IllegalArgumentException("Table "+tablename+" does not have version column");
		
		VersionVector vv = new VersionVector();
		
		ResultSet results = dbConn.executeQuery("SELECT version FROM "+tablename);
		
		while(results.next()){
			VersionVector v = new VersionVector(results.getString("version"));
			
			vv.addVersionVector(v);
		}
		
		this.addVersionVector(vv);
	}
	
	/**
	 * Returns 0 when this VersionVector is equal to the other VersionVector, 1 when this is newer than the other, 
	 * -1 when this is older than the other, and -2 when this is incomparable to the other (in conflict).
	 */
	public int compareTo(VersionVector other){
		if(this.equals(other))
			return(0);
		
		if(this.size() == other.size()){
			Set<String> thisSiteSet = this.siteSet();
			if(!thisSiteSet.equals(other.siteSet()))
				return(-2); //incomparable if each knows a site the other doesn't know
			
			int compOutcome = 0;
			for(String site : thisSiteSet){
				Integer thisCounter = new Integer(this.getCounter(site));
				int tempCompOutcome = thisCounter.compareTo(other.getCounter(site));
				
				if(tempCompOutcome == 0)
					continue;
				
				if(compOutcome != 0 && tempCompOutcome != compOutcome)
					return(-2); //incomparable if one site has newer entries from some sites and older from others
				
				compOutcome = tempCompOutcome;
			}
			
			return(compOutcome); //0 if all entries are the same, -1 or 1 if differing entries are uniformly different
		}
		else{
			VersionVector A = this;
			VersionVector B = other;
			int compOutcome = 1;
			
			//A should be the bigger set
			if(A.size() < B.size()){
				VersionVector temp = A;
				A = B;
				B = temp;
				compOutcome = -1;
			}
			
			final int diff = A.size() - B.size();
			int excessSites = 0;
			Set<String> ASet = A.siteSet();
			Set<String> BSet = B.siteSet();
			for(String site : ASet){
				if(!BSet.contains(site)){
					excessSites++;
					
					//Number of sites in A exceeds the difference between the sizes
					//of A and B therefore A and B each knows at least 1 site that
					//the other does not know about (hence conflict)
					if(excessSites > diff)
						return(-2);
					
					continue;
				}
				
				if(A.getCounter(site) < B.getCounter(site)){
					//A has entries that are older than
					//ones in B therefore conflict
					return(-2);
				}
			}
			
			//all entries in A are newer or equal to ones in B
			return(compOutcome);
		}
	}

	public boolean equals(Object o){
		VersionVector other = (VersionVector) o;
		
		if(this.size() != other.size())
			return(false);
		
		Set<String> thisSiteSet = this.siteSet();
		if(!thisSiteSet.equals(other.siteSet()))
			return(false);
		
		for(String site : thisSiteSet){
			if(this.getCounter(site) != other.getCounter(site))
				return(false);
		}
		
		return(true);
	}
	
	public int getCounter(String peername){
		if(!versionVector.containsKey(peername))
			return(-1);
		
		return(versionVector.get(peername));
	}

	public boolean hasVersionEntry(String cuentityid, String peername) throws SQLException{
		PreparedStatement ps = dbConn.getConnection().prepareStatement("SELECT * FROM data_versions WHERE cuentityid = '"+cuentityid+"' AND peername = '"+peername+"'");
		ResultSet result = ps.executeQuery();
		
		return(result.next());
	}
	
	public void loadByCUEntityID(String cuentityid) throws SQLException{
		if(dbConn == null)
			throw new IllegalStateException("Database Connection not set");
		
		PreparedStatement versionPS = dbConn.getConnection().prepareStatement("SELECT peername, counter FROM data_versions WHERE cuentityid = ?");
		versionPS.setString(1, cuentityid);
		
		loadByResultSet(versionPS.executeQuery());
	}
	
	public void loadByResultSet(ResultSet versionRS) throws SQLException{
		this.versionVector = new TreeMap<String, Integer>();
		while(versionRS.next()){
			String peername = versionRS.getString("peername");
			int counter = versionRS.getInt("counter");
			
			addVersion(peername, counter);
		}
	}
	
	public void setDBConnection(DBConnection dbConn){
		this.dbConn = dbConn;
	}

	public void setDBVersionVectorEntry(String peername, int maxcounter) throws SQLException{
		DBUtility dbUtil = new DBUtility(dbConn);
		
		PreparedStatement ps = null;
		if(dbUtil.selectCount("FROM versionvector WHERE peername = '"+peername+"'") == 0){
			ps = dbConn.getConnection().prepareStatement("INSERT INTO versionvector (maxcounter, peername) VALUES (?, ?)");
		}
		else{
			ps = dbConn.getConnection().prepareStatement("UPDATE versionvector SET maxcounter = ? WHERE peername = ?");
		}
		
		ps.setInt(1, maxcounter);
		ps.setString(2, peername);
		ps.executeUpdate();			
	}
	
	public void setVersion(String cuentityid, String peername, int counter) throws SQLException{
		DBUtility dbUtil = new DBUtility(dbConn);
		
		PreparedStatement ps = null;
		if(dbUtil.selectCount("FROM data_versions WHERE cuentityid = '"+cuentityid+"' AND peername = '"+peername+"'") == 0){
			ps = dbConn.getConnection().prepareStatement("INSERT INTO data_versions (counter, cuentityid, peername) VALUES (?, ?, ?)");
		}
		else{
			ps = dbConn.getConnection().prepareStatement("UPDATE data_versions SET counter = ? WHERE cuentityid = ? and peername = ?");
		}
		
		ps.setInt(1, counter);
		ps.setString(2, cuentityid);
		ps.setString(3, peername);
		ps.executeUpdate();
	}
	
	public Set<String> siteSet(){
		return(versionVector.keySet());
	}

	public int size(){
		return(versionVector.size());
	}
	
	public String toString(){
		String str = "";
		
		for(String site : this.siteSet())
			str += site + ":" + getCounter(site) + " ";
		
		return(str.trim());
	}
	
	public String toWhereClause(){
		if(this.siteSet() == null || this.siteSet().size() == 0)
			return("TRUE");
		
		String str = "(";
		
		String peerlist = "(";
		
		for(String site : this.siteSet()){
			str += "(peername = '"+site+"' AND counter > "+getCounter(site)+") OR ";
			
			peerlist +="'"+site+"', ";
		}
		
		peerlist = peerlist.substring(0, peerlist.length() - 2) + ")"; //2 is the length of the dangling ", "
		
		str += "peername NOT IN "+peerlist + ")";
		
		return(str);
	}
	
	/**
	 * Updates maxcounter entries in Version Vector table with the entries in this version vector.
	 */
	public void updateDBVersionVector() throws SQLException{
		if(dbConn == null)
			throw new IllegalStateException("Database Connection not set");
		
		for(String site : siteSet()){
			DBUtility dbUtil = new DBUtility(dbConn);
			
			ResultSet counterRS = dbConn.executeQuery("SELECT maxcounter FROM versionvector WHERE peername = '"+site+"'");
			
			PreparedStatement ps = null;
			
			final List<String> column = Arrays.asList("maxcounter");
			final String key = "peername"; 
			
			int maxcounter = 0;
			
			if(!counterRS.next())
				ps = dbUtil.prepareInsertStatement("versionvector", column, key);
			else{
				ps = dbUtil.prepareUpdateStatement("versionvector", column, key);
				maxcounter = counterRS.getInt("maxcounter");
			}
			
			int counter = getCounter(site);
			
			ps.setInt(1, Math.max(counter, maxcounter));
			ps.setString(2, site);
			ps.executeUpdate();
		}
	}
	
	public void versionChangeUnit(String cuentityid) throws SQLException{
		if(dbConn == null)
			throw new IllegalStateException("Database Connection not set");
		
		DBUtility dbUtil = new DBUtility(dbConn);
		
		final List<String> columns = Arrays.asList("peername", "counter");
		
		for(Map.Entry<String, Integer> versionEntry : versionVector.entrySet()){
			String peername = versionEntry.getKey();
			int counter = versionEntry.getValue();
			
			PreparedStatement ps = null;
			
			if(dbUtil.selectCount("FROM data_versions WHERE cuentityid = '"+cuentityid+"' AND peername = '"+peername+"'") == 0)
				ps = dbUtil.prepareInsertStatement("data_versions", columns, "cuentityid");
			else
				ps = dbUtil.prepareUpdateStatement("data_versions", columns, "cuentityid");
			
			ps.setString(1, peername);
			ps.setInt(2, counter);
			ps.setString(3, cuentityid);
			
			ps.executeUpdate();
		}
		
		updateDBVersionVector();
	}
}
