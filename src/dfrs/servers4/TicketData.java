package dfrs.servers4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TicketData {
	private static TicketData instance;
	private HashMap<String, HashMap<String, List<Ticket>>> data;
	private int recordID = 0;
	
	private TicketData() {
		data = new HashMap<String, HashMap<String, List<Ticket>>>();
	}

	public static synchronized TicketData getInstance() {
		if (instance == null) {
			instance = new TicketData();
		}
		return instance;
	}

	public synchronized HashMap<String, List<Ticket>> initData(String name) {
		HashMap<String, List<Ticket>> o = data.get(name);
		if (o == null) {
			o = new HashMap<String, List<Ticket>>();
			data.put(name, o);
//			addInitTicket(name);
		}
		return data.get(name);
	}
	
//	private void addInitTicket(String name) {
//		Ticket t = null;
//		if(DFRSServerMTL.SERVER_NAME.equals(name)) {
//			for(int i=0;i<10;i++) {
//				t = new Ticket("Zhen", "Du", "1819", "123", "Washington", "20161010", "First", "Montreal");
//				addTicket(name, t);
//				t = new Ticket("Zhen", "Du", "1819", "123", "New Delhi", "20161010", "Business", "Montreal");
//				addTicket(name, t);
//			}
//		} else if(DFRSServerWST.SERVER_NAME.equals(name)) {
//			for(int i=0;i<10;i++) {
//				t = new Ticket("Zhen", "Du", "1819", "123", "Montreal", "20161011", "First", "Washington");
//				addTicket(name, t);
//				t = new Ticket("Zhen", "Du", "1819", "123", "New Delhi", "20161011", "Business", "Washington");
//				addTicket(name, t);
//			}
//		} else if(DFRSServerNDL.SERVER_NAME.equals(name)) {
//			for(int i=0;i<10;i++) {
//				t = new Ticket("Zhen", "Du", "1819", "123", "Washington", "20161012", "First", "New Delhi");
//				addTicket(name, t);
//				t = new Ticket("Zhen", "Du", "1819", "123", "Montreal", "20161012", "Business", "New Delhi");
//				addTicket(name, t);
//			}
//		}
//	}
	
	public synchronized void addTicket(String server, Ticket t) {
		String index = Character.toUpperCase(t.getLastName().charAt(0)) + "" ;
		HashMap<String, List<Ticket>> o = data.get(server);
		ArrayList<Ticket> list = (ArrayList<Ticket>) o.get(index);
		if(list == null)
			list = new ArrayList<Ticket>();
		if(t.getRecordID() <= 0)
			t.setRecordID(++recordID);
		list.add(t);
		o.put(index, list);
	}

	public synchronized boolean sellTicket(String server, Ticket t) throws TransactionException {
		boolean result = false;
		if(server == null || t == null)
			return false;
		ArrayList<Flight> flight = (ArrayList<Flight>)FlightData.getInstance().initData(server);
		boolean r = false;
		Flight book = null;
		for(Flight f:flight) {
			if(f.getDeparture().equals(t.getDeparture())&&f.getDestination().equals(t.getDestination())&&f.getDepartureDate().equals(t.getDepartureDate())) {
				book = f;
				r = true;
				break;
			}
		}
		if(r) {
			if(book!=null&book.sellTicket(t.getTicketClass(), true)) {
				addTicket(server, t);
				result = true;
			} else {
				result = false;
				throw new TransactionException("No Enough Seats For This Ticket");
			}
		} else {
			result = false;
			throw new TransactionException("No Flight For This Ticket");
		}
		return result;
	}
	
	public synchronized void removeTicket(String server, int id) {
		Ticket t = getTicketRecord(server, id);
		if(server == null || t == null)
			return;
		
		String index = Character.toUpperCase(t.getLastName().charAt(0)) + "" ;
		HashMap<String, List<Ticket>> o = data.get(server);
		ArrayList<Ticket> list = (ArrayList<Ticket>) o.get(index);
		
		list.remove(t);
	}
	
	public synchronized void returnTicket(String server, int id) {
		Ticket t = getTicketRecord(server, id);
		if(server == null || t == null)
			return;
		
		String index = Character.toUpperCase(t.getLastName().charAt(0)) + "" ;
		HashMap<String, List<Ticket>> o = data.get(server);
		ArrayList<Ticket> list = (ArrayList<Ticket>) o.get(index);
		
		ArrayList<Flight> flight = (ArrayList<Flight>)FlightData.getInstance().initData(server);
		boolean r = false;
		Flight book = null;
		for(Flight f:flight) {
			if(f.getDeparture().equals(t.getDeparture())&&f.getDestination().equals(t.getDestination())&&f.getDepartureDate().equals(t.getDepartureDate())) {
				book = f;
				r = true;
				break;
			}
		}
		if(r) {
			if(book!=null&book.sellTicket(t.getTicketClass(), false)) {
				list.remove(t);
			}
		}
	}
	
	public boolean isExistTicket(String server, int id) {
		Ticket t = getTicketRecord(server, id);
		if(t == null)
			return false;
//		String index = Character.toUpperCase(t.getLastName().charAt(0)) + "" ;
//		HashMap<String, List<Ticket>> o = data.get(server);
//		ArrayList<Ticket> list = (ArrayList<Ticket>) o.get(index);
//		Iterator<Ticket> i = list.iterator();
//		while (i.hasNext()) {
//			Ticket f = i.next();
//			if (f != null) {
//				if(t.equals(f))
//					return true;
//			}
//		}
		return true;
	}
	
	public synchronized Ticket getTicketRecord(String server, int id) {
		HashMap<String, List<Ticket>> o = data.get(server);
		Iterator iter = o.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			ArrayList<Ticket> value = (ArrayList<Ticket>) entry.getValue();
			
			Iterator<Ticket> i = value.iterator();
			while (i.hasNext()) {
				Ticket f = i.next();
				if (f != null) {
					if (id == f.getRecordID()) {
						return f;
					}
				}
			}
		}
		return null;
	}
}
