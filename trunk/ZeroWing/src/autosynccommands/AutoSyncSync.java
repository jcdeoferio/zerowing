package autosynccommands;

import autowing.AutoSync;
import autowing.AutoSyncCommand;

public class AutoSyncSync extends AutoSyncCommand {
	public AutoSyncSync(int time) {
		super(time);
	}

	public void doCommand(AutoSync as){
		as.sync();
	}
	public String toString(){
		return "sync";
	}
}
