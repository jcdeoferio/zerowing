package autosynccommands;

import autowing.AutoSync;
import autowing.AutoSyncCommand;

public class AutoSyncConnectToTracker extends AutoSyncCommand{
	public AutoSyncConnectToTracker(int time) {
		super(time);
	}

	public void doCommand(AutoSync as){
		as.connectToTracker();
	}
	public String toString(){
		return "connectToTracker";
	}
}
