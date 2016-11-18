package dfrs.replicamanager;

public class ReplicaManager1 extends BaseRM {
	
	public static final String RM_HOST = "localhost";
	
	public static final int RM_RECEIVE_FE_PROT = 7001;
	public static final int RM_RECEIVE_SEQUENCER_PROT = 7101;
	public static final int RM_RECEIVE_RM_PROT = 7201;
	//HEARTBEAT
	public static final int RM_RECEIVE_HEARTBEAT_PROT = 7210;
	
	private static ReplicaManager1 rm;
	private String[] args;
	public ReplicaManager1(String[] args) {
		this.args = args;
	}

	public static void main(String[] args) {
		rm = new ReplicaManager1(args);
		rm.createServers(SERVERS);
		rm.startServer(SERVERS);
	}

	@Override
	protected int getFEport() {
		// TODO Auto-generated method stub
		return RM_RECEIVE_FE_PROT;
	}

	@Override
	protected int getSEport() {
		// TODO Auto-generated method stub
		return RM_RECEIVE_SEQUENCER_PROT;
	}
	
	@Override
	protected int getRMport() {
		// TODO Auto-generated method stub
		return RM_RECEIVE_RM_PROT;
	}

	@Override
	protected int getHBport() {
		// TODO Auto-generated method stub
		return RM_RECEIVE_HEARTBEAT_PROT;
	}

	@Override
	protected String getHost() {
		// TODO Auto-generated method stub
		return RM_HOST;
	}

	@Override
	protected int getS2FEport() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void createServers(String[] servers) {
		if(servers == null)
			return;
		for(int i=0;i<servers.length;i++) {
			registerServer(Server.createServer(this.getClass(), servers[i], args));
		}
	}

	@Override
	protected String getRMName() {
		return "RM1";
	}
}
