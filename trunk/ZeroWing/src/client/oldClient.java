//package client;
//
//import java.io.IOException;
//import java.sql.SQLException;
//import java.util.LinkedList;
//import java.util.List;
//
//import mechanic.Database;
//import mechanic.VersionVector;
//
//import signal.IdentifiableSimpleConnection;
//import signal.SimpleConnection;
//import util.ConsoleController;
//import util.ConsoleSystem;
//import util.Utility;
//
//public class oldClient implements ConsoleSystem {
//	/**
//	 * Automatic connection flag, simplest, zero user control in configuration
//	 * of ports/tracker location and so on.
//	 */
//	boolean automated;
//
//	/**
//	 * Should be used if external data files are available. (Configuration files
//	 * and so on)
//	 */
//	boolean first;
//	String peerName;
//	/**
//	 * Client -> Tracker connection.
//	 */
//	SimpleConnection tracker;
//
//	int timeout = 500;
//
//	boolean trackerLive = false;
//	boolean serving = false;
//	String busy = null;
//	Object busyControl = new Object();
//	SimpleConnection incomingPeer = null;
//	SimpleConnection outgoingPeer = null;
//	String defaultTrackerAddress = "0.0.0.0";
//	String myIP = "127.0.0.1";
//	String dbUser;
//	String dbName;
//	int defaultPort = 8888;
//	int peerPort = 8889;
//	int retries = 20;
//	
//	PeerServer peerService;
//	public LinkedList<AddressPort> candidates = new LinkedList<AddressPort>();
//	public Database db;
//	private ConsoleController cc;
//	
//	private oldClient(String peerName, String trackerAddress, int port,
//			String dbType, String ip, int dbPort, String dbName, String dbUser,
//			String dbPassword) throws SQLException{
//		cc = new ClientController(this);
//		this.peerName = peerName;
//		this.dbUser = dbUser;
//		this.dbName = dbName;
//		defaultTrackerAddress = trackerAddress;
//		defaultPort = port;
//		
//		setDB(dbType, ip, dbPort, dbName, dbUser, dbPassword);
//		cc.startConsole(this);
//	}
//	
//	/**
//	 * Safely handle Client construction. Client creation has a major requirement -
//	 * that the Database is accessible by the system. In the case that the login information
//	 * is not accessible, or for some other reason the system returns an exception, the system 
//	 * handles this by returning a null client. Any constructor should treat the returned null
//	 * as a 'working' flag.  
//	 * 
//	 * @param peerName
//	 * @param trackerAddress
//	 * @param port
//	 * @param dbType
//	 * @param ip
//	 * @param dbPort
//	 * @param dbName
//	 * @param dbUser
//	 * @param dbPassword
//	 * @return
//	 */
//	public static oldClient getClient(
//			String peerName, 
//			String trackerAddress, 
//			int port,
//			String dbType, 
//			String ip, 
//			int dbPort, 
//			String dbName, 
//			String dbUser,
//			String dbPassword){
//		
//		oldClient c;
//		try {
//			c = new oldClient(
//					peerName, trackerAddress, port, dbType, ip, dbPort, dbName,
//					dbUser, dbPassword
//			);
//			return c;
//		} catch (SQLException e) {
//			System.out.println("Client construction error caught");
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	@Deprecated 
//	public oldClient(String peerName, String trackerAddress, int port,
//			String dbname) throws SQLException {
//		cc = new ClientController(this);
//		this.peerName = peerName;
//		defaultTrackerAddress = trackerAddress;
//		defaultPort = port;
//		setDB("postgresql", "localhost", 5432, dbname, "postgres", "password");
//		// trackerLive = contactTracker();
//		// if(trackerLive){
//		// try {
//		// canStartServing = login(peerName, peerPort);
//		// } catch (IOException e) {
//		// e.printStackTrace();
//		// }
//		// }
//		// automated();
//		cc.startConsole(this);
//		// listen anyway.
//		// autoScanPort(peerPort);
//		// listen();
//	}
//
//	public int getPort() {
//		return peerPort;
//	}
//
//	public String getName() {
//		return peerName;
//	}
//	public String getDBUser(){
//		return dbUser;
//	}
//	public String getDBName(){
//		return dbName;
//	}
//
//	private void setDB(String dbType, String ip, int port, String dbName,
//			String dbUser, String dbPassword) throws SQLException {
//		db = new Database(dbType, ip, port, dbName, dbUser, dbPassword, peerName);
//	}
//
//	/**
//	 * called externally
//	 */
//	public void startClient() {
//		peerService = autoScanPort(peerPort);
//		if (peerService == null) {
//			System.out
//					.println("[Client.startClient]: unable to start PeerService, could not find port.");
//			return;
//		}
//		loginTracker();
//	}
//
//	public boolean loginTracker() {
//		trackerLive = contactTracker();
//		if (trackerLive) {
//			try {
//				login(peerName, peerPort);
//				displayln("[loginTracker]: login success");
//				ask();
//			} catch (IOException e) {
//				displayln("[loginTracker]: unable to log in.");
//				e.printStackTrace();
//				return false;
//			}
//		} else {
//			return false;
//		}
//		return true;
//	}
//
//	/**
//	 * called externally
//	 * 
//	 * @throws IOException
//	 */
//	public void manual(String ip, int port) throws IOException {
//		openTracker(ip, port);
//	}
//
//	/**
//	 * automatically search for unused ports (non-listening) on this client's
//	 * IP.
//	 * 
//	 * @param startPort
//	 * @return
//	 */
//	private PeerServer autoScanPort(int startPort) {
//		if(startPort < 4000) startPort = 4000;
//		short testPort = (short) startPort;
//		short endPort = (short) (testPort+50);
//		if(endPort < testPort) endPort = Short.MAX_VALUE;
//		
//		PeerServer testServer = null;
//		do{
//			try{
////				testServer = new PeerServer(testPort, this);
//				testServer.startServer();
//			} catch (IOException e){
//				displayln("Found peer on " + testPort
//						+ ", attempting next port.");
//				
//				testServer = null;
//				testPort++;
//			}	
//		} while(testServer ==null && testPort < endPort);
//		peerPort = testPort;
//		displayln("could not find any peer listening on "
//				+ peerPort + ".");
//		
//		return testServer;
//		
//		
////		SimpleConnection sc;
////		boolean success;
////		do {
////			displayln("[autoScanPort]: attempting port " + testPort);
////			sc = null;
////			try {
////				sc = new SimpleConnection(myIP, testPort);
////				sc.connect(timeout);
////				// System.out.println("success!");
////				System.out.println("Found peer on " + testPort
////						+ ", attempting next port.");
////				success = true;
////				sc.disconnect();
////				testPort++;
////			} catch (IOException e) {
////				displayln("[autoScanPort]: done: Serving on " + testPort);
////				testServer = new PeerServer(testPort, this);
////				peerPort = testPort;
////				success = false;
////				sc = null;
////				System.out.println("could not find any peer listening on "
////						+ peerPort + ".");
////				// e.printStackTrace();
////			}
////		
////		
////			if (sc == null)
////				break;
////		} while (success);
////		return testServer;
//	}
//
//	public boolean contactTracker() {
//		try {
//			openTracker(defaultTrackerAddress, defaultPort);
//
//		} catch (IOException e) {
//			displayln("Could not contact tracker on "
//					+ defaultTrackerAddress + " " + defaultPort);
//			// e.printStackTrace();
//			return false;
//		}
//		return true;
//	}
//
//	public void openTracker(String ip, int port) throws IOException {
//		displayln("[openTracker]: contacting tracker at " + ip + ":"
//				+ port);
//		tracker = connect(ip, port);
//	}
//
//	public SimpleConnection connect(String ip, int port) throws IOException {
//		SimpleConnection newConn;
//		newConn = new IdentifiableSimpleConnection(ip, port, peerName+"'s Tracker");
//		newConn.connect(timeout);
//		return newConn;
//	}
//
//	/**
//	 * login to server with the name 'name' and port. this is used to maintain
//	 * node uniqueness.
//	 * 
//	 * @param name
//	 * @param port
//	 * @throws IOException
//	 */
//	public boolean login(String name, int port) throws IOException {
//		if (tracker != null) {
//			if (trackerLive) {
//				tracker.hardSend("login " + port + " " + name);
//				String result = tracker.hardListen();
//				displayln("============= Tracker responded! YAY! =============");
//				if (result.equals("/login success")) {
//					trackerLive = true;
//					peerName = name;
//					peerPort = port;
//					return true;
//				}
//			}
//		}
//		displayln("Cannot log in : Tracker is not live.");
//		return false;
//	}
//
//	/**
//	 * System must have sent peer list request to tracker
//	 * 
//	 * @param sc
//	 * @throws IOException
//	 */
//	private boolean getResults(SimpleConnection sc) throws IOException {
//
//		String msg = sc.hardListen();
//		if (msg.equals("end"))
//			return false;
//		while (!msg.equals("end")) {
//			String[] data = msg.split(" ");
//			String candidateName = data[0];
//			String address = data[1];
//			try {
//				int port = Integer.parseInt(data[2]);
//				synchronized (candidates) {
//					AddressPort ap = new AddressPort(candidateName, address,
//							port);
//					displayln(ap.toString());
//					if (!candidates.contains(ap))
//						candidates.addLast(ap);
//				}
//			} catch (NumberFormatException e) {
//				e.printStackTrace();
//			}
//			
//			msg = sc.hardListen();
//		}
//		return true;
//	}
//
//	public void addPeerManual(String address, int port) {
//		synchronized (candidates) {
//			displayln("[Client.addPeerManual]: "+address+" "+port);
//			candidates.addLast(new AddressPort(address, port));
//		}
//	}
//
//	public void ask() {
//		if (candidates.size() == 0)
//			requestPeerList();
//
//		while (candidates.size() > 0) {
//			displayln("[ask]: candidates: " + candidates.size());
//			AddressPort ap = candidates.removeFirst();
//			displayln("[ask]: Current candidate: " + ap);
//			if (ap.getName() != null) {
//				if (ap.getName().equals(peerName)) {
//					displayln("[ask]: Won't connect to self. :|");
//					continue;
//				}
//			}
//
//			displayln("[ask]: Connecting to: " + ap.toString());
//			if (!(busy == null)) {
//				displayln("[ask]: Will not connect: busy: " + busy);
//				displayln("[ask]: PeerServer status: " + incomingPeer);
//				candidates.addFirst(ap);
//				return;
//			} else {
//				System.out
//						.println("[ask]: sending connection request to " + ap);
//				outgoingPeer = new IdentifiableSimpleConnection(ap.getAddress(), ap
//						.getPort(), peerName);
//
//				try {
//					synchronized (busyControl) {
//						outgoingPeer.connect(timeout);
//						displayln("[ask]: connection request done");
//						displayln("[ask]: target : " + ap.toString());
//						displayln("[ask]: busy: " + busy);
//						if (!(busy == null)) {
//							displayln("[ask]: Contacting failed : " + busy
//									+ " " + ap.toString());
//							try {
//								outgoingPeer.hardSend(null);
//								outgoingPeer.disconnect();
//							} catch (IOException e) {
//								System.out
//										.println("[ask]: Connection failed:  "
//												+ e.getMessage());
//							}
//							candidates.addLast(ap);
//							return;
//						} else {
//							busy = ap.toString();
//							displayln("[ask]: Contacting Success : " + busy
//									+ " " + ap.toString());
//
//							try {
//								displayln("[ask]: requesting update from "
//												+ ap);
//								requestUpdate(outgoingPeer);
//							} catch (IOException e) {
//								// candidates.addLast(ap);
//								busy = null;
//								displayln("[ask]: " + e.getMessage());
//							}
//							busy = null;
//							displayln("[ask]: done update request.");
//						}
//					}
//				} catch (IOException e) {
//					displayln("[ask]: could not connect to " + ap);
//					continue;
//					// e.printStackTrace();
//				}
//				continue;
//			}
//		}
//		displayln("[ask]: candidates exhausted.");
//
//	}
//
//	/**
//	 * start up server, wait for connections. Any received information is
//	 * handled by the peer server object
//	 */
//	public void listen() {
//		if (serving) {
//			displayln("unable to listen, peerService is unavailable.");
//			return;
//		}
//		serving = true;
//
//		if (peerService == null)
////			peerService = new PeerServer(peerPort, this);
//		try {
//			peerService.startServer();
//		} catch (IOException e) {
//			serving = false;
//			e.printStackTrace();
//		}
//	}
//
//	public void display(String s) {
//		System.out.print(s);
//	}
//
//	public void displayln(String s) {
//		display(peerName+" "+s + "\n");
//	}
//
//	public void requestPeerList() {
//		if (!trackerLive) {
//			displayln("[requestPeerList]: could not contact Tracker");
//			return;
//		} else {
//			displayln("[requestPeerList]: contacting tracker");
//		}
//		try {
//			displayln("[requestPeerList]: Waiting for peers from tracker.");
//			tracker.hardSend("peer list request");
//			getResults(tracker);
//		} catch (IOException e) {
//			trackerLive = false;
//			// e.printStackTrace();
//			displayln("[requestPeerList]: " + e.getLocalizedMessage());
//		} catch (ArrayIndexOutOfBoundsException e) {
//			displayln("[requestPeerList]: Message pattern conflict, aborting request");
//		}
//
//	}
//
//	public void requestUpdate(SimpleConnection sc) throws IOException {
//		sc.hardSend(constructUpdateRequest());
//		String dataIn;
//		do {
//			dataIn = sc.hardListen();
//			if (dataIn == null) {
//				return;
//			} else if (dataIn.equals("null")) {
//				return;
//			}
//			if (!dataIn.equals("end")) {
//				includeUpdate(dataIn);
//			}
//		} while (!dataIn.equals("end"));
//		try {
//			sc.disconnect();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		displayln("Got all updates");
//	}
//
//	/**
//	 * I just got an update, how do I 1) parse, 2) put in database
//	 * 
//	 * @param msg
//	 */
//	public void includeUpdate(String msg) {
//		try {
//			displayln("Got update: " + msg);
//			db.insertUpdate(msg);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * I just got a connection, how do I request data? my database
//	 * 
//	 * @return
//	 */
//	public String constructUpdateRequest() {
//		try {
//			return db.versionString();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return ("");
//	}
//
//	/**
//	 * most important method for listen state, called upon call of
//	 * PeerServer.messageReceived.
//	 * 
//	 * @param sc
//	 * @param update
//	 * @throws IOException
//	 */
//	public void sendUpdate(SimpleConnection sc, String update)
//			throws IOException {
//		// start sending updates to sc,
//		synchronized (busyControl) {
//			displayln("update reference: " + update);
//			List<String> updates = null;
//			try {
//				updates = db.getUpdates(new VersionVector(update));
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//			if(updates!=null)
//				for (String updateEntry : updates) {
//					displayln("Sending: " + updateEntry);
//					sc.hardSend(Utility.encode(updateEntry));
//				}
//			displayln("done sending updates");
//			sc.hardSend("end");
//			incomingPeer.disconnect();
//
//		}
//	}
//
//	/**
//	 * ConsoleSystem's handler for console commands. As of this version, all
//	 * parsing and execution is handled inside this method.
//	 * 
//	 * This method will later only parse messages, and will call a new method in
//	 * a currently unimplemented interface, the CommandSystem and CommandElement
//	 */
//	public void actionMessage(String action) {
//		action = action.trim();
//		displayln("action " + action);
//		try {
//			if (action.equals("help")) {
//				System.out
//						.println("update request, actual data, ask, SQL <EAVUpdate>, SQLSELECT <SQL select stmt>");
//			} else if (action.equals("help sql insert")) {
//				System.out
//						.println("<peername>:<timestamp>:<operation>:<tablename>:<entity>:<attribute>:<value>");
//			} else if (action.equals("update request")) {
//				displayln(db.versionString());
//			} else if (action.equals("actual data")) {
//				displayln(db.getDBUtility().dumpTable("students"));
//			} else if (action.equals("ask")) {
//				ask();
//			} else {
//				String[] data = action.split(" ");
//				if (data.length > 1) {
//					if (data[0].equals("SQL")) {
//						action = action.substring(data[0].length() + 1);
//						db.insertUpdate(action);
//					} else if (data[0].equals("SQLSELECT")) {
//						action = action.substring(data[0].length() + 1);
//						for (String s : db.queryToStringList(action)) {
//							displayln(s);
//						}
//						displayln("END SELECT");
//					}
//				}
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void printMessage(String msg) {
//		displayln(msg);
//	}
//
//	public String dumpTable(String table) throws SQLException {
//		return db.getDBUtility().dumpTable(table);
//	}
//	
//	public String toString(){
//			return peerName;
//	}
//}
