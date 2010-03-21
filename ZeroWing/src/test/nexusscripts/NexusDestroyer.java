package test.nexusscripts;

public class NexusDestroyer {

	public static void main(String[] args) {
		final int nPeers = 4;
		final String basePeerName = "cimbitest";

		for(int i = 0; i < nPeers; i++){
			final String peerName = basePeerName+i;
			System.out.println("DROP DATABASE "+peerName+";");
		}
	}

}
