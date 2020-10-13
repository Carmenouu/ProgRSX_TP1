/***
 * EchoClient
 * Example of a TCP client 
 * Date: 10/01/04
 * Authors:
 */
package stream;

import java.io.*;
import java.net.*;

import gui.ClientChat;

/**
 * 
 * @author Nel Bouvier & Carmen Prévot
 * @version 1.0
 */

public class EchoClient {
	
	/**
	 * The client socket.
	 */
	private static Socket sock = null;
	
	/**
	 * The client nickname.
	 */
	private static String pseudo;
	
	/**
	 * The client chat interface.
	 */
	private static ClientChat chat;
	
	/**
     * Set the client's nickname.
     * 
	 * @param pseudo The new client's nicknamet.
     */
	public static void setPseudo(String pseudo) { EchoClient.pseudo = pseudo; }
	
	/**
     * Start the thread used to handle server's messages.
     */
	private static void runListeningThread() {
		
		new Thread(new Runnable() {

			public void run() {
				
				BufferedReader socIn = null;
				try { socIn = new BufferedReader(new InputStreamReader(sock.getInputStream())); }
				catch(IOException e) { System.err.println("Failed to get the socket's input stream."); }
				String message;
				int delimiterPos;
				
				while(true) {
					try { message = socIn.readLine(); }
					catch(IOException e) { System.err.println("The connexion with the server has been lost."); break; }
					
					System.out.println("New message : " + message);
					
					delimiterPos = message.indexOf(ClientThread.MESSAGE_DELIMITER);
					chat.afficherNouveauMessage(message.substring(delimiterPos + 1), ClientThread.COLORS.get(message.substring(0, delimiterPos)));
				}
				
				try { socIn.close(); }
				catch(IOException e) { System.err.println("Failed to properly close the BufferedReader."); }
				try { sock.close(); }
				catch(IOException e) { System.err.println("Failed to properly close the Socket."); }
				
			}
		      
		}).start();
	      
	}
	
	/**
     * Send a message to the server.
     * 
	 * @param message The message to send.
     */
	public static void sendMessage(String message) {
		
		PrintStream socOut = null;
		try { socOut = new PrintStream(sock.getOutputStream()); }
		catch(IOException e) { System.err.println("Failed to get the socket's output stream."); }
		
		if(message.substring(0, 1).equals(ClientThread.COMMAND_ANNOUNCER)) {
			processMessage(message);
			socOut.println(message);
		}
		else socOut.println(pseudo + " : " + message);
		
	}
	
	/**
     * Process a message before sending it if necessary.
     * 
	 * @param message The message to process.
     */
	private static void processMessage(String message) {
		
		int delimiterPos  = message.indexOf(ClientThread.COMMAND_DELIMITER);
		
		switch(message.substring(1, delimiterPos)) {
		
			case ClientThread.COMMAND_CHANGE_CHANNEL_COMMAND: chat.clearChat(); break;
			default: break;
		
		}
		
	}

	/**
	*  Starts the client.
	*  
	*  @param hostname The name of the server to connect to.
	*  @param port The port of the server.
	**/
	public static void main(String[] args) throws IOException {
		
		if (args.length != 2) {
			System.out.println("Usage: java EchoClient <EchoServer host> <EchoServer port>");
			System.exit(1);
		}
		
		try {
			sock = new Socket(args[0], Integer.parseInt(args[1]));
			System.out.println("Connected to " + sock.getInetAddress());
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host:" + args[0]);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for " + "the connection to:"+ args[0]);
			System.exit(1);
		}

		chat = new ClientChat(/* args[0], args[1] */);
		runListeningThread();
		System.out.println("Connected as " + pseudo);
		
	}

}


