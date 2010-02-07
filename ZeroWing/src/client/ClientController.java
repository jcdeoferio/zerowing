package client;

import java.util.Scanner;

import util.ConsoleController;
import util.ConsoleSystem;

public class ClientController extends Thread implements ConsoleController{
	ConsoleSystem cs;
	
	public ClientController(ConsoleSystem cs){
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
		System.out.println("[ClientController]: Console system enabled.");
		Scanner scan = new Scanner(System.in);
		while(true){
			receivedMessage(scan.nextLine());
		}
	}



}
