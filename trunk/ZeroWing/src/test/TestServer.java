package test;

import java.io.IOException;

import signal.ServerConnection;

public class TestServer {
	ServerConnection serverConn;
	public static void main(String[] args){
		new TestServer(4000, "TestServer");
	}
	public TestServer(int port, String identifier){
		serverConn = new ServerConnection(port, identifier);
		try {
			serverConn.startServer();
		} catch (IOException e) {
			System.out.println("Server died");
			e.printStackTrace();
		}
	}
}
