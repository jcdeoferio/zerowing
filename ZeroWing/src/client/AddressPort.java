package client;

public class AddressPort{
	String address;
	int port;
	String name;
	
	public boolean equals(Object obj) {
		if (obj==null)return false;
		AddressPort ap = (AddressPort)obj;
		return ap.address.equals(address) && ap.port == port && ap.name.equals(name);
	}
	public AddressPort(String name, String address, int port){
		this.name = name;
		this.address = address;
		this.port = port;
	}
	public AddressPort(String address, int port){
		this.name = "null";
		this.address = address;
		this.port = port;
	}
	public int getPort(){
		return port;
	}
	public String getAddress(){
		return address;
	}
	public String toString(){
		return "AP:["+name+" "+address+" "+port+"]";
	}
	public String getName(){
		return name;
	}
	
}

