/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;

public class ClientThread extends Thread {
	
	public Socket clientSocket;
	
	ClientThread(Socket s) {
		this.clientSocket = s;
		EchoServerMultiThreaded.addClient(this.clientSocket);
	}
	
	/**
	* receives a request from client then sends an echo to the client
	* @param clientSocket the client socket
	**/
	public void run() {
		
		BufferedReader socIn = null;
		
		try { socIn = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream())); }
		catch(IOException e) { System.err.println("Failed to get the socket's input stream."); }
		
		while(true) {
			try { EchoServerMultiThreaded.setMessage(socIn.readLine()); }
			catch(IOException e) {
				System.out.println("Client disconnected " + this.clientSocket.getInetAddress());
				break;
			}
		}
		
		EchoServerMultiThreaded.removeClient(this.clientSocket);
		
		try { socIn.close(); }
		catch (IOException e) { System.err.println("Failed to properly close the BufferedReader."); }
		
	}
 
}
  