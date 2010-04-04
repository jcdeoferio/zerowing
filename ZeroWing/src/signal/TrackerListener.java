package signal;

import java.io.IOException;

import client.AddressPort;
import client.Client;

public class TrackerListener extends Thread {
	boolean running = false;
	Client c;

	public TrackerListener(Client c) {
		this.c = c;
	}

	public void run() {
		try {
			c.displayln("[requestPeerList]: Waiting for peers from tracker.");
			running = true;
			while (true) {
				String s = c.tracker.hardListen();
				c.displayln("[From - Tracker]: " + s);
				AddressPort ap = parse(s);
				if(ap!=null){
					c.addPeerViaAddressPort(ap);
				}
			}
		} catch (IOException e) {
			running = false;
			c.displayln("[requestPeerList]: send exception "
					+ e.getLocalizedMessage());
		}
	}

	public boolean isRunning() {
		return running;
	}

	public AddressPort parse(String msg) {
		String[] data = msg.split(" ");
		String candidateName = data[0];
		String address = data[1];
		if(data.length==2){
			if( (data[0].equals("/login")) ){
				if(data[1].equals("success")){
					c.displayln("[TrackerListener]: login success");
					return null;
				} else if(data[1].equals("denied")){
					c.displayln("[TrackerListener]: login denied");
					return null;
				}
				
			}
		}
		try {
			int i = 0;
			for(String s: data){
				c.display("["+i+"]"+s);
				i++;
			}c.displayln("");
			int port = Integer.parseInt(data[2]);
			AddressPort ap = new AddressPort(candidateName, address, port);
			c.displayln(ap.toString());
			return ap;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}

	}
}
