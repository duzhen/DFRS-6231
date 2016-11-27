package dfrs.servers4;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.omg.CORBA.ORB;

import dfrs.servers.BaseServerCluster;
import dfrs.utils.Utils;

public class ServerImplZhen {//extends ServerInterfacePOA {
	
	private ORB orb;
	
	private String LOG_PATH = Log.LOG_DIR+"LOG_";
	private String server;
	private String name;
	private int UDP_PORT;
	private int T_UDP_PORT;
	
	public ServerImplZhen(String server, String name, int udp, int tudp) {
		super();
		this.server = server;
		this.name = name;
		UDP_PORT = udp;
		T_UDP_PORT = tudp;
		LOG_PATH=LOG_PATH+server+"/"+server+"_LOG.txt";
		FlightData.getInstance().initData(server);
		TicketData.getInstance().initData(server);
		Log.createLogDir(Log.LOG_DIR + "LOG_" + server + "/");
		String s = "[" + server + "]-" + "DFRSServer ready and waiting ...";
		Log.i(Log.LOG_DIR + "LOG_" + server + "/" + server + "_LOG.txt", s);
		Utils.printFlight(server);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				initServer();
			}
		}).start();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				initTransactionServer();
			}
		}).start();
	}
	
	public void setORB(ORB orb_val) {
		orb = orb_val; 
	}
	
