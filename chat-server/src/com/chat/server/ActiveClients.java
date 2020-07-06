package com.chat.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * This class for storing active clients and sending messages
 *
 */
public class ActiveClients {
	
	private static Map<String,ClientHandler> activeClients;//a Map to store active clients
	
	static {
		activeClients = new HashMap<String,ClientHandler>();
	}
	
	/**
	 * adds new client to activeClients
	 * @param client
	 * @param clientThread
	 * @return
	 */
	public static synchronized boolean addClient(String client,ClientHandler clientHandler) {
		if(!activeClients.containsKey(client)) {
			activeClients.put(client, clientHandler);
			return true;
		}
		return false;
	}
	
	/**
	 * Removes a client from activeClients
	 * @param client
	 */
	public static synchronized void removeClient(String client) {
		activeClients.remove(client);
	}
	
	/**
	 * Checks if there is a client among the active clients with given name
	 * @param client
	 * @return
	 */
	public static boolean isClientExist(String client) {
		return activeClients.containsKey(client);
	}
	
	/**
	 * Sends message to all of the active clients other than sender client
	 * @param senderClient
	 * @param message
	 */
	public static synchronized void sendMessageToAllClients(String senderClient,String message) {
		
		for(String client : activeClients.keySet()) {
			if(!senderClient.equals(client)) {
				try {
					OutputStream outputStream = activeClients.get(client).getClientSocket().getOutputStream();
					PrintWriter output = new PrintWriter (outputStream,true);
					output.println(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/**
	 * Sends message to specific client
	 * @param senderClient
	 * @param receiverClient
	 * @param message
	 */
	public static synchronized void sendPrivateMessageToClient(String senderClient,String receiverClient,String message) {
		try {
			
			OutputStream outputStream = activeClients.get(receiverClient).getClientSocket().getOutputStream();
			PrintWriter output = new PrintWriter (outputStream,true);
			output.println(message);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns active client list to method caller
	 * @return
	 */
	public static List<String> getClientList(){
		List<String> clientList = new ArrayList<String>();
		for(String client : activeClients.keySet()) {
			clientList.add(client);
		}
		return clientList;
	}
}
