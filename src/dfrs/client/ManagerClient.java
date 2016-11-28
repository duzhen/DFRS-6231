package dfrs.client;

import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import dfrs.ServerInterface;
import dfrs.ServerInterfaceHelper;
import dfrs.servers4.Flight;
import dfrs.servers4.Log;
import dfrs.servers4.Ticket;
import dfrs.utils.Config;
import dfrs.utils.Utils;

public class ManagerClient {
	private static final String LOG_PATH = Log.LOG_DIR+"LOG_Manager"+"/";
	private static final String[] CITY = {"Montreal", "Washington", "New Delhi"};
	private static final String[] S_CITY = {"MTL", "WST", "NDL"};
	private String managerName = "default";
	private String server = "";
	private String serverName = "";
	private ServerInterface dfrsImpl;
	
	private void showMenu() {
		System.out.println("\n****Welcome to DFRS Manage System****\n");
		System.out.println("Please enter your manager ID:");
	}
	
	private int validInputOption(Scanner keyboard, int max) {
		int userChoice = 0;
		boolean valid = false;

		// Enforces a valid integer input.
		while (!valid) {
			try {
				userChoice = keyboard.nextInt();
				if(userChoice >=1 && userChoice <=max)
					valid = true;
				else {
					throw new Exception();
				}
			} catch (Exception e) {
				System.out.println("Invalid Input, please enter an Integer (1 - "+max+")\n");
				valid = false;
				keyboard.nextLine();
			}
		}
		return userChoice;
	}
	
	private void showBookedMenu() {
		String m = "Please select the record type (1-4)";
		String m1 = "1. First Class";
		String m2 = "2. Bussiness Class";
		String m3 = "3. Economy Class";
		String m4 = "4. ALL";
		System.out.println(m);
//not all members getBookFlightCount by type, so default is ALL
//		System.out.println(m1);
//		System.out.println(m2);
//		System.out.println(m3);
		System.out.println(m4);
		String s = "-"+managerName + " Choose ";
		
		Scanner keyboard = new Scanner(System.in);
		int userChoice = validInputOption(keyboard, 4);
		String type = "";
		switch (userChoice) {
		case 1:
			s+=m1;
			Log.i(LOG_PATH+managerName+".txt", s);
			type = Flight.FIRST_CLASS;
			break;
		case 2:
			s+=m2;
			Log.i(LOG_PATH+managerName+".txt", s);
			type = Flight.BUSINESS_CLASS;
			break;
		case 3:
			s+=m3;
			Log.i(LOG_PATH+managerName+".txt", s);
			type = Flight.ECONOMY_CLASS;
			break;
		case 4:
			s+=m4;
			Log.i(LOG_PATH+managerName+".txt", s);
			type = Flight.ALL_CLASS;
			break;
		default:
			System.out.println("Invalid Input, please try again.");
		}
		try {
			String value = dfrsImpl.getBookedFlightCount(managerName, type);
			s = "-Get Booked Flight Count:" + value;
			System.out.println(s);
			Log.i(LOG_PATH+managerName+".txt", s);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(LOG_PATH+managerName+".txt", e.getMessage());
		}
	}
	
	private int showCityMenu(Scanner keyboard) {
		System.out.println("Please choose the city (1-3)");
		System.out.println("1. Montreal");
		System.out.println("2. Washington");
		System.out.println("3. New Delhi");
		
		return validInputOption(keyboard, 3);
	}
	
