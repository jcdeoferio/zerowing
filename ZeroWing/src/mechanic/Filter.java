package mechanic;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import util.DBUtility;
import util.ParenScanner;

public class Filter {
	
	private static Object[] parseFilterEntryStr(String filterEntry){
		int colonDex = filterEntry.indexOf(':');
		String cuListStr = filterEntry.substring(0, colonDex);
		String whereClause = filterEntry.substring(colonDex+1);
		
		Set<String> cuSet = new HashSet<String>();
		String[] cuList = cuListStr.split(",");
		for(String cu : cuList)
			cuSet.add(cu);
		
		return(new Object[]{cuSet, whereClause});
	}

	@SuppressWarnings("unchecked")
	private static Object[] parseFilterStr(String filterStr){
		HashMap<Set<String>, String> filter = new HashMap<Set<String>, String>();
		HashSet<String> allCUSet = new HashSet<String>();
		
		ParenScanner parenSc = new ParenScanner(filterStr);
		
		while(parenSc.hasNext()){
			String filterEntry = parenSc.next();
			
			Object[] filterEntryReturn = parseFilterEntryStr(filterEntry);
			
			Set<String> cuSet = (Set<String>)filterEntryReturn[0]; //unchecked type cast was here
			String whereClause = (String)filterEntryReturn[1];
			
			filter.put(cuSet, whereClause);
			allCUSet.addAll(cuSet);
		}

		return(new Object[]{filter, allCUSet});
	}
	
	private DBConnection dbConn;
	private DBUtility dbUtil;
	private HashMap<Set<String>, String> filter;
	private HashSet<String> cuSet;
	private HashSet<String> tabSet;
	
	@SuppressWarnings("unchecked")
	public Filter(String filterStr, DBConnection dbConn) throws SQLException{
		Object[] parseReturn = parseFilterStr(filterStr);
		//unchecked type casts below
		filter = (HashMap<Set<String>, String>)parseReturn[0];
		cuSet = (HashSet<String>)parseReturn[1];
		this.dbConn = dbConn;
		this.dbUtil = new DBUtility(dbConn);
		
		tabSet = generateTabSet();
	}
	
	private HashSet<String> generateTabSet() throws SQLException {
		HashSet<String> tabSet = new HashSet<String>();
		
		for(String cuname : cuSet)
			tabSet.addAll(dbUtil.tablesByChangeUnit(cuname));
			
		return(tabSet);
	}

	public HashSet<String> getChangeUnits(){
		return(cuSet);
	}
	
	public HashSet<String> getTables(){
		return(tabSet);
	}
	
	//methods regarding tables involved
	//methods regarding change units involved
}
