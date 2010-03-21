package test.nexusscripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NexusAverager {
	
	static PrintStream ps = null;

	public static void main(String[] args) {
		final int nReps = 3;

		ArrayList<Integer> syncsList = new ArrayList<Integer>(1000);
		ArrayList<Integer> aveCounter = new ArrayList<Integer>(1000);
		for(int i = 0; i < nReps; i++){
			final String filename = "synclog-summary-"+i+".txt";
			
			Scanner sc = null;
			
			try {
				sc = new Scanner(new File(filename));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				continue;
			}
			
			final Pattern pat = Pattern.compile(".* PHT 2010 (\\d+)");
			for(int j = 0; sc.hasNextLine(); j++){
				String line = sc.nextLine();
				
				Matcher mat = pat.matcher(line);
				
				if(!mat.matches())
					continue;
				
				int syncs = Integer.parseInt(mat.group(1));
				
				if(j >= syncsList.size()){
					syncsList.add(syncs);
					aveCounter.add(1);
				}
				else{
					int prevSyncs = syncsList.get(j);
					syncsList.set(j, prevSyncs + syncs);
					
					int aveCount = aveCounter.get(j);
					aveCounter.set(j, aveCount+1);
				}
			}
		}
		
		int lastNonzeroDex = 0;
		for(int j = 0; j < aveCounter.size(); j++){
			int syncs = syncsList.get(j);
			int aveCount = aveCounter.get(j);
			
			int ave = syncs / aveCount;
			syncsList.set(j, ave);
			
			if(ave != 0)
				lastNonzeroDex = j;
		}
		
		try {
			ps = new PrintStream(new File("synclog-summary-average.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			ps = null;
		}
		
		for(int j = 0; j <= lastNonzeroDex; j++)
			print(j+"\t"+syncsList.get(j));
	}
	
	private static void print(String str){
		System.out.println(str);
		
		ps.println(str);
	}

}
