package signal;

import java.net.Socket;

public class IdentifiableSimpleConnection extends SimpleConnection {
	String identifier;
	public IdentifiableSimpleConnection(Socket socket, String identifier) {
		super(socket);
		this.identifier = identifier;
		displayln(": Socket Made");
	}
	public IdentifiableSimpleConnection(String address, int port, String identifier){
		super(address, port);
		this.identifier = identifier;
	}
	
	public String toString(){
		return identifier+" "+super.toString();
	}
	public void display(String s){
		super.display(identifier+" [IDSimpleConnection]"+s);
	}
}
