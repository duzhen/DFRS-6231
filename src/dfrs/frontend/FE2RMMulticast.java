package dfrs.frontend;

import dfrs.sequencer.ClusterManagerSender;

public class FE2RMMulticast {

	
	public String content ;
	
	public FE2RMMulticast(String content) {
		
		this.content = content;
	}

	public ClusterManagerSender cma1,cma2,cma3,cma4;
	public void initial(){
		cma1 = new ClusterManagerSender("localhost",7001,content);
		cma2 = new ClusterManagerSender("localhost",7002,content);
		cma3 = new ClusterManagerSender("localhost",7003,content);
		cma4 = new ClusterManagerSender("localhost",7004,content);
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