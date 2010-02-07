package autowing;

import java.util.HashMap;

import client.Client;

/**
 * Constructs and stores client data. Useful for tracking clients in a testing environment
 * 
 * @author kevinzana
 *
 */
public class ClientGenerator {
	static HashMap<String, Client> clients = new HashMap<String, Client>();
	
	public static Client getClient(
			String peerName,
			String trackerAddress,
			int port,
			String dbType,
			String ip,
			int dbPort,
			String dbName,
			String dbUser,
			String dbPassword
		){
			Client c = Client.getClient(
					peerName,
					trackerAddress,
					port,
					dbType,
					ip,
					dbPort,
					dbName,
					dbUser,
					dbPassword);	
		if (c==null)return null;
		if (clients.get(peerName)!=null){	
			System.out.println("client with peerName "+peerName+" already exists");
			return null;
		} else {
			clients.put(peerName, c);
			return c;
		}
	}
}
