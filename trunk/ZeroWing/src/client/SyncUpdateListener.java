package client;

import java.io.IOException;
import java.util.Scanner;

import signal.SimpleConnection;

public class SyncUpdateListener extends Thread {
	long timeMade;
	SimpleConnection sc;
	String[] text;
	boolean cont = true;
	boolean finished = false;
	Client c;

	public SyncUpdateListener(SimpleConnection sc, Client c) {
		timeMade = System.currentTimeMillis();
		this.sc = sc;
		this.c = c;
	}

	public void run() {
		try {
			while (cont) {
				String[] d;
				d = extractCommand(sc.hardListen());
				c.executeCommand(d[0], d[1], sc);
			}
		} catch (IOException e) {
			c.displayln("SyncUpdateListener for "+sc+" terminated. "
					+e.getLocalizedMessage());
		}
	}

	public long getLifeSeconds() {
		return (System.currentTimeMillis() - timeMade) / 1000;
	}

	public void stopListening() {
		cont = false;
	}

	public String[] getData() {
		return text;
	}

	public String[] safeGetData() {
		if (finished) {
			return text;
		} else
			return null;
	}

	private String[] extractCommand(String message) {
		Scanner scan = new Scanner(message);
		String[] str = new String[2];

		str[0] = scan.next();
		if (message.length() == str[0].length()) {
			str[1] = "";
			return str;
		}
		str[1] = message.substring(str[0].length() + 1);

		return str;
	}

}
