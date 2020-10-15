/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package stream;

import java.awt.Color;
import java.io.*;
import java.net.*;
import java.util.TreeMap;

/**
 * 
 * @author Nel Bouvier and Carmen Prévot
 * @version 1.0
 */

public class ServerClientThread extends Thread {

	public final static String MESSAGE_DELIMITER = " ";
	
	public final static String COLOR_NORMAL = "normal";
	public final static String COLOR_INFO = "info";
	public final static String COLOR_WARNING = "warning";
	public final static String COLOR_ALERT = "alert";
	
	/**
	 * The set of colors used in chat interface.
	 */
	public final static TreeMap<String, Color> COLORS = new TreeMap<>() {{
		put(COLOR_NORMAL, Color.BLACK);
		put(COLOR_INFO, Color.BLUE);
		put(COLOR_WARNING, Color.ORANGE);
		put(COLOR_ALERT, Color.RED);
	}};
	
	public final static String COMMAND_ANNOUNCER = "/";
	public final static String COMMAND_DELIMITER = " ";
	public final static String COMMAND_CHANGE_CHANNEL_COMMAND = "channel";
	
	/**
	 * The current channel.
	 */
	private int channel;
	
	/**
	 * The client socket.
	 */
	private Socket socket;
	
	/**
     * Create an instance of ClientThread.
     * 
	 * @param socket The client's socket.
	 * @param channel The channel in which the client is.
     */
	ServerClientThread(Socket socket, int channel) {
		
		this.socket = socket;
		this.channel = channel;
		
		ServerMultiThreaded.connectClient(this.socket, this.channel);
		
	}
	
	/**
     * Move the client to another channel.
     * 
	 * @param channel The channel in which the client will be move.
     */
	public void moveToChannel(int channel) {
		
		ServerMultiThreaded.disconnectClient(this.socket, this.channel);
		ServerMultiThreaded.connectClient(this.socket, channel);
		this.channel = channel;
		
	}
	
	/**
     * Process the message sent by the client.
     * Perform a treatment if needed.
     * 
	 * @param message The channel recieved.
     */
	private void processMessage(String message) {
		
		int delimiterPos;
		
		if(message.substring(0, 1).equals(COMMAND_ANNOUNCER)) {
			
			delimiterPos = message.indexOf(COMMAND_DELIMITER);
		
			switch(message.substring(1, delimiterPos)) {
			
				case COMMAND_CHANGE_CHANNEL_COMMAND: this.moveToChannel(Integer.parseInt(message.substring(delimiterPos + 1))); break;
				
				default: System.out.println("[Channel " + this.channel + "] Could not read the command : " + message); break;
				
			}
			
		} else {
			System.out.println("[Channel " + this.channel + "] New message from " + this.socket.getInetAddress());
			ServerMultiThreaded.sendMessage(COLOR_NORMAL + MESSAGE_DELIMITER + message, this.channel);
		}
		
	}
	
	/**
	* Starts the client thread.
	**/
	public void run() {
		
		BufferedReader socIn = null;
		String message = null;
		
		try { socIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream())); }
		catch(IOException e) { System.err.println("Failed to get the socket's input stream."); }
		
		while(true) {
			try { message = socIn.readLine(); }
			catch(IOException e) {
				System.out.println("[Channel " + this.channel + "] Client disconnected " + this.socket.getInetAddress());
				break;
			}
			
			this.processMessage(message);
		}
		
		ServerMultiThreaded.disconnectClient(this.socket, this.channel);
		
		try { socIn.close(); }
		catch (IOException e) { System.err.println("Failed to properly close the BufferedReader."); }
		
	}
 
}
  