package serverApp;

import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;

import server.ServerInterface;
import server.ServerInterfaceHelper;

public class FrontendServer {

	public static void main(String[] args) {
		
	try {
		Properties props = new Properties();
		props.put("org.omg.CORBA.ORBInitialPort", "1050");    
		props.put("org.omg.CORBA.ORBInitialHost", "127.0.0.1"); 

		ORB	 orb = ORB.init(args , props);
		POA rootpoa = (POA)orb.resolve_initial_references("RootPOA");
		rootpoa.the_POAManager().activate();
		FEImpl serverIm = new FEImpl("localhost", 8888);
		/*
		 6789 6790 MTL
		 6791 6792 WST
		 6793 6794 NDL
		 1050 mtl
		 1051 NDL
		 1052 WST
		 */
		//used initial client and send message
//		serverIm.portone = 6791;
//		serverIm.porttwo = 6793;
//		serverIm.location="MTL";

		org.omg.CORBA.Object ref = rootpoa.servant_to_reference(serverIm);
		ServerInterface href = ServerInterfaceHelper.narrow(ref);
		org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
		NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
		String name = "Server";
		NameComponent path[] = ncRef.to_name(name);
		ncRef.rebind(path, href);
		
//    	serverThread sTone = new serverThread(6789,serverIm);
//    	serverThread sTtwo = new serverThread(6790,serverIm);
    	
		System.out.println("Front end lServer ready and waiting ...");
		orb.run();
	}
	
	catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

}
