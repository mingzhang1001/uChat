package com.chat.client;

/**
 * Main class of the Client application
 * It creates a Client Login Form Gui
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try {
            javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            
        	@Override
        	public void run() {
                new ClientLoginGui().setVisible(true);
            }
        });
    }
    
}
