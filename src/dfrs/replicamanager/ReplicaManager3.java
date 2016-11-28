package dfrs.replicamanager;

import dfrs.servers.ServerCluster3;
import dfrs.utils.Config;

public class ReplicaManager3 extends BaseRM {
	public static final String RM_HOST = Config.getRmHost3();
	
	public static final int RM_RECEIVE_FE_PROT = 7003;
	public static final int RM_RECEIVE_SEQUENCER_PROT = 7103;
	public static final int RM_RECEIVE_RM_PROT = 7203;
	//HEARTBEAT
	public static final int RM_RECEIVE_HEARTBEAT_PROT = 7230;
	
	private static ReplicaManager3 rm;
	private String[] args;
	public ReplicaManager3(String[] args) {
		super(args);
		this.args = args;
	}

	public static void main(String[] args) {
		rm = new ReplicaManager3(args);
		rm.startRM();
		rm.startDemo();
	}
	
	@Override
	protected String sendCommandToServer(String command) {
		return RMSender.getInstance().send(ServerCluster3.SERVER_HOST, ServerCluster3.SC_RECEIVE_RM_PROT, command);
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
		return Config.FE_RECEIVE_SERVER_PORT_3;
	}

	@Override
	protected String getRMName() {
		return "3";
	}
	
	@Override
	protected String getLogFileName() {
		return "src/dfrs/replicamanager/RM3.txt";
	}
}