	private void showEditFlghtOptionMenu(int recordId) {
		String m[] = {"Please select the field name (1-6)",
					"1. Departure place",
					"2. Departure date",
					"3. Destination place",
					"4. First Class seats",
					"5. Business Class seats",
					"6. Economy Class seats"};
		System.out.println(m[0]);
//Not all members implemented edit Departure, date, destination, so only edit seats
//		System.out.println(m[1]);
//		System.out.println(m[2]);
//		System.out.println(m[3]);
		System.out.println(m[4]);
		System.out.println(m[5]);
		System.out.println(m[6]);
		String s = "-"+managerName + " Input record Is: "+recordId;
		Log.i(LOG_PATH+managerName+".txt", s);
		
		Scanner keyboard = new Scanner(System.in);
		int userChoice = validInputOption(keyboard, 6);
		
		s = "-"+managerName + " Choose " + m[userChoice];
		Log.i(LOG_PATH+managerName+".txt", s);
		
		int seats = -1;
		String fieldName = "";
		String value = "";
		if(userChoice == 1 || userChoice == 3) {
			int city = showCityMenu(keyboard);
			if(city == 1) {
				value = "Montreal";
			} else if(city == 2) {
				value = "Washington";
			} else if(city == 3) {
				value = "New Delhi";
			}
			s = "-"+managerName + " Choose " + value;
			Log.i(LOG_PATH+managerName+".txt", s);
		} else {
			System.out.println("Please enter new field value");
			if(userChoice > 3) {
				while (seats < 0) {
					try {
						seats = keyboard.nextInt();
					} catch (Exception e) {
						System.out.println("Invalid Input, please enter the number");
						seats = -1;
						keyboard.nextLine();
					}
				}
				value = seats + "";
			} else if(userChoice == 2) {
				boolean valid = false;
				// Enforces a valid integer input.
				while (!valid) {
					try {
						value = keyboard.next();
						if(Utils.validDate(value))
							valid = true;
						else {
							throw new Exception();
						}
					} catch (Exception e) {
						System.out.println("Invalid Input, please enter Date like 2016/12/25\n");
						valid = false;
						keyboard.nextLine();
					}
				}
			} else {
				value = keyboard.next();
			}
			s = "-"+managerName + " Enter New Value: " + value;
			Log.i(LOG_PATH+managerName+".txt", s);
		}
		try {
			switch (userChoice) {
			case 1:
				fieldName = Flight.DEPARTURE;
				break;
			case 2:
				fieldName = Flight.DATE;
				break;
			case 3:
				fieldName = Flight.DESTINATION;
				break;
			case 4:
				fieldName = Flight.F_SEATS;
				break;
			case 5:
				fieldName = Flight.B_SEATS;
				break;
			case 6:
				fieldName = Flight.E_SEATS;
				break;
			default:
				System.out.println("Invalid Input, please try again.");
			}
			String result = dfrsImpl.editFlightRecord(managerName, recordId+"", fieldName, value);
			s = "-"+managerName + " " + result;
			Log.i(LOG_PATH+managerName+".txt", s);
			System.out.println("Edit Flight Record "+result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void showEditMenu() {
		System.out.println("Please enter the flight record ID:");
		Scanner keyboard = new Scanner(System.in);
		boolean valid = false;
		int userInput = 0;
		
		while (!valid) {
			try {
				userInput = keyboard.nextInt();
				valid=true;
			} catch (Exception e) {
				System.out.println("Invalid Input, please enter the flight record ID:");
				valid = false;
				keyboard.nextLine();
			}
		}
		showEditFlghtOptionMenu(userInput);
	}

	private String showTransferCityMenu(Ticket t) {
		System.out.println("Please choose the city for transfer");
		int j=1;
		HashMap<String, String> map = new HashMap<String, String>();
		for(int i=0;i<CITY.length;i++) {
			
			if(!CITY[i].equals(serverName)) {//&&!CITY[i].equals(t.getDestination())) {
				System.out.println(j+"."+CITY[i]);
				map.put(j+"", CITY[i]);
				j++;
			}
		}
		Scanner keyboard = new Scanner(System.in);
		int input = validInputOption(keyboard, j-1);
		return map.get(input+"");
	}
	
	private void showTransferMenu() {
		System.out.println("Please enter the passenger ID:");
		Scanner keyboard = new Scanner(System.in);
		boolean valid = false;
		int userInput = 0;
		Ticket t = null;
		while (!valid) {
			try {
				userInput = keyboard.nextInt();
//				t = TicketData.getInstance().getTicketRecord(server, userInput);
//				if(t == null) {
//					throw new Exception();
//				}
				valid = true;
			} catch (Exception e) {
				System.out.println("Invalid Input, please enter the passengerID:");
				valid = false;
				keyboard.nextLine();
			}
		}
		String otherCity = showTransferCityMenu(t);
		String result = dfrsImpl.transferReservation(managerName, userInput+"", serverName, otherCity);
		String s = "-"+managerName + " " + result;
		Log.i(LOG_PATH+managerName+".txt", s);
		System.out.println("Transfer Reservation "+result);
	}
	
	private void showOptionMenu(Scanner keyboard) {
		String m = "Please select your option (1-3)";
		String m1 = "1. Get Booked Flight Count";
		String m2 = "2. Edit Flight Record";
		String m3 = "3. Transfer Reservation";
		String m4 = "4. Exit";
		System.out.println(m);
		System.out.println(m1);
		System.out.println(m2);
		System.out.println(m3);
		System.out.println(m4);
		String s = "-"+managerName + " Choose ";
		int userChoice = validInputOption(keyboard, 4);
		switch (userChoice) {
		case 1:
			s+=m1;
			Log.i(LOG_PATH+managerName+".txt", s);
			showBookedMenu();
			break;
		case 2:
			s+=m2;
			Log.i(LOG_PATH+managerName+".txt", s);
			showEditMenu();
			break;
		case 3:
			s+=m3;
			Log.i(LOG_PATH+managerName+".txt", s);
			showTransferMenu();
			break;
		case 4:
			s+=m4;
			Log.i(LOG_PATH+managerName+".txt", s);
			System.out.println("Have a nice day!");
			keyboard.close();
			System.exit(0);
		default:
			System.out.println("Invalid Input, please try again.");
		}
		initPage();
	}
	
	private boolean initConnection(String port) {
		try {
			// create and initialize the ORB
			Properties props = new Properties();
        	props.put("org.omg.CORBA.ORBInitialPort", port);
        	props.put("org.omg.CORBA.ORBInitialHost", Config.getFeHost());
			ORB orb = ORB.init(new String[]{}, props);

			// get the root naming context
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			// Use NamingContextExt instead of NamingContext,
			// part of the Interoperable naming Service.
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			// resolve the Object Reference in Naming
			String name = "Server";
			dfrsImpl = ServerInterfaceHelper.narrow(ncRef.resolve_str(name));
			String s = "\nNew Manager:"+managerName+"\n-Connect Server Successful";
			System.out.println(s);
			Log.i(LOG_PATH+managerName+".txt", s);
			return true;
		} catch (Exception e) {
			String s = "-Failed to find Server, please try again.\n";
			System.out.println(s);
			Log.e(LOG_PATH+managerName+".txt", s);
		}
		return false;
	}
	
	private boolean validManagerId(String input) {
		String pat = "(MTL|WST|NDL)\\d{4}" ;
        Pattern p = Pattern.compile(pat) ;
        Matcher m = p.matcher(input) ;
        return m.matches();
	}
	
	private String getServerPort(String input) {
		String pat = "(MTL)\\d{4}" ;
        if(Pattern.compile(pat).matcher(input).matches()) {
        	return Config.FE_CORBA_PORT;
        }
        pat = "(WST)\\d{4}" ;
        if(Pattern.compile(pat).matcher(input).matches()) {
        	return Config.FE_CORBA_PORT;
        }
		pat = "(NDL)\\d{4}" ;
		if(Pattern.compile(pat).matcher(input).matches()) {
        	return Config.FE_CORBA_PORT;
        }
		return "";
	}
	
	private String getServer(String input) {
		String pat = "(MTL)\\d{4}" ;
        if(Pattern.compile(pat).matcher(input).matches()) {
        	return S_CITY[0];
        }
        pat = "(WST)\\d{4}" ;
        if(Pattern.compile(pat).matcher(input).matches()) {
        	return S_CITY[1];
        }
		pat = "(NDL)\\d{4}" ;
		if(Pattern.compile(pat).matcher(input).matches()) {
        	return S_CITY[2];
        }
		return "";
	}
	
	private String getServerName(String input) {
		String pat = "(MTL)\\d{4}" ;
        if(Pattern.compile(pat).matcher(input).matches()) {
        	return CITY[0];
        }
        pat = "(WST)\\d{4}" ;
        if(Pattern.compile(pat).matcher(input).matches()) {
        	return CITY[1];
        }
		pat = "(NDL)\\d{4}" ;
		if(Pattern.compile(pat).matcher(input).matches()) {
        	return CITY[2];
        }
		return "";
	}
	
	private void initPage() {
		String userInput = "";
		Scanner keyboard = new Scanner(System.in);
		boolean valid = false;
		
		showMenu();

		// Enforces a valid integer input.
		while (!valid) {
			try {
				userInput = keyboard.next();
				valid = validManagerId(userInput);
				if (!valid) {
					System.out.println("Invalid Input, please enter your ManagerID");
				}
			} catch (Exception e) {
				System.out.println("Invalid Input, please enter your ManagerID");
				valid = false;
				keyboard.nextLine();
			}
		}
		managerName = userInput;
		server = getServer(userInput);
		serverName = getServerName(userInput);
		if (initConnection(getServerPort(userInput))) {
			showOptionMenu(keyboard);
		} else {
			initPage();
		}
	}
	
	public static void main(String[] args) {
		Log.createLogDir(LOG_PATH);
		new ManagerClient().initPage();
	}
}
