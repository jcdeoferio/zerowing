package test;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tester {

	static Scanner sc = new Scanner(System.in);
	
	public static void main(String[] args) {
		final Pattern pat = Pattern.compile(".*updates.*");
		for(int i = 0; sc.hasNextLine(); i++){
			String line = sc.nextLine();
			
			Matcher mat = pat.matcher(line);
			
			if(!mat.matches()){
				i--;
				continue;
			}
			
			System.out.println("cp "+line+"/synclog-summary.txt synclog-summary-"+i+".txt");			
		}
	}

}
