package dfrs.replicamanager;

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
import dfrs.net.HeartBeatSender;
import dfrs.servers1.ServerImpl1;
import dfrs.servers2.ServerImpl2;
import dfrs.servers3.ServerImpl3;
import dfrs.servers4.ServerImpl4;

class Server extends Thread {
	private ORB	orb;
	private int error;
	private String[] args;
	private String s;
	private String host;
	private String port;
	private Timer timer;
	
	public static Server createServer(Object o, String name, String[] args) {
		if(ReplicaManager1.class == o) {
			return new Server(new ServerImpl1(), args, name, ServerImpl1.SERVER_HOST, ServerImpl1.getCorbaPort(name), ReplicaManager1.RM_HOST, ReplicaManager1.RM_RECEIVE_HEARTBEAT_PROT);
		} else if(ReplicaManager2.class == o) {
			return new Server(new ServerImpl2(), args, name, ServerImpl2.SERVER_HOST, ServerImpl2.getCorbaPort(name), ReplicaManager2.RM_HOST, ReplicaManager2.RM_RECEIVE_HEARTBEAT_PROT);
		} else if(ReplicaManager3.class == o) {
			return new Server(new ServerImpl3(), args, name, ServerImpl3.SERVER_HOST, ServerImpl3.getCorbaPort(name), ReplicaManager3.RM_HOST, ReplicaManager3.RM_RECEIVE_HEARTBEAT_PROT);
		} else if(ReplicaManager4.class == o) {
			return new Server(new ServerImpl4(), args, name, ServerImpl4.SERVER_HOST, ServerImpl4.getCorbaPort(name), ReplicaManager4.RM_HOST, ReplicaManager4.RM_RECEIVE_HEARTBEAT_PROT);
		}
		return null;
	}
	
	public Server(ServerInterfacePOA impl, String[] args, String server, String host, String port, String RMHost, int RMPort) {
		this.args = args;
		this.s = server;
		this.host = host;
		this.port = port;
		this.timer = new Timer();
		this.orb = createCorbaServer(impl, args, server, host, port);
//		if(this.orb != null) {
			try {
				this.timer.schedule(new TimerTask() {
					public void run() {
						HeartBeatSender.getInstance().send(RMHost, RMPort, server);
					}
				}, 5000, 5000);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
//		}
	}

	public boolean isCreated() {
		return this.orb==null?true:false;
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
		if(timer != null) {
			timer.cancel();
		}
	}
	
	public String getServerName() {
		return s;
	}
	
	public String[] getServerArgs() {
		return args;
	}
	
	public String getServerHost() {
		return host;
	}
	
	public String getServerPort() {
		return port;
	}
	public int error() {
		return ++error;
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
}