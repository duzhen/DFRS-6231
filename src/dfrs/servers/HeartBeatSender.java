package dfrs.servers;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class HeartBeatSender {
	private static HeartBeatSender instance;
//	private HashMap<String,ReliableSocket> clientSockets;
	private HashMap<String,DatagramSocket> clientSockets;
	private HeartBeatSender() {
		clientSockets = new HashMap<String,DatagramSocket>();
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
			DatagramSocket value = (DatagramSocket) entry.getValue();
			if (value != null && !value.isClosed()) {
				try {
					value.close();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}
	
	public synchronized void send(String host, int port, String content) {
		try {
			DatagramSocket socket = clientSockets.get(host+port);
			if(socket == null || socket.isClosed()) {
				socket = new DatagramSocket();
				clientSockets.put(host+port, socket);
			}
			byte[] m = content.getBytes();
			DatagramPacket request = new DatagramPacket(m, m.length, InetAddress.getByName(host), port);
			socket.send(request);
		} catch(Exception e) {
			e.printStackTrace();
		}
//		try {
//			ReliableSocket socket = clientSockets.get(host+port);
//			if(socket == null || socket.isClosed()) {
//				socket = new ReliableSocket(host, port);
//				clientSockets.put(host+port, socket);
//			}
//	        ReliableSocketOutputStream outToServer = (ReliableSocketOutputStream) socket.getOutputStream();
//	        PrintWriter outputBuffer = new PrintWriter(outToServer);
//	        outputBuffer.println(content);
//	        outputBuffer.flush();
////	        socket.close();
//
//	    } catch (IOException ex) {
//	    	ex.printStackTrace();
//	    }
	}
}
