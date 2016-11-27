package dfrs.servers4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FlightData {
	private static FlightData instance;
	private HashMap<String, List<Flight>> data;
	private int recordID = 0;

	private FlightData() {
		data = new HashMap<String, List<Flight>>();
	}

	public static synchronized FlightData getInstance() {
		if (instance == null) {
			instance = new FlightData();
		}
		return instance;
	}

	public synchronized List<Flight> initData(String name) {
		List<Flight> o = data.get(name);
		if (o == null) {
			data.put(name, addInitFlight(name));
		}
		return data.get(name);
	}
	
	public synchronized void addNewFlight(String name, Flight f) {
		List<Flight> list = data.get(name);
		if(list == null)
			list = new ArrayList<Flight>();
		if(f.getRecordID() <= 0 || isRecordIdExist(f.getRecordID()))
			f.setRecordID(++recordID);
		list.add(f);
	}
	
	private boolean isRecordIdExist(int id) {
		Iterator iter = this.data.entrySet().iterator();
		int count = 0;
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			ArrayList<Flight> value = (ArrayList<Flight>) entry.getValue();
			for (Flight f : value) {
				if (f != null) {
					if (id == f.getRecordID()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public Flight getFlightByTicket(String server, Ticket t) {
		if(t == null)
			return null;
		ArrayList<Flight> flight = (ArrayList<Flight>)FlightData.getInstance().initData(server);
		for(Flight f:flight) {
			if(f.getDeparture().equals(t.getDeparture())&&f.getDestination().equals(t.getDestination())&&f.getDepartureDate().equals(t.getDepartureDate())) {
				return f;
			}
		}
		return null;
	}
	
	private ArrayList<Flight> addInitFlight(String name) {
		ArrayList<Flight> flight = new ArrayList<Flight>();
		
		if(ServerImpl4.SERVER_NAME[0].equals(name)) {
			
			Flight f = new Flight();
			f.setFlightName("CZ 101");
			f.setDeparture("Montreal");
			f.setDestination("Washington");
			f.setDepartureDate("20161010");
			f.setAchieveDate("20161011");
			f.setTotalBusinessTickets(100);
			f.setTotalFirstTickets(100);
			f.setTotalEconomyTickets(100);
			f.setRecordID(++recordID);
//			for(int i=0;i<10;i++)
//				f.sellTicket(Flight.FIRST_CLASS, true);
			flight.add(f);
			
			f = new Flight();
			f.setFlightName("CZ 201");
			f.setDeparture("Montreal");
			f.setDestination("New Delhi");
			f.setDepartureDate("20161010");
			f.setAchieveDate("20161011");
			f.setTotalBusinessTickets(100);
			f.setTotalFirstTickets(100);
			f.setTotalEconomyTickets(100);
			f.setRecordID(++recordID);
//			for(int i=0;i<10;i++)
//				f.sellTicket(Flight.BUSINESS_CLASS, true);
			flight.add(f);
		} else if(ServerImpl4.SERVER_NAME[1].equals(name)) {
			Flight f = new Flight();
			f.setFlightName("CW 101");
			f.setDeparture("Washington");
			f.setDestination("Montreal");
			f.setDepartureDate("20161010");
			f.setAchieveDate("20161011");
			f.setTotalBusinessTickets(100);
			f.setTotalFirstTickets(100);
			f.setTotalEconomyTickets(100);
			f.setRecordID(++recordID);
//			for(int i=0;i<10;i++)
//				f.sellTicket(Flight.FIRST_CLASS, true);
			flight.add(f);
			
			f = new Flight();
			f.setFlightName("CW 201");
			f.setDeparture("Washington");
			f.setDestination("New Delhi");
			f.setDepartureDate("20161010");
			f.setAchieveDate("20161011");
			f.setTotalBusinessTickets(100);
			f.setTotalFirstTickets(100);
			f.setTotalEconomyTickets(100);
			f.setRecordID(++recordID);
//			for(int i=0;i<10;i++)
//				f.sellTicket(Flight.BUSINESS_CLASS, true);
			flight.add(f);
		} else if(ServerImpl4.SERVER_NAME[2].equals(name)) {
			Flight f = new Flight();
			f.setFlightName("CN 101");
			f.setDeparture("New Delhi");
			f.setDestination("Washington");
			f.setDepartureDate("20161010");
			f.setAchieveDate("20161011");
			f.setTotalBusinessTickets(100);
			f.setTotalFirstTickets(100);
			f.setTotalEconomyTickets(100);
			f.setRecordID(++recordID);
//			for(int i=0;i<10;i++)
//				f.sellTicket(Flight.FIRST_CLASS, true);
			flight.add(f);
			
			f = new Flight();
			f.setFlightName("CN 201");
			f.setDeparture("New Delhi");
			f.setDestination("Montreal");
			f.setDepartureDate("20161010");
			f.setAchieveDate("20161011");
			f.setTotalBusinessTickets(100);
			f.setTotalFirstTickets(100);
			f.setTotalEconomyTickets(100);
			f.setRecordID(++recordID);
//			for(int i=0;i<10;i++)
//				f.sellTicket(Flight.BUSINESS_CLASS, true);
			flight.add(f);
		}
		
		return flight;
	}
}
