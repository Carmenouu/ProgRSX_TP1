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
	
	private final static String CHANNEL_CHANGE_COMMAND = "channel";
	
	private int channel;
	private Socket socket;
	
	ClientThread(Socket s, int channel) {
		
		this.socket = s;
		this.channel = channel;
		
		EchoServerMultiThreaded.connectClient(this.socket, this.channel);
		
	}
	
	public void changeChannel(int fromChannel, int toChannel) {
		
		EchoServerMultiThreaded.disconnectClient(this.socket, fromChannel);
		EchoServerMultiThreaded.connectClient(this.socket, toChannel);
		this.channel = toChannel;
		
	}
	
	private void processMessage(String message) {
		
		if(message.substring(0, 1).equals("/")) {
		
			switch(message.substring(1, message.indexOf(" "))) {
			
				case CHANNEL_CHANGE_COMMAND: this.changeChannel(this.channel, Integer.parseInt(message.substring(message.indexOf(" ") + 1))); break;
				
				default: System.out.println("[Channel " + this.channel + "] Could not read command : " + message); break;
				
			}
			
		} else {
			System.out.println("[Channel " + this.channel + "] New message from " + this.socket.getInetAddress());
			EchoServerMultiThreaded.sendMessage(message, this.channel);
		}
		
	}
	
	/**
	* receives a request from client then sends an echo to the client
	* @param socket the client socket
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
		
		EchoServerMultiThreaded.disconnectClient(this.socket, this.channel);
		
		try { socIn.close(); }
		catch (IOException e) { System.err.println("Failed to properly close the BufferedReader."); }
		
	}
 
}
  