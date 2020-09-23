package stream;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class ServerResponseThread extends Thread {
	
	/**
	* receives a request from client then sends an echo to the client
	* @param clientSocket the client socket
	**/
	public void run() {
		
        while(true) {
        	String message = EchoServerMultiThreaded.getMessage();
        	
        	if(message != null) {
				for(Socket client : EchoServerMultiThreaded.getClients()) {
					System.out.println("Sending message to " + client.getInetAddress());

			        PrintStream socOut = null;
					try { socOut = new PrintStream(client.getOutputStream()); }
					catch(IOException e) { System.err.println("Failed to get socket's output stream."); }
					
					socOut.println(message);
					socOut.flush();
				}
				
				EchoServerMultiThreaded.resetMessage();
	        }
		}
	
	}
	
}