//	@Override
	public Result transferReservation(int passengerID, String currentCity, String otherCity) {
//		System.out.println("["+server+"]-"+passengerID+"-START--transferReservation,passengerID:"+passengerID+"[this:"+this.toString()+"]");
		int port = 0;
		if(BaseServerCluster.SERVERS[0].equals(otherCity)) {
			port = ServerImpl4.T_UDP_PORT_NUM[0];
		} else if(BaseServerCluster.SERVERS[1].equals(otherCity)) {
			port = ServerImpl4.T_UDP_PORT_NUM[1];
		} else if(BaseServerCluster.SERVERS[2].equals(otherCity)) {
			port = ServerImpl4.T_UDP_PORT_NUM[2];
		}
		Result result = startTransferTransaction(passengerID, otherCity, "localhost", port);
		String s = "["+server+"]-ID:"+passengerID+" transfer departure from "+currentCity+" to "+otherCity+"["+result.success+":"+result.content+"]";
		System.out.println(s);
		Log.i(LOG_PATH, s);
		return result;
	}

	private void initTransactionServer() {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(T_UDP_PORT);
			// create socket at agreed port
			byte[] buffer = new byte[1000];
			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				
				Object b = Utils.resolve(request.getData());
				boolean result = false;
				String re = "";
				if(b != null) {
					Ticket t = (Ticket) b;
					TransferReservation.getInstance().addTransactionOperation(t.getRecordID()+"", new ITransaction() {

						@Override
						public void doCommit() throws TransactionException {
							t.setDeparture(name);
							TicketData.getInstance().sellTicket(server, t);
						}

						@Override
						public void backCommit() {
							if(TicketData.getInstance().isExistTicket(server, t.getRecordID())) {
								TicketData.getInstance().returnTicket(server, t.getRecordID());
							}
						}
					});
					Result r = TransferReservation.getInstance().doTransaction(t.getRecordID()+"");
					re = t.getRecordID()+":"+(r.success?"TRUE":"FALSE")+":"+r.content;
				} else {
					result = false;
					re = "0:FALSE:Object Error";
				}
				request.setData(re.getBytes());
				DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(),
						request.getPort());
				aSocket.send(reply);
			}
		}catch (SocketException e){System.out.println("["+server+"]-"+"Socket: " + e.getMessage());
		Log.i(LOG_PATH, "["+server+":"+T_UDP_PORT+"]-"+"Socket: " + e.getMessage());
		}catch (IOException e) {System.out.println("["+server+"]-"+"IO: " + e.getMessage());
		Log.i(LOG_PATH, "["+server+":"+T_UDP_PORT+"]-"+"IO: " + e.getMessage());
		}finally {if(aSocket != null) aSocket.close();}
	}
	
	private Result startTransferTransaction(int passengerID, String otherCity, String ip, int port) {
		Result result = new Result();
		result.success = false;
		result.content = "ID "+passengerID+"-Transfer Failed!";
		final Ticket t = TicketData.getInstance().getTicketRecord(server, passengerID);
		
		if(t == null) {
			result.success = false;
			result.content = "ID "+passengerID+"-Transfer Failed! No passengerID";
			return result;
		}
		if(otherCity==null||otherCity.equals(t.getDestination())) {
			result.success = false;
			result.content = "ID "+passengerID+"-Transfer Failed! Departure same with Destination";
			return result;
		}
		final Flight f = FlightData.getInstance().getFlightByTicket(server, t);
		boolean r = TransferReservation.getInstance().initTransaction(t.getRecordID()+"", new ITransaction() {

			@Override
			public void doCommit() throws TransactionException {
				TicketData.getInstance().removeTicket(server, t.getRecordID());
			}

			@Override
			public void backCommit() {
				if(!TicketData.getInstance().isExistTicket(server, t.getRecordID())) {
					TicketData.getInstance().addTicket(server, t);
				}
			}
		});
		if(!r) {
			result.success = false;
			result.content = "ID "+passengerID+"-Transfer Failed! PassengerID is transfering now";
			return result;
		}
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			byte[] m = Utils.convert(t);
			InetAddress aHost = InetAddress.getByName(ip);
			DatagramPacket request = new DatagramPacket(m, m.length, aHost, port);
			aSocket.send(request);
			
			byte[] buffer = new byte[1000];
			Result v = null;
			while (v==null) {
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				aSocket.setSoTimeout(2 * 1000);
				aSocket.receive(reply);
			
				String receive = new String(reply.getData(), 0, reply.getLength()).trim();
				if (receive != null) {
					String re[] = receive.split(":");
					if(re.length == 3) {
						boolean s = false;
						if ("TRUE".equals(re[1])) {
							s = true;
						} else {
							s = false;
						}
						TransferReservation.getInstance().pushNetPackage(re[0], new Result(s, re[2]));
					}
				}
				v = TransferReservation.getInstance().popNetPackage(passengerID+"");
				if(v != null) {
					if (v.success) {
						result.success = true;
						result.content = "ID " + passengerID + "-Transfer Success!";
						f.sellTicket(t.getTicketClass(), false);
					} else {
						TransferReservation.getInstance().removeTransaction(passengerID + "");
						result.success = false;
						result.content = "ID " + passengerID + "-Transfer Failed! " + v.content;
					}
				}
			}
		} catch (Exception e) {
			result.success = !TransferReservation.getInstance().removeTransaction(passengerID + "");
			if (result.success) {
				result.content = "ID " + passengerID + "-Transfer Success!";
				f.sellTicket(t.getTicketClass(), false);
			} else {
				result.content = "ID " + passengerID + "-Transfer Failed! " + e.getMessage();
			}
//		} catch (SocketException e) {
//			result.success = false;
//			result.content = "ID " + passengerID + "-Transfer Failed! "+"Socket: " + e.getMessage();
//			System.out.println("[" + server + "]-" + "Socket: " + e.getMessage());
//			Log.e(LOG_PATH, "[" + server + ":" + T_UDP_PORT + "]-" + "Socket: " + e.getMessage());
//		} catch (IOException e) {
//			result.success = false;
//			result.content = "ID " + passengerID + "-Transfer Failed! "+"IO: " + e.getMessage();
//			System.out.println("[" + server + "]-" + "IO: " + e.getMessage());
//			Log.e(LOG_PATH, "[" + server + ":" + T_UDP_PORT + "]-" + "IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}

		return result;
	}
	
	private void initServer() {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(UDP_PORT);
			// create socket at agreed port
			byte[] buffer = new byte[1000];
			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				String receive = new String(request.getData(), 0, request.getLength()).trim();
				int count = 0;
				if (Flight.FIRST_CLASS.equals(receive)) {
					count = getRecordTypeCount(Flight.FIRST_CLASS);
				} else if (Flight.BUSINESS_CLASS.equals(receive)) {
					count = getRecordTypeCount(Flight.BUSINESS_CLASS);
				} else if (Flight.ECONOMY_CLASS.equals(receive)) {
					count = getRecordTypeCount(Flight.ECONOMY_CLASS);
				} else if (Flight.ALL_CLASS.equals(receive)) {
					count = getRecordTypeCount(Flight.ALL_CLASS);
				}
				String re = server + " " + count;
				request.setData(re.getBytes());
				DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(),
						request.getPort());
				aSocket.send(reply);
				String s = "["+server+":"+UDP_PORT+"]-"+"Receive Require Request KEY: " + receive +" and Reply:" + re;
				System.out.print("\n"+s);
				Log.i(LOG_PATH, s);
			}
		}catch (SocketException e){System.out.println("["+server+"]-"+"Socket: " + e.getMessage());
		Log.i(LOG_PATH, "["+server+":"+UDP_PORT+"]-"+"Socket: " + e.getMessage());
		}catch (IOException e) {System.out.println("["+server+"]-"+"IO: " + e.getMessage());
		Log.i(LOG_PATH, "["+server+":"+UDP_PORT+"]-"+"IO: " + e.getMessage());
		}finally {if(aSocket != null) aSocket.close();}
	}
	
	private String getCountFromOtherServers(String recordType, String ip, int port) {
		DatagramSocket aSocket = null;
		String receive = "";
		try {
			aSocket = new DatagramSocket();
			byte[] m = recordType.getBytes();
			InetAddress aHost = InetAddress.getByName(ip);
			DatagramPacket request = new DatagramPacket(m, m.length, aHost, port);
			aSocket.send(request);
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);
			receive = new String(reply.getData(), 0, reply.getLength()).trim();
//			System.out.println("\n["+server+"]-"+"Receive From Other Server: " + receive);
		}catch (SocketException e){System.out.println("["+server+"]-"+"Socket: " + e.getMessage());
		Log.e(LOG_PATH, "["+server+"]-"+"Socket: " + e.getMessage());
		}catch (IOException e){System.out.println("["+server+"]-"+"IO: " + e.getMessage());
		Log.e(LOG_PATH, "["+server+"]-"+"IO: " + e.getMessage());
		}finally {if(aSocket != null) aSocket.close();}
		return receive;
	}
	
