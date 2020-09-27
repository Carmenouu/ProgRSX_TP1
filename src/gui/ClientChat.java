/**
 * 
 * 
 */
package gui ;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;

import org.eclipse.swt.layout.RowLayout;

import stream.ClientThread;
import stream.EchoClient;

/**
 * 
 * @author Nel Bouvier & Carmen Prévot
 * @version 1.0
 */

@SuppressWarnings("serial")
public class ClientChat extends JFrame {
    private String titreChat = "Programmation Réseaux - Chat";
    private String pseudo = null;

    private JTextArea output = new JTextArea();
    private JTextField message = new JTextField();
    private JButton boutonEnvoi = new JButton("Envoyer");
	private String hostname;
	private String port;
	private Socket sock = null ;

    
    /**
     * 
     * 
     */
    public ClientChat(String parHostname, String parPort) {
    	this.hostname = parHostname ;
    	this.port = parPort ;
    	this.setTitle(titreChat);
        this.initialisation();
        this.identificationClient();
    }

    /**
     * 
     */
    public void initialisation() {
    	this.setAlwaysOnTop(true);
        
    	// Création de la fenêtre
        JPanel panel = (JPanel)this.getContentPane();
        getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
        JScrollPane scroll = new JScrollPane(output);
        panel.add(scroll);
        JPanel zoneEcriture = new JPanel();
        GridBagLayout gbl_zoneEcriture = new GridBagLayout();
        gbl_zoneEcriture.columnWidths = new int[] {40, 500, 40, 100};
        gbl_zoneEcriture.rowHeights = new int[] {20, 150, 20};
        gbl_zoneEcriture.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0};
        gbl_zoneEcriture.rowWeights = new double[]{0.0, 0.0, 0.0};
        zoneEcriture.setLayout(gbl_zoneEcriture);
        panel.add(zoneEcriture);
                GridBagConstraints gbc_message = new GridBagConstraints();
                gbc_message.gridheight = 2;
                gbc_message.fill = GridBagConstraints.BOTH;
                gbc_message.insets = new Insets(0, 0, 0, 5);
                gbc_message.gridx = 1;
                gbc_message.gridy = 1;
                zoneEcriture.add(this.message, gbc_message);
                
                // Appui sur entrée
                message.addKeyListener(new KeyAdapter() {
                	public void keyReleased(KeyEvent event) {
                		if (event.getKeyChar() == '\n')
                			envoiMessage(null);
                	}
                });
                this.message.requestFocus();
                this.message.setSize(500, 150);
                this.message.setMargin(new Insets(10,10,10,10));
                        GridBagConstraints gbc_boutonEnvoi = new GridBagConstraints();
                        gbc_boutonEnvoi.fill = GridBagConstraints.HORIZONTAL;
                        gbc_boutonEnvoi.insets = new Insets(0, 0, 5, 5);
                        gbc_boutonEnvoi.gridx = 3;
                        gbc_boutonEnvoi.gridy = 1;
                        zoneEcriture.add(this.boutonEnvoi, gbc_boutonEnvoi);
                        
                                // Gestion action d'envoi
                                boutonEnvoi.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        envoiMessage(e);
                                    }
                                });
                                this.boutonEnvoi.setSize(100, 50);

        // Mise en forme de la fenêtre
        this.setSize(800,700);
        this.setVisible(true);
        this.output.setSize(550, 350);
        this.output.setBackground(new Color(220,220,220));
        this.output.setMargin(new Insets(10,10,10,10));
        this.output.setEditable(false);
        
    }
    /**
     * 
     */

    public void identificationClient() {
         this.pseudo = JOptionPane.showInputDialog(this, "Entrez votre pseudo : ",this.titreChat,  JOptionPane.OK_OPTION);
         if (this.pseudo != null ){
			this.setTitle(titreChat + " - Session de " + pseudo);
         }
		else  
        	System.exit(0);
    }


    /**
     * 
     */
    
    public void envoiMessage(ActionEvent e) {
    	String msg=this.message.getText();
    	
    	if(msg!=null && !msg.isEmpty() && msg!="/n"){
    		
    		try {
    			sock = new Socket(hostname, Integer.parseInt(port));
    			System.out.println("Connected to " + sock.getInetAddress());
    		} catch (UnknownHostException e1) {
    			System.err.println("Don't know about host:" + hostname);
    			System.exit(1);
    		} catch (IOException e1) {
    			System.err.println("Couldn't get I/O for " + "the connection to:"+ hostname);
    			System.exit(1);
    		}
    		
    		this.afficherNouveauMessage(pseudo + " : " + msg);
    		this.message.setText("");
    		this.message.requestFocus();
    		
    		new Thread(new Runnable() {

    			public void run() {
    				
    				PrintStream socOut = null;
    				try { socOut = new PrintStream(sock.getOutputStream()); }
    				catch(IOException e) { System.err.println("Failed to get the socket's output stream."); }
    				BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    				String line = pseudo + " : " + msg;
    				//if (msg.equals(".")) socOut.println(ClientThread.channelConnectionHeader + msg);
					//else 
						socOut.println(ClientThread.messageHeader + line);		
    				
    			}
    			
    		}).run();
    		
    	}
    	else
    		System.exit(0);
    }
    


	public void afficherNouveauMessage(String readLine) {
		this.output.append(readLine+"\n");
		
	}
   
}