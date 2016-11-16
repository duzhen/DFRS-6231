package dfrs.frontend;

import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;

import dfrs.ServerInterface;
import dfrs.ServerInterfaceHelper;

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
	
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(serverIm);
			ServerInterface href = ServerInterfaceHelper.narrow(ref);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			String name = "Server";
			NameComponent path[] = ncRef.to_name(name);
			ncRef.rebind(path, href);
			
			System.out.println("Front end lServer ready and waiting ...");
			orb.run();
			//begin to run the corba server
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
