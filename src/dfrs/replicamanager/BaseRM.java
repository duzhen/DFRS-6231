package dfrs.replicamanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import dfrs.servers.BaseServerCluster;
import dfrs.utils.Utils;
import net.rudp.ReliableServerSocket;

public abstract class BaseRM {
	
	public static final String STATE_RUNNING = "Running";//0
	public static final String STATE_TERMINATED = "Terminated";//1
	public static final String STATE_RECOVERING = "Recovering";//2
	
	public static final String[] STATES = new String[] {STATE_RUNNING,STATE_TERMINATED,STATE_RECOVERING};
	
	private State state;
	private InetAddress Host;
	private boolean isStopped = false;
	private ClusterManager cluster;
	
	class State {
		private String state;
		private int error;
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
			if(BaseServerCluster.SERVER_MTL.equals(server)) {
				alive[0] =  System.currentTimeMillis();
			} else if(BaseServerCluster.SERVER_WST.equals(server)) {
				alive[1] =  System.currentTimeMillis();
			}  else if(BaseServerCluster.SERVER_NDL.equals(server)) {
				alive[2] =  System.currentTimeMillis();
			} 
		}
		//s
		public long getAlive(String server) {
			long now = System.currentTimeMillis();
			long last = 0;
			if(BaseServerCluster.SERVER_MTL.equals(server)) {
				last = alive[0];
			} else if(BaseServerCluster.SERVER_WST.equals(server)) {
				last = alive[1];
			}  else if(BaseServerCluster.SERVER_NDL.equals(server)) {
				last = alive[2];
			}
			return now-last;
		}
		
		public int error() {
			return ++error;
		}
	}
	public BaseRM(String[] args) {
		this.cluster = new ClusterManager(this, args);
		this.state = new State(STATE_TERMINATED);
		String[] last = new String[1];
		try {
			new Timer().schedule(new TimerTask() {
				public void run() {
					String server = "";
					String s = "Server Running";
					for(int i=0;i<BaseServerCluster.SERVERS.length;i++) {
						if(state.getAlive(BaseServerCluster.SERVERS[i]) > 2000) {
							server+=(BaseServerCluster.SERVERS[i]+" "+state.getAlive(BaseServerCluster.SERVERS[i])+" ");
							s = " Crashed";
						}
					}
					if(!server.equals(last[0]))
						System.out.println(getRMName()+"-" + server + s);
					last[0] = server;
				}
			}, 1000, 2000);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	protected abstract String getRMName();
	protected abstract String getHost();
	protected abstract int getFEport();
	protected abstract int getSEport();
	protected abstract int getS2FEport();
	protected abstract int getRMport();
	protected abstract int getHBport();
	protected abstract String restartServer();
	
	protected void startRM() {
		isStopped = false;
		startReceiveHeartBeat();
		startReceiveRM();
		startReceiveSE();
		startReceiveFE();
		state.setRMState(0);
	}
	
	protected void startTest() {
		showTestMenu();
	}
	
	private boolean recoveryServer() {
		state.setRMState(2);
		return true;
	}
	
	private String processFECommand(String content) {
		String result = "";
		if(content == null)
			return result;
		String[] params = content.split("\\$");
		if("ERROR".equals(params[0])) {
			result = countingErrorTimes();
		} else if("ISALIVE".equals(params[0])) {
			result = checkAlive();
		} else if("RESTART".equals(params[0])) {
			result = restartServer();
		}
		return result;
	}
	
	private String checkAlive() {
		String result = state.getRMState();
		if(result.equals(STATE_RUNNING)) {
			if(state.getAlive(BaseServerCluster.SERVER_MTL) > 10*1000) {
				result = STATE_TERMINATED;
			} else if(state.getAlive(BaseServerCluster.SERVER_WST) > 10*1000) {
				result = STATE_TERMINATED;
			} else if(state.getAlive(BaseServerCluster.SERVER_NDL) > 10*1000) {
				result = STATE_TERMINATED;
			}
		}
		return result;
	}
	
	private String countingErrorTimes() {
		if(state.error()>=3) {
			//recovering
			return STATE_RECOVERING;
		} else {
			//correct
			return STATE_RUNNING;
		}
//		if(getServer(SERVER_MTL).error()>=3) {
//			stopAllServer();
//			if(recoveryServer()) {
//				createServers(SERVERS);
//				startServer(SERVERS);
//			}
//		} else {
//			state.getAlive(SERVER_MTL);
//		}
	}
	
	public String processSocketRequest(String source, String content) {
		String result = "";
		if("RM".equals(source)) {
			return "";
		}
		while(!state.getRMState().equals(STATE_RUNNING)) {
			continue;
		}
		if("FE".equals(source)) {
			result = processFECommand(content);
		} else if("SE".equals(source)) {
			result = cluster.requestCorbaServer(content, getHost(), getS2FEport());
		} else if("HB".equals(source)) {
//			System.out.println(content);
			String[] params = content.split("\\$");
			if(params.length>1) {
				state.setAlive(params[0]);
			}
		}
		return result;
	}
	
	//FE
	protected void startReceiveFE() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ReliableServerSocket[] serverSocket = new ReliableServerSocket[1];
				while (true) {
					try {
						if (serverSocket[0] == null || serverSocket[0].isClosed())
							serverSocket[0] = new ReliableServerSocket(getFEport());
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
								System.out.println("FE:" + content);
								if(content == null)
									break;
								if(content.length() == 0)
									continue;
								String reply = processSocketRequest("FE", content);
								// message send back to client
//								ReliableSocketOutputStream outToClient = (ReliableSocketOutputStream) connectionSocket
//										.getOutputStream();
								PrintWriter outputBuffer = new PrintWriter(connectionSocket
										.getOutputStream());
								outputBuffer.println(reply);
								outputBuffer.flush();
							} catch (IOException e) {
								System.out.println("ReceiveFE readLine: " + e.getMessage());
								break;
							}
						}
					} catch (IOException e) {
						System.out.println("ReceiveFE accept: " + e.getMessage());
					}
				}
			}
		}).start();
	}
	//SE
	protected void startReceiveSE() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ReliableServerSocket[] serverSocket = new ReliableServerSocket[1];
				while (true) {
					try {
						if (serverSocket[0] == null || serverSocket[0].isClosed())
							serverSocket[0] = new ReliableServerSocket(getSEport());
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
								System.out.println("SE:"+content);
								if(content == null)
									break;
								if(content.length() == 0)
									continue;
				                String reply = processSocketRequest("SE",content);
				             // message send back to client
//								ReliableSocketOutputStream outToClient = (ReliableSocketOutputStream) connectionSocket
//										.getOutputStream();
								PrintWriter outputBuffer = new PrintWriter(connectionSocket
										.getOutputStream());
								outputBuffer.println(reply);
								outputBuffer.flush();
							} catch (IOException e) {
								System.out.println("ReceiveSE readLine: " + e.getMessage());
								break;
							}
						}
					} catch (IOException e) {
						System.out.println("ReceiveSE accept: " + e.getMessage());
					}
				}
			}
		}).start();
	}
	//RM
	protected void startReceiveRM() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ReliableServerSocket[] serverSocket = new ReliableServerSocket[1];
				while (true) {
					try {
						if (serverSocket[0] == null || serverSocket[0].isClosed())
							serverSocket[0] = new ReliableServerSocket(getRMport());
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
								System.out.println("RM:"+content);
								if(content == null)
									break;
								if(content.length() == 0)
									continue;
				                processSocketRequest("RM", content);
							} catch (IOException e) {
								System.out.println("ReceiveRM readLine: " + e.getMessage());
								break;
							}
						}
					} catch (IOException e) {
						System.out.println("ReceiveRM accept: " + e.getMessage());
					}
				}
			}
		}).start();
	}
	//Heartbeat
	protected void startReceiveHeartBeat() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				DatagramSocket[] serverSocket = new DatagramSocket[1];
				byte[] buffer = new byte[1000];
				while (true) {
					try {
						if (serverSocket[0] == null || serverSocket[0].isClosed())
						serverSocket[0] = new DatagramSocket(getHBport());
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
						while (true) {
							try {
								DatagramPacket request = new DatagramPacket(buffer, buffer.length);
								serverSocket[0].receive(request);
								String content = new String(request.getData(), 0, request.getLength()).trim();
								if(content == null || content.length() == 0)
									continue;
								processSocketRequest("HB", content);
							} catch (IOException e) {
									Utils.println("Heartbeat readLine: " + e.getMessage());
								break;
							}
						}
					} catch (Exception e) {
						Utils.println("Heartbeat accept: " + e.getMessage());
					}
				}
				
