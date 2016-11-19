package dfrs.frontend;

import dfrs.sequencer.ClusterManagerSender;

public class FE2RMMulticast {

	
	public String content ;
	
	public FE2RMMulticast(String content) {
		
		this.content = content;
	}

	public ClusterManagerSender cma1,cma2,cma3,cma4;
	public void initial(){
		cma1 = new ClusterManagerSender("localhost",8201,content);
		cma2 = new ClusterManagerSender("localhost",8202,content);
		cma3 = new ClusterManagerSender("localhost",8203,content);
		cma4 = new ClusterManagerSender("localhost",8204,content);
	}
	
	public void execute(){
		cma1.run();
		cma2.run();
		cma3.run();
		cma4.run();
	}

}
