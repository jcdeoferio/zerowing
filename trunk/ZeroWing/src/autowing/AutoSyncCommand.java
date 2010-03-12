package autowing;

public class AutoSyncCommand {
	private int time;
	static double scale = 1;
	static void setScale(double newScale){
		scale = newScale;
		
		if(scale < 1 )scale = 1;
	}
	public AutoSyncCommand(int time){
		this.time = time;
	}
	
	public void doCommand(AutoSync as){
		as.sync();
	}
	public int getTime(){
		return (int)(time*scale);
	}
	public String toString(){
		return "AutoSyncCommand "+time;
	}
}


// AutoSyncWait asw = New AutoSyncCommand();
// asw.someMethodInAutoSyncWait();