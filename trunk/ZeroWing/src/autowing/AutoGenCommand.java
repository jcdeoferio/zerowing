package autowing;

public class AutoGenCommand {
	int time;
	static double scale = 1;
	static void setScale(double newScale){
		scale = newScale;
		
		if(scale<1)scale = 1;
	}
	public void doCommand(AutoGen ag){
		
	}
	public int getTime(){
		return (int)scale*time;
	}
}
