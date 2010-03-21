package test.nexusscripts;

public class NexusValidator {

	public static void main(String[] args) {
		final int nPeers = 10;
		final String basePeerName = "cimbitest";
		
		for(int i = 0; i < nPeers; i++){
			final String peerName = basePeerName+i;
			System.out.println("CONNECT "+peerName+";");
			System.out.println("SELECT count(*) FROM A;");
			System.out.println("SELECT count(*) FROM B;");
			System.out.println("SELECT * FROM versionvector order by maxcounter, peername;");
		}
	}

}
