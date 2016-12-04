package dfrs.replicamanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import dfrs.servers.BaseServerCluster;
import dfrs.utils.Config;
import dfrs.utils.Utils;
import net.rudp.ReliableServerSocket;

public abstract class BaseRM {
	
	public static final String STATE_INITIAL = "Initial";//0
	public static final String STATE_RUNNING = "Running";//0
	public static final String STATE_TERMINATED = "Terminated";//1
	public static final String STATE_RECOVERING = "Recovering";//2
	
//	public static final String[] STATES = new String[] {STATE_RUNNING,STATE_TERMINATED,STATE_RECOVERING};
	
	private State state;
//	private InetAddress Host;
//	private boolean isStopped = false;
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
		public void setRMState(String s) {
			this.state = s;
		}

		public String getRMState() {
			return this.state;
		}
		
		public void initAliveTime() {
			for(int i=0;i<alive.length;i++) {
				alive[i] =  0;
			}
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
		
		public void initError() {
			error = 0;
		}
	}
	public BaseRM(String[] args) {
		this.cluster = new ClusterManager(this, args);
		this.state = new State(STATE_INITIAL);
//		String[] server = new String[1];
		String[] last = new String[1];
//		server[0] = "";
		last[0] = "";
		try {
			new Timer().schedule(new TimerTask() {
				public void run() {
					String server = "";
					String s = STATE_RUNNING;//Server "+STATE_INITIAL;
					for(int i=0;i<BaseServerCluster.SERVERS.length;i++) {
						if(state.getAlive(BaseServerCluster.SERVERS[i]) > 2000) {
//							server+=(BaseServerCluster.SERVERS[i]+" "+state.getAlive(BaseServerCluster.SERVERS[i])+" ");
							server+=(BaseServerCluster.SERVERS[i]+" ");
							if(STATE_RUNNING.equals(state.getRMState())) {
								s = "Crash";
							} else {
								s = state.getRMState();
							}
						}
					}
					server+=s;
					if(!server.equals(last[0])&&!STATE_RECOVERING.equals(s))
						System.out.println("RM"+getRMName()+"-" + server);
					else if(!state.getRMState().equals(last[0])&&!"Crash".equals(s)&&!STATE_RECOVERING.equals(s)){
						server = state.getRMState();
						System.out.println("RM"+getRMName()+"-" + server);
					}
						
					last[0] = server;
				}
			}, 1000, 2000);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
//		System.out.println("RM"+getRMName()+"-" +"Server On");
	}
	protected abstract String getRMName();
//	protected abstract String getFEHost();
	protected abstract int getFEport();
	protected abstract int getSEport();
	protected abstract int getS2FEport();
	protected abstract int getRMport();
	protected abstract int getHBport();
	protected abstract String sendCommandToServer(String command);
	protected abstract String getLogFileName();
	
	protected void startRM() {
		startReceiveHeartBeat();
		startReceiveRM();
		startReceiveSE();
		startReceiveFE();
		state.setRMState(STATE_RUNNING);
	}
	
	private boolean correctData(String id, int n) {
		//Correct
//		if(Config.TEST) {
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		//edit book,edit is easy, get data log from others than use a special corba interface/HB piggyback insert data directly
		//edit transfer is hard, failed need transfer again, success but should faild need reback
		/**BUT WE NEEDN'T IMPLEMENT**/
//		cluster.correctData(id, n);
//		System.out.println("RM"+n+" has corrected record " + id);
		return true;
	}
	
	private boolean recoveryData() {
		cluster.recoveryData();
		System.out.println("RM"+getRMName()+" finish recovery");
		return true;
	}
	
	private String processFECommand(String content) {
		String result = "";
		if(content == null)
			return result;
		String[] params = content.split("\\$");
		int n = Integer.valueOf(getRMName());
		if(params.length>=2*n) {
			if("correct".equals(params[2*(n-1)])) {
				
			} else if("wrong".equals(params[2*(n-1)])) {
				System.out.println("RM"+n+":Receive wrong");
				result = countingErrorTimes(params[0], n);
			} else if("crash".equals(params[2*(n-1)])) {
				System.out.println("RM"+n+":Receive crash");
				if(Config.TEST||STATE_TERMINATED.equals(checkAlive())) {
					System.out.println("RM"+n+":Is crashed, restart now");
					result = recoveringServer();
				} else {
					System.out.println("RM"+n+":No crash");
					result = countingErrorTimes(params[0], n);
				}
			}
		}
		return result;
	}
	
	private String recoveringServer() {
		String result;
		synchronized (state) {
			result = sendCommandToServer(STATE_RECOVERING);
			if(STATE_RECOVERING.equals(result)) {
				state.setRMState(STATE_RECOVERING);
				state.initAliveTime();
				state.initError();
				System.out.println("RM"+getRMName()+"-" + STATE_RECOVERING);
				cluster.updateCorbaClient(BaseServerCluster.SERVERS);
				if(recoveryData()) {
					result = sendCommandToServer(STATE_RUNNING);
					if(STATE_RUNNING.equals(result)) {
						state.setRMState(STATE_RUNNING);
					}
				}
			}
		}
		return result;
	}
	
	private String checkAlive() {
		String result = state.getRMState();
		if(result.equals(STATE_RUNNING)) {
			if(state.getAlive(BaseServerCluster.SERVER_MTL) > 2*1000) {
				result = STATE_TERMINATED;
			} else if(state.getAlive(BaseServerCluster.SERVER_WST) > 2*1000) {
				result = STATE_TERMINATED;
			} else if(state.getAlive(BaseServerCluster.SERVER_NDL) > 2*1000) {
				result = STATE_TERMINATED;
			}
		}
		return result;
	}
	
	private String countingErrorTimes(String id, int n) {//id:total id from SE, n is number of serverCluster
		if(state.error()>=3) {
			state.initError();
			System.out.println("RM"+n+":error time > 3");
			recoveringServer();
			return STATE_RECOVERING;
		} else {
			correctData(id, n);
			return STATE_RUNNING;
		}
	}
	
	public String processSocketRequest(String source, String content) {
		String result = "";
		if("RM".equals(source)) {
			return result;
		} else if("HB".equals(source)) {
//			System.out.println(content);
			String[] params = content.split("\\$");
			if(params.length>1) {
				state.setAlive(params[0]);
				state.setRMState(params[1]);
			}
			return result;
		}
//		while(!state.getRMState().equals(STATE_RUNNING)) {
//			System.out.println("RM DID NOT STATE_RUNNING");
//			try {
//				Thread.sleep(2000);
//			} catch (Exception e1) {
//				System.out.println(e1.getMessage());
//			}
//			continue;
//		}
		synchronized (state) {
			if("FE".equals(source)) {
				result = processFECommand(content);
			} else if("SE".equals(source)) {
				result = cluster.processRequest(content, Config.getFeHost(), getS2FEport());
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
//								if(content.length() == 0)
//									continue;
								String reply = processSocketRequest("FE", content);
								// message send back to client
//								ReliableSocketOutputStream outToClient = (ReliableSocketOutputStream) connectionSocket
//										.getOutputStream();
//								PrintWriter outputBuffer = new PrintWriter(connectionSocket
//										.getOutputStream());
//								outputBuffer.println(reply);
//								outputBuffer.flush();
								connectionSocket.close();
								break;
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

	protected void startDemo() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				showDemoMenu();
			}
		}).start();
	}
	
	public void showDemoMenu() {
		System.out.println("\n****Welcome to DFRS System****\n");
		System.out.println("Please select your test item (1-2)");
		System.out.println("1. Test Software Failure");
		System.out.println("2. Test Crash Failure");
		if(!"3".equals(getRMName()))
			System.out.println("3. Print Information");
		while(true) {
			Scanner keyboard = new Scanner(System.in);
			int choose = Utils.validInputOption(keyboard, 3);
			if(choose == 1) {
				System.out.println("************!!Replica Manager Is In DEMO Model!!************");
				cluster.demoFailure();
			} else if(choose == 2) {
				System.out.println("************!!Replica Manager Shutdown The Servers!!************");
				sendCommandToServer(BaseServerCluster.CRASH);
			} else if(choose == 3) {
				if(!"3".equals(getRMName())) {
					sendCommandToServer(BaseServerCluster.PRINT);
					System.out.println("Print at ServerCluster"+getRMName());
				}
			}
		}
	}
}
