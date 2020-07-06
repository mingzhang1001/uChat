package com.chat.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * This class for client message handling 
 *
 */
public class ClientHandler implements Runnable {

	private ServerGui serverGui; //serverGui Object to update Server screen
	private Socket clientSocket; //Connected client's socket, to send and receive via this socket
	private String username; //Client's username
	
	public ClientHandler(Socket clientSocket,ServerGui serverGui) {
		this.clientSocket = clientSocket;
		this.serverGui = serverGui;
	}
	
	@Override
	public void run() {
		try {
			//Receive data from client
			receiveMessage();
		} catch (IOException e) {
			//This means client is disconnected from server
			//Update screen
			StringBuffer eventMessage = new StringBuffer();
			eventMessage.append("\n").append(getUsername()).append(" is disconnected");
			
			serverGui.addEventToEventsTextArea(eventMessage.toString());
			serverGui.removeUserFromList(getUsername());
			
			//remove from active clients
			ActiveClients.removeClient(getUsername());
			
			//Inform other clients that this user disconnected from Server
			ActiveClients.sendMessageToAllClients(getUsername(), "LOGOUT:"+getUsername());
			
		}
	}
	
	private void receiveMessage() throws IOException {
		boolean hasData = false;
		StringBuffer receiveData = new StringBuffer();
		
		while(true) {
			hasData = SocketUtil.readFromSocket(clientSocket, receiveData);///Read data from Client's socket
			if(hasData) {//If there is a new data, we will handle it
				
				//Received data has two parts seperated by : (colon) character
				String dataStr = receiveData.toString();
				int indexOf = dataStr.indexOf(":");
				String command = dataStr.substring(0,indexOf);//command part
				String data = dataStr.substring(indexOf+1); //Message data part
				
				if("LOGIN".equals(command)) {//If new user connects, it sends LOGIN command
					String username = data;//second part is username for this command
					if(ActiveClients.isClientExist(username)) {//Check if username already exists
						System.out.println("User exists : "+username);
						sendMessageToCurrentClient("FAIL");//Username exists, send FAIL message to client
					}else {//username is available
						System.out.println("Login successfull : "+username);
						ActiveClients.addClient(username,this);//add this client to active clients
						setUsername(username);//sets name
						
						serverGui.addEventToEventsTextArea("\n"+username+ " is connected");//Update serverGui screen
						serverGui.addUserToList(username);//Update serverGui screen user list
						
						sendMessageToCurrentClient("SUCCESS");//Send SUCCESS message to client
						
						//Inform other clients that this user connected to Server 
						ActiveClients.sendMessageToAllClients(getUsername(), "NEWLOGIN:"+getUsername());
					}
				}else if("MESSAGE".equals(command)) {//If user sends a message, it sends MESSAGE command
					//second part is message for this command
					
					//Checks if it is a private message
					if(data.indexOf("\\") >= 0){
						int i = data.indexOf("\\");
						String temp = data.substring(i + 1);
						if(temp.indexOf("\\") > 0) {
							int j = temp.indexOf("\\");
							String privateMessageUser = temp.substring(0, j);//client wants to send this user
							
							boolean clientExist = ActiveClients.isClientExist(privateMessageUser);//check user exists
							if(clientExist) {//if exists send message to that client
								StringBuffer privateMessage = new StringBuffer();
								privateMessage.append("MESSAGE:").append(getUsername()).append(" : ").append(temp.substring(j+1));
								
								//Send message to specific client
								ActiveClients.sendPrivateMessageToClient(getUsername(), privateMessageUser, privateMessage.toString());
							}else {
								StringBuffer userNotExistMessage = new StringBuffer();
								userNotExistMessage.append("MESSAGE:").append("Message could not be sent. ").append(privateMessageUser).append(" is not exists");
								sendMessageToCurrentClient(userNotExistMessage.toString());
							}
							continue;
						}
					}
					
					StringBuffer messageToOtherClients = new StringBuffer();
					messageToOtherClients.append(command).append(":").append(getUsername()).append(" : ").append(data);
					
					//Send message to all clients
					ActiveClients.sendMessageToAllClients(getUsername(), messageToOtherClients.toString());
				}else if("USERLIST".equals(command)) {//When new client connects, it requests all active clients to fill it's user list
					List<String> clientList = ActiveClients.getClientList();//Returns active clients
					
					//Sends all active clients
					for(String clientName : clientList) {
						if(!clientName.equals(username)) {
							sendMessageToCurrentClient("USERLIST:"+clientName);
						}
					}
				}else if("NEWUSERNAME".equals(command)) {//When client changes it's username, it sends this command
					//second part is new username for this command
					String newUsername = data;
					
					if(ActiveClients.isClientExist(newUsername)) {//checks if new username already exists
						sendMessageToCurrentClient("NEWUSERNAME:FAIL");//Username already exists, send FAIL message to client
					}else {
						//new username doesn't exists, now we will change it's username
						
						StringBuffer messageToOtherClients = new StringBuffer();//a message to inform other clients
						messageToOtherClients.append("MESSAGE:").append(getUsername()).append(" has been changed to ").append(newUsername);
						
						//remove from active clients
						ActiveClients.removeClient(getUsername());
						//delete old username from server gui user list
						serverGui.removeUserFromList(getUsername());
						
						//Inform other clients to delete old username from their user list
						ActiveClients.sendMessageToAllClients(getUsername(), "DELETEUSER:"+getUsername());
						
						//set new username
						setUsername(newUsername);
						
						//add to active clients
						ActiveClients.addClient(newUsername, this);
						//add new username to server gui user list
						serverGui.addUserToList(newUsername);
						
						//Inform other clients to add new username to their user list
						ActiveClients.sendMessageToAllClients(getUsername(), "ADDUSER:"+getUsername());
						
						//Inform other clients which username changed
						ActiveClients.sendMessageToAllClients(getUsername(), messageToOtherClients.toString());
						
						//Update server screen events
						serverGui.addEventToEventsTextArea("\n"+messageToOtherClients.toString().substring(8));
						
						//send SUCCESS message to client
						sendMessageToCurrentClient("NEWUSERNAME:SUCCESS");
					}
				}
			}
		}
	}
	
	/**
	 * this method sends message to connected client
	 * @param message
	 */
	private void sendMessageToCurrentClient(String message) {
		try {
			OutputStream outputStream = clientSocket.getOutputStream();
			PrintWriter output = new PrintWriter (outputStream,true);
			output.println(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public Socket getClientSocket() {
		return clientSocket;
	}
}
