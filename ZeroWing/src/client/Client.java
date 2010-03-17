package client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mechanic.Database;
import mechanic.Filter;
import mechanic.VersionVector;

import signal.IdentifiableSimpleConnection;
import signal.SimpleConnection;
import signal.TrackerListener;
import util.ConsoleController;
import util.ConsoleSystem;
import util.ParenScanner;
import util.Utility;

public class Client implements ConsoleSystem{
	HashMap<SimpleConnection, SyncUpdateListener> waitingUpdates;
	HashMap<SimpleConnection, String> sources;
	
	public Database db;
	String peerName;
	String trackerAddress;
	int trackerPort;
	String dbType;
	String ip;
	int dbPort;
	String dbName;
	String dbUser;
	String dbPassword;
	ConsoleController cc;
	int peerPort = 8889;
	private List<String> updateStringBuffer;
	
	
	PeerServer peerService;
	
	
	
	public LinkedList<AddressPort> candidates = new LinkedList<AddressPort>();
	
	int timeout = 500;
	public SimpleConnection tracker;
	private boolean trackerLive;
	TrackerListener trackerListener;
	
	private Client(String peerName, String trackerAddress, int port,
			String dbType, String ip, int dbPort, String dbName, String dbUser,
			String dbPassword) throws SQLException{
		cc = new ClientController(this);
		this.peerName = peerName;
		this.dbUser = dbUser;
		this.dbName = dbName;
		this.trackerAddress = trackerAddress;
		this.trackerPort = port;
		this.ip = ip;
		this.updateStringBuffer = new LinkedList<String>();
		
		waitingUpdates = new HashMap<SimpleConnection, SyncUpdateListener>();
		sources = new HashMap<SimpleConnection, String>();
		setDB(dbType, ip, dbPort, dbName, dbUser, dbPassword);
		cc.startConsole(this);
	}
	public static Client getClient(
			String peerName, 
			String trackerAddress, 
			int port,
			String dbType, 
			String ip, 
			int dbPort, 
			String dbName, 
			String dbUser,
			String dbPassword){
		
		Client c;
		try {
			c = new Client(
					peerName, trackerAddress, port, dbType, ip, dbPort, dbName,
					dbUser, dbPassword
			);
			return c;
		} catch (SQLException e) {
			System.out.println("Client construction error caught");
			e.printStackTrace();
			return null;
		}
	}
	// ============================ PEER SERVICE
	
	public void startClient() {
		peerService = autoScanPort(peerPort);
		displayln("[Client.startClient]: PeerService started");
		if (peerService == null) {
			System.out
					.println("[Client.startClient]: unable to start PeerService, could not find port.");
			return;
		}
		loginTracker();
	}
	private PeerServer autoScanPort(int startPort){
		if(startPort < 4000) startPort = 4000;
		int testPort = startPort;
		int endPort = (testPort+50);
		if(endPort < testPort) endPort = Short.MAX_VALUE + Math.abs(Short.MIN_VALUE);
		PeerServer testServer = null;
		do{
			try{
				testServer = new PeerServer(testPort, this);
				testServer.startServer();
			} catch (IOException e){
				displayln("Found peer on " + testPort
						+ ", attempting next port.");
				
				testServer = null;
				testPort++;
			}	
		} while(testServer ==null && testPort < endPort);
		peerPort = testPort;
		displayln("could not find any peer listening on "
				+ peerPort + ".");
		
		return testServer;
	}
	
