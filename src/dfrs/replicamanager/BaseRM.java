package dfrs.replicamanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;

import dfrs.ServerInterface;
import dfrs.ServerInterfaceHelper;
import dfrs.ServerInterfacePOA;
import dfrs.servers1.ServerImpl1;
import net.rudp.ReliableServerSocket;
import net.rudp.ReliableSocket;
import net.rudp.ReliableSocketOutputStream;

public abstract class BaseRM {
	private InetAddress Host;
	private boolean isStopped = false;
	private ClusterManager cluster;
	private HashMap<String, Server> servers;
	public BaseRM() {
		servers = new HashMap<String, Server>();
		this.cluster = new ClusterManager();
	}
	protected abstract String getHost();
	protected abstract int getFEport();
	protected abstract int getSEport();
	protected abstract int getS2FEport();
	protected abstract int getRMport();
	protected abstract int getHBport();
	
	protected Server getServer(String server) {
		return servers.get(server);
	}
	
	class Server extends Thread {
		private ORB	orb;
		
		public Server(ServerInterfacePOA impl, String[] args, String server, String host, String port) {
			orb = createCorbaServer(impl, args, server, host, port);
		}

		@Override
		public void run() {
			super.run();
			if(orb != null)
				orb.run();
		}

		protected void shutdown(boolean wait_for_completion) {
			if(orb != null) {
				orb.shutdown(wait_for_completion);
				orb.destroy();
			}
		}
	}
	
	protected void initServer(ServerInterfacePOA impl, String[] args, String server, String host, String port) {
		isStopped = true;
		Server s = getServer(server);
		if(s!=null&&s.isAlive()) {
			s.shutdown(false);
		}
		s = new Server(impl, args, server, host, port);
		servers.put(server, s);
		cluster.addCorbaClient(args, server, host, port);
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
	}
	
	protected void stopServer(String[] servers) {
		if(servers == null)
			return;
		for(int i=0;i<servers.length;i++) {
			Server s = getServer(servers[i]);
			if(s!=null&&s.isAlive()) {
				s.shutdown(false);
			}
		}
		isStopped = true;
	}
	
	private ORB createCorbaServer(ServerInterfacePOA impl, String[] args, String server, String host, String port) {
		try {
			Properties props = new Properties();
			props.put("org.omg.CORBA.ORBInitialPort", port);    
			props.put("org.omg.CORBA.ORBInitialHost", host); 

			ORB	 orb = ORB.init(args , props);
			POA rootpoa = (POA)orb.resolve_initial_references("RootPOA");
			rootpoa.the_POAManager().activate();
//			FEImpl impl = new FEImpl("localhost", 8888);

			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(impl);
			ServerInterface href = ServerInterfaceHelper.narrow(ref);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			String name = "Server";
			NameComponent path[] = ncRef.to_name(name);
			ncRef.rebind(path, href);
			
			System.out.println(server + " server ready and waiting ...");
			return orb;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected void startReceiveFE() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
		            ReliableServerSocket serverSocket = new ReliableServerSocket(getFEport());
		            
		            while(!isStopped){
		                Socket connectionSocket = serverSocket.accept();
		                
		                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		                System.out.println("Server: "+inFromClient.readLine());
		                
		                // message send back to client
		                ReliableSocketOutputStream outToClient = (ReliableSocketOutputStream) connectionSocket.getOutputStream();
		                PrintWriter outputBuffer = new PrintWriter(outToClient);
		                outputBuffer.println("Processed Sentence From Server");
		                outputBuffer.flush();
		                
		                connectionSocket.close();
		            }
		            
		            serverSocket.close();

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
		            
		            while(!isStopped){
		                Socket connectionSocket = serverSocket.accept();
		                
		                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		                String content = inFromClient.readLine();
		                System.out.println("Server: "+content);
		                String[] params = content.split("\\$");
		                for(int i=0;i<params.length;i++) {
		                	System.out.println(i+": "+params[i]);
		                }
		                String reply = "failed";
		                if(true) {//) {
		                	reply = cluster.getCorbarClient(ServerImpl1.SERVER_MTL).editFlightRecord(params[1], params[2], params[3], params[4], Integer.valueOf(params[5]), Integer.valueOf(params[6]), Integer.valueOf(params[7]));
		                }
		                // message send back to client
		                ReliableSocket clientSocket = new ReliableSocket(getHost(), getS2FEport());
		                ReliableSocketOutputStream outToServer = (ReliableSocketOutputStream) clientSocket.getOutputStream();
		                PrintWriter outputBuffer = new PrintWriter(outToServer);
		                outputBuffer.println(reply);
		                outputBuffer.flush();
		                clientSocket.close();
		                
		                connectionSocket.close();
		            }
		            
		            serverSocket.close();

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
		            
		            while(!isStopped){
		                Socket connectionSocket = serverSocket.accept();
		                
		                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		                System.out.println("Server: "+inFromClient.readLine());
		                
		                // message send back to client
		                ReliableSocketOutputStream outToClient = (ReliableSocketOutputStream) connectionSocket.getOutputStream();
		                PrintWriter outputBuffer = new PrintWriter(outToClient);
		                outputBuffer.println("Processed Sentence From Server");
		                outputBuffer.flush();
		                
		                connectionSocket.close();
		            }
		            
		            serverSocket.close();

		        } catch (IOException ex) {

		        } 
			}
		}).start();
	}
	//heartbeat
	protected void startReceiveHeartBeat() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ReliableServerSocket serverSocket = new ReliableServerSocket(getHBport());

					while (!isStopped) {
						Socket connectionSocket = serverSocket.accept();

						BufferedReader inFromClient = new BufferedReader(
								new InputStreamReader(connectionSocket.getInputStream()));
						System.out.println("Server: " + inFromClient.readLine());

						// message send back to client
						ReliableSocketOutputStream outToClient = (ReliableSocketOutputStream) connectionSocket
								.getOutputStream();
						PrintWriter outputBuffer = new PrintWriter(outToClient);
						outputBuffer.println("Processed Sentence From Server");
						outputBuffer.flush();

						connectionSocket.close();
					}

					serverSocket.close();

				} catch (IOException ex) {

				}
			}
		}).start();
	}
}
