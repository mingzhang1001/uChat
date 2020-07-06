package com.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class for creating Server Socket in background and listening new clients
 *
 */
public class Server extends Thread {
	
	private ServerGui serverGui;//serverGui Object to update Server screen
	
	private final int PORT_NUM = 7889;//Port number to communicate between server and clients
	
	public Server(ServerGui serverGui) {//We passed the ServerGui object to constructor
		this.serverGui = serverGui;
	}

	@Override
	public void run() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(PORT_NUM);//Creates a ServerSocket
			do {
				Socket clientSocket = serverSocket.accept();//Waits until a client connects
				
				ClientHandler newClient = new ClientHandler(clientSocket,serverGui);//Creates a clientHandler
				new Thread(newClient).start();//newClient starts in the background with another Thread
				
			}while(true);//endless loop for accepting multiple clients
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(serverSocket != null && !serverSocket.isClosed()) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
