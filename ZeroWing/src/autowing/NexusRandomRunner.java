package autowing;

import java.util.HashMap;

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

	public NexusRandomRunner(Site[] sites, long delay, 
			int inserts, int syncs,
			int updates, int deletes) {
		super(sites, delay);
		this.inserts = inserts;
		this.syncs = syncs;
		this.updates = updates;
		this.deletes = deletes;
	}

	public void run() {
		log("NexusRandomRunner started");
		
		
	}
	
	private void insert(String command, Site target){
		
	}
	
	private void sync(Site source, Site target){
		
	}
	
	private void update(String comand, Site target){
		
	}
	
	private void delete(String command, Site target){
		
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
