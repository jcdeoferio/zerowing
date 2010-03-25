package test.nexusscripts;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import util.Utility;

import autowing.NexusRandomRunner;
import autowing.Site;
import client.AddressPort;
import client.Client;

import mechanic.DBConnection;

public class NexusOne {
	
	private static final String defaultDBName = "mysql";
	private static final String trackerIP = "127.0.0.1";
	private static final int trackerPort = 8700;
	private static final String dbIP = "127.0.0.1";
	private static final String dbType = "mysql";
	private static final int dbPort = 3306;
	private static final String user = "root";
	private static final String password = "password";
	private static final String baseDBName = "cimbitest";
	private static final int nPeers = 4;
	private static long delay = 0;
	private static int syncs = 600;

	public static void main(String[] args) {
		final int nRuns = 4;
		
		PrintStream prtstr = new PrintStream(System.out);
		for(int runCtr = 0; runCtr < nRuns; runCtr++){
			System.setOut(new PrintStream(new OutputStream() {
			      public void write(int b) {
			      // NO-OP
			      }}));
			
			prtstr.println("Starting run#"+runCtr+" "+Utility.now());
			
			try{
				prtstr.println("initializing databases");
				nexusInitialize();
				
				Site[] sites = new Site[nPeers];
				HashMap<String, AddressPort>conns = new HashMap<String, AddressPort>();
				
				prtstr.println("initializing clients");
				for (int i = 0; i < nPeers; i++){
					String peerName = "peer"+i;
					String dbName = baseDBName+i;
					
					System.out.println(">> setting up client "+peerName);
					
					Client c = Client.getClient(
							peerName, 
							trackerIP,
							trackerPort,
							dbType,
							dbIP, 
							dbPort, 
							dbName, 
							user, 
							password);
					
					Site s = Site.getSite(c, "");
					sites[i] = s;
				}
				
				for(Site s : sites){
					System.out.println(s);
				}
				
				prtstr.println("insert run "+Utility.now());
				//insert run
				NexusRandomRunner nr = new NexusRandomRunner(
						sites, delay,
						100, syncs, 
						0, 0
				);
				
				nr.startClients();
				
				for(Site s : sites){
					AddressPort ap;
					String ip = s.as.c.getIP();
					int port = s.as.c.getClientPeerServerPort();
					ap = new AddressPort(s.as.c.getName(),ip,port);
					System.out.println(ap);
					conns.put(ap.getName(), ap);
				}
				
				nr.setAddMap(conns);
				
				Thread t = new Thread(nr);
				t.start();
				
				t.join();
				
				prtstr.println("summarizing");
				//file counting
				NexusFileCounter.main(new String[]{});
				
				prtstr.println("moving files");
				//log filing
				for(String command : getLogFilingCommands("inserts"))
					Runtime.getRuntime().exec(command);
				
				prtstr.println("update run "+Utility.now());
				//update run
				nr = new NexusRandomRunner(
						sites, delay,
						0, syncs, 
						100, 0
				);
//				
//				nr.startClients();
//				
//				for(Site s : sites){
//					AddressPort ap;
//					String ip = s.as.c.getIP();
//					int port = s.as.c.getClientPeerServerPort();
//					ap = new AddressPort(s.as.c.getName(),ip,port);
//					System.out.println(ap);
//					conns.put(ap.getName(), ap);
//				}
//				
				nr.setAddMap(conns);
				
				t = new Thread(nr);
				t.start();
				
				t.join();
				
				prtstr.println("summarizing");
				//file counting
				NexusFileCounter.main(new String[]{});
				
				prtstr.println("moving files");
				//log filing
				for(String command : getLogFilingCommands("updates"))
					Runtime.getRuntime().exec(command);
			}
			catch(SQLException e){
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			prtstr.println("Done running#"+runCtr+" "+Utility.now());
		}
	}
	
	private static void nexusInitialize() throws SQLException{		
		for(int i = 0; i < nPeers; i++){
			final String dbName = baseDBName+i;
			
			recreateDB(dbName);
		}
	}
	
	private static void recreateDB(String dbName) throws SQLException{
		DBConnection dbConn = DBConnection.getDBConnection("mysql", "localhost", 3306, dbName, user, password);
		
		dbConn.execute("DROP DATABASE "+dbName);
		
		dbConn = DBConnection.getDBConnection("mysql", "localhost", 3306, defaultDBName, user, password);
		
		dbConn.execute("CREATE DATABASE "+dbName);
		
		dbConn = DBConnection.getDBConnection("mysql", "localhost", 3306, dbName, user, password);
		
		dbConn.execute("CREATE TABLE A (columnA1 int, columnA2 int)");
		dbConn.execute("CREATE TABLE B (columnB1 int, columnB2 int)");
	}
	
	private static List<String> getLogFilingCommands(String postfix){
		final String foldername = "synclog-"+Utility.timestamp()+"-auto-"+postfix;
		List<String> commands = new LinkedList<String>();
		
		commands.add("mkdir "+foldername);
		
		String fileList = "";
		for(int i = 0; i < nPeers; i++){
			final String peerName = "peer"+i;
			final String filename = "synclog-"+peerName+".txt";
			
			fileList += filename + " ";
		}
		
		commands.add("cp "+fileList+foldername);
		commands.add("cp synclog-summary.txt "+foldername);
		
		return(commands);
	}

}
