package autosynccommands;

import java.sql.SQLException;

import autowing.AutoSync;
import autowing.AutoSyncCommand;

public class AutoSyncStartClient extends AutoSyncCommand {
	public AutoSyncStartClient(int time) {
		super(time);
	}

	public void doCommand(AutoSync as){
		try {
			as.startClient();
		} catch (SQLException e) {
			System.out.println(as.getClientInfo() + " Error on startClient() call");
			e.printStackTrace();
		}
	}
	public String toString(){
		return "startClient";
	}
}
