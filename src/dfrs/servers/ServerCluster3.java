package dfrs.servers;

import dfrs.utils.Config;

public class ServerCluster3 extends BaseServerCluster {
	
	public static final String SERVER_HOST = Config.getServerHost3();
	//CORBA
	public static final String SERVER_MTL_CORBA_PORT = "9070";
	public static final String SERVER_WST_CORBA_PORT = "9071";
	public static final String SERVER_NDL_CORBA_PORT = "9072";
	//HEARTBEAT
//	public static final int RM_HEARTBEAT_MTL_PORT = 7231;
//	public static final int RM_HEARTBEAT_WST_PORT = 7232;
//	public static final int RM_HEARTBEAT_NDL_PORT = 7233;
	
	public static String getCorbaPort(String server) {
		if(SERVER_MTL.equals(server)) {
			return SERVER_MTL_CORBA_PORT;
		} else if(SERVER_WST.equals(server)) {
			return SERVER_WST_CORBA_PORT;
		} else if(SERVER_NDL.equals(server)) {
			return SERVER_NDL_CORBA_PORT;
		}
		return "";
	}
	
	private static ServerCluster3 rm;
	private String[] args;
	public ServerCluster3(String[] args) {
		this.args = args;
	}

	public static void main(String[] args) {
		rm = new ServerCluster3(args);
		rm.createServers(SERVERS);
		rm.startServer(SERVERS);
	}

	@Override
	protected void createServers(String[] servers) {
		if(servers == null)
			return;
		for(int i=0;i<servers.length;i++) {
			registerServer(CorbaServer.createServer(this.getClass(), servers[i], args));
		}
	}
}
