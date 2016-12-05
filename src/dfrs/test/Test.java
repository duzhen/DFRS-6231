package dfrs.test;

import java.util.Properties;
import java.util.Random;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import dfrs.ServerInterface;
import dfrs.ServerInterfaceHelper;
import dfrs.servers4.Ticket;
import dfrs.utils.Config;

public abstract class Test {
	
	private static final int RANDOM = 100;
	private static final int LOOP = 20;
	
	ServerInterface dfrs[] = new ServerInterface[9];
	String ticketType[] = new String[] {"First", "Business", "Economy", "All"};
	String ticketDestination[] = new String[] {"Montreal", "Washington", "New Delhi"};
	String server[] = new String[] {"MTL", "WST", "NDL"};
	int finish[] = {0,0,0};
	int transfer[] = {0,0,0,0,0,0};
	int transferFailed[] = {0,0,0,0,0,0};
	
	protected abstract int getThreadNum();
	
	
	public Test() {
		super();
		for(int j=0;j<finish.length;j++)
			finish[j] = 0;
		for(int j=0;j<transfer.length;j++)
			transfer[j] = 0;
		for(int j=0;j<transferFailed.length;j++)
			transfer[j] = 0;
	}


	private synchronized int finished(int departure) {
		return ++finish[departure];
	}
	
	private synchronized int transfered(int index) {
		return ++transfer[index];
	}
	
	private synchronized int transferFailed(int index) {
		return ++transferFailed[index];
	}
	
	private synchronized void PrinterTransfer(int departure) {
//		if(departure == 0) {
//			System.out.println("Transfer from Montreal   to Washington Success:"+transfer[0]+" Failed:"+transferFailed[0]);
//			System.out.println("Transfer from Montreal   to New Delhi  Success:"+transfer[1]+" Failed:"+transferFailed[1]);
//		} else if(departure == 1) {
//			System.out.println("Transfer from Washington to Montreal   Success:"+transfer[2]+" Failed:"+transferFailed[2]);
//			System.out.println("Transfer from Washington to New Delhi  Success:"+transfer[3]+" Failed:"+transferFailed[3]);
//		} else if(departure == 2) {
//			System.out.println("Transfer from New Delhi  to Montreal   Success:"+transfer[4]+" Failed:"+transferFailed[4]);
//			System.out.println("Transfer from New Delhi  to Washington Success:"+transfer[5]+" Failed:"+transferFailed[5]);
//		}
	}
	
	private synchronized int getBookedCount(ServerInterface dfrs, int type, int departure) {
		String count = dfrs.getBookedFlightCount(server[departure], ticketType[3]);//type]);
		if(count != null) {
			String c[] = count.split(",");
			if(c.length == 3) {
				return Integer.valueOf(c[departure].substring(4));
			}
		}
		return 0;
	}
	
	private int getRandomDestination(int departure, int random) {
		int des = 0;
		if(departure == 0) {
			des = (random%2)+1;
		} else if(departure == 1) {
			int r = random%4;
			if(r%2 == 1) {
				r-=1;
			}
			des = r;
		} else if(departure == 2) {
			des = random%2;
		}
		return des;
	}
	
	private int[] getDestination(int departure) {
		int des[] = {0,1};
		if(departure == 0) {
			des[0] = 1;
			des[1] = 2;
		} else if(departure == 1) {
			des[0] = 0;
			des[1] = 2;
		} else if(departure == 2) {
			des[0] = 0;
			des[1] = 1;
		}
		return des;
	}
	
