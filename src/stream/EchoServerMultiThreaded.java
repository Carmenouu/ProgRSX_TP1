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
		
		EchoServerMultiThreaded.retrieveHistoric(client, channel);
		
	}
	
	public static synchronized void disconnectClient(Socket client, int channel) {

		System.out.println("[Channel " + channel + "] Client left : " + client.getInetAddress());
		channels.get(channel).remove(client);
		
	}
	
	public static synchronized List<Socket> getClients(int channel) { return channels.get(channel); }
	
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
	
	public static synchronized void sendMessage(String message, int channel) {

		for(Socket client : channels.get(channel)) {
			System.out.println("[Channel " + channel + "] Sending message to " + client.getInetAddress());
			
	        PrintStream socOut = null;
			try { socOut = new PrintStream(client.getOutputStream()); }
			catch(IOException e) { System.err.println("Failed to get socket's output stream."); continue; }
			
			socOut.println(message);
			socOut.flush();
			
		}
		
		saveMessage(message, channel);
		
	}
	
	public static void sendMessage(Socket client, String message, int channel) {
		
		System.out.println("[Channel " + channel + "] Sending message to " + client.getInetAddress());
		
		PrintStream socOut = null;
		try { socOut = new PrintStream(client.getOutputStream()); }
		catch(IOException e) { System.err.println("Failed to get socket's output stream."); return; }
		
		socOut.println(message);
		socOut.flush();
		
	}
	
	private static String getChannelHistoricFile(int channel) {
		
		File file = new File("historics/" + channel + ".txt");
		
		try { file.createNewFile(); }
		catch(IOException e) { }
		
		return file.getAbsolutePath();
		
	}
	
	private static void saveMessage(String message, int channel) {
		
		BufferedWriter writer = null;
		
		System.out.println("[Channel " + channel + "] Saving message for channel " + channel);
		
		try { writer = new BufferedWriter(new FileWriter(getChannelHistoricFile(channel), true)); }
		catch(IOException e) { System.err.println("Failed to open the file."); return; }
		
		try {
			writer.write(message);
			writer.newLine();
		} catch(IOException e) { System.err.println("Failed to write in the file."); }
		
		try { writer.close(); }
		catch(IOException e) { System.err.println("Failed to properly close the BufferedWriter."); }
		
	}
	
	public static void retrieveHistoric(Socket client, int channel) {

		String line = null;
		BufferedReader reader = null;
		
		System.out.println("[Channel " + channel + "] Retrieving historized messages for channel " + channel);
		
		try { reader = new BufferedReader(new FileReader(getChannelHistoricFile(channel))); }
		catch(FileNotFoundException e) { System.err.println("Failed to open historic."); return; }
		
		do {
			try { line = reader.readLine(); }
			catch(IOException e) { System.err.println("Failed to read the next historized message."); break; }
			if(line != null) sendMessage(client, line, channel);
		} while(line != null);
		
		try { reader.close(); }
		catch(IOException e) { System.err.println("Failed to properly close the BufferedReader."); }
		
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

  