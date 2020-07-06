package com.chat.server;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultCaret;


/**
 * Server Gui class for creating GUI
 * Screen actions (button click) is handled in this class
 */
public class ServerGui extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7466735596022552501L;
	
    private Server server;
    
    //Gui variables
    private DefaultListModel<String> userListModel;
    private JButton btnStartServer;
    private JScrollPane eventsScrollPane;
    private JTextArea eventsTextArea;
    private JLabel labelEvents;
    private JLabel labelUserList;
    private JList<String> userList;
    private JScrollPane userListScrollPane;
    // End of Gui variables declaration        
	
	/**
     * Creates new form ServerGui
     */
    public ServerGui() {
        initComponents();//creates the gui
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    private void initComponents() {
    	userListModel = new DefaultListModel<String>();
        userListScrollPane = new JScrollPane();
        userList = new JList<>();
        labelUserList = new JLabel();
        eventsScrollPane = new JScrollPane();
        eventsTextArea = new JTextArea();
        labelEvents = new JLabel();
        btnStartServer = new JButton();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chat Server");
        setPreferredSize(new java.awt.Dimension(640, 480));

        userList.setModel(userListModel);
        userListScrollPane.setViewportView(userList);

        labelUserList.setText("Users");

        eventsTextArea.setEditable(false);
        eventsTextArea.setColumns(20);
        eventsTextArea.setFont(new java.awt.Font("Arial", 0, 12)); // 
        eventsTextArea.setRows(5);
        DefaultCaret caret = (DefaultCaret)eventsTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        eventsScrollPane.setViewportView(eventsTextArea);

        labelEvents.setText("Events");

        btnStartServer.setText("Start Server");
        btnStartServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartServerActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelUserList)
                        .addGap(150, 150, 150)
                        .addComponent(labelEvents))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnStartServer, GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                            .addComponent(userListScrollPane, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(eventsScrollPane, GroupLayout.PREFERRED_SIZE, 471, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(78, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(labelUserList)
                    .addComponent(labelEvents))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(eventsScrollPane)
                    .addComponent(userListScrollPane, GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(btnStartServer)
                .addContainerGap())
        );

        labelUserList.getAccessibleContext().setAccessibleName("lblUserList");

        setSize(new java.awt.Dimension(752, 516));
        setLocationRelativeTo(null);
    }                      

    /**
     * This method is called when Start Server button clicked
     * It starts a Server in the background and waits for clients
     * @param evt
     */
    private void btnStartServerActionPerformed(java.awt.event.ActionEvent evt) {                                               
    	server = new Server(this);
    	server.start();
    	eventsTextArea.setText("Server Started");
        eventsTextArea.append("\nWaiting users...");
        btnStartServer.setEnabled(false);
    }   
    
    /**
     * This method adds new user to user list on the screen
     * @param username
     */
    public synchronized void addUserToList(String username) {
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
    public synchronized void removeUserFromList(String username) {
    	if(SwingUtilities.isEventDispatchThread())
    		return;
    	SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				userListModel.removeElement(username);
			}
    		
    	});
    	
    }
    
    /**
     * This method appends a new event message to events text box
     * @param message
     */
    public synchronized void addEventToEventsTextArea(String message) {
    	eventsTextArea.append(message);
    }
}
