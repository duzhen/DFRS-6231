package dfrs.sequencer;

public class SE2CMMulticast {
	
	public String content ;
	
	public SE2CMMulticast(String content) {
		
		this.content = content;
	}

	public ClusterManagerSender cma1,cma2,cma3,cma4;
	public void initial(){
		cma1 = new ClusterManagerSender("localhost",7101,content);
		cma2 = new ClusterManagerSender("localhost",7102,content);
		cma3 = new ClusterManagerSender("localhost",7103,content);
		cma4 = new ClusterManagerSender("localhost",7104,content);
	}
	
	public void execute(){
		cma1.run();
		cma2.run();
		cma3.run();
		cma4.run();
	}
}
