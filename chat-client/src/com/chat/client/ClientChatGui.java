package com.chat.client;

import java.io.IOException;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author ismail
 */
public class ClientChatGui extends javax.swing.JFrame implements Runnable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7784067247729276142L;
	
	private Socket socket;
	private String username;
	
	//Gui variables
	private DefaultListModel<String> userListModel;
    private javax.swing.JButton btnEditUsername;
    private javax.swing.JButton btnSendMessage;
    private javax.swing.JScrollPane userLisctScrollPane;
    private javax.swing.JScrollPane messageAreaScrollPane;
    private javax.swing.JLabel lblUsers;
    private javax.swing.JTextArea messageTextArea;
    private javax.swing.JTextField txtMessage;
    private javax.swing.JTextField txtUsername;
    private javax.swing.JList<String> userList;
    // End of Gui variables declaration      
	
	/**
     * Creates new form ClientChatGui
     */
    public ClientChatGui(Socket socket,String username) {
        initComponents();
        this.socket = socket;
        this.username = username;
        txtUsername.setText(username);
        requestUserlistFromServer();
    }
    
    private void requestUserlistFromServer() {
    	StringBuffer data = new StringBuffer();
    	data.append("USERLIST:");
    	sendMessageDataToServer(data.toString());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    private void initComponents() {
    	
    	userListModel = new DefaultListModel<String>();
        userLisctScrollPane = new javax.swing.JScrollPane();
        userList = new javax.swing.JList<>();
        lblUsers = new javax.swing.JLabel();
        messageAreaScrollPane = new javax.swing.JScrollPane();
        messageTextArea = new javax.swing.JTextArea();
        txtMessage = new javax.swing.JTextField();
        btnSendMessage = new javax.swing.JButton();
        txtUsername = new javax.swing.JTextField();
        btnEditUsername = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Client Chat");

        userList.setModel(userListModel);
        userLisctScrollPane.setViewportView(userList);

        lblUsers.setText("Users");

        messageTextArea.setEditable(false);
        messageTextArea.setColumns(20);
        messageTextArea.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        messageTextArea.setRows(5);
        DefaultCaret caret = (DefaultCaret)messageTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        messageAreaScrollPane.setViewportView(messageTextArea);

        btnSendMessage.setText("Send");
        btnSendMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendMessageActionPerformed(evt);
            }
        });

        btnEditUsername.setText("Edit Username");
        btnEditUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditUsernameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblUsers)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnEditUsername, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                            .addComponent(userLisctScrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(txtUsername, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(txtMessage)
                                .addGap(18, 18, 18)
                                .addComponent(btnSendMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(messageAreaScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 536, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(lblUsers)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(messageAreaScrollPane)
                    .addComponent(userLisctScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSendMessage))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEditUsername)
                .addGap(19, 19, 19))
        );

        setSize(new java.awt.Dimension(752, 516));
        setLocationRelativeTo(null);
    }                        
    
    //Edit username action method
    private void btnEditUsernameActionPerformed(java.awt.event.ActionEvent evt) {                                                
        String newUsername = txtUsername.getText();
        if(!"".equals(newUsername) && !newUsername.equals(this.username)) {
        	StringBuffer data = new StringBuffer();
    		data.append("NEWUSERNAME:").append(newUsername);
    		
    		//Send new username information to Server than wait for response
    		sendMessageDataToServer(data.toString());
    		
    		//disable username text and edit button until receive response message from server
    		txtUsername.setEditable(false);
    		btnEditUsername.setEnabled(false);
        }
    }                                               

    //Send message action method
    private void btnSendMessageActionPerformed(java.awt.event.ActionEvent evt) {
    	String message = txtMessage.getText();
    	if(!"".equals(message)) {
    		StringBuffer data = new StringBuffer();
    		data.append("MESSAGE:").append(message);
    		sendMessageDataToServer(data.toString());
    		txtMessage.setText("");

    		StringBuffer messageLine = new StringBuffer();
    		messageLine.append(this.username).append(" : ").append(message).append("\n");
    		addMessageToTextArea(messageLine.toString());
    	}
    }
    
    private void sendMessageDataToServer(String data) {
    	try {
			socket.getOutputStream().write(data.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private synchronized void addMessageToTextArea(String message) {
    	messageTextArea.append(message);
    }
    
	@Override
	public void run() {
		
		try {
			boolean hasData = false;
			StringBuffer receiveData = new StringBuffer();
			
			while(true) {
				hasData = SocketUtil.readFromSocket(socket, receiveData);
				if(hasData) {//Data received from server
					
					//data has two parts seperated by : (colon) character
					String dataStr = receiveData.toString();
					int indexOf = dataStr.indexOf(":");
					String command = dataStr.substring(0,indexOf);//command part
					String data = dataStr.substring(indexOf+1).trim();//message data part
					if("MESSAGE".equals(command)) {//A new message is received from server
						StringBuffer messageLine = new StringBuffer();
						messageLine.append(data).append("\n");
						addMessageToTextArea(messageLine.toString());//Update screen
					}else if("NEWLOGIN".equals(command)) {//new client connected to server
						StringBuffer messageLine = new StringBuffer();
						messageLine.append(data).append(" is connected").append("\n");
						addMessageToTextArea(messageLine.toString());
						addUserToList(data.trim());
					}else if("LOGOUT".equals(command)) {//a client disconnected from server
						StringBuffer messageLine = new StringBuffer();
						messageLine.append(data).append(" is disconnected").append("\n");
						addMessageToTextArea(messageLine.toString());
						removeUserFromList(data.trim());
					}else if("USERLIST".equals(command)) {//active clients are received
						addUserToList(data.trim());
					}else if("NEWUSERNAME".equals(command)) {//change username response message is received
						if("SUCCESS".equals(data)) {//change username is successfull
							this.username = txtUsername.getText(); // update username
							addMessageToTextArea("Username changed\n");//update screen
						}else {
							addMessageToTextArea("Username change failed. Username exists.\n");//update screen with fail message
						}
						
						//Enable username text and edit button
						txtUsername.setEditable(true);
			    		btnEditUsername.setEnabled(true);
					}else if("DELETEUSER".equals(command)) {
						removeUserFromList(data.trim());
					}else if("ADDUSER".equals(command)) {
						addUserToList(data.trim());
					}
				}
			}
		} catch (Exception e) {
			this.setVisible(false);
			this.dispose();
			System.exit(0);
		}finally {
			if(socket != null && !socket.isClosed()) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
	 /**
     * This method adds new user to user list on the screen
     * @param username
     */
    public void addUserToList(String username) {
    	if(SwingUtilities.isEventDispatchThread())
    		return;
    	SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				userListModel.addElement(username);
			}
		});
    	
    }
    
    /**
     * This method deletes user from user list on the screen
     * @param username
     */
    public void removeUserFromList(String username) {
    	if(SwingUtilities.isEventDispatchThread())
    		return;
    	SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				userListModel.removeElement(username);
			}
    		
    	});
    	
    }
}
