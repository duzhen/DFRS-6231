package dfrs.sequencer;

import dfrs.frontend.FESender;
import dfrs.replicamanager.ReplicaManager1;
import dfrs.replicamanager.ReplicaManager2;
import dfrs.replicamanager.ReplicaManager3;
import dfrs.replicamanager.ReplicaManager4;
import dfrs.utils.Config;
import net.rudp.ReliableSocket;

public class SE2CMMulticast {
	
	public String content ;
	
	public SE2CMMulticast(String content) {
		
		this.content = content;
	}

	public ClusterManagerSender cma1,cma2,cma3,cma4;
	public void initial(){
		ReliableSocket socket = FESender.getInstance().getSocket(Config.getRmHost1(),ReplicaManager1.RM_RECEIVE_SEQUENCER_PROT);
		cma1 = new ClusterManagerSender(socket,content);
		socket = FESender.getInstance().getSocket(Config.getRmHost2(),ReplicaManager2.RM_RECEIVE_SEQUENCER_PROT);
		cma2 = new ClusterManagerSender(socket,content);
		socket = FESender.getInstance().getSocket(Config.getRmHost3(),ReplicaManager3.RM_RECEIVE_SEQUENCER_PROT);
		cma3 = new ClusterManagerSender(socket,content);
		socket = FESender.getInstance().getSocket(Config.getRmHost4(),ReplicaManager4.RM_RECEIVE_SEQUENCER_PROT);
		cma4 = new ClusterManagerSender(socket,content);
	}
	
	public void execute(){
		
		try {
			cma1.start();
			cma1.join();
			cma2.start();
			cma2.join();
			cma3.start();
			cma3.join();
			cma4.start();
			cma4.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
