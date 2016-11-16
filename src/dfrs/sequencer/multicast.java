package dfrs.sequencer;

public class multicast {
	
	public String content ;
	
	public multicast(String content) {
		
		this.content = content;
	}

	public clusterManagerAddress cma1,cma2,cma3,cma4;
	public void initial(){
		cma1 = new clusterManagerAddress("localhost",8001,content);
		cma2 = new clusterManagerAddress("localhost",8002,content);
		cma3 = new clusterManagerAddress("localhost",8003,content);
		cma4 = new clusterManagerAddress("localhost",8004,content);
	}
	
	public void execute(){
		cma1.run();
		cma2.run();
		cma3.run();
		cma4.run();
	}
}
