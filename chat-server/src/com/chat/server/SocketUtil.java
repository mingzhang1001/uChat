package com.chat.server;

import java.io.IOException;
import java.net.Socket;

public class SocketUtil {
	/**
	 * Read message from client's socket
	 * @return
	 * @throws IOException
	 */
	public static boolean readFromSocket(Socket socket,StringBuffer messageBuffer) throws IOException {
		boolean hasData = true;
		messageBuffer.setLength(0);//empty message buffer before read from socket
		do {
			int readData = socket.getInputStream().read();//waits until get a message
			if (readData <= 0) {
				if (messageBuffer.length() <= 0) {//It means we didn't get any message
					hasData = false;
				}
				break;
			}
			char readSingleChar = (char) readData;
			if(readSingleChar == '\n') {//end of line, it means end of the message
				
				break;
			}
			messageBuffer.append(readSingleChar);//append the data to the messageBuffer 
		} while (socket.getInputStream().available() > 0);//check if there is a data in the socket input
		return hasData;
	}
}