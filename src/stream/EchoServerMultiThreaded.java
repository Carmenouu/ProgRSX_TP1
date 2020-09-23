/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class EchoServerMultiThreaded  {
	
	public static String message = null;
	public static List<Socket> clients = new ArrayList<>();
	
	public static synchronized String getMessage() {
		return message;
	}
	
	public static synchronized void setMessage(String msg) {
		message = msg;
		System.out.println("New message : " + msg);
	}
	
	public static synchronized void resetMessage() {
		message = null;
	}
	
	public static synchronized List<Socket> getClients() {
		return clients;
	}
	
	public static synchronized void addClient(Socket client) {
		clients.add(client);
		System.out.println("New client : " + client);
	}
	
	public static synchronized boolean removeClient(Socket client) {
		System.out.println("Client left : " + client);
		return clients.remove(client);
	}
  
	/**
	* main method
	* @param EchoServer port
	* 
	**/
	public static void main(String args[]) { 
		
		ServerSocket listenSocket = null;
		   
		if (args.length != 1) {
			System.out.println("Usage: java EchoServer <EchoServer port>");
			System.exit(1);
		}
		
		try {
			listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
			new ServerResponseThread().start();
			
			System.out.println("Server ready..."); 
			while(true) {
				Socket clientSocket = listenSocket.accept();
				new ClientThread(clientSocket).start();
				System.out.println("Connexion from : " + clientSocket.getInetAddress());
			}
		} catch (Exception e) { System.err.println("Error in EchoServer:" + e); }
		
		try { listenSocket.close(); }
		catch(IOException e) { System.err.println("Failed to properly close the ServerSocket."); }
		
	}
	
}

  