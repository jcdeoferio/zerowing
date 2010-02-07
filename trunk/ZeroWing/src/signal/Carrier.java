package signal;

public class Carrier {
	String lastIp;
	int lastPort;
	boolean live;
	String name;
	SimpleConnection sc;
	
	public Carrier(String lastIp, SimpleConnection sc){
		this.lastIp = lastIp;
		this.sc = sc;
		name = null;
		lastPort = -1;
		live = false;
		
		if(this.lastIp.charAt(0)=='/'){
			this.lastIp = this.lastIp.substring(1);
		}
	}
	
	public String toString(){
		return "[Carrier]: "+name+" "+lastIp+" "+lastPort;
	}
	public String toMessageString(){
		return name+" "+lastIp+ " "+lastPort;
	}
	
	public void setPort(int lastPort){
		this.lastPort = lastPort;
	}
	public void setLive(boolean live){
		this.live = live;
	}
	public void setName(String name){
		this.name = name;
	}
	public SimpleConnection getConn(){
		return sc;
	}
	public String getLast(){
		return lastIp+lastPort;
	}
	
}
