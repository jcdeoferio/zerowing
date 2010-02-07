package test;

import java.io.IOException;

import signal.IdentifiableSimpleConnection;
import signal.SimpleConnection;

public class TestClient {
	public static void main(String[] args){
		System.out.println("hey!");
		new TestClient(4000);
		
	}
	public TestClient(int port){
		SimpleConnection sc;
		
		boolean success;
		do{
			System.out.println("port: "+port);
			sc = null;
			try {
				sc = new IdentifiableSimpleConnection("0.0.0.0", port, "TestClient");
				sc.connect(500);
				System.out.println("success!");
				success = true;
				sc.disconnect();
				port++;
			} catch (IOException e) {
				System.out.println("done!");
				success = false;
				sc = null;
				e.printStackTrace();
			}
			if(sc == null)break;
		} while(success);
		
			
	}
}
