package autowing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

import client.AddressPort;
import client.Client;

import autosynccommands.AutoSyncAddPeer;
import autosynccommands.AutoSyncConnectToTracker;
import autosynccommands.AutoSyncStartClient;
import autosynccommands.AutoSyncSync;
import autosynccommands.AutoSyncWait;

public class Nexus {
	Site[] sites;
	HashMap<String, AddressPort> conns;
	public static Nexus getNexus(String fileTarget){
		return new Nexus(fileTarget);
	}
	public static Nexus getRandomNexus(
			long delay,
			String dbType,
			String dbName,
			int dbPort, 
			int peers, 
			int inserts, 
			int syncs, int updates, int deletes
		){
		return new Nexus(
				delay,
				dbType,
				dbName,
				dbPort,
				peers,
				inserts,
				syncs, updates, deletes
			);
	}
	
	public static void main(String[] args){
		@SuppressWarnings("unused")
//		Nexus one = getNexus("testdata.txt");
		Nexus one = getRandomNexus(
				1,
				"postgresql",
				"cimbitest",
				8870,
				3, //:TODO change number of peers
				1000,
				600,
				1000,
				100
		);
	}
	
	
	/**
	 * USED FOR TESTING PURPOSES
	 * 
	 * Nodes are made from node1 -> nodex ->  nodemax, 1 < x < nodemax
	 * password is 'password' without quotes
	 * @param dbType 
	 * @param dbName 
	 * @param dbPort
	 * @param peers
	 * @param inserts
	 * @param syncs
	 * @param updates
	 * @param deletes
	 */
	private Nexus(
			long delay,
			String dbType, String dbName, int dbPort, 
			int peers, int inserts, int syncs, 
			int updates, int deletes){
		sites = new Site[peers];
		conns = new HashMap<String, AddressPort>();
		String dbUser = "postgres";
		String dbPassword = "password";
		for (int i=0;i<peers; i++){
			String peerName = "peer"+i;
			String dbNameMine = dbName+i;
			System.out.println(">> setting up client "+peerName);
			Client c = Client.getClient(
					peerName, 
					"127.0.0.1", 
					8871, 
					dbType, 
					"127.0.0.1", 
					dbPort, 
					dbNameMine, 
					dbUser, 
					dbPassword);
			
			Site s = Site.getSite(c, "");
			sites[i] = s;
		}
		for(Site s : sites){
			System.out.println(s);
		}
		NexusRandomRunner nr = new NexusRandomRunner(
				sites, delay,
				inserts, syncs, 
				updates, deletes
		);
		nr.startClients();
		for(Site s: sites){
			AddressPort ap;
			String ip = s.as.c.getIP();
			int port = s.as.c.getClientPeerServerPort();
			ap = new AddressPort(s.as.c.getName(),ip,port);
			System.out.println(ap);
			conns.put(ap.getName(), ap);
		}
		nr.setAddMap(conns);
		nr.run();
	}
	
	private Nexus(String fileTarget){
		sites = grabData(fileTarget);
		conns = new HashMap<String, AddressPort>();
		for(Site s : sites){
			System.out.println(s);
		}
		NexusRunner nr = new NexusRunner(sites, 1);
		Site[] sites = nr.sites;
		nr.startClients();
		for(Site s: sites){
			AddressPort ap;
			String ip = s.as.c.getIP();
			int port = s.as.c.getClientPeerServerPort();
			ap = new AddressPort(s.as.c.getName(),ip,port);
			System.out.println(ap);
			conns.put(ap.getName(), ap);
		}
		for(Site s:sites){
			s.setCommands(
					parseCommands(s.commandString)
			);
			System.out.println(s.as.c.getName() + ": " + s.commandList());
		}
		
		
		nr.setMax(20);
		nr.run();
		System.out.println("Nexus init code complete, only threads remain.");
	}
	
	
	
	/**
	 * Grab Test data from fileTarget.
	 * @param fileTarget
	 */
	public Site[] grabData(String fileTarget){
		File testData = new File(fileTarget);
		try {
			Scanner scan = new Scanner(testData);
			int dbPort = scan.nextInt();
			String dbUser = scan.next();
			String dbPassword = scan.next();
			scan.nextLine();
			int siteCount = scan.nextInt();
			scan.nextLine(); //Remove extraneous newline.
			Site[] sites = new Site[siteCount];
			
			for(int i=0;i<siteCount;i++){
				String clientInfo = scan.nextLine();
				String clientAI = scan.nextLine();
								
				System.out.println("["+i+"] Info: "+clientInfo);
				System.out.println("AI: "+ clientAI);

				Client c = getClient(clientInfo, dbPort, dbUser,dbPassword);

				Site s = Site.getSite(c, clientAI);
				sites[i] = s;
			}
			return sites;
			
			
		} catch (FileNotFoundException e) {
			System.out.println("file not found: "+fileTarget);
			e.printStackTrace();
			return null;
		}
	}
	@SuppressWarnings("unused")
	private Client getClient(String s){
		String[] info = s.split(" ");
		Client c = Client.getClient(
				info[0], 
				info[1], 
				Integer.parseInt(info[2]), 
				info[3], 
				info[4],
				Integer.parseInt(info[5]),
				info[6],
				info[7], 
				info[8]);
		
		return c;
	}
	private Client getClient(String s, int dbPort, String dbUser, String dbPassword){
		String[] info = s.split(" ");
		Client c = Client.getClient(
				info[0], 
				info[1], 
				Integer.parseInt(info[2]), 
				info[3], 
				info[4],
				dbPort,
				info[6],
				dbUser, 
				dbPassword);
		
		return c;
	}
	
	
	private LinkedList<AutoSyncCommand> parseCommands(String text){
		String[] commands = text.split(" ");
		LinkedList<AutoSyncCommand> commandList = new LinkedList<AutoSyncCommand>();
		for(int i=0;i<commands.length;i++){
			String commName = commands[i];
			if(commName.equals("addPeer")){
//				int time = Integer.parseInt(commands[++i]);
//				String ip = commands[++i];
//				int port = Integer.parseInt(commands[++i]);
//				
//				System.out.println("<<< ("+ip+") "+port);
//				AutoSyncAddPeer ap = new AutoSyncAddPeer(time, ip,port);
				int time = Integer.parseInt(commands[++i]);
				String target = commands[++i];
				System.out.println("target name: "+target);
				AddressPort ap0 = conns.get(target);
				if(ap0!=null){
					AutoSyncAddPeer ap = new AutoSyncAddPeer(time, conns.get(target));
					commandList.add(ap);
				}
			}
			else if(commName.equals("connectToTracker")){
				int time = Integer.parseInt(commands[++i]);
				AutoSyncConnectToTracker ctt = new AutoSyncConnectToTracker(time);
				commandList.add(ctt);
			}
			else if(commName.equals("startClient")){
				int time = Integer.parseInt(commands[++i]);
				AutoSyncStartClient sc = new AutoSyncStartClient(time);
				commandList.add(sc);
			}
			else if(commName.equals("wait")){
				int time = Integer.parseInt(commands[++i]);
				AutoSyncWait w = new AutoSyncWait(time);
				commandList.add(w);
			}
			else if(commName.equals("sync")){
				int time = Integer.parseInt(commands[++i]);
				AutoSyncSync s = new AutoSyncSync(time);
				commandList.add(s);
			}
		}
		
		return commandList;
	}
	
}
