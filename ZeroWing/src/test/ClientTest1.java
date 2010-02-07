package test;

import java.io.IOException;

import signal.IdentifiableSimpleConnection;
import signal.SimpleConnection;

public class ClientTest1 {
	public static void main(String[] args){
		SimpleConnection sc;
		
		sc = new IdentifiableSimpleConnection
			("0.0.0.0", 8888, "clientTest");
		
		
		try {
			System.out.println("Starting conn.");
			sc.connect(500);
			System.out.println("Sending message");
			sc.sendMessage("heya!");
			System.out.println(sc.getMessage());
		} catch (IOException e) {
			System.out.println("connection error");
			e.printStackTrace();
		}
		
		
		System.out.println("end client");
		
		
	}
}
