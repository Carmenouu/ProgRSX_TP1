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
import java.util.TreeMap;

public class EchoServerMultiThreaded  {
	
	public final static int defaultChannel = 0;
	public static TreeMap<Integer, List<Socket>> channels = new TreeMap<>();
	
	public static synchronized void connectClient(Socket client, int channel) {
		
		if(channels.get(channel) == null) channels.put(channel, new ArrayList<>());
		channels.get(channel).add(client);
		System.out.println("[Channel " + channel + "] New client : " + client.getInetAddress());
		
	}
	
	public static synchronized void disconnectClient(Socket client, int channel) {

		System.out.println("[Channel " + channel + "] Client left : " + client.getInetAddress());
		channels.get(channel).remove(client);
		
	}
	
	public static synchronized List<Socket> getClients(int channel) { return channels.get(channel); }
	
	public static synchronized void sendMessage(String message, int channel) {

		for(Socket client : channels.get(channel)) {
			System.out.println("[Channel " + channel + "] Sending message to " + client.getInetAddress());

	        PrintStream socOut = null;
			try { socOut = new PrintStream(client.getOutputStream()); }
			catch(IOException e) { System.err.println("Failed to get socket's output stream."); }
			
			socOut.println(message);
			socOut.flush();
		}
		
	}
	
	private static void runAcceptClientThread(int port) {

		new Thread(new Runnable() {

			public void run() {
				
				ServerSocket listenSocket = null;
				
				try {
					listenSocket = new ServerSocket(port);
					System.out.println("Server ready on port " + port); 
					
					while(true) {
						Socket clientSocket = listenSocket.accept();
						new ClientThread(clientSocket, defaultChannel).start();
					}
				} catch (IOException e) { System.err.println("Error in EchoServer:" + e); }
				
				try { listenSocket.close(); }
				catch(IOException e) { System.err.println("Failed to properly close the ServerSocket."); }
				
			}
			
		}).start();
		
	}
  
	/**
	* main method
	* @param EchoServer port
	* 
	**/
	public static void main(String args[]) {
		   
		if (args.length != 1) {
			System.out.println("Usage: java EchoServer <EchoServer port>");
			System.exit(1);
		}
		
		runAcceptClientThread(Integer.parseInt(args[0]));
		
	}
	
}

  