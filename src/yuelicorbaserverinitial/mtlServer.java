package yuelicorbaserverinitial;

import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;

import server.ServerInterface;
import server.ServerInterfaceHelper;


public class mtlServer {
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
		serverIm.portone = 6791;
		serverIm.porttwo = 6793;
		serverIm.location="MTL";
		//create a planeMap record
		managerRecord a = new managerRecord();
		a.managerID="MTL0001";
		a.destination = "WST";
		a.flightDate ="11072016";
		serverIm.planeMap.put((a.destination+a.flightDate) , a);
		
		//create a book flight
//		passengerRecord recorda = new passengerRecord();
//		recorda.firstName="yue";
//		recorda.lastName = "li";
//		recorda.address="123";
//		recorda.phoneNumber="321";
//		recorda.destination="NDL";
//		recorda.flightClass="economy";
//		recorda.flightDate = "11022016";
//		recorda.RecordID = serverIm.counter;
//		serverIm.counter++;
//		Character lNameFLetter = recorda.lastName.substring(0, 1).toCharArray()[0];
//		recordLIst recordList = new recordLIst();
//		serverIm.recordTable.put(lNameFLetter, recordList);
//		serverIm.recordTable.get(lNameFLetter).recordList.add(recorda);
		//finish book flight
		
		org.omg.CORBA.Object ref = rootpoa.servant_to_reference(serverIm);
		ServerInterface href = ServerInterfaceHelper.narrow(ref);
		org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
		NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
		String name = "mtlServer";
		NameComponent path[] = ncRef.to_name(name);
		ncRef.rebind(path, href);
		
    	serverThread sTone = new serverThread(6789,serverIm);
    	serverThread sTtwo = new serverThread(6790,serverIm);
    	
		System.out.println("mtlServer ready and waiting ...");
		orb.run();
	}
	
	catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
}
