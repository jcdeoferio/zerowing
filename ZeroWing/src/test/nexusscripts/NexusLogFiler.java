package test.nexusscripts;

import util.Utility;

public class NexusLogFiler {

	public static void main(String[] args) {
		final int nPeers = 4;
		
		final String foldername = "synclog-"+Utility.timestamp();
		
		System.out.println("mkdir "+foldername);
		
		String fileList = "";
		for(int i = 0; i < nPeers; i++){
			final String peerName = "peer"+i;
			final String filename = "synclog-"+peerName+".txt";
			
			fileList += filename + " ";
		}
		
		System.out.println("mv "+fileList+foldername);
		System.out.println("mv synclog-summary.txt "+foldername);
	}

}
