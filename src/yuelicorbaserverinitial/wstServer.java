package yuelicorbaserverinitial;

import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;

import server.ServerInterface;
import server.ServerInterfaceHelper;

public class wstServer {

	public static void main(String[] args) {


		try {
			Properties props = new Properties();
			props.put("org.omg.CORBA.ORBInitialPort", "1050");    
			props.put("org.omg.CORBA.ORBInitialHost", "127.0.0.1"); 

			ORB	 orb = ORB.init(args , props);
			POA rootpoa = (POA)orb.resolve_initial_references("RootPOA");
			rootpoa.the_POAManager().activate();
			ServerImpl1 serverIm = new ServerImpl1();
			/*
			 6789 6790 MTL
			 6791 6792 WST
			 6793 6794 NDL
			 1050 mtl
			 1051 NDL
			 1052 WST
			 */
			//used initial client and send message
			serverIm.portone = 6789;
			serverIm.porttwo = 6794;
			serverIm.location="WST";
			//create flight to mtl
			managerRecord a = new managerRecord();
			a.managerID="WST0001";
			a.destination = "MTL";
			a.flightDate ="11022016";
			serverIm.planeMap.put((a.destination+a.flightDate) , a);
			
			//create flight to NDL
			managerRecord b = new managerRecord();
			b.managerID="WST0001";
			b.destination = "NDL";
			b.flightDate ="11022016";
			serverIm.planeMap.put((b.destination+b.flightDate) , b);
			
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(serverIm);
			ServerInterface href = ServerInterfaceHelper.narrow(ref);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			String name = "wstServer";
			NameComponent path[] = ncRef.to_name(name);
			ncRef.rebind(path, href);
			System.out.println("wstServer ready and waiting ...");
			
	    	serverThread sTone = new serverThread(6791,serverIm);
	    	serverThread sTtwo = new serverThread(6792,serverIm);
	    	
			orb.run();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	
	}
}
