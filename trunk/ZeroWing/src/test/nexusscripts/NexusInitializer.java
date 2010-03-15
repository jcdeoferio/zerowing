package test.nexusscripts;

import java.sql.SQLException;

import mechanic.Database;

public class NexusInitializer {

	public static void main(String[] args) {
		final int nPeers = 10;
		final String basePeerName = "cimbitest";
		
		for(int i = 0; i < nPeers; i++){
			final String peerName = basePeerName+i;
			
			try {
				new Database("mysql", "localhost", 3306, peerName, "root", "password", peerName);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
