package dfrs.servers;

import dfrs.utils.Config;

public class ServerCluster2 extends BaseServerCluster {
	
	public static final String CORBA = "2";
	public static final String SERVER_HOST = Config.getServerHost2();
	//CORBA
	public static final String SERVER_CORBA_PORT = "1050";//"9060";
	//RM COMMAND
	public static final int SC_RECEIVE_RM_PROT = 7320;
	
	private static ServerCluster2 rm;
	private String[] args;
	public ServerCluster2(String[] args) {
		this.args = args;
	}

	public static void main(String[] args) {
		rm = new ServerCluster2(args);
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
