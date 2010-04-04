package test;

import java.io.IOException;

import javax.swing.JOptionPane;

import signal.Tracker;

public class Mainer {
	public static void main(String[] args){
		Tracker servConn;
		int port = -1;
		while(port == -1){
			String strPort = JOptionPane.showInputDialog(null, "Tracker Port Number:", "8888");
			try{
				port = Integer.parseInt(strPort);
			}catch(Exception e){
				port = -1;
			}
		}
		servConn = new Tracker(port, "MainerTracker");
		
		try {
			servConn.startServer();
//			System.out.println("starting server at "+servConn.getLocalAddress().toString());
		} catch (IOException e) {
			e.printStackTrace();
//			System.out.println("Server got disconnected");
		}
	}
}
