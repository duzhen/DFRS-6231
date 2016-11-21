package dfrs.servers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import dfrs.replicamanager.BaseRM;
import net.rudp.ReliableServerSocket;
import net.rudp.ReliableSocketOutputStream;

public abstract class BaseServerCluster {
	
	public static final String SERVER_MTL = "Montreal";
	public static final String SERVER_WST = "Washington";
	public static final String SERVER_NDL = "New Delhi";
	public static final String FAILURE = "Failure";
	public static final String CRASH = "Crash";
	
	public static final String[] SERVERS = new String[] {SERVER_MTL,SERVER_WST,SERVER_NDL};
	private HashMap<String, CorbaServer> servers;
	private String serverState = BaseRM.STATE_RUNNING;
	
	protected abstract void createServers(String[] servers);
	protected abstract int getSCport();
	protected abstract String getCorba();
	
	public BaseServerCluster() {
		this.servers = new HashMap<String, CorbaServer>();
	}

	private CorbaServer getServer(String server) {
		return servers.get(server);
	}

	protected void registerServer(CorbaServer server) {
		if(server == null)
			return;
		CorbaServer s = getServer(server.getServerName());
		if (s != null && s.isAlive()) {
			s.shutdown(false);
		}
		if (server.isCreated()) {
			servers.put(server.getServerName(), server);
		}
	}

	private void startServer(String[] servers, String state) {
		if (servers == null)
			return;
		for (int i = 0; i < servers.length; i++) {
			CorbaServer s = getServer(servers[i]);
			if (s != null) {
				s.start();
				if(state != null && state.length() != 0) {
					s.setServerState(state);
				}
			}
		}
	}
	
	protected void startServer(String[] servers) {
		startServer(servers, BaseRM.STATE_RUNNING);
	}

	private boolean stopAllServer() {
		boolean result = true;
		for (int i = 0; i < SERVERS.length; i++) {
			CorbaServer s = getServer(SERVERS[i]);
			if (s != null && s.isAlive()) {
				try {
					s.shutdown(false);
				} catch(Exception e) {
					e.printStackTrace();
					result = false;
				}
			} else {
				result = true;
			}
		}
		HeartBeatSender.getInstance().closeSocket();
//		return result;
		return true;
	}
	
	private void setServerState(String[] servers, String state) {
		for (int i = 0; i < servers.length; i++) {
			CorbaServer s = getServer(servers[i]);
			if (s != null) {
				if(state != null && state.length() != 0) {
					s.setServerState(state);
				}
			}
		}
	}
	
	public String getServerState() {
		return serverState;
	}
	public void setServerState(String serverState) {
		this.serverState = serverState;
	}
	
	private String processSocketRequest(String source, String content) {
		if("RM".equals(source)) {
			System.out.println("Server"+getCorba()+":Receive "+content);
			if(BaseRM.STATE_RECOVERING.equals(content) && !BaseRM.STATE_RECOVERING.equals(getServerState())) {
				setServerState(BaseRM.STATE_RECOVERING);
				if(stopAllServer()) {
					System.out.println("Server"+getCorba()+":Sever Down ");
					createServers(SERVERS);
					startServer(SERVERS, BaseRM.STATE_RECOVERING);
					System.out.println("Server"+getCorba()+":Is Recovering");
				}
			} else if(BaseRM.STATE_RUNNING.equals(content) && BaseRM.STATE_RECOVERING.equals(getServerState())) {
				setServerState(BaseRM.STATE_RUNNING);
				setServerState(SERVERS, BaseRM.STATE_RUNNING);
				System.out.println("Server"+getCorba()+":Sever Is Running");
			} else if(CRASH.equals(content)) {
				System.out.println("Server"+getCorba()+":Test Crash");
				stopAllServer();
			}
//			else if(FAILURE.equals(content)) {
//				
//			} 
		}
		return content;
	}
	
	//Receive RM
	protected void startReceiveRM() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ReliableServerSocket[] serverSocket = new ReliableServerSocket[1];
				while (true) {
					try {
						if (serverSocket[0] == null || serverSocket[0].isClosed())
							serverSocket[0] = new ReliableServerSocket(getSCport());
					} catch (IOException e) {
						System.out.println(e.getMessage());
						try {
							Thread.sleep(10000);
						} catch (Exception e1) {
							System.out.println(e1.getMessage());
						}
						continue;
					}
					try {
						Socket connectionSocket = serverSocket[0].accept();
						while (true) {
							try {
								BufferedReader inFromClient = new BufferedReader(
										new InputStreamReader(connectionSocket.getInputStream()));
								String content = inFromClient.readLine();
								String reply = processSocketRequest("RM", content);
								// message send back to client
								ReliableSocketOutputStream outToClient = (ReliableSocketOutputStream) connectionSocket
										.getOutputStream();
								PrintWriter outputBuffer = new PrintWriter(outToClient);
								outputBuffer.println(reply);
								outputBuffer.flush();
							} catch (IOException e) {
								System.out.println("Heartbeat readLine: " + e.getMessage());
								break;
							}
						}
					} catch (IOException e) {
						System.out.println("Heartbeat accept: " + e.getMessage());
					}
				}
			}
		}).start();
	}
}
