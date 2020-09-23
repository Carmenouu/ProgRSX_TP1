package http.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class WebPing {
  public static void main(String[] args) {
  
      if (args.length != 2) {
      	System.err.println("Usage java WebPing <server host name> <server port number>");
      	return;
      }	
  
   String httpServerHost = args[0];
    int httpServerPort = Integer.parseInt(args[1]);
     httpServerHost = args[0];
      httpServerPort = Integer.parseInt(args[1]);

    try {
      Socket sock = new Socket(httpServerHost, httpServerPort);
	  BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
	  BufferedReader socIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
      PrintStream socOut = new PrintStream(sock.getOutputStream());
      
      System.out.println("Connected to " + sock.getInetAddress());
      
      while(true) {
	      try {
	    	  socOut.println(stdIn.readLine());
	    	  System.out.println(socIn.readLine());
	      } catch(Exception e) {
	          System.err.println(e);
	          break;
	      }
      }
      
      socIn.close();
      socOut.close();
      sock.close();
    } catch (java.io.IOException e) {
      System.err.println("Can't connect to " + httpServerHost + ":" + httpServerPort);
      System.err.println(e);
    }
  }
}