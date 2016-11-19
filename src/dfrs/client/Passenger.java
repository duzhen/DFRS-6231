package dfrs.client;

import java.util.Properties;
import java.util.Scanner;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import dfrs.ServerInterface;
import dfrs.ServerInterfaceHelper;



public class Passenger {

	
	public static String passengerRecvServer = "";
	public static Scanner keyboard = null;
	public static ServerInterface serverImpl;
	public static String firstName;
	public static String lastName;
	public static String address;
	public static String currentCity = "";
	public static String phoneNumber;
	public static String destination ;
	public static String flightClass;
	public static String flightDate;
	
	public static void main(String[] args) {
		
		try {
			Properties props = new Properties();
		    props.put("org.omg.CORBA.ORBInitialPort", "1050");    
		    props.put("org.omg.CORBA.ORBInitialHost", "127.0.0.1"); 
		    ORB orb = ORB.init(args, props);

			org.omg.CORBA.Object objRef =
			orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = 
					NamingContextExtHelper.narrow(objRef);
			serverImpl = ServerInterfaceHelper.narrow(ncRef.resolve_str("Server"));
		
			//helloImpl.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		 int i = 0;
		 boolean out = true;
		 keyboard=new Scanner(System.in);
			System.out.println("Enter your edit current City:");
			currentCity = keyboard.next();
		 
		while(i<10 && out)
		{
			System.out.println("\n****Welcome to passenger console****\n");
			i++;

			System.out.println("This is the montreal passenger console"); 
			 
			 System.out.println("Enter your first name:");
			 firstName = keyboard.next();
			 
			 System.out.println("Enter your last name:");
			 lastName = keyboard.next();
			 
			 
			 System.out.println("Enter your address:");
			 address = keyboard.next();
			 
			 
			 System.out.println("Enter your phoneNumber:");
			 phoneNumber = keyboard.next();
			 
			 
			 System.out.println("Enter your destination:");
			 destination = keyboard.next();
			 
			 
			 System.out.println("Enter your flight Class:");
			 flightClass = keyboard.next();

			 System.out.println("Enter your flightDate:");
			 flightDate = keyboard.next();

			passengerRecvServer=serverImpl.bookFlight( currentCity,  firstName, 
					 lastName,  address,  phoneNumber, 
					 destination,  flightClass,  flightDate);
			System.out.println(passengerRecvServer);

		}//while
		
	}

}
