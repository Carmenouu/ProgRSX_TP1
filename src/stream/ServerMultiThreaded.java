package stream;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * @author Nel Bouvier and Carmen Prévot
 * @version 1.0
 */

public class ServerMultiThreaded  {
	
	private final static int DEFAULT_CHANNEL = 0;
	private static TreeMap<Integer, List<Socket>> channels = new TreeMap<>();
	
	/**
     * Connect a client to a channel.
     * 
	 * @param client The client we want to connect.
	 * @param channel The channel used to connect the client into.
     */
	public static synchronized void connectClient(Socket client, int channel) {
		
		if(channels.get(channel) == null) channels.put(channel, new ArrayList<>());
		
		sendMessage(ServerClientThread.COLOR_INFO + ServerClientThread.MESSAGE_DELIMITER + "Un utilisateur est entré dans le canal.", channel, null, false);
		
		channels.get(channel).add(client);
		System.out.println("[Channel " + channel + "] New client : " + client.getInetAddress());
		
		sendMessage(ServerClientThread.COLOR_INFO + ServerClientThread.MESSAGE_DELIMITER + "Vous venez d'entrer dans le canal " + channel + ".", channel, client, false);
		retrieveHistoric(client, channel);
		
	}
	
	/**
     * Disonnect a client from a channel.
     * 
	 * @param client The client we want to disconnect.
	 * @param channel The channel from which the client needs to be disconnected.
     */
	public static synchronized void disconnectClient(Socket client, int channel) {

		System.out.println("[Channel " + channel + "] Client left : " + client.getInetAddress());
		channels.get(channel).remove(client);
		
		sendMessage(ServerClientThread.COLOR_INFO + ServerClientThread.MESSAGE_DELIMITER + "Un utilisateur est sorti du canal.", channel, null, false);
		
	}
	
	/**
     * Get a list of clients in a channel.
     * 
	 * @param channel The channel we want to get clients of.
	 * 
	 * @return A List of Sockets which represents clients of the channel.
     */
	public static synchronized List<Socket> getClients(int channel) { return channels.get(channel); }
	
	/**
     * Start the thread used to handle clients's connections.
     * 
	 * @param port The port on which open the socket.
     */
	private static void runAcceptClientThread(int port) {

		new Thread(new Runnable() {

			public void run() {
				
				ServerSocket listenSocket = null;
				
				try {
					
					listenSocket = new ServerSocket(port);
					System.out.println("Server ready on port " + port); 
					
					while(true) {
						Socket clientSocket = listenSocket.accept();
						new ServerClientThread(clientSocket, DEFAULT_CHANNEL).start();
					}
					
				} catch (IOException e) { System.err.println("Error in EchoServer:" + e); }
				
				try { listenSocket.close(); }
				catch(IOException e) { System.err.println("Failed to properly close the ServerSocket."); }
				
			}
			
		}).start();
		
	}
	
	/**
     * Send a message to every clients of a channel or only a client.
     * Allows to save the message in the channel's historic.
     * 
	 * @param message The message to send.
	 * @param channel The channel to send the message to.
	 * @param optionalArgs Options : instance of Socket - The client to send the message to. Instance of Boolean - The message to send.
     */
	public static synchronized void sendMessage(String message, int channel, Object... optionalArgs) {
		
		List<Socket> clients = optionalArgs.length == 0 || optionalArgs[0] == null ? channels.get(channel) : new ArrayList<>() {{ add((Socket)optionalArgs[0]); }};

		for(Socket client : clients) {
			System.out.println("[Channel " + channel + "] Sending message to " + client.getInetAddress());
			
	        PrintStream socOut = null;
			try { socOut = new PrintStream(client.getOutputStream()); }
			catch(IOException e) { System.err.println("Failed to get socket's output stream."); continue; }
			
			socOut.println(message);
			socOut.flush();
			
		}

		if(optionalArgs.length == 0 || optionalArgs.length > 1 && (Boolean)optionalArgs[1]) { saveMessage(message, channel); }
		
	}
	
	/**
     * Get the channel's historic's file's path.
     * 
	 * @param channel The channel we want to get the historic of.
	 * 
	 * @return String the file's path or null if the file couldn't be created.
     */
	private static String getChannelHistoricFile(int channel) {
		
		File file = new File("historics/" + channel + ".txt");
		
		if(!file.exists()) {
			try { file.createNewFile(); }
			catch(IOException e) { System.out.println("Could not create file."); return null; }
		}
		
		return file.getAbsolutePath();
		
	}
	
	/**
     * Save a message in the channel's historic.
     * 
	 * @param message The message we want to save.
	 * @param channel The channel in which the message needs to be saved.
     */
	private static void saveMessage(String message, int channel) {
		
		BufferedWriter writer = null;
		
		System.out.println("[Channel " + channel + "] Saving message for channel " + channel);
		
		try {
			
			writer = new BufferedWriter(new FileWriter(getChannelHistoricFile(channel), true));
			writer.write(message);
			writer.newLine();
			writer.close();
			
		} catch(IOException e) { System.err.println("Failed to write in the file."); }
		
	}
	
	/**
     * Retrieve the historic of a channel to a client.
     * 
	 * @param client The client to retrieve the historic to.
	 * @param channel The channel to retrieve the historic of.
     */
	public static void retrieveHistoric(Socket client, int channel) {

		String line = null;
		BufferedReader reader;
		
		System.out.println("[Channel " + channel + "] Retrieving historized messages for channel " + channel);
		
		try {
			reader = new BufferedReader(new FileReader(getChannelHistoricFile(channel)));
			
			do {
				try { line = reader.readLine(); }
				catch(IOException e) { System.err.println("Failed to read the next historized message."); break; }
				if(line != null) sendMessage(line, channel, client, false);
			} while(line != null);
			
			reader.close();
			
		} catch(FileNotFoundException e) { System.err.println("Failed to open the historic."); }
		  catch(IOException e) { System.err.println("Could not read the historic."); }
		
	}
  
	/**
	 * Starts the server.
	 * 
	 * @param args The port number.
	 * 
	 **/
	public static void main(String args[]) {
		   
		if(args.length < 1) { System.out.println("Arg1 should be the server's running port."); System.exit(1); }
		
		runAcceptClientThread(Integer.parseInt(args[0]));
		
	}
	
}

  