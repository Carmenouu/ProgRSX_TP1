package stream;

import java.awt.Color;
import java.io.*;
import java.net.*;
import java.util.TreeMap;

import gui.ClientMutiCastChat;

/**
 * 
 * @author Nel Bouvier and Carmen Prévot
 * @version 1.0
 */

public class ClientMultiCast {
	
	public final static String MESSAGE_DELIMITER = " ";
	
	public final static String COLOR_NORMAL = "normal";
	public final static String COLOR_INFO = "info";
	public final static String COLOR_WARNING = "warning";
	public final static String COLOR_ALERT = "alert";
	public final static TreeMap<String, Color> COLORS = new TreeMap<>() {{
		put(COLOR_NORMAL, Color.BLACK);
		put(COLOR_INFO, Color.BLUE);
		put(COLOR_WARNING, Color.ORANGE);
		put(COLOR_ALERT, Color.RED);
	}};
	
	public final static String COMMAND_ANNOUNCER = "/";
	public final static String COMMAND_DELIMITER = " ";
	public final static String COMMAND_CHANGE_CHANNEL_COMMAND = "channel";
	
	public final static TreeMap<Integer, String> GROUPS = new TreeMap<>() {{
		put(0, "228.5.6.7");
		put(1, "228.5.6.8");
		put(2, "228.5.6.9");
	}};
	
	/**
	 * The server's port.
	 */
	private static int groupPort;
	
	/**
	 * The server's address.
	 */
	private static InetAddress groupAddress;
	
	/**
	 * The client socket.
	 */
	private static MulticastSocket sock = null;
	
	/**
	 * The client nickname.
	 */
	private static String pseudo;
	
	/**
	 * The client chat interface.
	 */
	private static ClientMutiCastChat chat;
	
	/**
     * Set the server's port.
     * 
	 * @param port The new server's port.
     */
	public static void setGroupPort(int port) { ClientMultiCast.groupPort = port; }
	
	/**
     * Set the server's address.
     * 
	 * @param address The new server's address.
     */
	public static void setGroupAddress(String address) {
		
		try { ClientMultiCast.groupAddress = InetAddress.getByName(address); }
		catch(UnknownHostException e) { System.out.println("Unknown Host."); }
		
	}
	
	/**
     * Set the client's nickname.
     * 
	 * @param pseudo The new client's nicknamet.
     */
	public static void setPseudo(String pseudo) { ClientMultiCast.pseudo = pseudo; }
	
	/**
     * Start the thread used to handle server's messages.
     */
	private static void runListeningThread() {
		
		new Thread(new Runnable() {

			public void run() {
				
				byte[] buf;
				DatagramPacket data;
				String message;
				int delimiterPos;
				
				while(true) {
					
					buf = new byte[1000];
					data = new DatagramPacket(buf, buf.length);
							
					try { sock.receive(data); }
					catch(IOException e) { System.err.println("The connexion with the server has been lost."); break; }
					
					message = new String(data.getData());
					
					System.out.println("New message : " + message);
					
					delimiterPos = message.indexOf(MESSAGE_DELIMITER);
					chat.afficherNouveauMessage(message.substring(delimiterPos + 1), COLORS.get(message.substring(0, delimiterPos)));
					
				}
				
				closeConnexion();
				
			}
		      
		}).start();
	      
	}	
	
	/**
     * Send a message to the server.
     * 
	 * @param message The message to send.
     */
	public static void sendMessage(String message) { sendMessage(message, COLOR_NORMAL); }
	
	/**
     * Send a message to the server.
     * 
	 * @param message The message to send.
	 * @param color The color of the message.
     */
	public static void sendMessage(String message, String color) {
		
		DatagramPacket data;
		
		if(message.substring(0, 1).equals(COMMAND_ANNOUNCER)) { processMessage(message); return; }
		else { message = color + MESSAGE_DELIMITER + pseudo + " : " + message; }
		
		data = new DatagramPacket(message.getBytes(), message.length(), groupAddress, groupPort);
		
		try { sock.send(data); }
		catch(IOException e) { System.err.println("Could not send the message."); } 
		
	}
	
	/**
     * Process a message before sending it if necessary.
     * 
	 * @param message The message to process.
     */
	private static void processMessage(String message) {
		
		int delimiterPos  = message.indexOf(COMMAND_DELIMITER);
		
		switch(message.substring(1, delimiterPos)) {
		
			case COMMAND_CHANGE_CHANNEL_COMMAND:
				sendMessage("s'est déconnecté du salon.", COLOR_INFO);
				chat.clearChat();
				changeGroup(Integer.parseInt(message.substring(delimiterPos + 1)));
				sendMessage("s'est connecté au salon.", COLOR_INFO);
				break;
				
			default: break;
		
		}
		
	}
	
	/**
     * Moving the client to another server.
     * 
	 * @param channel The server to connect the client to.
     */
	protected static void changeGroup(int channel) {
		
		closeConnexion();
		
		setGroupAddress(GROUPS.get(channel));
		
		try { sock.joinGroup(groupAddress); }
		catch(IOException e) { System.err.println("Couldn't connect to the server."); }
		
	}
	
	/**
     * Close the client connexion to the server.
     */
	public static void closeConnexion() {
		
		try { sock.leaveGroup(groupAddress); }
		catch(Exception e) { System.err.println("Failed to properly close the Socket."); }
		
	}

	/**
	*  Starts the client.
	*  
	 * @param args The name of the server to connect to and the port of the server.
	 * @throws IOException Exceptions can be thrown if there are connection issues.
	**/
	public static void main(String[] args) throws IOException {

		if (args.length != 2) {
			System.out.println("Usage: java ClientMultiCast <Host's address> <Host's port>");
			System.exit(1);
		}
		
		setGroupAddress(args[0]);
		setGroupPort(Integer.parseInt(args[1]));
		
		try {
			sock = new MulticastSocket(groupPort);
			sock.joinGroup(groupAddress);
			sendMessage("s'est connecté au salon.", COLOR_INFO);
			System.out.println("Connected.");
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host:" + args[0]);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for " + "the connection to:"+ args[0]);
			System.exit(1);
		}

		chat = new ClientMutiCastChat();
		runListeningThread();
		System.out.println("Connected as " + pseudo);
		
	}

}


