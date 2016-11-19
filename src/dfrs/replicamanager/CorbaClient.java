package dfrs.replicamanager;

import java.util.HashMap;
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import dfrs.ServerInterface;
import dfrs.ServerInterfaceHelper;
import dfrs.servers.BaseServerCluster;
import dfrs.servers.ServerCluster1;
import dfrs.servers.ServerCluster2;
import dfrs.servers.ServerCluster3;
import dfrs.servers.ServerCluster4;

public class CorbaClient {
	private HashMap<String, ServerInterface> clients = new HashMap<String, ServerInterface>();
	private BaseRM rm;
	private String[] args;
	private String host;
	private String port;

	public CorbaClient(BaseRM rm, String[] args) {
		this.rm = rm;
		this.args = args;
	}

	public String requestCorbaServer(String input) {
		String reply = "error";
		
		String[] params = input.split("\\$");
        for(int i=0;i<params.length;i++) {
        	System.out.println(i+": "+params[i]);
        }
        if(true) {//) {
        	try {
        		reply = getCorbarClient(BaseServerCluster.SERVER_MTL).editFlightRecord(params[1], params[2], params[3], params[4], Integer.valueOf(params[5]), Integer.valueOf(params[6]), Integer.valueOf(params[7]));
        	} catch (Exception e) {
        		e.printStackTrace();
        		clients.remove(BaseServerCluster.SERVER_MTL);
        		reply = getCorbarClient(BaseServerCluster.SERVER_MTL).editFlightRecord(params[1], params[2], params[3], params[4], Integer.valueOf(params[5]), Integer.valueOf(params[6]), Integer.valueOf(params[7]));
        	}
        }
      
		return params[0]+"$"+reply;
	}
	
	public ServerInterface getCorbarClient(String server) {
		if(server == null || server.length() == 0)
			return null;
		ServerInterface corba = clients.get(server);
		if(corba == null) {
			if (ReplicaManager1.class == rm.getClass()) {
				corba = createCorba(args, ServerCluster1.SERVER_HOST, ServerCluster1.getCorbaPort(server));
			} else if (ReplicaManager2.class == rm.getClass()) {
				corba = createCorba(args, ServerCluster2.SERVER_HOST, ServerCluster2.getCorbaPort(server));
			} else if (ReplicaManager3.class == rm.getClass()) {
				corba = createCorba(args, ServerCluster3.SERVER_HOST, ServerCluster3.getCorbaPort(server));
			} else if (ReplicaManager4.class == rm.getClass()) {
				corba = createCorba(args, ServerCluster4.SERVER_HOST, ServerCluster4.getCorbaPort(server));
			}
			clients.put(server, corba);
		}
		return corba;
	}

	private ServerInterface createCorba(String[] args, String host, String port) {
		try {
			Properties props = new Properties();
			props.put("org.omg.CORBA.ORBInitialPort", port);
			props.put("org.omg.CORBA.ORBInitialHost", host);
			ORB orb = ORB.init(args, props);

			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			return ServerInterfaceHelper.narrow(ncRef.resolve_str("Server"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}