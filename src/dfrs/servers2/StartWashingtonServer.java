package dfrs.servers2;



import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import DFRSApp.Server;
import DFRSApp.ServerHelper;
import driver.TestConcurrency;

public class StartWashingtonServer {

	public static String SERVER_NAME = "Washington";

	public static void main(String args[]) {
		System.out.println(SERVER_NAME + " Server is ready and waiting ...");
		StartWashingtonServer server = new StartWashingtonServer();
		server.initializeServer(args);
 
	}

	private void initializeServer(String args[]) {
		try {
			// create and initialize the ORB //// get reference to rootpoa &amp;
			// activate the POAManager
			ORB orb = ORB.init(args, null);
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			// create servant and register it with the ORB
			WashingtonServerObj obj = new WashingtonServerObj();
			obj.setORB(orb);

			// get object reference from the servant
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(obj);
			Server href = ServerHelper.narrow(ref);

			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			NameComponent path[] = ncRef.to_name(SERVER_NAME);
			ncRef.rebind(path, href);


			// wait for invocations from clients
			while (true) {
				orb.run();
 
			}
		}

		catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}

		System.out.println(SERVER_NAME + " Server Exiting ...");

	}
}
