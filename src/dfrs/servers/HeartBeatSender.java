package dfrs.servers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.rudp.ReliableSocket;
import net.rudp.ReliableSocketOutputStream;

class HeartBeatSender {
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
	
	public synchronized void send(String host, int port, String content) {
		try {
			ReliableSocket socket = clientSockets.get(host+port);
			if(socket == null || socket.isClosed()) {
				socket = new ReliableSocket(host, port);
				clientSockets.put(host+port, socket);
			}
	        ReliableSocketOutputStream outToServer = (ReliableSocketOutputStream) socket.getOutputStream();
	        PrintWriter outputBuffer = new PrintWriter(outToServer);
	        outputBuffer.println(content);
	        outputBuffer.flush();
//	        socket.close();

	    } catch (IOException ex) {
	    	ex.printStackTrace();
	    }
	}
}
