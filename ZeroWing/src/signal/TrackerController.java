package signal;

import java.util.Scanner;

import util.ConsoleController;
import util.ConsoleSystem;

public class TrackerController extends Thread implements ConsoleController{
	ConsoleSystem cs;
	
	public TrackerController(ConsoleSystem cs){
		this.cs = cs;
	}
	
	public void startConsole(ConsoleSystem cs) {
		this.start();
	}
	
	public void receivedMessage(String msg) {
		cs.printMessage(msg);
		cs.actionMessage(msg);
	}

	public void run() {
		System.out.println("Tracker Controller Started.");
		Scanner scan = new Scanner(System.in);
		while(true){
			receivedMessage(scan.nextLine());
		}
	}



}
