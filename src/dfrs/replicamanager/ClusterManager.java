package dfrs.replicamanager;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import dfrs.ServerInterface;
import dfrs.ServerInterfaceHelper;
import net.rudp.ReliableSocket;
import net.rudp.ReliableSocketOutputStream;

public class ClusterManager {
	private HashMap<String, ServerInterface> clients = new HashMap<String, ServerInterface>();
	private ArrayList<String> logs = new ArrayList<String>();
	
	public void recoveryLog() {
		for(String input:logs) {
			requestCorbaServer(input);
		}
	}
	
	private void saveRequestLog(String input) {
		logs.add(input);
	}
	
	public String requestCorbaServer(String input) {
		String reply = "error";
		
		String[] params = input.split("\\$");
        for(int i=0;i<params.length;i++) {
        	System.out.println(i+": "+params[i]);
        }
        if(true) {//) {
        	reply = getCorbarClient(BaseRM.SERVER_MTL).editFlightRecord(params[1], params[2], params[3], params[4], Integer.valueOf(params[5]), Integer.valueOf(params[6]), Integer.valueOf(params[7]));
        }
      
		return params[0]+"$"+reply;
	}
	
	public String requestCorbaServer(String input, String host, int port) {
		String reply = requestCorbaServer(input);
		
        ReliableSocket clientSocket;
		try {
			clientSocket = new ReliableSocket(host, port);
			ReliableSocketOutputStream outToServer = (ReliableSocketOutputStream) clientSocket.getOutputStream();
			PrintWriter outputBuffer = new PrintWriter(outToServer);
			outputBuffer.println(reply);
			outputBuffer.flush();
			clientSocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		saveRequestLog(input);
		return reply;
	}
	
	private ServerInterface getCorbarClient(String server) {
		return clients.get(server);
	}
	
	public void createCorbaClient(String[] args, String server, String host, String port) {
		clients.put(server, getCorba(args, host, port));
	}

	private ServerInterface getCorba(String[] args, String host, String port) {
		try {
			Properties props = new Properties();
		    props.put("org.omg.CORBA.ORBInitialPort", port);    
		    props.put("org.omg.CORBA.ORBInitialHost", host); 
		    ORB orb = ORB.init(args, props);

			org.omg.CORBA.Object objRef =
			orb.resolve_initial_references("NameService");
			
			NamingContextExt ncRef = 
					NamingContextExtHelper.narrow(objRef);
			return ServerInterfaceHelper.narrow(ncRef.resolve_str("Server"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
