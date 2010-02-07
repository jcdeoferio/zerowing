package autosynccommands;

import client.AddressPort;
import autowing.AutoSync;
import autowing.AutoSyncCommand;

public class AutoSyncAddPeer extends AutoSyncCommand {
	String ip;
	int port;
	public AutoSyncAddPeer(int time, String ip, int port) {
		super(time);
		this.ip = ip;
		this.port = port;
	}
	public AutoSyncAddPeer(int time, AddressPort ap){
		super(time);
		this.ip = ap.getAddress();
		this.port = ap.getPort();
	}

	public void doCommand(AutoSync as){
		as.addPeer(ip, port);
	}
	public String toString(){
		return "addPeer("+ip+" "+port+")";
	}
}
