/***
 * EchoClient
 * Example of a TCP client 
 * Date: 10/01/04
 * Authors:
 */
package stream;

import java.io.*;
import java.net.*;

public class EchoClient {
	
	private static void runWritingThread(Socket sock) {

		new Thread(new Runnable() {

			public void run() {
				
				PrintStream socOut = null;
				try { socOut = new PrintStream(sock.getOutputStream()); }
				catch(IOException e) { System.err.println("Failed to get the socket's output stream."); }
				BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
				String line = null;
				
				while(true) {
					try { line = stdIn.readLine(); }
					catch(IOException e) { System.err.println("Error while reading console data."); }
					
					if (line.substring(0, 1).equals(".")) socOut.println(ClientThread.channelConnectionHeader + line.substring(1));
					else socOut.println(ClientThread.messageHeader + line);
				}
				
			}
			
		}).start();
		
	}
	
	private static void runListeningThread(Socket sock) {
		
		new Thread(new Runnable() {

			public void run() {
				
				BufferedReader socIn = null;
				try { socIn = new BufferedReader(new InputStreamReader(sock.getInputStream())); }
				catch(IOException e) { System.err.println("Failed to get the socket's input stream."); }
				
				while(true) {
					try { System.out.println("New message : " + socIn.readLine()); }
					catch(IOException e) {
						System.err.println("The connexion with he server has been lost.");
						break;
					}
				}
				
				try { socIn.close(); }
				catch(IOException e) { System.err.println("Failed to properly close the BufferedReader."); }
				try { sock.close(); }
				catch(IOException e) { System.err.println("Failed to properly close the Socket."); }
				
			}
		      
		}).start();
	      
	}

	/**
	*  main method
	*  accepts a connection, receives a message from client then sends an echo to the client
	**/
	public static void main(String[] args) throws IOException {
	
		Socket sock = null;
		
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
		
		runWritingThread(sock);
		runListeningThread(sock);
		
	}

}