	protected void startBookTest(ServerInterface dfrs, int th, int departure, boolean show) {
		Random r = new Random(System.currentTimeMillis());
		new Thread(new Runnable() {

			@Override
			public void run() {
				for(int i=0;i<LOOP;i++) {
					int random = r.nextInt(RANDOM);
					String type = ticketType[random%3];
					int des = getRandomDestination(departure, random);
					Ticket ticket = new Ticket("Zhen", "Du", "1819", "55667788", ticketDestination[des], "2016/12/25", type, ticketDestination[departure]);
					String success = dfrs.bookFlight(server[departure], ticket.getFirstName(), ticket.getLastName(), ticket.getAddress(),
							ticket.getPhone(), ticket.getDestination(), ticket.getDepartureDate(), ticket.getTicketClass());
					System.out.println(server[departure]+"[B]-"+th+":"+success);
					try {
						Thread.sleep(random*100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(2*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(finished(departure)==getThreadNum()) {
					int count = getBookedCount(dfrs, 3, departure);
					System.out.println(server[departure]+"[Total Booked]"+":"+count);
//					System.out.println(dfrs.getAllFlightInfo());
					if(show)
						PrinterTransfer(departure);
				}
			}
		}).start();
	}
	
	protected void startTransferTest(ServerInterface dfrs, int th, int departure) {
		Random r = new Random(System.currentTimeMillis());
		new Thread(new Runnable() {

			@Override
			public void run() {
				for(int i=0;i<2*LOOP;i++) {
					int random = r.nextInt(RANDOM);
					int[] des = getDestination(departure);
					String result = dfrs.transferReservation(server[departure], i+"", ticketDestination[departure], ticketDestination[des[0]]);
					int index = 0;
					if(departure == 0) {
						index = des[0]-1;
					} else if(departure == 1) {
						index = des[0]==0?2:3;
					} else if(departure == 2) {
						index = des[0]==0?4:5;
					}
//					if(result.success) {
//						transfered(index);
//					} else {
//						transferFailed(index);
//					}
					result = dfrs.transferReservation(server[departure], i+"", ticketDestination[departure], ticketDestination[des[1]]);
					if(departure == 0) {
						index = des[1]-1;
					} else if(departure == 1) {
						index = des[1]==0?2:3;
					} else if(departure == 2) {
						index = des[1]==0?4:5;
					}
//					if(result.success) {
//						transfered(index);
//					} else {
//						transferFailed(index);
//					}
					System.out.println(server[departure]+"[T]-"+th+":"+result);
					try {
						Thread.sleep(random*100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(2*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(finished(departure)==getThreadNum()) {
					int count = getBookedCount(dfrs, 3, departure);
					System.out.println(server[departure]+"[Total Booked]"+":"+count);
//					System.out.println(dfrs.getAllFlightInfo());
					PrinterTransfer(departure);
				}
			}
		}).start();
	}

	protected void startEditTest(ServerInterface dfrs, int th, int departure, boolean show) {
		Random r = new Random(System.currentTimeMillis());
		new Thread(new Runnable() {

			@Override
			public void run() {
				for(int i=0;i<LOOP;i++) {
					int random = r.nextInt(RANDOM);
					//departure 0,1,2
					//MTL 1,2 WST 3,4 NDL 5,6
					int id = (random%2+1)+2*departure;
					int book = 100;
					book = getBookedCount(dfrs, random%3, departure);
					String result = dfrs.editFlightRecord(server[departure], id+"", ticketType[random%3], book/2+"");
					System.out.println(server[departure]+"[E]-"+th+":"+result);
					try {
						Thread.sleep(random*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(2*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(finished(departure)==getThreadNum()) {
					int count = getBookedCount(dfrs, 3, departure);
					System.out.println(server[departure]+"[Total Booked]"+":"+count);
//					System.out.println(dfrs.getAllFlightInfo());
					if(show)
						PrinterTransfer(departure);
				}
			}
		}).start();
	}
	
	protected ServerInterface initConnection(int i, String port) {
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
			ServerInterface dfrsImpl = ServerInterfaceHelper.narrow(ncRef.resolve_str(name));
			
			System.out.println("[Thread-"+i+"] Lookup completed and Connect Successful");
			return dfrsImpl;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected void initConnection() {
		for(int i=0;i<dfrs.length;i++) {
			if(i%3==0) {
				dfrs[i] = initConnection(i, Config.FE_CORBA_PORT);
			} else if(i%3==1) {
				dfrs[i] = initConnection(i, Config.FE_CORBA_PORT);
			} else if(i%3==2) {
				dfrs[i] = initConnection(i, Config.FE_CORBA_PORT);
			}
		}
	}
}
