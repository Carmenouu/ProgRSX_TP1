///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {

  /**
   * WebServer constructor.
   */
  protected void start() {
    ServerSocket s;

    System.out.println("Webserver starting up on port 80");
    System.out.println("(press ctrl-c to exit)");
    try {
      // create the main server socket
      s = new ServerSocket(80);
    } catch (Exception e) {
      System.err.println("Error: " + e);
      return;
    }

    System.out.println("Waiting for connection");
    for (;;) {
      try {
        // wait for a connection
        Socket remote = s.accept();
        // remote is now the connected socket
  	    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
  	    BufferedReader socIn = new BufferedReader(new InputStreamReader(remote.getInputStream()));
        PrintStream socOut = new PrintStream(remote.getOutputStream());
        
        System.out.println("New connexion from : " + s.getInetAddress());
        
        while(true) {
	      try {
	    	  System.out.println(socIn.readLine());
	    	  socOut.println(stdIn.readLine());
	      } catch(Exception e) {
	          System.err.println(e);
	          break;
	      }
        }
        
        socIn.close();
        socOut.close();
        remote.close();
      } catch (Exception e) {
        System.err.println("Error: " + e);
        break;
      }
    }
  }

  /**
   * Start the application.
   * 
   * @param args
   *            Command line parameters are not used.
   */
  public static void main(String args[]) {
    WebServer ws = new WebServer();
    ws.start();
  }
}
