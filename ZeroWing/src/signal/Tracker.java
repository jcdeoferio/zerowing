package signal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import util.ConsoleSystem;

public class Tracker extends ServerConnection implements ConsoleSystem{
	volatile HashMap<SimpleConnection, Carrier> fleet;
	volatile HashMap<String, Carrier> usedIpPorts;
	volatile HashMap<String, Carrier> names;
	TrackerController trackerController;
	
	Object fleetLock = new Object();
	String identifier;
	
	public Tracker(int port, String identifier) {
		super(port, identifier);
		
		fleet = new HashMap<SimpleConnection, Carrier>();
		names = new HashMap<String, Carrier>();
		
		usedIpPorts = new HashMap<String, Carrier>();
		System.out.println("[Tracker Operator]: Tracker initialized");
		startConsole();
	}

	public void startConsole(){
		trackerController = new TrackerController(this);
		trackerController.startConsole(this);
	}
	
	/**
	 * Override by Tracker to control ServerConnection
	 * 
	 * Message Received.
	 * 
	 * @param sc
	 * @param msg
	 */
	public synchronized void messageReceived(SimpleConnection sc, String msg){
		Carrier car = fleet.get(sc);
		System.out.println("<<"+sc.toString()+">>"+msg);
		if(car==null){
			System.out.println("[TrackerConnection]: "+sc+" not a member of fleet");
			return;
		}
		String[] msgArr = msg.split(" ");
		for(int i=0;i<msgArr.length;i++)System.out.print("["+i+"]"+msgArr[i]);
//		System.out.println(car.lastIp+":"+msg);
		
		System.out.println("[TrackerMessageReceived]:"+msg);
		if(msgArr[0].equals("login") && msgArr.length==3){
			System.out.println("message is: "+msg);
			loginInfo(sc, car, msgArr[1], msgArr[2]);
		} else if(msg.equals("print")){
			activeFleet();
		} else if(msg.equals("peer list request")){
			try {
				sendFleet(sc);
//				sc.hardSend("end");
			} catch (IOException e) {
				System.out.println("unable to complete peer request for "+sc);
//				e.printStackTrace();
			}
		}
	}
	
	private void activeFleet(){
		Iterator<String> iters = names.keySet().iterator();
		while(iters.hasNext()){
			System.out.println(names.get(iters.next()));
		}
	}
	
	private void sendFleet(SimpleConnection sc) throws IOException{
		String[] s;
		synchronized(names){
			s = new String[names.size()];
			names.keySet().toArray(s);
		}
		
//		sc.hardSend(s.length+"");
		for(String msg: s){
			try{
				sc.hardSend(names.get(msg).toMessageString());
			} catch (IOException e){
				System.out.println("Reply interrupted at <"+msg+">");
				throw (e);
			}
		}
	}
	
	/**
	 * handle logging in matters
	 * @param sc
	 * @param car
	 * @param port
	 * @param name
	 */
	private void loginInfo(SimpleConnection sc,Carrier car, String port, String name){
		synchronized(usedIpPorts){
			// get previous state of this name
			Carrier car2 = names.get(name);
			
			// if previous state exists, 
			if(car2 != null){
				Carrier car3 = usedIpPorts.get(car2.getLast());
				if(car2.name==car3.name){
					usedIpPorts.remove(car2.getLast());
				}
				try {
					car2.getConn().disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("previous carrier removed.");
			}
			
			
			int i = safeParseInt(port);
			car2 = usedIpPorts.get(car.lastIp+i);
			
			SimpleConnection owningIP;
			if(car2==null)owningIP = null;
			else {
				owningIP = car2.getConn();
			}
			if(i==-1 || owningIP!=null){
				if(owningIP!=sc)
					sc.sendMessage("/login denied");
				else 
					sc.sendMessage("/login again? weird.");
			} else {
				usedIpPorts.put(car.lastIp+i, car);
				car.setPort(i);
				car.setName(name);
				
				names.put(name, car);
				sc.sendMessage("/login success");
			}
		}
	}
	
	private int safeParseInt(String s){
		if(s==null)return -1;
		else{
			try{
				return Integer.parseInt(s); 
			}catch(NumberFormatException e){
				e.printStackTrace();
			}
		}
		return -1;
	}
	
	
	/**
	 * Override by Tracker to control ServerConnection
	 * 
	 * New SimpleConnection established
	 * 
	 * @param sc
	 */
	public synchronized void gotSignal(SimpleConnection sc){
		System.out.println("======[TrackerGotSignal]: "+sc);
		Carrier c = new Carrier(sc.address, sc);
		safePut(sc,c);
	}
	
	public void safePut(SimpleConnection sc, Carrier c){
		synchronized(fleet){
			fleet.put(sc, c);
		}
	}
	public void safeRemove(SimpleConnection sc, Carrier c){
		synchronized(fleet){
			fleet.remove(sc);
		}
	}
	
	
	/**
	 * Override by Tracker to control ServerConnection
	 * 
	 * SimpleConnection lost
	 * @param sc
	 */
	public void lostSignal(SimpleConnection sc){
		Carrier lost = fleet.get(sc);
		System.out.println("lost signal to "+lost.name);
		if(lost!=null){
			safeRemove(sc, lost);
		}
	}

	/**
	 * called by ConsoleController
	 */
	public void actionMessage(String action) {
//		System.out.println("action: "+action);
		if(action.trim().equals("peerlist")){
			System.out.println("Printing known peers:\n###");
			activeFleet();
			System.out.println("###");
		}
	}

	/**
	 * called by ConsoleController
	 */
	public void printMessage(String msg) {
		System.out.println("console: "+msg);
	}
}






