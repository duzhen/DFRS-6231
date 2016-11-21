package dfrs.replicamanager;

import dfrs.servers.ServerCluster1;
import dfrs.utils.Config;

public class ReplicaManager1 extends BaseRM {
	public static final String RM_HOST = Config.getRmHost1();
	
	public static final int RM_RECEIVE_FE_PROT = 7001;
	public static final int RM_RECEIVE_SEQUENCER_PROT = 7101;
	public static final int RM_RECEIVE_RM_PROT = 7201;
	//HEARTBEAT
	public static final int RM_RECEIVE_HEARTBEAT_PROT = 7210;
	
	private static ReplicaManager1 rm;
	private String[] args;
	public ReplicaManager1(String[] args) {
		super(args);
		this.args = args;
	}

	public static void main(String[] args) {
		rm = new ReplicaManager1(args);
		rm.startRM();
		if(Config.TEST) {
			rm.startDemo();
		}
	}
	
	@Override
	protected String sendCommandToServer(String command) {
		return RMSender.getInstance().send(ServerCluster1.SERVER_HOST, ServerCluster1.SC_RECEIVE_RM_PROT, command);
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
//		return Config.getFeHost();
//	}

	@Override
	protected int getS2FEport() {
		return Config.FE_RECEIVE_SERVER_PORT_1;
	}

	@Override
	protected String getRMName() {
		return "1";
	}
}
