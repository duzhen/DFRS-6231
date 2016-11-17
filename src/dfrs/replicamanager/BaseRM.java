package dfrs.replicamanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;

import dfrs.ServerInterface;
import dfrs.ServerInterfaceHelper;
import dfrs.ServerInterfacePOA;
import net.rudp.ReliableServerSocket;
import net.rudp.ReliableSocketOutputStream;

public abstract class BaseRM {
	private InetAddress Host;
	private ClusterManager cluster;
	private ArrayList<String> sList;
	public BaseRM() {
//		sList = new ArrayList<String>();
		this.cluster = new ClusterManager();
	}
	
	protected abstract int getFEport();
	protected abstract int getSEport();
	protected abstract int getRMport();
	protected abstract int getHBport();
	
	protected void initServer(ServerInterfacePOA impl, String[] args, String server, String host, int port) {
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
			orb.run();
			//server running and save corba client to cluster
			this.cluster.addCorbaClient(args, server, host, port);
//			sList.add(server);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void startReceiveFE() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
		            ReliableServerSocket serverSocket = new ReliableServerSocket(getFEport());
		            
		            boolean isStopped = false;
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
		});
	}
	//SE
	protected void startReceiveSE() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
		            ReliableServerSocket serverSocket = new ReliableServerSocket(getRMport());
		            
		            boolean isStopped = false;
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
		});
	}
	//RM
	protected void startReceiveRM() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
		            ReliableServerSocket serverSocket = new ReliableServerSocket(getRMport());
		            
		            boolean isStopped = false;
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
		});
	}
	//heartbeat
	protected void startReceiveHeartBeat() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ReliableServerSocket serverSocket = new ReliableServerSocket(getHBport());

					boolean isStopped = false;
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
		});
	}
}
