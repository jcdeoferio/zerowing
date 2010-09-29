package test.netfree.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Analyzes data stored in <code>fileName</code>.
 * @author kevinzana
 *
 */
public class DataAnalyzer {
	String namePrefix;
	String nameSuffix;
	int reps;
	int repstart = 0;
	long[] data;
	public DataAnalyzer(String namePrefix, String nameSuffix, int reps, int datalength){
		this.namePrefix = namePrefix;
		this.nameSuffix = nameSuffix;
		this.reps = reps;
		data = new long[datalength];
	}
	public void readData() throws IOException{
		int max = reps + repstart;
		int len = data.length;
		int[] withData = new int[len];
		for(int i=0;i<len;i++)withData[i]=0;
//		System.out.println(": start: "+0+" max: "+max);
		for(int i=repstart;i<max;i++){
			String fileName=namePrefix+""+i+""+nameSuffix;
			File f = new File(fileName);
//			System.out.println("run: "+i+" >> "+fileName);
//			displayln("reading "+f.getAbsolutePath());
			BufferedReader br = new BufferedReader(new FileReader(f));
			for(int line =0;line<len;line++){
				String dataline = br.readLine();
//				System.out.println(">> "+i+":"+line+":"+dataline+" <<");
				String[] splitted = dataline.split("\t");
//				displayln(">>"+ splitted[2]);
				if(!splitted[2].equals("0")){
					int syncCost = Integer.parseInt(splitted[2]);
					data[line] = data[line]+ syncCost;
					withData[line]++;
//					displayln(syncCost+"");
				}
			}
		}
		for(int i=0;i<len;i++){
//			if(data[i]!=0){
//				displayln(i+":"+data[i]/len);
//			}
			double simpleAverage = (double)data[i]/(double)reps;
			double nonzeroAverage = (double)data[i]/(double)withData[i];
			if(withData[i]==0)nonzeroAverage = 0;
//			System.out.println( simpleAverage + " : " + nonzeroAverage);
			System.out.println( simpleAverage);
//			System.out.println( nonzeroAverage );
		}
//		displayln("done!");
	}
	public void displayln(String msg){
		System.out.println("[DataAnalyzer]"+msg);
	}
	
	public static void main(String[] args) throws IOException{
		// 4 nodes
//		DataAnalyzer da = new DataAnalyzer("offlinesyncs/offlinesynctest-",".txt", 247, 600);
		
		// 4 nodes
//		DataAnalyzer da = new DataAnalyzer("syncs-4nodes/offlinesynctest-",".txt", 240, 600);
		
		// 6 nodes
//		DataAnalyzer da = new DataAnalyzer("syncs-6nodes/offlinesynctest-",".txt", 206, 600);

		// 8 nodes
//		DataAnalyzer da = new DataAnalyzer("syncs-8nodes/offlinesynctest-",".txt", 240, 600);		

		// 10 nodes
		DataAnalyzer da = new DataAnalyzer("syncs-10nodes/offlinesynctest-",".txt", 26, 600);
		
		da.readData();
	}
}
