package dfrs.replicamanager;

import java.util.HashMap;
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import dfrs.ServerInterface;
import dfrs.ServerInterfaceHelper;

public class ClusterManager {
	private HashMap<String, ServerInterface> servers = new HashMap<String, ServerInterface>();

	public ServerInterface getCorbarClient(String server) {
		return servers.get(server);
	}
	
	public void addCorbaClient(String[] args, String server, String host, String port) {
		servers.put(server, getCorba(args, host, port));
	}

	private ServerInterface getCorba(String[] args, String host, String port) {
		try {
			Properties props = new Properties();
		    props.put("org.omg.CORBA.ORBInitialPort", port);    
		    props.put("org.omg.CORBA.ORBInitialHost", host); 
		    ORB orb = ORB.init(args, props);

			org.omg.CORBA.Object objRef =
			orb.resolve_initial_references("NameService");
			
			NamingContextExt ncRef = 
					NamingContextExtHelper.narrow(objRef);
			return ServerInterfaceHelper.narrow(ncRef.resolve_str("Server"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
