package dfrs.servers2;

import java.io.FileWriter;

public class Log {
	private String who;
	private String timeStamp;
	private String operation;
	
	
	public Log(String timeStamp, String who, String operation) {
		this.who = who;
		this.timeStamp = timeStamp;
		this.operation = operation;
	}
	
	
	public void writeToLog(String fileName) {
		 try {
	         	FileWriter fw = new FileWriter(fileName, true);
	         	fw.write(this + "\n");	         
	            fw.close();
	     } catch (Exception e) {
	    	 e.printStackTrace();
	     }
	}


	@Override
	public String toString() {
		return "[" + this.timeStamp + "] " + who + " " + operation + ".";
	}
	
	
	
}
