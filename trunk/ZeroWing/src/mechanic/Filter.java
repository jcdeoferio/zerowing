package mechanic;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import util.DBUtility;
import util.Utility;

public class Filter {
		
	private DBConnection dbConn;
	private DBUtility dbUtil;
	private HashMap<String, String> filter;
	private HashSet<String> cuSet;
	private HashSet<String> tabSet;
	
	private static String NO_CONN = "Connection not set";
	public static String FILTER_VAR_NAME = "filter";
	
	public Filter(){
		filter = new HashMap<String, String>();
		cuSet = new HashSet<String>();
		tabSet = new HashSet<String>();
	}
	
	
	public Filter(String filterStr) throws SQLException{
		setupFieldsByFilterStr(filterStr);
	}
	
	public Filter(DBConnection dbConn) throws SQLException{
		setDBConnection(dbConn);
		
		String filterStr = dbUtil.getVariable(FILTER_VAR_NAME);
		
		if(filterStr == null){
			System.out.println("NO VAR!");
			filterStr = "";
		}
		else{
			System.out.println("HAS VAR!");
		}
		
		setupFieldsByFilterStr(filterStr);
	}
	
	public void setDBConnection(DBConnection dbConn){
		this.dbConn = dbConn;
		this.dbUtil = new DBUtility(dbConn);
	}
	
	@SuppressWarnings("unchecked")
	private void setupFieldsByFilterStr(String filterStr) throws SQLException{
		Object[] parseReturn = parseFilterStr(filterStr);
		//unchecked type casts below
		filter = (HashMap<String, String>)parseReturn[0];
		cuSet = (HashSet<String>)parseReturn[1];		
		//tabSet = generateTabSet(); //TODO:evaluate use
		tabSet = null;
	}
	
	private HashSet<String> generateTabSet() throws SQLException {
		if(dbConn == null)
			throw new IllegalStateException(NO_CONN);
		
		HashSet<String> tabSet = new HashSet<String>();
		
		for(String cuname : cuSet)
			tabSet.addAll(dbUtil.tablesByChangeUnit(cuname));
			
		return(tabSet);
	}

	public HashSet<String> getChangeUnits(){
		return(new HashSet<String>(cuSet));
	}
	
	public HashSet<String> getTables(){
		return(new HashSet<String>(tabSet));
	}
	
	public String getForCU(String cuname){
		if(filter.containsKey(cuname)){
			System.out.println("GETFORCU:"+cuname+":"+(filter.get(cuname)));
			return(filter.get(cuname));
		}
		else
			return("TRUE");
	}
	
	public void setForCU(String cuname, String filterStr){
		filter.put(cuname, filterStr);
	}
	
	//cuname1:where1 cuname2:where2 ...
	public String toString(){
		String filterStr = "";
		
		for(Map.Entry<String, String> filterEntry : filter.entrySet()){
			String cuname = Utility.encode(filterEntry.getKey());
			String whereClause = Utility.encode(filterEntry.getValue());
			
			String filterEntryStr = cuname+":"+whereClause;
			filterStr += filterEntryStr+" ";
		}
		
		return(filterStr.trim());
	}
	
	public void saveToDB() throws SQLException{
		dbUtil.putVariable(FILTER_VAR_NAME, this.toString());
	}
	
	//methods regarding tables involved
	//methods regarding change units involved
	
	//filterEntry: Utility.encode(cuname):Utility.encode(whereClause)
	private static Object[] parseFilterEntryStr(String filterEntry){
		int colonDex = filterEntry.indexOf(':');
		String cuname = Utility.decode(filterEntry.substring(0, colonDex));
		String whereClause = Utility.decode(filterEntry.substring(colonDex+1));
		
		return(new Object[]{cuname, whereClause});
	}

	//filterStr: filterEntry filterEntry ...
	private static Object[] parseFilterStr(String filterStr){
		HashMap<String, String> filter = new HashMap<String, String>();
		HashSet<String> allCUSet = new HashSet<String>();
		
		String[] filterEntries = filterStr.split(" ");
		
		try{
			for(String filterEntry : filterEntries){
				Object[] filterEntryReturn = parseFilterEntryStr(filterEntry);
				
				String cuname = (String)filterEntryReturn[0];
				String whereClause = (String)filterEntryReturn[1];
				
				filter.put(cuname, whereClause);
				allCUSet.add(cuname);
			}
		}
		catch(StringIndexOutOfBoundsException e){}
		
		return(new Object[]{filter, allCUSet});
	}

}
