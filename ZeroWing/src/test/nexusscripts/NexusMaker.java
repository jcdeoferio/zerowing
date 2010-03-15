package test.nexusscripts;

public class NexusMaker {

	public static void main(String[] args) {
		final int nPeers = 10;
		final String basePeerName = "cimbitest";
		
		for(int i = 0; i < nPeers; i++){
			String peerName = basePeerName+i;
			System.out.println("CREATE DATABASE "+peerName+";");
			System.out.println("CONNECT "+peerName+";");
			System.out.println("CREATE TABLE A (columnA1 int, columnA2 int);");
			System.out.println("CREATE TABLE B (columnB1 int, columnB2 int);");
			System.out.println();
		}
	}

}