//	@Override
	public synchronized Result bookFlight(String firstName, String lastName, String address, String phone, String destination,
			String date, String ticketClass) {
		String s = "["+server+"]-"+"Request Book Flight Order Passenger Info Is\n     -FirstName:"+firstName+"\n"
				+"     -lastName:"+lastName +"\n"
				+"     -address:"+address +"\n"
				+"     -phone:"+phone +"\n"
				+"     -destination:"+destination +"\n"
				+"     -date:"+date +"\n"
				+"     -ticketClass:"+ticketClass;
		System.out.println(s);
		Log.i(LOG_PATH, s);
		Result result = new Result();
		boolean r = false;
		String info = "Book Success, Thank you!";
		try {
			Ticket t = new Ticket(firstName, lastName, address, phone, destination, date, ticketClass, this.name);
			r = TicketData.getInstance().sellTicket(server, t);
		} catch(TransactionException e) {
			info = "Book Failed, "+e.getMessage();
		}
		s = "     -"+info;
		System.out.println(s);
		Log.i(LOG_PATH, s);
		result.success = r;
		result.content=info;
		Utils.printFlight(server);
		return result;
	}
//	@Override
	public String getBookedFlightCount(String recordType) {
		String s = "["+server+"]-"+"Receive Get Booked Flight Count Request, RecordType Is: " + recordType;
		System.out.println("\n"+s);
		Log.i(LOG_PATH, s);
		int count = getRecordTypeCount(recordType);
		String value = "";
		if(ServerImpl4.SERVER_NAME[0].equals(server)) {
			value = server + " " +count+",";
			value +=getCountFromOtherServers(recordType, "localhost", ServerImpl4.UDP_PORT_NUM[1]);
			value +=",";
			value +=getCountFromOtherServers(recordType, "localhost", ServerImpl4.UDP_PORT_NUM[2]);
		} else if(ServerImpl4.SERVER_NAME[1].equals(server)) {
			value =getCountFromOtherServers(recordType, "localhost", ServerImpl4.UDP_PORT_NUM[0]);
			value += ("," + server + " " +count+",");
			value +=getCountFromOtherServers(recordType, "localhost", ServerImpl4.UDP_PORT_NUM[2]);
		} else if(ServerImpl4.SERVER_NAME[2].equals(server)) {
			value +=getCountFromOtherServers(recordType, "localhost", ServerImpl4.UDP_PORT_NUM[0]);
			value +=",";
			value +=getCountFromOtherServers(recordType, "localhost", ServerImpl4.UDP_PORT_NUM[1]);
			value += ("," + server + " " +count);
		}
		s = "Reply Value Is: " + value;
		System.out.println("\n"+"["+server+"]-"+s);
		Log.i(LOG_PATH, "     -"+s);
		return value;
	}

	private synchronized int getRecordTypeCount(String recordType) {
		HashMap<String,List<Ticket>> tickets = TicketData.getInstance().initData(server);
		Iterator iter = tickets.entrySet().iterator();
		int count = 0;
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			ArrayList<Ticket> value = (ArrayList<Ticket>)entry.getValue();
			Iterator<Ticket> i = value.iterator();
			while (i.hasNext()) {
				Ticket f = i.next();
				if (f != null) {
					if(f!=null) {
						if (!recordType.equals(Flight.ALL_CLASS)) {
							if (recordType.equals(f.getTicketClass())) {
								count++;
							}
						} else {
							count++;
						 }
					}
				}
			}
		}
		return count;
	}