//				ReliableServerSocket[] serverSocket = new ReliableServerSocket[1];
//				while (true) {
//					try {
//						if (serverSocket[0] == null || serverSocket[0].isClosed())
//							serverSocket[0] = new ReliableServerSocket(getHBport());
//					} catch (IOException e) {
//						System.out.println(e.getMessage());
//						try {
//							Thread.sleep(10000);
//						} catch (Exception e1) {
//							System.out.println(e1.getMessage());
//						}
//						continue;
//					}
//					try {
//						Socket connectionSocket = serverSocket[0].accept();
//						while (true) {
//							try {
//								BufferedReader inFromClient = new BufferedReader(
//										new InputStreamReader(connectionSocket.getInputStream()));
//								String content = inFromClient.readLine();
//								if(content == null || content.length() == 0)
//									continue;
//								processSocketRequest("HB", content);
//							} catch (IOException e) {
//									Utils.println("Heartbeat readLine: " + e.getMessage());
//								break;
//							}
//						}
//					} catch (IOException e) {
//						Utils.println("Heartbeat accept: " + e.getMessage());
//					}
//				}
			}
		}).start();
	}
	
	public static void showTestMenu() {
		System.out.println("\n****Welcome to DFRS System****\n");
		System.out.println("Please select your test item (1-3)");
		System.out.println("1. Test Software Failure");
		System.out.println("2. Test Crash Failure");
		System.out.println("3. Both");
		Scanner keyboard = new Scanner(System.in);
		int choose = Utils.validInputOption(keyboard, 3);
		if(choose == 1) {
//			ticket.setTicketClass(Flight.FIRST_CLASS);
		} else if(choose == 2) {
//			ticket.setTicketClass(Flight.BUSINESS_CLASS);
		} else if(choose == 3) {
//			ticket.setTicketClass(Flight.ECONOMY_CLASS);
		}
	}
}
