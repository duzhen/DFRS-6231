package dfrs.replicamanager;

import dfrs.servers1.ServerImpl1;

public class ReplicaManager1 extends BaseRM {
	
	public static final String RM_HOST = "localhost";
	
	public static final int RM_RECEIVE_FE_PROT = 7001;
	public static final int RM_RECEIVE_SEQUENCER_PROT = 7101;
	public static final int RM_RECEIVE_RM_PROT = 7201;
	//HEARTBEAT
	public static final int RM_RECEIVE_HEARTBEAT_PROT = 7210;
	
	private ReplicaManager1 rm;
	private Thread threadMTL;
	private Thread threadWST;
	private Thread threadNDL;
	
	public ReplicaManager1(String[] args) {
		threadMTL = new Thread(new Runnable() {
			@Override
			public void run() {
				initServer(new ServerImpl1(), args, ServerImpl1.SERVER_MTL, RM_HOST, ServerImpl1.SERVER_MTL_CORBA_PORT);
			}
		});
		threadWST = new Thread(new Runnable() {
			@Override
			public void run() {
				initServer(new ServerImpl1(), args, ServerImpl1.SERVER_WST, RM_HOST,  ServerImpl1.SERVER_WST_CORBA_PORT);
			}
		});
		threadNDL = new Thread(new Runnable() {
			@Override
			public void run() {
				initServer(new ServerImpl1(), args, ServerImpl1.SERVER_NDL, RM_HOST,  ServerImpl1.SERVER_NDL_CORBA_PORT);
			}
		});
	}

	private void startServer() {
		threadMTL.start();
		threadWST.start();
		threadNDL.start();
		startReceiveHeartBeat();
		startReceiveRM();
		startReceiveSE();
		startReceiveFE();
	}
	
	public static void main(String[] args) {
		ReplicaManager1 rm = new ReplicaManager1(args);
		rm.startServer();
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
}
