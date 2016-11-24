package dfrs.replicamanager;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;

import dfrs.utils.Config;
import net.rudp.ReliableSocket;

public class ClusterManager {
	private ArrayList<String> logs = new ArrayList<String>();
	private BaseRM rm;
	private String[] args;
	private CorbaClient corba;
	private int expectID;
	private boolean test = false;
	
	public ClusterManager(BaseRM rm, String[] args) {
		this.rm = rm;
		this.args = args;
		this.corba = new CorbaClient(rm, args);
	}
	
	public void recoveryData() {
//		String logs = RMSender.getInstance().send(HOST, PORT, command);
//		for(String input:logs) {
//			corba.requestCorbaServer(input);
//		}
	}
	
	public void correctData() {
//		String data = RMSender.getInstance().send(HOST, PORT, command);
//		String[] servers = data
//		corba.correctCorbaData(data);
	}
	
	public void updateCorbaClient(String[] servers) {
		corba.updateCorbaClient(servers);
	}
	
	private void saveRequestLog(String input) {
		logs.add(input);
	}
	
	public void demoFailure() {
		test = true;
	}
	
	public String processRequest(String input, String host, int port, boolean isRecover) {
		String reply = "error";
		if(input == null)
			return reply;
		String[] params = input.split("\\$");
		if(params!=null&&params.length>0) {
			for(int i=0;i<params.length;i++) {
	        	System.out.println(i+": "+params[i]);
	        }
			reply = corba.requestCorbaServer(params, test);
//			while(Integer.valueOf(params[1])>expectID) {
//				if(Integer.valueOf(params[1])==expectID+1) {
//					reply = corba.requestCorbaServer(params, test);
//					expectID++;
//					break;
//				} else {
//					String data[] = RMSender.getInstance().send(HOST, PORT, "6$expectID");
//					for(int i=0;i<data.length;i++) {
//						String[] p = data[i].split("\\$");
//						corba.requestCorbaServer(p, false);
//						expectID++;
//					}
//				}
//			}
		}
		if(test) {
			System.out.println(rm.getClass()+":Test Fail");
			test = false;
		}
		if(Config.TEST) {
			if("555".equals(params[1])||"556".equals(params[1])||"557".equals(params[1]))
				return reply;
		}
		if (!isRecover) {
	        ReliableSocket clientSocket;
			try {
				clientSocket = new ReliableSocket(host, port);
//				ReliableSocketOutputStream outToServer = (ReliableSocketOutputStream) clientSocket.getOutputStream();
				PrintWriter outputBuffer = new PrintWriter(clientSocket.getOutputStream());
				outputBuffer.println(reply);
				outputBuffer.flush();
//				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//	            inFromClient.readLine();
//				clientSocket.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
//			if(reply.contains("success"))
			saveRequestLog(input);
		}

		return reply;
	}
}
