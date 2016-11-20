package dfrs.replicamanager;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;

import net.rudp.ReliableSocket;

public class ClusterManager {
	private ArrayList<String> logs = new ArrayList<String>();
	private BaseRM rm;
	private String[] args;
	private CorbaClient corba;
	private boolean test = false;
	
	public ClusterManager(BaseRM rm, String[] args) {
		this.rm = rm;
		this.args = args;
		this.corba = new CorbaClient(rm, args);
	}
	
	public void recoveryLog() {
		for(String input:logs) {
			corba.requestCorbaServer(input);
		}
	}
	
	private void saveRequestLog(String input) {
		logs.add(input);
	}
	
	public void testFailure() {
		test = true;
	}
	public String requestCorbaServer(String input, String host, int port) {
		String reply = corba.requestCorbaServer(input);
		
        ReliableSocket clientSocket;
		try {
			clientSocket = new ReliableSocket(host, port);
//			ReliableSocketOutputStream outToServer = (ReliableSocketOutputStream) clientSocket.getOutputStream();
			PrintWriter outputBuffer = new PrintWriter(clientSocket.getOutputStream());
			outputBuffer.println(reply);
			outputBuffer.flush();
			clientSocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		if(reply.contains("success"))
		saveRequestLog(input);
		return reply;
	}
}
