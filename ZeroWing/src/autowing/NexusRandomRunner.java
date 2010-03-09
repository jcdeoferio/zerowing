package autowing;

import java.util.HashMap;

import mechanic.DBConnection;

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
		
		insertRun(inserts);
		syncRun(syncs);
		
		updateRun(updates);
		syncRun(syncs);
		
		deleteRun(deletes);
		syncRun(syncs);
		
		log("NexusRandomRunner ended");
	}
	
	private void prelimInserts(){
		int root = 1;
		int mid = 3;
		int least = 6;
		
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
		
	}
	private void prelimLeast(Site targ, DBConnection dbConn){
		
	}

	private void syncRun(int syncs){
		for(int i=syncs; i>-1 ; i--){
			Site a = getRandomSite();
			Site b = getRandomSite();
			twoWaySync(a,b);
		}
	}
	
	private void insertRun(int inserts){
		for(int i=inserts; i>-1; i--){
			Site targ = getRandomSite();
			DBConnection dbConn = getDBConn(targ);
			String command = generateInsertCommand(dbConn);
			
			insert(command, targ, dbConn);
		}
	}
	private void updateRun(int updates){
		for(int i=updates; i>-1 ; i--){
			Site targ = getRandomSite();
			DBConnection dbConn = getDBConn(targ);
			String command = generateUpdateCommand(dbConn);
			
			update(command, targ, dbConn);
		}
	}
	
	private void deleteRun(int deletes){
		for(int i=deletes; i>-1 ; i--){
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
				
	}
		
	//TODO : update code
	private void update(String comand, Site target, DBConnection db){
		
	}
	
	//TODO : delete code
	private void delete(String command, Site target, DBConnection db){
		
	}
	
	private String generateInsertCommand(DBConnection dbConn){
		return "woo random";
	}
	
	private String generateUpdateCommand(DBConnection dbConn){
		return "woo random";
	}
	
	private String generateDeleteCommand(DBConnection dbConn){
		return "woo random";
	}
	
	
	private Site getRandomSite(){
		int i = (int) (Math.random()*siteSize);
		return sites[i];
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
