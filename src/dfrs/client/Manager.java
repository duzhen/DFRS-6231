package dfrs.client;

import java.util.Properties;
import java.util.Scanner;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import dfrs.ServerInterface;
import dfrs.ServerInterfaceHelper;
import dfrs.utils.Config;

public class Manager {
	
	public static String managerRecvServer = "";
	public static String passengerRecvServer = "";
	public static String managerID = "";
	public static String currentCity = "";
	public static Scanner keyboard = null;
	public static ServerInterface FEImpl;
	public static String destination ;
	//public static String flightClass;
	public static String flightDate;
	public static int economy ;
	public static int business;
	public static int firstclass;
	
	public static void showMenumg()
	{
		System.out.println("\n****Welcome to manager console****\n");
		System.out.println("Please select an option (1-5)");
		System.out.println("1. getBookedFlightCount");
		System.out.println("2. editFlightRecord");
		System.out.println("3. transferReservation");
		System.out.println("4. Exit");
	}
	
	public static void change()
	{

		
		System.out.println("Enter your edit plane destination:");
		 destination = keyboard.next();
		 
		 System.out.println("Enter your add plane flight Date:");
		 flightDate = keyboard.next();
		 
		 System.out.println("Enter the number of economy seats:");
		 economy = keyboard.nextInt();

		 
		 System.out.println("Enter the number of business seats:");
		 business = keyboard.nextInt();

		 
		 System.out.println("Enter the number of first class seats:");
		 firstclass = keyboard.nextInt();
	
		managerRecvServer=FEImpl.editFlightRecord(currentCity,managerID,destination,
				 flightDate,  economy,  business,  firstclass);
		
		System.out.println(managerRecvServer);
		
		destination="";
		flightDate="";
		economy = 55;
		business = 20;
		firstclass = 15;
	}
	
public static void main(String[] args) {
	


		try {
			Properties props = new Properties();
		    props.put("org.omg.CORBA.ORBInitialPort", Config.FE_CORBA_PORT);    
		    props.put("org.omg.CORBA.ORBInitialHost", Config.getFeHost()); 
		    ORB orb = ORB.init(args, props);

			org.omg.CORBA.Object objRef =
			orb.resolve_initial_references("NameService");
			
			NamingContextExt ncRef = 
					NamingContextExtHelper.narrow(objRef);
			FEImpl = ServerInterfaceHelper.narrow(ncRef.resolve_str("Server"));
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		System.out.println("This is the  manager console, please write the manager ID");
		keyboard=new Scanner(System.in);
		managerID = keyboard.nextLine();
		
		System.out.println("Enter your edit current City:");
		currentCity = keyboard.next();
		
		int i = 0;
		int userChoice=0;
		boolean out = true;
		showMenumg();
		 
		while(i<10 && out)
		{
			i++;
			userChoice=keyboard.nextInt();
			switch(userChoice)
			{
//			case 1: 
//				getBookedFlightCount();
//				showMenumg();
//				break;
			case 2:
				change();
				showMenumg();
				break;
//			case 3:
//				System.out.println(transferReservation());
//				showMenumg();
//				break;
			case 4:
				System.out.println("Have a nice day!");
				out=false;
				break;

			default:
				System.out.println("Invalid Input, please try again.");
			}
		}//while
	}
}