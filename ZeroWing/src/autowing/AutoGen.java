package autowing;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Scanner;

import client.Client;

/**
 * Still lacks body to manipulate database.
 * Code may be similar to Client dabatase access.
 * 
 * JC? heehee.
 * @author kevinzana
 *
 */
public class AutoGen {
	int currentTime;
	LinkedList<AutoGenCommand> commands;
	Client c;
	Scanner scan;
	boolean done;
	private AutoGen(Client c){
		this.c = c;
		commands = new LinkedList<AutoGenCommand>();
		done = false;
		
	}
	
	
	public static AutoGen getAutoGen(Client c){
		AutoGen ag = new AutoGen(c);
		try {
			ag.scan = new Scanner(new File(c.getName()+"DBSource.txt"));
			c.displayln("[AutoGen]: DBSource found.");
			return ag;
		} catch (FileNotFoundException e) {
			c.displayln("[AutoGen]: ERROR "+e.getLocalizedMessage());
			c.displayln("[AutoGen]: Compensating for lack of DBSource.txt ");
			return new AutoGen(c);
		}
		
	}
	public boolean reduceTime(){
		if(done)return false;
		if(!scan.hasNext()){
			done = true;
			return false;
		}
		if(currentTime == 0){
			String s = scan.nextLine();
			Scanner y = new Scanner(s);
			int length = y.nextInt();
			String command = y.nextLine().substring(1);
			currentTime = length;
			try {
				c.getDatabase().getDBConnection().executeUpdate(command);
				c.displayln("[AutoGen]: Successfully inserted "+command);
			} catch (SQLException e) {
				c.displayln("[AutoGen]: ERROR: "+e.getLocalizedMessage());
			}
		} else {
			currentTime--;
		}
		return false;
	}
	
	public boolean reduceTimeOld(){
		if(commands.size()==0)currentTime=40; //WARNING RANDOM ARBITRARY NUMBER
		if(currentTime==0){
			AutoGenCommand ag = commands.removeFirst();
			doCommand(ag);
			commands.addLast(ag);
			currentTime = commands.getFirst().getTime();
			return true;
		} else {
			currentTime--;
		}
		return false;
	}
	private void doCommand(AutoGenCommand asc){
		asc.doCommand(this);
	}
}
