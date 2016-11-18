package dfrs.net;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import net.rudp.ReliableSocket;
import net.rudp.ReliableSocketOutputStream;

public class HeartBeatSender {
	private static HeartBeatSender instance;
	private HashMap<String,ReliableSocket> clientSockets;
	
	private HeartBeatSender() {
		clientSockets = new HashMap<String,ReliableSocket>();
	}

	public static synchronized HeartBeatSender getInstance() {
		if (instance == null) {
			instance = new HeartBeatSender();
		}
		return instance;
	}
	
	public void closeSocket() {
		
	}
	
	public synchronized void send(String host, int port, String content) {
		try {
			ReliableSocket socket = clientSockets.get(host+port);
			if(socket == null) {
				socket = new ReliableSocket(host, port);
				clientSockets.put(host+port, socket);
			}
	        ReliableSocketOutputStream outToServer = (ReliableSocketOutputStream) socket.getOutputStream();
	        PrintWriter outputBuffer = new PrintWriter(outToServer);
	        outputBuffer.println(content);
	        outputBuffer.flush();
//	        clientSocket.close();

	    } catch (IOException ex) {
	    	ex.printStackTrace();
	    }
	}
}
