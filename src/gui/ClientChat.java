/**
 * 
 * 
 */
package gui ;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import stream.EchoClient;

/**
 * 
 * @author Nel Bouvier & Carmen Prévot
 * @version 1.0
 */

@SuppressWarnings("serial")
public class ClientChat extends JFrame {
	
    private String titreChat = "Programmation Réseaux - Chat";

    private JTextPane output = new JTextPane();
    private JTextField message = new JTextField();
    private JButton boutonEnvoi = new JButton("Envoyer");

    
    /**
     * 
     * 
     */
    public ClientChat() {
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
                this.message.addKeyListener(new KeyAdapter() {
                	public void keyReleased(KeyEvent event) {
                		if (event.getKeyChar() == '\n')
                        	EchoClient.sendMessage(message.getText());
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
                                    	EchoClient.sendMessage(message.getText());
                                    }
                                });
                                this.boutonEnvoi.setSize(100, 50);

        // Mise en forme de la fenêtre
        this.setSize(800,700);
        this.setVisible(true);
        this.output.setMargin(new Insets(5, 5, 5, 5));
        
    }
    /**
     * 
     */

    public void identificationClient() {

    	String pseudo;
    	
    	do { pseudo = JOptionPane.showInputDialog(this, "Entrez votre pseudo : ",this.titreChat,  JOptionPane.OK_OPTION); }
    	while(pseudo.equals(""));
    	
    	if(pseudo == null) System.exit(0);
    	
    	EchoClient.setPseudo(pseudo);
		this.setTitle(titreChat + " - Session de " + pseudo);
		
    }

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

	public void afficherNouveauMessage(String readLine, Color color) {
		this.appendToPane(this.output, readLine + "\n", color);
	}
	
	public void clearChat() {
		this.output.setText(null);
	}
   
}