package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

public class CSVCreator {
	String srcFileName;
	String destFileName;
	File srcFile;
	File destFile;

	public CSVCreator(String src, String dest) {
		srcFileName = src;
		destFileName = dest;
		srcFile = new File(srcFileName);
		destFile = new File(destFileName);
	}

	public void convert() throws FileNotFoundException {
		Scanner in = new Scanner(srcFile);
		PrintStream out = new PrintStream(destFile);
		while (in.hasNextLine()) {
			String line = in.nextLine();
			String[] items = line.split("\\s+");
			for (int i=0;i<items.length;i++) {
				out.print(items[i]+(i<items.length-1?",":""));
			}
			out.println();
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException{
		new CSVCreator("synclog-summary-average-inserts.txt", "synclog-summary-average-inserts.csv").convert();
		new CSVCreator("synclog-summary-average-updates.txt", "synclog-summary-average-updates.csv").convert();
	}
}
