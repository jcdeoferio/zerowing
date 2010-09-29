package test.netfree.modules;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;

import util.ParenScanner;
import util.Utility;

import mechanic.Database;
import mechanic.Filter;
import mechanic.VersionVector;

/**
 * Contains all necessary encode-decode for the Zero Wing protocol. Which is awesome.
 * At the moment, it will only contain encode() and decode() methods. 
 * @author kevinzana
 *
 */
public class ZeroWingTestModem {
	PrintStream logger = null;	
	boolean print = false;
	public ZeroWingTestModem(boolean print){
		this.print = print;
	}
	public ZeroWingTestModem(PrintStream logger, boolean print){
		this.logger = logger; 
		this.print = print;
	}
	public List<String> getUpdateList(Node getter, Node giver){
		String updateRequest = constructUpdateRequest(getter.db);
		return getUpdateList(giver, updateRequest);
	}
	public List<String> getUpdateList(
			Node node, String updateRequest){
		displayln("[getUpdateList]=================================");
		displayln("[getUpdateList]:"+node.getPeerName());
		displayln("[getUpdateList](request):"+updateRequest);
		if(logger!=null)
			logger.println("(request):"+updateRequest);
		
		Database db = node.db;
		Filter filter;
		VersionVector vv;
		ParenScanner psc;
		
		String filterStr;
		String versionVectorStr;

		List<String> updates = null;

		psc = new ParenScanner(updateRequest);
		filterStr = psc.next();
		versionVectorStr = psc.next();
		displayln("    Using filter ["+filterStr+"]");
		displayln("    Using vv ["+versionVectorStr+"]");
		try {
			filter = new Filter(Utility.decode(filterStr));
			vv = new VersionVector(Utility.decode(versionVectorStr));
			updates = db.getUpdates(filter, vv);
		} catch (SQLException e) {
			displayln("[EXCEPTION]: "+e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		
		return updates;
	}
	public String constructUpdateRequest(Database db) {
		try {
			return ("("+Utility.encode(db.filterString())+")("+Utility.encode(db.versionString())+")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return("");
	}
	
	public void displayln(String msg){
		if(print)
			System.out.println("[ZeroWingTestModem]"+msg);
		
	}
}
