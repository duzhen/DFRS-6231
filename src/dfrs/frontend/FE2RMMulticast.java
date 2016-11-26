package dfrs.frontend;

import dfrs.replicamanager.ReplicaManager1;
import dfrs.replicamanager.ReplicaManager2;
import dfrs.replicamanager.ReplicaManager3;
import dfrs.replicamanager.ReplicaManager4;

import dfrs.utils.Config;

public class FE2RMMulticast {

	
	public String content ;
	
	public FE2RMMulticast(String content) {
		
		this.content = content;
	}

	public FE2RMSender cma1,cma2,cma3,cma4;
	public void initial(){
		cma1 = new FE2RMSender(Config.getRmHost1(),ReplicaManager1.RM_RECEIVE_FE_PROT,content);
		cma2 = new FE2RMSender(Config.getRmHost2(),ReplicaManager2.RM_RECEIVE_FE_PROT,content);
		cma3 = new FE2RMSender(Config.getRmHost3(),ReplicaManager3.RM_RECEIVE_FE_PROT,content);
		cma4 = new FE2RMSender(Config.getRmHost4(),ReplicaManager4.RM_RECEIVE_FE_PROT,content);
	}
	
	public void execute(){
		cma1.run();
		cma2.run();
		cma3.run();
		cma4.run();
		
		try {
			cma1.join();
			cma2.join();
			cma3.join();
			cma4.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
