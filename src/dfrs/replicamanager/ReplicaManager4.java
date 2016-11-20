package dfrs.replicamanager;

import dfrs.utils.Config;

public class ReplicaManager4 extends BaseRM {
	public static final String RM_HOST = Config.getRmHost4();
	
	public static final int RM_RECEIVE_FE_PROT = 7004;
	public static final int RM_RECEIVE_SEQUENCER_PROT = 7104;
	public static final int RM_RECEIVE_RM_PROT = 7204;
	//HEARTBEAT
	public static final int RM_RECEIVE_HEARTBEAT_PROT = 7240;
	
	private static ReplicaManager4 rm;
	private String[] args;
	public ReplicaManager4(String[] args) {
		super(args);
		this.args = args;
	}

	public static void main(String[] args) {
		rm = new ReplicaManager4(args);
		rm.startRM();
	}
	
	@Override
	protected String restartServer() {
		return RMSender.getInstance().send(RM_HOST, RM_RECEIVE_HEARTBEAT_PROT, STATE_RECOVERING);
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
		return RM_HOST;
	}

	@Override
	protected int getS2FEport() {
		return Config.FE_RECEIVE_SERVER_PORT_4;
	}

	@Override
	protected String getRMName() {
		return "RM4";
	}
}
