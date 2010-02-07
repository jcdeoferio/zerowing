package autosynccommands;

import autowing.AutoSync;
import autowing.AutoSyncCommand;

public class AutoSyncWait extends AutoSyncCommand {
	public AutoSyncWait(int time) {
		super(time);
	}
	public void doCommand(AutoSync as){
		
	}
	public String toString(){
		return "wait";
	}
}
