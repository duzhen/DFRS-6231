package dfrs.client;

import java.util.Properties;
import java.util.Scanner;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import dfrs.ServerInterface;
import dfrs.ServerInterfaceHelper;
import dfrs.servers4.Flight;
import dfrs.servers4.Ticket;
import dfrs.utils.Config;
import dfrs.utils.Utils;

public class PassengerClient {
	private ServerInterface dfrsImpl;
	private Ticket ticket;
	
	public static void showMenu() {
		System.out.println("\n****Welcome to DFRS System****\n");
		System.out.println("Please select your departure station (1-3) or 4.Exit");
		System.out.println("1. Montreal");
		System.out.println("2. Washington");
		System.out.println("3. New Delhi");
		System.out.println("4. Exit");
	}

	public static void showDestinationMenu(int departure) {
		System.out.println("\nPlease select your destination station (1-2) or 3.Back");
		if(departure == 1) {
			System.out.println("1. Washington");
			System.out.println("2. New Delhi");
		} else if(departure == 2) {
			System.out.println("1. Montreal");
			System.out.println("2. New Delhi");
		} else if(departure == 3) {
			System.out.println("1. Montreal");
			System.out.println("2. Washington");
		}
		System.out.println("3. Back");
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
	
	public void inputFirstName(Scanner keyboard) {
		System.out.println("\nPlease input your First Name:\n");
		ticket.setFirstName(keyboard.next());
	}
	
	public void inputLastName(Scanner keyboard) {
		System.out.println("\nPlease input your Last Name:\n");
		boolean valid = false;
		String input = "";
		while (!valid) {
			try {
				input = keyboard.next();
				if(Utils.validLastName(input))
					valid = true;
				else {
					throw new Exception();
				}
			} catch (Exception e) {
				System.out.println("Invalid Input, please input right name\n");
				valid = false;
				keyboard.nextLine();
			}
		}
		ticket.setLastName(input);
	}
	
	public void inputAddress(Scanner keyboard) {
		System.out.println("\nPlease input your Address:\n");
		ticket.setAddress(keyboard.next());
	}
	
	public void inputPhone(Scanner keyboard) {
		System.out.println("\nPlease input your Phone Number:\n");
		ticket.setPhone(keyboard.next());
	}
	
	public void chooseClass(Scanner keyboard) {
		System.out.println("\nPlease choose your ticket Class\n");
		System.out.println("1. First");
		System.out.println("2. Business");
		System.out.println("3. Economy");
		int choose = validInputOption(keyboard, 3);
		if(choose == 1) {
			ticket.setTicketClass(Flight.FIRST_CLASS);
		} else if(choose == 2) {
			ticket.setTicketClass(Flight.BUSINESS_CLASS);
		} else if(choose == 3) {
			ticket.setTicketClass(Flight.ECONOMY_CLASS);
		}
	}
	
	public void departureDate(Scanner keyboard) {
		System.out.println("\nPlease input your Departure Date:\n");
		boolean valid = false;
		String input = "";
		// Enforces a valid integer input.
		while (!valid) {
			try {
				input = keyboard.next();
				if(Utils.validDate(input))
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
		ticket.setDepartureDate(input);
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
			
			System.out.println("Lookup completed and Connect Successful");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void initPage() {
		int userChoice = 0;
		int step = 1;
		int maxChoice = 0;
		int departure = 0;
		Scanner keyboard = new Scanner(System.in);

		while (true) {
			if(step == 1) {
				maxChoice = 4;
				showMenu();
			} else if(step == 2) {
				maxChoice = 3;
				showDestinationMenu(userChoice);
			} else if(step == 3) {
				inputFirstName(keyboard);
				step++;
			} else if(step == 4) {
				inputLastName(keyboard);
				step++;
			} else if(step == 5) {
				inputAddress(keyboard);
				step++;
			} else if(step == 6) {
				inputPhone(keyboard);
				step++;
			} else if(step == 7) {
				chooseClass(keyboard);
				step++;
			} else if(step == 8) {
				departureDate(keyboard);
				step++;
				try {
					String success = dfrsImpl.bookFlight(ticket.getDeparture(), ticket.getFirstName(), ticket.getLastName(), ticket.getAddress(),
							ticket.getPhone(), ticket.getDestination(), ticket.getDepartureDate(), ticket.getTicketClass());
					System.out.println("BookFlight "+success);
				} catch (Exception e) {
					e.printStackTrace();
				}
				step = 1;
				continue;
			}
			
			Boolean valid = false;

			// Enforces a valid integer input.
			while (!valid && step <=2) {
				try {
					userChoice = keyboard.nextInt();
					if(userChoice >=1 && userChoice <=maxChoice)
						valid = true;
					else {
						throw new Exception();
					}
				} catch (Exception e) {
					System.out.println("Invalid Input, please enter an Integer (1-"+maxChoice+")");
					valid = false;
					keyboard.nextLine();
				}
			}

			// Manage user selection.
			if(step == 1) {
				departure = userChoice;
				if(manageInput(userChoice, keyboard))
					step++;
			} else if(step == 2) {
				if (manageDesInput(userChoice, departure))
					step++;
				else
					step--;
			}
		}
	}
	
	private boolean manageDesInput(int userChoice, int departure) {
		boolean success = false;
		ticket = new Ticket();
		if(departure == 1) {
			ticket.setDeparture("Montreal");
		} else if(departure == 2) {
			ticket.setDeparture("Washington");
		} else if(departure == 3) {
			ticket.setDeparture("New Delhi");
		}
		switch (userChoice) {
		case 1:
			if(departure == 1) {
				ticket.setDestination("Washington");
			} else if(departure == 2) {
				ticket.setDestination("Montreal");
			} else if(departure == 3) {
				ticket.setDestination("Montreal");
			}
			success = true;
			break;
		case 2:
			if(departure == 1) {
				ticket.setDestination("New Delhi");
			} else if(departure == 2) {
				ticket.setDestination("New Delhi");
			} else if(departure == 3) {
				ticket.setDestination("Washington");
			}
			success = true;
			break;
		case 3:
			success = false;
			break;
		default:
			System.out.println("Invalid Input, please try again.");
		}
		
		return success;
	}

	private boolean manageInput(int userChoice, Scanner keyboard) {
		boolean success = false;
		switch (userChoice) {
		case 1:
			success = initConnection(Config.FE_CORBA_PORT);
			break;
		case 2:
			success = initConnection(Config.FE_CORBA_PORT);
			break;
		case 3:
			success = initConnection(Config.FE_CORBA_PORT);
			break;
		case 4:
			System.out.println("Have a nice day!");
			keyboard.close();
			System.exit(0);
		default:
			System.out.println("Invalid Input, please try again.");
		}
		return success;
	}

	public static void main(String[] args) {
		new PassengerClient().initPage();
	}
}
