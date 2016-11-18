package dfrs.replicamanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

import net.rudp.ReliableServerSocket;
import net.rudp.ReliableSocketOutputStream;

public abstract class BaseRM {
	
	public static final String SERVER_MTL = "Montreal";
	public static final String SERVER_WST = "Washington";
	public static final String SERVER_NDL = "New Delhi";
	public static final String STATE_RUNNING = "Running";//0
	public static final String STATE_TERMINATED = "Terminated";//1
	public static final String STATE_RECOVERING = "Recovering";//2
	
	public static final String[] SERVERS = new String[] {SERVER_MTL,SERVER_WST,SERVER_NDL};
	public static final String[] STATES = new String[] {STATE_RUNNING,STATE_TERMINATED,STATE_RECOVERING};
	
	private State state;
	private InetAddress Host;
	private boolean isStopped = false;
	private ClusterManager cluster;
	private HashMap<String, Server> servers;
	
	class State {
		String state;
		private long[] alive = new long[3];
		public State(String state) {
			super();
			this.state = state;
			for(int i=0;i<alive.length;i++) {
				alive[i] =  System.currentTimeMillis();
			}
		}
		public void setRMState(int s) {
			if(s<0||s>2)
				return;
			state = STATES[s];
		}

		public String getRMState() {
			return state;
		}
		
		public void setAlive(String server) {
			if(SERVER_MTL.equals(server)) {
				alive[0] =  System.currentTimeMillis();
			} else if(SERVER_WST.equals(server)) {
				alive[1] =  System.currentTimeMillis();
			}  else if(SERVER_NDL.equals(server)) {
				alive[2] =  System.currentTimeMillis();
			} 
		}
		//s
		public long getAlive(String server) {
			long now = System.currentTimeMillis();
			long last = 0;
			if(SERVER_MTL.equals(server)) {
				last = alive[0];
			} else if(SERVER_WST.equals(server)) {
				last = alive[1];
			}  else if(SERVER_NDL.equals(server)) {
				last = alive[2];
			}
			return (now-last)/1000;
		}
	}
	public BaseRM() {
		this.servers = new HashMap<String, Server>();
		this.cluster = new ClusterManager();
		this.state = new State(STATE_TERMINATED);
	}
	protected abstract String getRMName();
	protected abstract String getHost();
	protected abstract int getFEport();
	protected abstract int getSEport();
	protected abstract int getS2FEport();
	protected abstract int getRMport();
	protected abstract int getHBport();
	protected abstract void createServers(String[] servers);
	
	protected Server getServer(String server) {
		return servers.get(server);
	}
	
	protected void registerServer(Server server) {
		isStopped = true;
		Server s = getServer(server.getServerName());
		if(s!=null&&s.isAlive()) {
			s.shutdown(false);
		}
		if(s!=null&&s.isCreated()) {
			servers.put(server.getServerName(), server);
			cluster.createCorbaClient(server.getServerArgs(), server.getServerName(), server.getServerHost(), server.getServerPort());
		}
	}

	protected void startServer(String[] servers) {
		if(servers == null)
			return;
		for(int i=0;i<servers.length;i++) {
			Server s = getServer(servers[i]);
			if(s!=null)
				s.start();
		}
		isStopped = false;
		startReceiveHeartBeat();
		startReceiveRM();
		startReceiveSE();
		startReceiveFE();
		state.setRMState(0);
	}
	
//	protected void stopCorbaServer(String servers) {
//		if (servers == null)
//			return;
//		Server s = getServer(servers);
//		if (s != null && s.isAlive()) {
//			s.shutdown(false);
//		}
//	}
	
	protected void stopAllServer() {
		for(int i=0;i<SERVERS.length;i++) {
			Server s = getServer(SERVERS[i]);
			if(s!=null&&s.isAlive()) {
				s.shutdown(false);
			}
		}
		isStopped = true;
		state.setRMState(1);
	}
	
