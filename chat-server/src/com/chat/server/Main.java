package com.chat.server;

/**
 * Main class of the Server application
 * It creates a Server Gui
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {  
        try {
            javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ServerGui().setVisible(true);
            }
        });
    }
}
