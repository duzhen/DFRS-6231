package dfrs.servers;

import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;

import dfrs.ServerInterface;
import dfrs.ServerInterfaceHelper;
import dfrs.ServerInterfacePOA;
import dfrs.replicamanager.BaseRM;
import dfrs.replicamanager.ReplicaManager1;
import dfrs.replicamanager.ReplicaManager2;
import dfrs.replicamanager.ReplicaManager3;
import dfrs.replicamanager.ReplicaManager4;
import dfrs.servers1.ServerImpl1;
import dfrs.servers2.ServerImpl2;
import dfrs.servers3.ServerImpl3;
import dfrs.servers4.ServerImpl4;

class CorbaServer extends Thread {
	private ORB	orb;
	private String serverState = "$";
	private String[] args;
	private String server;
//	private String host;
//	private String port;
	private Timer timer;
	
	public static CorbaServer createServer(Object o, String name, String[] args) {
		if (ServerCluster1.class == o) {
			return new CorbaServer(new ServerImpl1(), args, name, ServerCluster1.SERVER_HOST,
					ServerCluster1.SERVER_CORBA_PORT, ReplicaManager1.RM_HOST,
					ReplicaManager1.RM_RECEIVE_HEARTBEAT_PROT);
		} else if (ServerCluster2.class == o) {
			return new CorbaServer(new ServerImpl2(), args, name, ServerCluster2.SERVER_HOST,
					ServerCluster2.SERVER_CORBA_PORT, ReplicaManager2.RM_HOST,
					ReplicaManager2.RM_RECEIVE_HEARTBEAT_PROT);
		} else if (ServerCluster3.class == o) {
			return new CorbaServer(new ServerImpl3(), args, name, ServerCluster3.SERVER_HOST,
					ServerCluster3.SERVER_CORBA_PORT, ReplicaManager3.RM_HOST,
					ReplicaManager3.RM_RECEIVE_HEARTBEAT_PROT);
		} else if (ServerCluster4.class == o) {
			return new CorbaServer(new ServerImpl4(), args, name, ServerCluster4.SERVER_HOST,
					ServerCluster4.SERVER_CORBA_PORT, ReplicaManager4.RM_HOST,
					ReplicaManager4.RM_RECEIVE_HEARTBEAT_PROT);
		}
		return null;
	}
	
	public CorbaServer(ServerInterfacePOA impl, String[] args, String server, String host, String port, String RMHost, int RMPort) {
		this.args = args;
		this.server = server;
//		this.host = host;
//		this.port = port;
		this.timer = new Timer();
		this.orb = createCorbaServer(impl, args, server, host, port);
//		if(this.orb != null) {
			try {
				this.timer.schedule(new TimerTask() {
					public void run() {
						HeartBeatSender.getInstance().send(RMHost, RMPort, server+serverState);
					}
				}, 1000, 1000);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
//		}
	}

	public boolean isCreated() {
		return this.orb==null?false:true;
	}
	
	@Override
	public void run() {
		super.run();
		if(orb != null) {
			setServerState("$"+BaseRM.STATE_RUNNING);
			orb.run();
		}
	}

	protected void shutdown(boolean wait_for_completion) {
		if(orb != null) {
			orb.shutdown(wait_for_completion);
			orb.destroy();
		}
		if(timer != null) {
			timer.cancel();
		}
	}
	

	public String getServerState() {
		return serverState;
	}

	public void setServerState(String serverState) {
		this.serverState = serverState;
	}

	public String getServerName() {
		return server;
	}
	
	private ORB createCorbaServer(ServerInterfacePOA impl, String[] args, String server, String host, String port) {
		try {
			Properties props = new Properties();
			props.put("org.omg.CORBA.ORBInitialPort", port);    
			props.put("org.omg.CORBA.ORBInitialHost", host); 

			ORB	 orb = ORB.init(args , props);
			POA rootpoa = (POA)orb.resolve_initial_references("RootPOA");
			rootpoa.the_POAManager().activate();

			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(impl);
			ServerInterface href = ServerInterfaceHelper.narrow(ref);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			String name = server;
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
}