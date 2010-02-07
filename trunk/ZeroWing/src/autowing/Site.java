package autowing;

import java.util.LinkedList;

import client.Client;

public class Site{
	AutoSync as;
	AutoGen ag;
	String commandString;
	private Site(Client c){
		as = AutoSync.getAutoSync(c);
		ag = AutoGen.getAutoGen(c);
	}
	/**
	 * Construction also requires DB Connection info for AutoGen. Right now it contains only
	 * information for operating AutoSync.
	 * @return
	 */
	public static Site getSite(Client c){
		return new Site(c);
	}
	public static Site getSite(Client c, LinkedList<AutoSyncCommand> comms){
		Site s = new Site(c);
		s.as = AutoSync.getAutoSync(c, comms);
		return s;
	}
	public static Site getSite(Client c, String commandString){
		Site s = new Site(c);
		s.commandString = commandString;
//		s.as = AutoSync.getAutoSync(c, comms);
		
		return s;
	}
	public void setCommands(LinkedList<AutoSyncCommand> comms){
		this.as = AutoSync.getAutoSync(this.as.c, comms);
	}
	
	public String getCommands(){
		return commandString;
	}

	
	/**
	 * It may be a good idea to randomize who gets to go first (in the case of
	 * simultaneous operations)
	 */
	public void tickTimer(int tick){
		boolean syncWorked = ag.reduceTime();
		boolean genWorked = as.reduceTime();
		if(syncWorked || genWorked) System.out.println(" ============ [Site]: "+tick+" done");
	}
	
	public String toString(){
		return as.toString()+" "+ag.toString();
	}
	public String commandList(){
		String s = "";
		for(AutoSyncCommand asc: as.commands){
			s += asc.toString()+" ";
		}
		return s;
	}
}
