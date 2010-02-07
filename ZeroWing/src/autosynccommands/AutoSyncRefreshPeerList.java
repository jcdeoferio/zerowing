package autosynccommands;

import autowing.AutoSync;
import autowing.AutoSyncCommand;

public class AutoSyncRefreshPeerList extends AutoSyncCommand {
	public AutoSyncRefreshPeerList(int time) {
		super(time);
	}

	public void doCommand(AutoSync as){
		as.refreshPeerList();
	}
	public String toString(){
		return "refreshPeerList";
	}
}
