package dfrs.servers;

import dfrs.utils.Config;

public class ServerCluster3 extends BaseServerCluster {
	
	public static final String SERVER_HOST = Config.getServerHost3();
	//CORBA
	public static final String SERVER_CORBA_PORT = "9070";
	//RM COMMAND
	public static final int SC_RECEIVE_RM_PROT = 7330;
	
	private static ServerCluster3 rm;
	private String[] args;
	public ServerCluster3(String[] args) {
		this.args = args;
	}

	public static void main(String[] args) {
		rm = new ServerCluster3(args);
		rm.createServers(SERVERS);
		rm.startServer(SERVERS);
		rm.startReceiveRM();
	}

	@Override
	protected void createServers(String[] servers) {
		if(servers == null)
			return;
		for(int i=0;i<servers.length;i++) {
			registerServer(CorbaServer.createServer(this.getClass(), servers[i], args));
		}
	}
	
	@Override
	protected int getSCport() {
		return SC_RECEIVE_RM_PROT;
	}
}
