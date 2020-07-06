package com.chat.client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

/**
 *	Client Login Form
 */
public class ClientLoginGui extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4512995199078267686L;
	
	private final int PORT_NUM = 7889;//Port number to communicate between server and client
	
	//Gui variables                     
    private JButton btnLogin;
    private JLabel lblError;
    private JLabel lblUsername;
    private JTextField txtUsername;
    private JLabel lblServerIp;
    private JTextField txtServerIpAddress;
    // End of Gui variables declaration     
	
	/**
     * Creates new form ClientLoginGui
     */
    public ClientLoginGui() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    private void initComponents() {

    	lblUsername = new JLabel();
        btnLogin = new JButton();
        txtUsername = new JTextField();
        lblError = new JLabel();
        lblServerIp = new JLabel();
        txtServerIpAddress = new JTextField();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Client Login");

        lblUsername.setText("Username : ");

        btnLogin.setText("Login");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        lblError.setForeground(new java.awt.Color(255, 0, 0));

        lblServerIp.setText("Server Ip Address :");

        txtServerIpAddress.setText("127.0.0.1");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(81, 81, 81)
                        .addComponent(lblError, GroupLayout.PREFERRED_SIZE, 191, GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnLogin, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE))
                        .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(81, 81, 81)
                                    .addComponent(lblUsername, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(39, 39, 39)
                                    .addComponent(lblServerIp, GroupLayout.PREFERRED_SIZE, 107, GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtUsername, GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                                .addComponent(txtServerIpAddress)))))
                .addContainerGap(49, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addComponent(lblError)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUsername)
                    .addComponent(txtUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblServerIp)
                    .addComponent(txtServerIpAddress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(btnLogin)
                .addGap(47, 47, 47))
        );

        setSize(new java.awt.Dimension(345, 250));
        setLocationRelativeTo(null);
    }                  

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {                                         
        if(txtUsername.getText() != null && !"".equals(txtUsername.getText())) {//check if username is not empty
        	
        	txtUsername.setEditable(false);
        	btnLogin.setEnabled(false);
        	
        	Socket socket = null;
        	try{
        		socket = new Socket(txtServerIpAddress.getText(), PORT_NUM);//connecting to localhost server
        		
        		//Connection successful
        		
        		//Now we create a login message with username
        		StringBuffer message = new StringBuffer();
        		message.append("LOGIN:");
        		message.append(txtUsername.getText());
        		
        		//send login message to server
        		sendMessageToServer(socket, message.toString());
        		
        		//Wait for server response
        		SocketUtil.readFromSocket(socket, message);
        		if("SUCCESS".equals(message.toString().trim())) {//if login is succefull
        			lblError.setText("Login success.");
        			
        			//close login form and open client chat form
        			this.setVisible(false);
        			ClientChatGui clientChat = new ClientChatGui(socket,txtUsername.getText());
        			clientChat.setVisible(true);
        			
        			//create a background to listen message from server
        			new Thread(clientChat).start();
        			
        		}else {//Login failed
        			lblError.setText("Login failed. Username exists.");
        			txtUsername.setEditable(true);
                	btnLogin.setEnabled(true);
        		}
        		
			}catch (ConnectException e) {
				//connection error
				lblError.setText("Failed to connect Server.");
    			txtUsername.setEditable(true);
            	btnLogin.setEnabled(true);
            	JOptionPane.showMessageDialog(this, "Server does not exist. Check server ip address.","Error",JOptionPane.ERROR_MESSAGE);
			}catch (UnknownHostException e) {
				//connection error
				lblError.setText("Failed to connect Server.");
    			txtUsername.setEditable(true);
            	btnLogin.setEnabled(true);
            	JOptionPane.showMessageDialog(this, "Server does not exist. Check server ip address.","Error",JOptionPane.ERROR_MESSAGE);
			}catch (Exception e) {
				e.printStackTrace();
			}
        }
    }
    
    private void sendMessageToServer(Socket socket,String message) {
    	try {
			socket.getOutputStream().write(message.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}