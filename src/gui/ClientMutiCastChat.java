package gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import stream.ClientMultiCast;

/**
 * The Client Chat interface.
 * @author Nel Bouvier et Carmen Prévot
 * @version 1.0
 */

@SuppressWarnings("serial")
public class ClientMutiCastChat extends JFrame {
	
	/**
	 * The title of the chat window
	 */
    private String titreChat = "Programmation Réseaux - Chat";

    /**
     * The output of the chat, including all the message on this chat channel.
     */
    private JTextPane output = new JTextPane();
    
    /**
     * The text input field, where the client writes his new message.
     */
    private JTextField message = new JTextField();
    
    /**
     * The submit button.
     */
    private JButton boutonEnvoi = new JButton("Envoyer");

    
    /**
     * Creates a new instance of ClientChat.
     * 
     */
    public ClientMutiCastChat() {
    	this.setTitle(titreChat);
        this.initialisation();
        this.identificationClient();
    }

    /**
     * Initializes the chat interface.
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
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ClientMultiCast.closeConnexion();
                e.getWindow().dispose();
            }
        });
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
        this.message.addKeyListener(new KeyAdapter() {
        	public void keyReleased(KeyEvent event) {
        		if (event.getKeyChar() == '\n')
                	ClientMultiCast.sendMessage(message.getText());
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
            	ClientMultiCast.sendMessage(message.getText());
            }
        });
        this.boutonEnvoi.setSize(100, 50);

        // Mise en forme de la fenêtre
        this.setSize(800,700);
        this.setVisible(true);
        this.output.setMargin(new Insets(5, 5, 5, 5));
        
    }
    
    /**
     * Asks for client's nickname.
     * 
     */
    public void identificationClient() {

    	String pseudo;
    	
    	do { pseudo = JOptionPane.showInputDialog(this, "Entrez votre pseudo : ",this.titreChat,  JOptionPane.OK_OPTION); }
    	while(pseudo.equals(""));
    	
    	if(pseudo == null) System.exit(0);
    	
    	ClientMultiCast.setPseudo(pseudo);
		this.setTitle(titreChat + " - Session de " + pseudo);
		
    }

    /**
     * Appends a new message to the chat pane, setting the color of the message.
     * @param tp The chat pane.
     * @param msg The new message to add.
     * @param c The color of the message.
     */
    private void appendToPane(JTextPane tp, String msg, Color c) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }

    /**
     * Shows the new message on the chat interface (calling the appendToPane method).
     * @param readLine The new message.
     * @param color The color of this new message.
     */
	public void afficherNouveauMessage(String readLine, Color color) {
		this.appendToPane(this.output, readLine + "\n", color);
	}
	
	/**
	 * Clears the chat output. Used when the client moves to another channel.
	 */
	public void clearChat() {
		this.output.setText(null);
	}
   
}
