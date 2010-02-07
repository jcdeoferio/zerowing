package test;

import java.io.IOException;

import signal.Tracker;

public class Mainer {
	public static void main(String[] args){
		Tracker servConn;
		servConn = new Tracker(8888, "MainerTracker");
		
		try {
			servConn.startServer();
//			System.out.println("starting server at "+servConn.getLocalAddress().toString());
		} catch (IOException e) {
			e.printStackTrace();
//			System.out.println("Server got disconnected");
		}
	}
}
