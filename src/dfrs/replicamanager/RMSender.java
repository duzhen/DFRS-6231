package dfrs.replicamanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.rudp.ReliableSocket;

class RMSender {
	private static RMSender instance;
	private HashMap<String,ReliableSocket> clientSockets;
	
	private RMSender() {
		clientSockets = new HashMap<String,ReliableSocket>();
	}

	public static synchronized RMSender getInstance() {
		if (instance == null) {
			instance = new RMSender();
		}
		return instance;
	}
	
	public void closeSocket() {
		Iterator iter = this.clientSockets.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			ReliableSocket value = (ReliableSocket) entry.getValue();
			if (value != null && !value.isClosed()) {
				try {
					value.close();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}
	
	public synchronized String send(String host, int port, String content) {
		try {
			ReliableSocket socket = clientSockets.get(host+port);
			if(socket == null || socket.isClosed()) {
				socket = new ReliableSocket(host, port);
				clientSockets.put(host+port, socket);
			}
//	        ReliableSocketOutputStream outToServer = (ReliableSocketOutputStream) socket.getOutputStream();
	        PrintWriter outputBuffer = new PrintWriter(socket.getOutputStream());
	        outputBuffer.println(content);
	        outputBuffer.flush();
	        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        String read = inFromServer.readLine();
	        return read;
//	        socket.close();

	    } catch (IOException ex) {
	    	ex.printStackTrace();
	    }
		return "";
	}
}
