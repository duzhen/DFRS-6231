package dfrs.replicamanager;

import java.net.ConnectException;
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
import dfrs.utils.Config;
import dfrs.utils.Utils;

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
	//
	public String correctCorbaData(String[] servers,String[] data) {
//		for(int i=0;i<servers.length&&i<data.length;i++) {
//			getCorbaClient(server[]).updateDataBase(data[]);
//		}
		return "";
	}
	
	public String requestCorbaServer(String[] params, boolean test) {
		String reply = "error";
		
		if(params!=null&&params.length>0) {
			String server = Utils.getServer(params[2]);
			ServerInterface corba = getCorbaClient(server);
			if(corba != null) {
		        try {
		        	reply = requestCorba(params, corba);
	        	} catch (Exception e) {
	        		System.out.println(e.getMessage());
	        		clients.remove(BaseServerCluster.SERVER_MTL);
	        		corba = getCorbaClient(server);
	        		try {
			        	reply = requestCorba(params, corba);
		        	} catch (Exception e1) {
		        		System.out.println(e1.getMessage());
		        	}
	        	}
			}
			if(test) {
				if(Config.SUCCESS.equals(reply)) {
					return params[1]+"$"+Config.FAIL;
				} else if(Config.FAIL.equals(reply)) {
					return params[1]+"$"+Config.SUCCESS;
				}
			}
			return params[1]+"$"+reply;
		}

		return reply;
	}

	private String requestCorba(String[] params, ServerInterface corba) throws Exception {
		String reply = "error";
		if(corba==null)
			return reply;
		try {
			if("1".equals(params[0])&&params.length>10) {
				reply = corba.bookFlight(params[2], params[3], params[4], params[5], params[7], params[8], params[9], params[10]);
			} else if("2".equals(params[0])&&params.length>8) {
				reply = corba.editFlightRecord(params[2], params[3], params[4], params[5], Integer.valueOf(params[6]), Integer.valueOf(params[7]), Integer.valueOf(params[8]));
			} else if("3".equals(params[0])&&params.length>3) {
				reply = corba.getBookedFlightCount(params[2], params[3]);
			} else if("4".equals(params[0])&&params.length>5) {
				reply = corba.transferReservation(params[2], params[3], params[4], params[5]);
			}
		} catch (Exception e) {
    		throw e;
    	}
		return reply;
	}
	
	public void updateCorbaClient(String[] servers) {
		ServerInterface corba = null;
		for(int i=0;i<servers.length;i++) {
			if (ReplicaManager1.class == rm.getClass()) {
				corba = createCorba(args, servers[i], ServerCluster1.CORBA, ServerCluster1.SERVER_HOST, ServerCluster1.SERVER_CORBA_PORT);
			} else if (ReplicaManager2.class == rm.getClass()) {
				corba = createCorba(args, servers[i], ServerCluster2.CORBA, ServerCluster2.SERVER_HOST, ServerCluster2.SERVER_CORBA_PORT);
			} else if (ReplicaManager3.class == rm.getClass()) {
				corba = createCorba(args, servers[i], ServerCluster3.CORBA, ServerCluster3.SERVER_HOST, ServerCluster3.SERVER_CORBA_PORT);
			} else if (ReplicaManager4.class == rm.getClass()) {
				corba = createCorba(args, servers[i], ServerCluster4.CORBA, ServerCluster4.SERVER_HOST, ServerCluster4.SERVER_CORBA_PORT);
			}
			if(corba !=null)
				clients.put(servers[i], corba);
		}
	}
	
	private ServerInterface getCorbaClient(String server) {
		if(server == null || server.length() == 0)
			return null;
		ServerInterface corba = clients.get(server);
		if(corba == null) {
			if (ReplicaManager1.class == rm.getClass()) {
				corba = createCorba(args, server, ServerCluster1.CORBA, ServerCluster1.SERVER_HOST, ServerCluster1.SERVER_CORBA_PORT);
			} else if (ReplicaManager2.class == rm.getClass()) {
				corba = createCorba(args, server, ServerCluster2.CORBA, ServerCluster2.SERVER_HOST, ServerCluster2.SERVER_CORBA_PORT);
			} else if (ReplicaManager3.class == rm.getClass()) {
				corba = createCorba(args, server, ServerCluster3.CORBA, ServerCluster3.SERVER_HOST, ServerCluster3.SERVER_CORBA_PORT);
			} else if (ReplicaManager4.class == rm.getClass()) {
				corba = createCorba(args, server, ServerCluster4.CORBA, ServerCluster4.SERVER_HOST, ServerCluster4.SERVER_CORBA_PORT);
			}
			clients.put(server, corba);
		}
		return corba;
	}

	private ServerInterface createCorba(String[] args, String server, String n, String host, String port) {
		try {
			Properties props = new Properties();
			props.put("org.omg.CORBA.ORBInitialPort", port);
			props.put("org.omg.CORBA.ORBInitialHost", host);
			ORB orb = ORB.init(args, props);

			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			return ServerInterfaceHelper.narrow(ncRef.resolve_str(server+n));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}