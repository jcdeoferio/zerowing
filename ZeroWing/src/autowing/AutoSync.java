package autowing;

import java.sql.SQLException;
import java.util.LinkedList;

import client.Client;

import operator.ClientInterface;

/**
 * AutoSync enables and disables its corresponding Zero Wing element.
 * Requires a pre-made Client object, i.e., 
 * 
 * @author charmander, bulbasaur
 *
 */
public class AutoSync implements ClientInterface{
	Client c;
	int currentTime;
	LinkedList<AutoSyncCommand> commands;
	private AutoSync(Client c){
		this.c = c;
	}
	
	public static AutoSync getAutoSync(Client c){
		AutoSync as = new AutoSync(c);
		return as;
	}
	public static AutoSync getAutoSync(Client c, LinkedList<AutoSyncCommand> comms){
		AutoSync as = new AutoSync(c);
		as.commands = comms;
		return as;
	}
	
	public boolean reduceTime(){
		if(commands.size()==0)currentTime=40; //WARNING RANDOM ARBITRARY NUMBER
		if(currentTime==0){
			AutoSyncCommand asc = commands.removeFirst();
			System.out.println("[AutoSync]: ("+c.getName()+") "+asc);
			doCommand(asc);
			commands.addLast(asc);
			currentTime = commands.getFirst().getTime();
			return true;
		} else {
			currentTime--;
		}
		return false;
	}
	private void doCommand(AutoSyncCommand asc){
		asc.doCommand(this);
	}
	public void addCommand(AutoSyncCommand asc){
		commands.add(asc);
	}
	public String getClientInfo(){
		return c.getName()+" DB:"+c.getDBName()+" User:"+c.getDBUser();
	}

// =============================  INTERFACE CODE =====================================
	public void connectToTracker() {
		c.loginTracker();
	}

	public void refreshPeerList() {
		c.requestPeerList();
		
	}

	public void startClient() throws SQLException {
		// TODO Auto-generated method stub
		c.startClient();
	}

	public void sync() {
		// TODO Auto-generated method stub
		c.ask();
	}

	//no need to call this as refreshpeerlist should add the peers.
	public void addPeer(String ip, int port) {
		c.addPeerManual(ip, port);
		refreshPeerList();
	}
//	======================= END INTERFACE CODE =======================================
}
