package util;

import java.io.IOException;

import signal.IdentifiableSimpleConnection;
import signal.SimpleConnection;

@SuppressWarnings("serial")
public class SimpleClient extends SimpleUI {
	String name;
	int serverPort;
	int port;
	SimpleConnection sc;
	
	public static void main(String[] args){
		new SimpleClient("test", 8888); 
	}
	
	
	public SimpleClient(String name, int port) {
		super(name);
		
		this.port = port;
		
		connect(port);
	}
	public void onAction(String str){
		displayln("client: "+str);
		action(str);
	}
	public void displayln(String s){
		display(s+"\n");
	}
	private void connect(int port){
		if(sc == null){
			sc = new IdentifiableSimpleConnection("0.0.0.0", port,"SimpleClient");
			
			try {
				sc.connect(500);
				displayln("connection success: "+sc.toString());
			} catch (IOException e) {
				e.printStackTrace();
				displayln("Error: WAENOTSOCKLOL");
				sc = null;
			}
		} else{
			displayln("Connected to "+sc.toString());
		}
	}

	private synchronized void action(String s){
		String[] comm = s.trim().split(" ");
		if(s.trim().equals("/connect")){
			connect(port);
		} else if(comm.length>1){
			if(comm[0].equals("/send")){
				try {
					sc.hardSend(s.substring(comm[0].length()+1));
				} catch (IOException e) {
					e.printStackTrace();
					try {
						sc.disconnect();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			} else if(comm[0].equals("login") && comm.length==3){
				sc.sendMessage(s);
				String login = sc.getMessage();
				if(login.equals("/login success")){
					this.name = comm[2];
					this.serverPort = Integer.parseInt(comm[1]);
					this.setTitle("Connected: "+name+" "+serverPort);
					
					displayln("success: "+this.name+" "+this.serverPort);
				}
			}
		}
	}
	
}




