//	@Override
	public Result editFlightRecord(int recordID, String fieldName, String newValue) {
		String s = "["+server+"]-"+"Receive Edit Flight Record Request"+" recordID:" + recordID + " fieldName:" + fieldName + " newValue:" + newValue;
		System.out.println("\n"+s);
		Log.i(LOG_PATH, s);
		ArrayList<Flight> flight = (ArrayList<Flight>)FlightData.getInstance().initData(server);
		Result result = new Result();
		boolean find = false;
		boolean r = false;
		String info = "Edit Flight Record Success, Thank you!";
		for(Flight f:flight) {
			if(f.getRecordID() == recordID) {
				s="["+server+"]-"+"Find recordID:" + f.getRecordID();
				System.out.println(s);
				Log.i(LOG_PATH, s);
				s="     -"+f.toString();
				System.out.println(s);
				Log.i(LOG_PATH, s);
				find = true;
				if(Flight.DEPARTURE.equals(fieldName)) {
					if(newValue!=null&&!newValue.equals(f.getDestination())) {
						f.setDeparture(newValue);
						r = true;
					} else {
						info = "Edit Flight Record Failed, Because Departure same with Destination.";
					}
				} else if(Flight.DATE.equals(fieldName)) {
					f.setDepartureDate(newValue);
					r = true;
				} else if(Flight.DESTINATION.equals(fieldName)) {
					if(newValue!=null&&!newValue.equals(f.getDeparture())) {
						f.setDestination(newValue);
						r = true;
					} else {
						info = "Edit Flight Record Failed, Because Destination same with Departure.";
					}
				} else if(Flight.F_SEATS.equals(fieldName)) {
					int old = f.getTotalFirstTickets()-f.getBalanceFirstTickets();//getRecordTypeCount(Flight.F_SEATS);
					if(Integer.valueOf(newValue) >= old) {
						f.setTotalFirstTickets(Integer.valueOf(newValue));
						r = true;
					} else {
						info = "Edit Flight Record Failed, Because new seats number "+newValue+" less than booked number " + old;
					}
				} else if(Flight.B_SEATS.equals(fieldName)) {
					int old = f.getTotalBusinessTickets()-f.getBalanceBusinessTickets();//getRecordTypeCount(Flight.B_SEATS);
					if(Integer.valueOf(newValue) >= old) {
						f.setTotalBusinessTickets(Integer.valueOf(newValue));
						r = true;
					} else {
						info = "Edit Flight Record Failed, Because new seats number "+newValue+" less than booked number " + old;
					}
				} else if(Flight.E_SEATS.equals(fieldName)) {
					int old = f.getTotalEconomyTickets()-f.getBalanceEconomyTickets();//getRecordTypeCount(Flight.E_SEATS);
					if(Integer.valueOf(newValue) >= old) {
						f.setTotalEconomyTickets(Integer.valueOf(newValue));
						r = true;
					} else {
						info = "Edit Flight Record Failed, Because new seats number "+newValue+" less than booked number " + old;
					}
				}
				if(r) {
					s = "["+server+"]-"+"Edit Record Successful New Value Is:";
					System.out.println(s);
					Log.i(LOG_PATH, s);
					s = "     -"+f.toString();
					System.out.println(s);
					Log.i(LOG_PATH, s);
					Utils.printFlight(this.server);
				}
				break;
			}
		}
		if(!find) {
			Flight f = new Flight();
			f.setRecordID(recordID);
			if(Flight.DEPARTURE.equals(fieldName)) {
				f.setDeparture(newValue);
			} else if(Flight.DATE.equals(fieldName)) {
				f.setDepartureDate(newValue);
			} else if(Flight.DESTINATION.equals(fieldName)) {
				f.setDestination(newValue);
			} else if(Flight.F_SEATS.equals(fieldName)) {
				f.setTotalFirstTickets(Integer.valueOf(newValue));
			} else if(Flight.B_SEATS.equals(fieldName)) {
				f.setTotalBusinessTickets(Integer.valueOf(newValue));
			} else if(Flight.E_SEATS.equals(fieldName)) {
				f.setTotalEconomyTickets(Integer.valueOf(newValue));
			}
			FlightData.getInstance().addNewFlight(server, f);
			r = true;
			info = "ADD Flight Record Success, Thank you!";
			s = "["+server+"]-"+"Can't Find Record Create A New One:" + f.toString();
			System.out.println(s);
			Log.i(LOG_PATH, s);
			Utils.printFlight(this.server);
		}
		result.success = r;
		result.content=info;
		
		System.out.println("     -"+info);
		Log.i(LOG_PATH, "["+server+"]-"+info);
		return result;
	}

//	@Override
//	public String getAllFlightInfo() {
//		String result = "";
//		ArrayList<Flight> flight = (ArrayList<Flight>)FlightData.getInstance().initData(server);
//		for(Flight f:flight) {
//			result+=f.toString();
//			result+="\n";
//		}
//		return result;
//	}
}
