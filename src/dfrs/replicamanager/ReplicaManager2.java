package dfrs.replicamanager;

import dfrs.servers.ServerCluster2;
import dfrs.utils.Config;

public class ReplicaManager2 extends BaseRM {
	public static final String RM_HOST = Config.getRmHost2();
	
	public static final int RM_RECEIVE_FE_PROT = 7002;
	public static final int RM_RECEIVE_SEQUENCER_PROT = 7102;
	public static final int RM_RECEIVE_RM_PROT = 7202;
	//HEARTBEAT
	public static final int RM_RECEIVE_HEARTBEAT_PROT = 7220;
	
	private static ReplicaManager2 rm;
	private String[] args;
	public ReplicaManager2(String[] args) {
		super(args);
		this.args = args;
	}

	public static void main(String[] args) {
		rm = new ReplicaManager2(args);
		rm.startRM();
	}
	
	@Override
	protected String sendCommandToServer(String command) {
		return RMSender.getInstance().send(ServerCluster2.SERVER_HOST, ServerCluster2.SC_RECEIVE_RM_PROT, command);
	}
	
	@Override
	protected int getFEport() {
		return RM_RECEIVE_FE_PROT;
	}

	@Override
	protected int getSEport() {
		return RM_RECEIVE_SEQUENCER_PROT;
	}
	
	@Override
	protected int getRMport() {
		return RM_RECEIVE_RM_PROT;
	}

	@Override
	protected int getHBport() {
		return RM_RECEIVE_HEARTBEAT_PROT;
	}

//	@Override
//	protected String getFEHost() {
//		return RM_HOST;
//	}

	@Override
	protected int getS2FEport() {
		return Config.FE_RECEIVE_SERVER_PORT_2;
	}

	@Override
	protected String getRMName() {
		return "2";
	}
	
	@Override
	protected String getLogFileName() {
		return "src/dfrs/replicamanager/RM2.txt";
	}
}
