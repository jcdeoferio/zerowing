package client;

import java.util.Scanner;

import signal.ServerConnection;
import signal.SimpleConnection;

public class PeerServer extends ServerConnection{
	Client client;
	public PeerServer(int port, Client client) {
		super(port, client.getName());
		this.client = client;
	}
	public void lostSignal(SimpleConnection sc){

	}
	public void gotSignal(SimpleConnection sc){

	}
	public void messageReceived(SimpleConnection sc, String msg){
		client.displayln("Incoming message from ["+sc+"]");
		if(sc.equals(client.tracker)){
			client.displayln("[PeerServer.messageReceived] Tracker: "+msg);
		} else {
			client.displayln("[PeerServer.messageReceived] Peer: "+msg);
		}
		String[] str = extractCommand(msg);
		client.displayln("[PeerServer.messageReceived]: ("+str[0]+") "+str[1] );
		client.executeCommand(str[0],str[1],sc);
	}
	private String[] extractCommand(String message){
		Scanner scan = new Scanner(message);
		String[] str = new String[2];
		
		str[0] =scan.next();
		if(message.length()==str[0].length()){
			str[1] = "";
			return str;
		}
		str[1] = message.substring(str[0].length()+1);
		
		return str;
	}
	
	public void waitingTerminated(){
		client.displayln("[PeerServer]: terminated waiting");
//		client.serving = false;
	}
	
}


// old lostSignal Code
//client.displayln("[PeerServer.lostSignal]: "+sc+" vs "+client.incomingPeer);
//if(sc==client.incomingPeer){
//	synchronized(client.busyControl){
//		client.busy = null;
//		client.incomingPeer = null;
//	}
//}else {
//	try {
//		sc.hardSend(null);
//		sc.disconnect();
//	} catch (IOException e) {
//		e.printStackTrace();
//	}
//}

// old gotSignal Code
//client.displayln("[PeerServer.gotSignal]: "+sc);
//if(client.busy!=null || client.incomingPeer!=null){
//	if(client.incomingPeer!=null)
//		client.displayln("[PeerServer.gotSignal]: losing due to incomingPeer != null");
//	else client.displayln("[PeerServer.gotSignal]: lost due to client.busy != null");
//	lostSignal(sc);
//	
//	return;
//}
//synchronized(client.busyControl){
//	if(client.busy==null){
//		client.displayln("[PeerServer.gotSignal], busyControl: busyAssignment"+sc);
//		client.busy = sc.toString();
//		client.incomingPeer = sc;
//	}else {
//		client.displayln("[PeerServer.gotSignal], busyControl :  currently busy, drop "+sc);
//		lostSignal(sc);
//		try {
//			sc.disconnect();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//}

//old messageReceived Code
//client.displayln("[PeerServer.messageReceived]: "+msg+sc);
//if(sc==client.incomingPeer){
//	client.displayln("[PeerServer.messageReceived]: client is currentPeer.");
//	try {
//		client.sendUpdate(sc, msg);
//		client.displayln("[PeerServer.messageReceived]: sendUpdate done.");
//		sc.disconnect();
//		client.busy = null;
//	} catch (IOException e) {
//		client.displayln("[PeerServer.messageReceived]: link terminated");
//		client.busy = null;
//		e.printStackTrace();
//		lostSignal(sc);
//		client.incomingPeer = null;
//		client.busy = null;
//	}
//} else {
//	if(!(client.busy==null)){
//		try {
//			sc.disconnect();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//}