	private boolean recoveryServer() {
		state.setRMState(2);
		return true;
	}
	private String countingErrorTimes(String content) {
//		String[] params = content.split("\\$");
		if(getServer(SERVER_MTL).error()>=3) {
			stopAllServer();
			if(recoveryServer()) {
				createServers(SERVERS);
				startServer(SERVERS);
			}
		} else {
			state.getAlive(SERVER_MTL);
		}
		return SERVER_MTL;
	}
	public String processSocketRequest(String source, String content) {
		if("RM".equals(source)) {
			return "";
		}
		while(!state.getRMState().equals("Running")) {
			continue;
		}
		if("FE".equals(source)) {
			 String server = countingErrorTimes(content);
		} else if("SE".equals(source)) {
			cluster.requestCorbaServer(content, getHost(), getS2FEport());
		} else if("HB".equals(source)) {
			state.setAlive(content);
			System.out.println(getRMName()+"-" + content + " alive");
		}
		return "";
	}
	
	//FE
	protected void startReceiveFE() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
		            ReliableServerSocket serverSocket = new ReliableServerSocket(getFEport());
		            while (true) {
//		            while(!isStopped){
		                Socket connectionSocket = serverSocket.accept();
		                
		                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		                String content = inFromClient.readLine();
		                System.out.println("FE: "+content);
		                String reply = processSocketRequest("FE", content);
		                // message send back to client
		                ReliableSocketOutputStream outToClient = (ReliableSocketOutputStream) connectionSocket.getOutputStream();
		                PrintWriter outputBuffer = new PrintWriter(outToClient);
		                outputBuffer.println(reply);
		                outputBuffer.flush();
		                
		                connectionSocket.close();
		            }
		            
//		            serverSocket.close();

		        } catch (IOException ex) {

		        } 
			}
		}).start();
	}
	//SE
	protected void startReceiveSE() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
		            ReliableServerSocket serverSocket = new ReliableServerSocket(getSEport());
		            while (true) {
//		            while(!isStopped){
		                Socket connectionSocket = serverSocket.accept();
		                
		                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		                String content = inFromClient.readLine();
		                System.out.println("SE: "+content);
		                processSocketRequest("SE",content);
		                
		                connectionSocket.close();
		            }
		            
//		            serverSocket.close();

		        } catch (IOException ex) {

		        } 
			}
		}).start();
	}
	//RM
	protected void startReceiveRM() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
		            ReliableServerSocket serverSocket = new ReliableServerSocket(getRMport());
		            while (true) {
//		            while(!isStopped){
		                Socket connectionSocket = serverSocket.accept();
		                
		                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		                String content = inFromClient.readLine();
		                System.out.println("RM: "+content);
		                processSocketRequest("RM", content);
		                // message send back to client
		                ReliableSocketOutputStream outToClient = (ReliableSocketOutputStream) connectionSocket.getOutputStream();
		                PrintWriter outputBuffer = new PrintWriter(outToClient);
		                outputBuffer.println("Processed Sentence From Server");
		                outputBuffer.flush();
		                
		                connectionSocket.close();
		            }
		            
//		            serverSocket.close();

		        } catch (IOException ex) {

		        } 
			}
		}).start();
	}
	//Heartbeat
	protected void startReceiveHeartBeat() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int n = 0;
				while (n++!=5) {
					try {
						ReliableServerSocket serverSocket = new ReliableServerSocket(getHBport());
						Socket connectionSocket = serverSocket.accept();
						while (true) {
							// while (!isStopped) {
							try {
								BufferedReader inFromClient = new BufferedReader(
										new InputStreamReader(connectionSocket.getInputStream()));
								String content = inFromClient.readLine();
								processSocketRequest("HB", content);
							} catch (Exception e) {
								e.printStackTrace();
								connectionSocket.close();
								serverSocket.close();
								break;
							}
						}
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		}).start();
	}
}
