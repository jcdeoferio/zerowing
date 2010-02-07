package signal;

import java.io.IOException;

public class MessageWaiter extends Thread{
	SimpleConnection simpleConnection;
	ServerConnection serverConnection;
	public MessageWaiter(ServerConnection serverConnection,
			SimpleConnection simpleConnection){
		this.serverConnection = serverConnection;
		this.simpleConnection = simpleConnection;
	}
	
	public void run(){
		String msg;
		while(simpleConnection.connected){
			try {
				msg = simpleConnection.hardListen();
				if(msg==null){
					if(simpleConnection instanceof IdentifiableSimpleConnection){
						String name = ((IdentifiableSimpleConnection)simpleConnection).toString();
						System.out.println("[MessageWaiter]: "+name);
					}
					System.out.println("[MessageWaiter]: null msg received.");
					throw(new IOException());
				} else if(msg.trim().equals("null")){
					System.out.println("[MessageWaiter]: null msg received.");
					throw(new IOException());
				}
				serverConnection.messageReceived(simpleConnection, msg);
			} catch (IOException e) {
//				e.printStackTrace();
				System.out.println("[MessageWaiter]: "+e.getMessage());
				serverConnection.lostSignal(simpleConnection);
				simpleConnection.connected = false;
			}
		}
		serverConnection.lostSignal(simpleConnection);
		System.out.println("[MessageWaiter]: terminated.");
	}
}
