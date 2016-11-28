package dfrs.servers2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.omg.CORBA.ORB;

public class MontrealServerObj extends BaseObj {

	private ORB orb;
 
	private DatagramSocket aSocket;
	private HashMap<Character, ArrayList<PassengerRecord>> passengerRecordsMap;
//	private ArrayList<FlightRecord> flightRecords;
	private int bookedCount = 0;

	public MontrealServerObj() {
		initDatabase();
		new Thread(new Runnable()
	    {
	        @Override
	        public void run() {
	        	initUDP();
	        }
	    }).start();
		printPassengerRecord();
		printFlightRecord();
	}

	private void initDatabase() {
		passengerRecordsMap = new HashMap<>();
		flightRecords = new ArrayList<>();
		addInitialFlightRecord();
		addInitialPassengerRecord();
 
	}

	// Send response...
	public void initUDP() {
		try {
			aSocket = new DatagramSocket(ServerInfo.getServerMaps().get("Montreal"));
			byte[] incomingData = new byte[1024];
			System.out.println("UDP Server is up and listening to " + ServerInfo.getServerMaps().get("Montreal"));
			while (true) {
				DatagramPacket request = new DatagramPacket(incomingData, incomingData.length);
				aSocket.receive(request);
				
				byte[] data = request.getData();
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				ObjectInputStream is = new ObjectInputStream(in);

				try {
					Object o = is.readObject();
					if (o instanceof PassengerRecord) {// transfer
						PassengerRecord pr = (PassengerRecord) o;
						String reply;
						if(insertPassengerRecord(pr)) {
							reply = "success";
						} else {
							reply = "fail";
						}
						byte[] replyByte = reply.getBytes();
						DatagramPacket replyPacket = new DatagramPacket(replyByte, replyByte.length, request.getAddress(), request.getPort());
						aSocket.send(replyPacket);
						
					} else { // count
						String reply = bookedCount + "";
						byte[] replyByte = reply.getBytes();
						DatagramPacket replyPacket = new DatagramPacket(replyByte, replyByte.length, request.getAddress(), request.getPort());
						aSocket.send(replyPacket);
					}

				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException i) {
			i.printStackTrace();
		}

	}

	public void setORB(ORB orb_val) {
		orb = orb_val;
	}

	@Override
	public boolean bookFlight(String firstName, String lastName, String address, String phoneNumber,
			String destination, String flightClass, String dateOfFlight) {
		// TODO Auto-generated method stub
		// public PassengerRecord(Passenger passenger, String destination,
		// String flightClass, String dateOfFlight) {
		// public Passenger(String firstName, String lastName, String address,
		// String phoneNumber) {
		// new FlightRecord("0001", "Montreal", "Washington", 20, 30, 40,
		// "2016-11-06-10-10"));
		int seats = this.queryAvailableSeatsAndUpdates(flightClass, destination, dateOfFlight);
		if (seats > 0) {
			Passenger p = new Passenger(firstName, lastName, address, phoneNumber);
			PassengerRecord pr = new PassengerRecord(p, destination, flightClass, dateOfFlight);
			Character c = lastName.toLowerCase().charAt(0);
			synchronized(this) {
				if (passengerRecordsMap.get(c) != null) {
					ArrayList<PassengerRecord> list = passengerRecordsMap.get(c);
					list.add(pr);
					passengerRecordsMap.put(c, list);
					
				} else {
					ArrayList<PassengerRecord> list = new ArrayList<>();
					list.add(pr);
					passengerRecordsMap.put(c, list);
				}
				bookedCount++;
			}
			
			String ts = new Date().toString();
			String who = "Passenger: " + firstName + " " + lastName;
			String operation = "booked a flight from Montreal to " + destination + " at " + dateOfFlight;
			new Log(ts, who, operation).writeToLog("src/dfrs/servers2/montreal_log.txt");
			
			
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean editFlightRecord(String recordID, String departure, String destination, int eco,
			int busi, int fit, String dateOfFlight) {
		// TODO Auto-generated method stub
		int index = this.findFlightRecordsByID(recordID);
		synchronized (this) {
			if (index == -1) {// add a new record
				FlightRecord fr = new FlightRecord(recordID, "Montreal", destination, eco, busi, fit, dateOfFlight);
				flightRecords.add(fr);
				
				
				String ts = new Date().toString();
				String who = "Manager";
				String operation = "add a flight from Montreal to " + fr.getDestination().toString() + " at " + fr.getDateOfFlight();
				new Log(ts, who, operation).writeToLog("src/dfrs/servers2/montreal_log.txt");
				
			} else { // edit this record
				FlightRecord fr = flightRecords.get(index);
				
				 
				
				fr.setDeparture(departure);
				fr.setDestination(destination);
				fr.setEconomySeats(eco);
				fr.setBusinessSeats(busi);
				fr.setFitsSeats(fit);
				fr.setDateOfFlight(dateOfFlight);
				
				String ts = new Date().toString();
				String who = "Manager";
				String operation = "edit a flight from Montreal to " + fr.getDestination().toString() + " at " + fr.getDateOfFlight();
				new Log(ts, who, operation).writeToLog("src/dfrs/servers2/montreal_log.txt");
			}
		}
		printFlightRecord();
		return true;
	}

	@Override
	public String getBookedFlightCount() {
		// TODO Auto-generated method stub
		String countWST = send("", "localhost", ServerInfo.getServerMaps().get("Washington"));
		String countNDL = send("", "localhost", ServerInfo.getServerMaps().get("NewDelhi"));
		StringBuilder sb = new StringBuilder();
		//EDIT
		sb.append("MTL$" + bookedCount + "$");
		sb.append("WST$" + countWST + "$");
		sb.append("NDL$" + countNDL + "$");
//		sb.append("MTL" + bookedCount + "\n");
//		sb.append("WST" + countWST + "\n");
//		sb.append("NDL" + countNDL + "\n");
		//END
		String ts = new Date().toString();
		String who = "Manager";
		String operation = "count the number of all the flight records";
		new Log(ts, who, operation).writeToLog("src/dfrs/servers2/montreal_log.txt");
		
		return sb.toString();
	}

	@Override
	public boolean transferReservation(String passengerID, String currentCity, String otherCity) {
		// TODO Auto-generated method stub
		int[] pack = findPassengerRecordByID(passengerID);
		if (pack[1] != -1) {
			char c = (char) pack[0];
			int i = pack[1];
			synchronized (this) {
				PassengerRecord pr = passengerRecordsMap.get(c).remove(i);
				send(pr, "localhost", ServerInfo.getServerMaps().get(otherCity));
				printPassengerRecord();
			}
			
			String ts = new Date().toString();
			String who = "Manager";
			String operation = "transfer the passenger recordID : " + passengerID + " from " + "Montreal to " + otherCity;
			new Log(ts, who, operation).writeToLog("src/dfrs/servers2/montreal_log.txt");
			
			return true;
		} else {
			printPassengerRecord();
			return false;
		}
	}

	private void addInitialFlightRecord() {
		flightRecords.add(new FlightRecord("0001", "Montreal", "Washington", 20, 30, 4, "2016-11-06-10-10"));
		flightRecords.add(new FlightRecord("0002", "Montreal", "Washington", 21, 31, 41, "2016-11-06-10-11"));
		flightRecords.add(new FlightRecord("0003", "Montreal", "Washington", 22, 32, 42, "2016-11-06-10-12"));
		flightRecords.add(new FlightRecord("0004", "Montreal", "Washington", 23, 33, 43, "2016-11-06-10-13"));
		flightRecords.add(new FlightRecord("0005", "Montreal", "Washington", 24, 34, 44, "2016-11-06-10-14"));
		flightRecords.add(new FlightRecord("0006", "Montreal", "NewDelhi", 20, 30, 40, "2016-11-06-10-10"));
		flightRecords.add(new FlightRecord("0007", "Montreal", "NewDelhi", 21, 31, 41, "2016-11-06-10-11"));
		flightRecords.add(new FlightRecord("0008", "Montreal", "NewDelhi", 22, 32, 42, "2016-11-06-10-12"));
		flightRecords.add(new FlightRecord("0009", "Montreal", "NewDelhi", 23, 33, 43, "2016-11-06-10-13"));
		flightRecords.add(new FlightRecord("0010", "Montreal", "NewDelhi", 24, 34, 44, "2016-11-06-10-14"));
	}
	
	
	private void addInitialPassengerRecord() {
		ArrayList<PassengerRecord> aList = new ArrayList<>();
		String[] firstNames = {"Michael", "Joshua", "Daniel", "Joseph", "David"};
		String[] lastNames = {"Jo", "Johnson", "Jim", "Jam", "Jones"};
		String[] addresses = {"AAA", "BBB", "CCC", "DDD", "EEE"};
		for(int i = 0; i < firstNames.length; i++) {
			Passenger p = new Passenger(firstNames[i], lastNames[i], addresses[i], "11111111");
			PassengerRecord pr = new PassengerRecord(p, "Washington", "fit", "2016-10-01-10-01");
			pr.setPassengerID(100 + i + "");
			aList.add(pr);
		}
		passengerRecordsMap.put('j', aList);
		bookedCount += firstNames.length;
	}

	private int queryAvailableSeatsAndUpdates(String flightClass, String dest, String date) {
		for (FlightRecord fr : flightRecords) {
			switch (flightClass) {
			case "economy":
				if (fr.getEconomySeats() > 0 && fr.getDateOfFlight().equals(date) && fr.getDestination().equals(dest)) {
					int seat = fr.getEconomySeats();
					fr.setEconomySeats(seat - 1);
					return seat;
				} else {
					return 0;
				}

			case "business":
				if (fr.getBusinessSeats() > 0 && fr.getDateOfFlight().equals(date)
						&& fr.getDestination().equals(dest)) {
					int seat = fr.getBusinessSeats();
					fr.setBusinessSeats(seat - 1);
					return seat;
				} else {
					return 0;
				}

			case "fit":
				if (fr.getFitsSeats() > 0 && fr.getDateOfFlight().equals(date) && fr.getDestination().equals(dest)) {
					int seat = fr.getFitsSeats();
					fr.setFitsSeats(seat - 1);
					return seat;
				} else {
					return 0;
				}

			default:
				break;
			}
		}
		return -1;
	}
//EDIT MOVE TO BASE
//	private int findFlightRecordsByID(String recordID) {
//		for (FlightRecord fr : flightRecords) {
//			if (fr != null) {
//				if (fr.getRecordID().equals(recordID)) {
//					return flightRecords.indexOf(fr);
//				}
//			}
//
//		}
//		return -1;
//	}

	private int[] findPassengerRecordByID(String passengerID) {
		int[] result = { -1, -1 };
		if (!passengerRecordsMap.isEmpty()) {
			for (Map.Entry<Character, ArrayList<PassengerRecord>> item : passengerRecordsMap.entrySet()) {
				Character c = item.getKey();
				ArrayList<PassengerRecord> list = item.getValue();
				for (PassengerRecord pr : list) {
					if (pr.getPassengerID().equals(passengerID)) {
						result[0] = c;
						result[1] = list.indexOf(pr);
					}
				}
			}
		}
		return result;
	}
	
	private boolean insertPassengerRecord(PassengerRecord pr) {
		if(pr != null) {
			char c = pr.getPassenger().getLastName().charAt(0);
			ArrayList<PassengerRecord> list = passengerRecordsMap.get(c);
			if(list != null) {
				list.add(pr);
			} else {
				list = new ArrayList<>();
				list.add(pr);
			}
			passengerRecordsMap.put(c, list);
			printPassengerRecord();
			return true;
		} else return false;
	}

	public void printFlightRecord() {
		System.out.println("");
		System.out.println("Printing Flight Record....");
		for (FlightRecord fr : flightRecords) {
			System.out.println(fr);
		}
		System.out.println("");
	}

	public void printPassengerRecord() {
		System.out.println("");
		System.out.println("Printing Passenger Record....");
		for (Map.Entry<Character, ArrayList<PassengerRecord>> item : passengerRecordsMap.entrySet()) {
			Character c = item.getKey();
			ArrayList<PassengerRecord> list = item.getValue();
			System.out.println(c + ":");
			for (PassengerRecord pr : list) {
				System.out.println(pr);
			}
		}
		System.out.println("");
	}
	
//	public String send(String message, String host, int port) {
//		try {
//			aSocket = new DatagramSocket();
//			InetAddress aHost = InetAddress.getByName(host);
//			byte[] m = message.getBytes();
//
//			DatagramPacket request = new DatagramPacket(m, message.length(), aHost, port);
//			aSocket.send(request);
//			byte[] buffer = new byte[1000];
//			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
//			aSocket.receive(reply);
//			return new String(reply.getData());
//		} catch (SocketException e) {
//			System.out.println("socket error");
//			return null;
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			System.out.println("UnknownHostException error");
//			return null;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			System.out.println("IOException error");
//			return null;
//		} finally {
//			if (aSocket != null)
//				aSocket.close();
//		}
//	}

	public String send(Object o, String host, int port) {
		try {

			aSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(host);
			byte[] incomingData = new byte[1024];
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(outputStream);
			os.writeObject(o);
			byte[] data = outputStream.toByteArray();
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
			aSocket.send(sendPacket);
			 
			DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
			aSocket.receive(incomingPacket);
			return new String(incomingPacket.getData());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		} catch (SocketException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
