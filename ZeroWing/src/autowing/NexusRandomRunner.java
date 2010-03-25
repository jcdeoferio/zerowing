package autowing;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import util.DBUtility;

import mechanic.DBConnection;
import mechanic.Database;

import autosynccommands.AutoSyncAddPeer;
import autosynccommands.AutoSyncSync;

import client.AddressPort;

public class NexusRandomRunner extends NexusRunner {
	/**
	 * needs generator, needs one-method calls to
	 * 
	 * @param sites
	 * @param delay
	 */
	HashMap<String, AddressPort> conns;
	int inserts;
	int syncs;
	int updates;
	int deletes;
	int siteSize;
	
	int time = 1;
	AutoSyncSync sync = new AutoSyncSync(time);

	public NexusRandomRunner(Site[] sites, long delay, 
			int inserts, int syncs,
			int updates, int deletes) {
		super(sites, delay);
		this.inserts = inserts;
		this.syncs = syncs;
		this.updates = updates;
		this.deletes = deletes;
		this.siteSize = sites.length;
	}

	public void run() {
		log("NexusRandomRunner started");
		
		if(inserts > 0){
			prelimInserts();
			
			insertRun(inserts);
			syncRun(syncs);
		}
		
		if(updates > 0){
			updateRun(updates);
			syncRun(syncs);
		}
		
		if(deletes > 0){
			deleteRun(deletes);
			syncRun(syncs);
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		log("NexusRandomRunner ended");
		
		
	}
	
	private void prelimInserts(){
		int root = 1;
		int mid = 1;
		int least = 2;
		
		int ind = 0;
		
		for(int i=0;i<root; i++){
			Site targ = sites[i];
			DBConnection dbConn = getDBConn(targ);
			prelimRoot(targ, dbConn);
		}
		ind = ind+root;
		for(int i=0;i<mid;i++){
			Site targ = sites[ind+i];
			DBConnection dbConn = getDBConn(targ);
			prelimMid(targ, dbConn);
		}
		ind = ind+mid;
		for(int i=0;i<least; i++){
			Site targ = sites[ind+i];
			DBConnection dbConn = getDBConn(targ);
			prelimLeast(targ, dbConn);
		}
		
	}
	
	 //TODO : fix preliminary data insertion;
	private void prelimRoot(Site targ, DBConnection dbConn){
		
	}
	private void prelimMid(Site targ, DBConnection dbConn){
		setMaxValue(targ.getDatabase(), 7000);
	}
	private void prelimLeast(Site targ, DBConnection dbConn){
		setMaxValue(targ.getDatabase(), 4000);
	}
	
	private void setMaxValue(Database db, int maxVal){
		for(String table : new String[]{"A", "B"}){
			String cuname = "cu_"+table;
			
			String filterStr = "";
			for(String colNum : new String[]{"1", "2"}){
				String column = table+"_column"+table+colNum;

				filterStr += column+" < "+maxVal+" AND ";
			}
			
			filterStr = filterStr.substring(0, filterStr.length()-5); //5 length of dangling ' AND ';
			
			try {
				db.setFilterForCU(cuname, filterStr);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void syncRun(int syncs){
		for(int i=syncs; i>0 ; i--){
//			Site a = getRandomSite();
//			Site b = getRandomSite();
			Site[] ss = get2RandomSites();
			twoWaySync(ss[0],ss[1]);
			System.out.println("SYNC#"+i);
		}
	}
	
	private void insertRun(int inserts){
		for(int i=inserts; i>0; i--){
			Site targ = getRandomSite();
			DBConnection dbConn = getDBConn(targ);
			String command = generateInsertCommand(dbConn);
			
			insert(command, targ, dbConn);
		}
	}
	private void updateRun(int updates){
		for(int i=updates; i>0 ; i--){
			Site targ = getRandomSite();
			DBConnection dbConn = getDBConn(targ);
			List<String> commands = generateUpdateCommand(dbConn);
			
			update(commands, targ, dbConn);
		}
	}
	
	private void deleteRun(int deletes){
		for(int i=deletes; i>0 ; i--){
			Site targ = getRandomSite();
			DBConnection dbConn = getDBConn(targ);
			String command = generateDeleteCommand(dbConn);
			
			delete(command, targ, dbConn);
		}
	}
	
	
	private void twoWaySync(Site a, Site b){
		sync(a,b);
		sync(b,a);
	}
	
	private void sync(Site source, Site target){
		AutoSyncAddPeer ap = getAddPeerCommand(source);
		target.as.doCommand(ap);
		target.as.doCommand(sync);
	}
	
	//TODO : insert code
	private void insert(String command, Site target, DBConnection db){
		try {
			db.executeUpdate(command);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
		
	//TODO : update code
	private void update(List<String> commands, Site target, DBConnection db){
		try {
			for(String command : commands)
				db.executeUpdate(command);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//TODO : delete code
	private void delete(String command, Site target, DBConnection db){
		
	}
	
	private int randInt(int maxVal){
		Random rand = new Random();
		return(rand.nextInt(maxVal));
	}
	
	private String generateInsertCommand(DBConnection dbConn){
		int randVal1 = randInt(10000);
		int randVal2 = randInt(10000);
		
		String table = null;
		
		if(randInt(10) % 2 == 0)
			table = "A";
		else
			table = "B";
		
		return("INSERT INTO "+table+" (column"+table+"1, column"+table+"2) VALUES ("+randVal1+", "+randVal2+")");
	}
	
	private List<String> generateUpdateCommand(DBConnection dbConn){
		DBUtility dbUtil = new DBUtility(dbConn);
		
		int randVal = randInt(10000);
		
		String table = null;
		
		if(randInt(10) % 2 == 0)
			table = "A";
		else
			table = "B";
		
		String column = "column"+table;
		
		if(randInt(10) % 2 == 0)
			column += "1";
		else
			column += "2";
		
		int rowCount = -1;
		try {
			rowCount = dbUtil.selectCount("FROM "+table);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		int randOffset = randInt(rowCount);
		
		final String createTempTable = "CREATE TEMPORARY TABLE temp (col integer);";
		final String populateTempTable = "INSERT INTO temp (col) VALUES ((SELECT "+column+" FROM "+table+" LIMIT 1 OFFSET "+randOffset+"));";
		final String updateStmt = "UPDATE "+table+" SET "+column+" = "+randVal+" WHERE "+column+" = (SELECT col FROM temp ORDER BY col LIMIT 1);";
		final String dropTempTable = "DROP TABLE temp;";
		
		List<String> statements = new LinkedList<String>();
		statements.add(createTempTable);
		statements.add(populateTempTable);
		statements.add(updateStmt);
		statements.add(dropTempTable);
		
		return(statements);
	}
	
	private String generateDeleteCommand(DBConnection dbConn){
		return "woo random";
	}
	
	
	private Site getRandomSite(){
//		int i = (int) (Math.random()*siteSize);
		Random r = new Random();
		int i = r.nextInt(siteSize);
		
		
		return sites[i];
	}
	private Site[] get2RandomSites(){
		Random r = new Random();
		
		int i,j;
		i = j = 0;
		while(i==j){
			i= r.nextInt(siteSize);
			j = r.nextInt(siteSize);
		}
		return new Site[] {sites[i], sites[j]};
	}
	
	private DBConnection getDBConn(Site s){
		DBConnection db = s.as.c.getDatabase().getDBConnection();
		return db;
	}
	
	private AutoSyncAddPeer getAddPeerCommand(Site s){
		AutoSyncAddPeer ap = new AutoSyncAddPeer(
				time, 
				conns.get(s.as.c.getName())
		);
		
		return ap;
	}

	/**
	 * Should print out data to a file to save lalala.
	 * 
	 * @param s
	 */
	private void log(String s) {
		System.out.println(s);
		
		//TODO: change code to print out to a file.
	}

	public void setAddMap(HashMap<String, AddressPort> adds) {
		this.conns = adds;
	}

}
