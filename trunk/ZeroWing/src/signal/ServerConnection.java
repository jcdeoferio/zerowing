package signal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class ServerConnection {
	int port;
	
	ServerSocket socket;
	Object addRemoveLock;
	SignalWaiter sigWait;
	String identifier;
	
	public ServerConnection(int port, String identifier){
		this.port = port;
		this.identifier = identifier;
		addRemoveLock = new Object();
	}
	/**
	 * initialize server, start listening for incoming SimpleConnections.
	 * @return
	 */
	public void startServer() throws IOException{
		socket = new ServerSocket(port);

		sigWait = new SignalWaiter(this, identifier);
		
		sigWait.start();
		displayln("[Operator]: Server successfully started");
	}
	
	public SocketAddress getLocalAddress(){
		return socket.getLocalSocketAddress();
	}
	
	/**
	 * keep alive waiting for signal
	 */
	public Socket waitForSignal(){
		try {
			return socket.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * send 'message' to 'target'
	 * @param target
	 * @param message
	 */
	public synchronized void sendMessage(String target, String message){
		
	}
	
	/**
	 * Called by internal thread MessageWaiter, allows for anytime
	 * receiving data.
	 * 
	 * @param sc
	 * @param msg
	 */
	public void messageReceived(SimpleConnection sc, String msg){
		displayln(sc+": "+msg);
	}
	
	/**
	 * New SimpleConnection established
	 * 
	 * @param sc
	 */
	public void gotSignal(SimpleConnection sc){
		
	}
	
	/**
	 * SimpleConnection lost
	 * @param sc
	 */
	public void lostSignal(SimpleConnection sc){
		
	}
	
	/**
	 * Called when signal waiter is no longer waiting.
	 */
	public void waitingTerminated(){
		
		
	}
	public void display(String s){
		System.out.print(identifier+"[ServerConnection]"+s);
	}
	public void displayln(String s){
		display(s+"\n");
	}
	
	static class SignalWaiter extends Thread{
		ServerConnection conn;
		String identifier;
		protected SignalWaiter(ServerConnection conn, String identifier){
			this.conn = conn;
			this.identifier = identifier;
		}
		
		public void run(){
			while(!conn.socket.isClosed()){
				conn.displayln("[Operator]: turning on detector !");
				Socket weGotSignal;
				
				weGotSignal = conn.waitForSignal();
				SimpleConnection onScreen = new IdentifiableSimpleConnection(weGotSignal, identifier+"(incoming)");
				if(weGotSignal!=null){
					conn.displayln("[Operator]: we got signal !");
					conn.displayln("[Operator]:"+onScreen.myAddress);
					conn.displayln("[Operator]:"+onScreen.address);
					
					MessageWaiter messWait = new MessageWaiter(conn,onScreen);
					messWait.start();
					
					conn.gotSignal(onScreen);
					
					
					
				}
			}
			conn.displayln("[Operator]: SignalWaiter terminated");
			conn.waitingTerminated();
		}
	}
}
