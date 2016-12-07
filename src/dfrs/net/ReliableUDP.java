package dfrs.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TransferQueue;

import dfrs.net.ReliableUDP.ReliableUDPServer.DataPackage;

public class ReliableUDP {

	class ReliableSocket {
		private ReliableSocket() {
			
		}
	}
	
	class ReliableUDPServer {
		private LinkedBlockingQueue<TransferQueue<String>> accept;
		private LinkedBlockingQueue<TransferQueue<String>> queue;
		private int port;
		private DatagramSocket socket;
		private HashMap<String, String> connections;
		private boolean isRead = true;
		private String id;
		private int expireID = 0;
		private Queue<DataPackage> priorityQueue;
		private Timer timer;
		private DatagramPacket request;
		public ReliableUDPServer(int port) throws SocketException {
			this.port = port;
			this.socket = new DatagramSocket(port);
			this.queue = new LinkedBlockingQueue<TransferQueue<String>>();
			this.accept = new LinkedBlockingQueue<TransferQueue<String>>();
			this.connections = new HashMap<String, String>();
			this.priorityQueue = new PriorityQueue<DataPackage>();
			try {
				this.timer.schedule(new TimerTask() {
					public void run() {
						if(priorityQueue.size()!=0) {
							if(request!=null) {
								try {
									replyToClient(request, expireID);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}, 1000, 2000);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
		

		class DataPackage {
		 
		    private int id;
		    private String content;
		 
		    public DataPackage(int i, String c){
		        this.id=i;
		        this.content=c;
		    }
		 
		    public int getId() {
		        return id;
		    }
		 
		    public String getContent() {
		        return content;
		    }
		}
		public Comparator<DataPackage> idComparator = new Comparator<DataPackage>(){
			
			@Override
			public int compare(DataPackage c1, DataPackage c2) {
				return (int) (c1.getId() - c2.getId());
			}
		};

		public String read() throws IOException {
			byte[] buffer = new byte[1000];
			DatagramPacket request = new DatagramPacket(buffer, buffer.length);
			while(true) {
				socket.receive(request);
				String receive = new String(request.getData(), 0, request.getLength()).trim();
				if(id == null) {
					if("hand".equals(receive)) {
						id = UUID.randomUUID().toString();
						replyToClient(request, ++expireID);
						connections.put(id, request.getAddress().getHostName());
					}
				} else {
					String ip = connections.get(id);
					if(ip != null && ip.equals(request.getAddress().getHostName())) {
						if(receive!=null) {
							String[] params = receive.split("@");
							if(params.length>1) {
								if(Integer.valueOf(params[0]) == expireID) {
									replyToClient(request, ++expireID);
									return params[1];
								} else if(Integer.valueOf(params[0]) > expireID) {
									priorityQueue.add(new DataPackage(Integer.valueOf(params[0]), params[1]));
									if(priorityQueue.peek().id == expireID)	{
										replyToClient(request, ++expireID);
										return priorityQueue.poll().content;
									}
								}
								
							}
						}
						return receive;
					} else {
						throw new IOException();
					}
				}
			}
		}

		private void replyToClient(DatagramPacket request, int id) throws IOException {
			request.setData((id+"@"+id).getBytes());
			DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(),
					request.getPort());
			socket.send(reply);
		}
		
		public boolean close() {
			if(socket!=null) {
				socket.close();
				return true;
			} else {
				return false;
			}
		}
	}
	
	class ReliableUDPClient {
		private LinkedBlockingQueue<TransferQueue<String>> queue;
		private int port;
		private DatagramSocket socket;
		private String ip;
		private String id;
		private int expireID = 0;
		private Queue<DataPackage> priorityQueue;
		private Timer timer;
		private DatagramPacket request;
		
		public ReliableUDPClient(String ip, int port) throws SocketException {
			this.ip = ip;
			this.port = port;
			queue = new LinkedBlockingQueue<TransferQueue<String>>();
			socket = new DatagramSocket();
		}
		
		public boolean hand() throws IOException {
			send("hand");
			return true;
		}
		public void send(String content) throws IOException {
			byte[] m = content.getBytes();
			InetAddress aHost = InetAddress.getByName(ip);
			DatagramPacket request = new DatagramPacket(m, m.length, aHost, port);
			socket.send(request);
		}
		
		private void replyToClient(DatagramPacket request, int id) throws IOException {
			request.setData((id+"@"+id).getBytes());
			DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(),
					request.getPort());
			socket.send(reply);
		}
		
		public String read() throws IOException {
			byte[] buffer = new byte[1000];
			DatagramPacket request = new DatagramPacket(buffer, buffer.length);
			while(true) {
				socket.receive(request);
				String receive = new String(request.getData(), 0, request.getLength()).trim();
				if(id == null) {
					if("hand".equals(receive)) {
						id = UUID.randomUUID().toString();
						replyToClient(request, ++expireID);
//						connections.put(id, request.getAddress().getHostName());
					}
				} else {
//					String ip = connections.get(id);
					if(ip != null && ip.equals(request.getAddress().getHostName())) {
						if(receive!=null) {
							String[] params = receive.split("@");
							if(params.length>1) {
								if(Integer.valueOf(params[0]) == expireID) {
									replyToClient(request, ++expireID);
									return params[1];
								} else if(Integer.valueOf(params[0]) > expireID) {
//									priorityQueue.add(new DataPackage(Integer.valueOf(params[0]), params[1]));
									if(priorityQueue.peek().id == expireID)	{
										replyToClient(request, ++expireID);
										return priorityQueue.poll().content;
									}
								}
								
							}
						}
						return receive;
					} else {
						throw new IOException();
					}
				}
			}
		}
	}
}
