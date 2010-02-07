package settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Simple SystemSettings loader, will make testing faster and easier,
 * keep in mind this stores data in a text file.
 * 
 * USAGE: 
 * First use:
 * system (in our case, UI) calls getSystemSettings, and finds no settingsFile.
 * returns null, and thus system should use default.
 * 
 * Further uses:
 * System calls getSystemSettings, finds file, and matches the strings, loads strings
 * in textFields.
 * 
 * After modification of relevant data, textField addition and so on:
 * System calls getSystemSettings, finds file, and data does not match with TextFields.
 * Ignores this data, loads defaults. --On exit, settingsFile now matches new fields.
 * 
 * Further use after modification:
 * System calls getSystemSettings, finds file, and now matches strings properly.
 * 
 * @author kevinzana
 *
 */
public class SystemSettings {
	/**
	 * Get system settings. returns null on error.
	 * used linkedlist so files wouldn't have to contain header max line numbers.
	 * 
	 * USES TRIMMAGE {@code String.trim()}
	 * @param settingsFile
	 * @return
	 */
	public static LinkedList<String> getSystemSettings(String settingsFile){
		File f = new File(settingsFile);
		LinkedList<String> settings = new LinkedList<String>();
		try {
			Scanner s = new Scanner(f);
			while(s.hasNextLine()){
				settings.addLast(s.nextLine().trim());
			}			
			s.close();
			return settings;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * retrieves strings as array.:) as opposed to linkedlist.
	 * 
	 * @param settingsFile
	 * @return
	 */
	public static String[] getSystemSettingsArray(String settingsFile){
		LinkedList<String> linkedData = getSystemSettings(settingsFile);
		if(linkedData == null) return null;
		String[] arr = new String[linkedData.size()];
		linkedData.toArray(arr);
		return arr;
	}
	
	/**
	 * Save system settings. prints stack trace on exception.
	 * @param settings
	 * @param settingsFile
	 */
	public static void insertSystemSettings(LinkedList<String> settings, String settingsFile){
		File f = new File(settingsFile);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			String newline = System.getProperty("line.separator");
			while(!settings.isEmpty()){
				bw.write(settings.removeFirst()+newline);
				bw.flush();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public static void insertSettingsAsArray(String[] settings, String settingsFile){
		LinkedList<String> toSave = new LinkedList<String>();
		for(String s: settings){
			toSave.addLast(s);
		}
		System.out.println(toSave.size());
		insertSystemSettings(toSave, settingsFile);
	}
	
	
}