	// ============================================ OutGoing Related
	public void ask(){
		if (candidates.size() == 0){ 
			displayln(" No Peers to Connect to");
			return;
		}
		LinkedList<AddressPort> tempCands = new LinkedList<AddressPort>();
		synchronized(candidates){
			for(AddressPort ap: candidates){
				tempCands.addLast(ap);
			}
		}
		for(AddressPort ap: tempCands){
			displayln("[ask]: sending connection request to " + ap);
			if(ap.getName().equals(peerName)){
				displayln("[ask]: won't connect to self " + ap);
				displayln("[ask]: "+ap.getAddress()+" "+ip);
				displayln("[ask]: "+ap.getPort()+" "+peerPort);
				continue;
			}
			SimpleConnection outgoingPeer = new IdentifiableSimpleConnection(ap.getAddress(), ap
					.getPort(), peerName+"(outgoing)");
			
			try {
				outgoingPeer.connect(timeout);
				displayln("[ask]: Successfullly Connected to " + ap.toString());
				try {
					displayln("[ask]: requesting update from "
									+ ap);
					requestUpdate(outgoingPeer);
				} catch (IOException e) {
					displayln("[ask]: Error in sending to " + e.getMessage());
				}
				
			} catch (IOException e) {
				displayln("[ask]: Encountered exception: "+e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
	}
	public void requestUpdate(SimpleConnection sc) throws IOException{
		synchronized(sc){
			sendMessage("updateRequest",constructUpdateRequest(),sc);
			synchronized(waitingUpdates){
				if(waitingUpdates.containsKey(sc)){
					displayln(sc+ " is already in list");
					SyncUpdateListener sul = waitingUpdates.get(sc);
					if(!sul.isAlive()){
						displayln(sc+ " stopped, and is restarting");
						sul.start();
					}
				} else{
					SyncUpdateListener sul = new SyncUpdateListener(sc,this);
					waitingUpdates.put(sc, sul);
					sul.start();
				}
			}
		}
	}
	private void sendMessage(String messageType, String message, SimpleConnection sc) throws IOException{
		sc.hardSend(messageType+" "+message);
	}
	
	public void executeCommand(String command,String params,SimpleConnection sc){
		synchronized(sc){
			displayln("[executeCommand]: ["+command+"] "+params+" --> "+sc);
			if(command.equals("")){
				return;
			}
			else if(command.equals("updateRequest")){
				List<String> updates = null;
				try {
					sendMessage("syncPartner", Utility.encode(peerName), sc);
					ParenScanner psc = new ParenScanner(params);
					String filterStr = psc.next();
					System.out.println(filterStr);
					Filter filter = new Filter(Utility.decode(filterStr));
					VersionVector vv = new VersionVector(Utility.decode(psc.next()));
					System.out.println(filter.toString());
					updates = db.getUpdates(filter, vv);
					displayln("[executeCommand:updateRequest]: updates built.");
					
					sendMessage("testUpdate", peerName+" Sending test update",sc);
//					if(updates!=null){
//						//TODO:REMOVE
//						PrintStream ps = null;
//						try {
//							ps = new PrintStream(new File("as_encoded_for_send.txt"));
//						} catch (FileNotFoundException e) {
//							e.printStackTrace();
//						}
//						for(String updateEntry:updates){
//							ps.println(Utility.encode(updateEntry));
//							sendMessage("updateEntry",Utility.encode(updateEntry),sc);
//						}
//					} else {
//						
//					}
					sendMessage("endUpdate", peerName+" Sending test update",sc);
				} catch (SQLException e) {
					displayln("[executeCommand:updateRequest]: SQL Exception "+e.getLocalizedMessage());
				} catch (IOException e) {
					displayln("[executeCommand:updateRequest]: IOException "+e.getLocalizedMessage());
				}
			} else if(command.equals("syncPartner")){
				String syncPartner= Utility.decode(params);
				sources.put(sc, syncPartner);
				db.setSyncPartner(syncPartner);
				
			} else if(command.equals("testUpdate")){
				displayln("[executeCommand.testUpdate] <"+params+"> S:"+sc);
			} else if(command.equals("updateEntry")){
//				//TODO:REMOVE
//				PrintStream ps = null;
//				try {
//					ps = new PrintStream(new File("as_received.txt"));
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				}
//				ps.println(params);
				String updateString = Utility.decode(params);
//				//TODO:REMOVE
//				try {
//					ps = new PrintStream(new File("as_first_decoded.txt"));
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				}
//				ps.println(updateString);
				System.out.println(">> ADD "+params +"\n>> "+updateString );
				updateStringBuffer.add(updateString);
			}
			else if(command.equals("endUpdate")){
				String pardner = sources.get(sc);
				System.out.println(Utility.now()+" Inserting "+updateStringBuffer.size()+" updates on partner "+pardner);
				
				for(String updateString : updateStringBuffer){
					try {
						if(db.compareToLocalCU(updateString) == -2){
							System.out.println("Conflicting update! Defaulting to accept");
						}
						
						db.insertUpdate(updateString, pardner);
					} catch (SQLException e) {
						displayln("[executeCommand:updateRequest]: SQL Exception "+e.getLocalizedMessage());
						e.printStackTrace();
					}
				}
				System.out.println(Utility.now()+" Done Inserting updates");
				
				updateStringBuffer.clear();
				
				db.unsetSyncPartner();
				
				try {
					sc.disconnect();
					displayln("Successful Disconnect: "+sc);
				} catch (IOException e) {
					displayln("Disconnect ERROR" + sc);
					e.printStackTrace();
				}
			}
		}
	}
	
	// ============================================  DB Related
	private void setDB(String dbType, String ip, int port, String dbName,
			String dbUser, String dbPassword) throws SQLException {
		db = new Database(dbType, ip, port, dbName, dbUser, dbPassword, peerName);
	}
	
	public String dumpTable(String table) throws SQLException {
		return db.getDBUtility().dumpTable(table);
	}
	public String constructUpdateRequest() {
		try {
			return ("("+Utility.encode(db.filterString())+")("+Utility.encode(db.versionString())+")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return("");
	}
	
	public SimpleConnection connect(String ip, int port) throws IOException {
		SimpleConnection newConn;
		displayln("[Client.connect]: Attemptin Connection to: "+ip+ " " + port);
		newConn = new IdentifiableSimpleConnection(ip, port, peerName+"'s Tracker");
		newConn.connect(timeout);
		displayln("[Client.connect]: Connection Success "+newConn);
		return newConn;
	}
	/**
	 * I just got an update, how do I 1) parse, 2) put in database
	 * 
	 * @param msg
	 */
	private void includeUpdate(String msg) {
		try {
			displayln("Got update: " + msg);
			db.insertUpdate(msg, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// ======================================       TRACKER RELATED
	public boolean loginTracker() {
		trackerLive = contactTracker();
		displayln("[Client.loginTracker]: Tracker status: "+trackerLive);
		if (trackerLive) {
			try {
				login(peerName, peerPort);
				displayln("[loginTracker]: login success");
//				ask();
			} catch (IOException e) {
				displayln("[loginTracker]: unable to log in.");
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
		return true;
	}
	public boolean contactTracker() {
		try {
			openTracker(trackerAddress, trackerPort);
			
		} catch (IOException e) {
			displayln("Could not contact tracker on "
					+ trackerAddress + " " + trackerPort);
			// e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean login(String name, int port) throws IOException {
		
		if (tracker != null) {
			if (trackerLive) {
				tracker.hardSend("login " + port + " " + name);
//				String result = tracker.hardListen();
//				displayln("============= Tracker responded! YAY! =============");
//				if (result.equals("/login success")) {
//					trackerLive = true;
//					peerName = name;
//					peerPort = port;
//					return true;
//				}
			}
		}
		displayln("Cannot log in : Tracker is not live.");
		return false;
	}
	public void openTracker(String ip, int port) throws IOException {
		displayln("[openTracker]: contacting tracker at " + ip + ":"
				+ port);
		tracker = connect(ip, port);
	}
	
	public void requestPeerList() {
		if (!trackerLive) {
			displayln("[requestPeerList]: could not contact Tracker");
			return;
		} else {
			displayln("[requestPeerList]: contacting tracker");
		}
		
		
		if(trackerListener==null){
			trackerListener = new TrackerListener(this);
		} 
		if (!trackerListener.isRunning()){
			trackerListener = new TrackerListener(this);
			trackerListener.start();
			trackerLive = true;
			displayln("[requestPeerList]: Tracker contacted");
			displayln("============= Tracker responded! YAY! =============");
		}
		
		try {
			tracker.hardSend("peer list request");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	// ======================================       // TRACKER RELATED
	
	public void display(String s) {
		System.out.print(s);
	}

	public void displayln(String s) {
		display(peerName+" "+s + "\n");
	}

	
	public void addPeerManual(String address, int port) {
		synchronized (candidates) {
			displayln("[Client.addPeerManual]: "+address+" "+port);
			AddressPort ap = new AddressPort(address,port);
			addPeerViaAddressPort(ap);
		}
	}
	public void addPeerViaAddressPort(AddressPort ap){
		boolean match = false;
		for(AddressPort ap2: candidates){
			if(ap.equals(ap2)){
				match = true;
			}
		}
		if(!match)candidates.addLast(ap);
	}
	// ============================================== GETTERS
	public String getName() {
		return peerName;
	}
	public int getClientPeerServerPort(){
		return peerPort;
	}

	public String toString(){
		return peerName;
	}
	public String getDBUser(){
		return dbUser;
	}
	public String getDBName(){
		return dbName;
	}
	public String getIP(){
		return ip;
	}
	public Database getDatabase(){
		return db;
	}
// ================================================= CONSOLE =========================
	public void actionMessage(String action) {
		action = action.trim();
		displayln("action " + action);
		try {
			if (action.equals("help")) {
				System.out
						.println("update request, actual data, ask, SQL <EAVUpdate>, SQLSELECT <SQL select stmt>");
			} else if (action.equals("help sql insert")) {
				System.out
						.println("<peername>:<timestamp>:<operation>:<tablename>:<entity>:<attribute>:<value>");
			} else if (action.equals("update request")) {
				displayln(db.versionString());
			} else if (action.equals("actual data")) {
				displayln(db.getDBUtility().dumpTable("students"));
			} else if (action.equals("ask")) {
//				ask();
			} else {
				String[] data = action.split(" ");
				if (data.length > 1) {
					if (data[0].equals("SQL")) {
						action = action.substring(data[0].length() + 1);
						db.insertUpdate(action, peerName);
						displayln("WARNING: using "+peerName+" as syncPartner. Might result in magic.");
					} else if (data[0].equals("SQLSELECT")) {
						action = action.substring(data[0].length() + 1);
						for (String s : db.queryToStringList(action)) {
							displayln(s);
						}
						displayln("END SELECT");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void printMessage(String msg) {
		displayln(msg);
	}
}	
