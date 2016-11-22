package dfrs.sequencer;

import dfrs.replicamanager.ReplicaManager1;
import dfrs.replicamanager.ReplicaManager2;
import dfrs.replicamanager.ReplicaManager3;
import dfrs.replicamanager.ReplicaManager4;
import dfrs.utils.Config;

public class SE2CMMulticast {
	
	public String content ;
	
	public SE2CMMulticast(String content) {
		
		this.content = content;
	}

	public ClusterManagerSender cma1,cma2,cma3,cma4;
	public void initial(){
		cma1 = new ClusterManagerSender(Config.getRmHost1(),ReplicaManager1.RM_RECEIVE_SEQUENCER_PROT,content);
		cma2 = new ClusterManagerSender(Config.getRmHost2(),ReplicaManager2.RM_RECEIVE_SEQUENCER_PROT,content);
		cma3 = new ClusterManagerSender(Config.getRmHost3(),ReplicaManager3.RM_RECEIVE_SEQUENCER_PROT,content);
		cma4 = new ClusterManagerSender(Config.getRmHost4(),ReplicaManager4.RM_RECEIVE_SEQUENCER_PROT,content);
	}
	
	public void execute(){
		cma1.run();
		cma2.run();
		cma3.run();
		cma4.run();
	}
}
