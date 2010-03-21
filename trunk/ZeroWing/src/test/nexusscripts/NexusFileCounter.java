package test.nexusscripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.Utility;

public class NexusFileCounter {
	
	static PrintStream ps;

	public static void main(String[] args) {
		final int nPeers = 4;
//		final String basePeerName = "cimbitest";
		
		try {
			ps = new PrintStream(new File("synclog-summary.txt"));
		} catch (FileNotFoundException e) {
			System.err.println(e);
			ps = null;
		}

		TreeMap<Date, LinkedList<Integer>> syncTab = new TreeMap<Date, LinkedList<Integer>>();
		for(int i = 0; i < nPeers; i++){
			final String peerName = "peer"+i;
			final String filename = "synclog-"+peerName+".txt";
			
			Scanner sc = null;
			
			try {
				sc = new Scanner(new File(filename));
			} catch (FileNotFoundException e) {
				System.err.println(e);
				continue;
			}
			
			final Pattern logPat = Pattern.compile("(.*): (\\d*) (.*) --> (.*)");
			while(sc.hasNextLine()){
				String line = sc.nextLine();
				
				Matcher logMat = logPat.matcher(line);
				
				if(!logMat.matches())
					continue;

				Date timestamp = Utility.parseDateStr(logMat.group(1));
				int syncs = Integer.parseInt(logMat.group(2));
				
				if(!syncTab.containsKey(timestamp))
					syncTab.put(timestamp, new LinkedList<Integer>());
				
				syncTab.get(timestamp).add(syncs);
			}
		
		}
		
		int totalSyncs = 0;
		int nonzeros = 0;
		for(Map.Entry<Date, LinkedList<Integer>> syncEntry : syncTab.entrySet()){
			String dateStr = syncEntry.getKey().toString();
			LinkedList<Integer> syncSet = syncEntry.getValue();
			
			for(Integer nSyncs : syncSet){
				print(dateStr + " " + nSyncs);

				totalSyncs += nSyncs;
				
				if(nSyncs != 0)
					nonzeros++;
			}
		} 
		
		print("Total number of entities sent: "+totalSyncs);
		print("Number of nonzero syncs: "+nonzeros);
	}
	
	static void print(String str){
		System.out.println(str);
		
		if(ps != null)
			ps.println(str);
	}

}
