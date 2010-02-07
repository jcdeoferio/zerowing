package signal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SimpleConnection {
	String address;
	String myAddress;
	int port;
	PrintWriter streamWriter;
	BufferedReader streamReader;

	Socket socket;
	boolean connected;

	public SimpleConnection(String address, int port) {
		this.address = address;
		this.port = port;
		connected = false;
		socket = null;
		myAddress = "uninit";
		
		
	}

	/**
	 * Assume connected, as created by Server
	 * 
	 * @param socket
	 */
	public SimpleConnection(Socket socket) {
		this.socket = socket;
		this.address = socket.getInetAddress().toString();
		this.port = socket.getPort();
		
		connected = socket.isConnected();
		myAddress = socket.getLocalAddress().toString();

		try {
			fixStreams();
		} catch (IOException e) {
			e.printStackTrace();
			connected = false;
			socket = null;
		}
		
	}
	
	
	
	public String toString(){
		return "[SimpleConnection] "+address+":"+port;
	}

	/**
	 * Use this method for Sockets created for outgoing messages.
	 * @throws IOException
	 */
	public void connect(int timeout) throws IOException {
		displayln("[SimpleConnection.connect]: attempting connection");
		if (socket==null) {
			socket = new Socket();
			socket.bind(null);
			socket.connect(new InetSocketAddress(address, port), timeout);
			fixStreams();
			connected = true;
			displayln("[SimpleConnection.connect]: Connection established.");
			myAddress = socket.getLocalAddress().toString();
			displayln("[SimpleConnection]: Connection established.");
		} else if (socket.isConnected()) {
			displayln("[SimpleConnection.connect]: Socket is already connected");
			socket = null;
			connect(timeout);
		}
	}
	protected void display(String s){
		System.out.print(s);
	}
	protected void displayln(String s){
		display(s+"\n");
	}
	
	private void fixStreams() throws IOException{
		streamWriter = new PrintWriter(
				socket.getOutputStream(), true);
		streamReader = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
		displayln("[Socket.fixStreams]: Streams ready");
	}
	

	public void disconnect() throws IOException {
		try{
			if (socket.isConnected()) {
				socket.close();
			}
			connected = false;
		} catch (IOException e){
			displayln("[Socket.disconnect]: Error in closing");
			throw(e);
		}
		
	}

	/**
	 * this implementation allows control based on the nature of the
	 * IOException.
	 * 
	 * @param s
	 * @throws IOException
	 */
	public void hardSend(String s) throws IOException {
		displayln("[Socket.hardSend]: < "+s+" >");
		streamWriter.println(s);
	}

	public boolean sendMessage(String s) {
		try {
			hardSend(s);
			return true;
		} catch (IOException e) {
			e.printStackTrace();

			return false;
		}
	}

	/**
	 * This implementation allows control based on IOException. returns a String
	 * message from referred socket.
	 * 
	 * @return
	 * @throws IOException
	 */
	public String hardListen() throws IOException {
		return streamReader.readLine();
	}

	/**
	 * Safe listen, returns false if error has been encountered. on error, it is
	 * safer to disconnect.
	 * 
	 * @param s
	 * @return
	 */
	public String getMessage() {
		try {
			return hardListen();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
