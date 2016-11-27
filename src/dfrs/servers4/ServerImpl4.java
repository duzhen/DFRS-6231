package dfrs.servers4;

import dfrs.ServerInterfacePOA;

public class ServerImpl4 extends ServerInterfacePOA {

	public static final String[] SERVERS = new String[] {"Montreal","Washington","New Delhi"};
	public static final String[] SERVER_NAME = {"MTL","WST","NDL"};
	public static final int[] UDP_PORT_NUM = {3020,3021,3022};
	public static final int[] T_UDP_PORT_NUM = {4020,4021,4022};
	
	private ServerImplZhen imple;
	
	public ServerImpl4(int i) {
		imple = new ServerImplZhen(SERVER_NAME[i], SERVERS[i],
				UDP_PORT_NUM[i], T_UDP_PORT_NUM[i]);
	}
	
	public static ServerInterfacePOA getServerImpl(int i) {
		return new ServerImpl4(i);
	}
	
	@Override
	public String bookFlight(String departure, String firstName, String lastName, String address, String phoneNumber,
			String destination, String flightClass, String flightDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBookedFlightCount(String managerID, String recordType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String editFlightRecord(String managerID, String recordID, String fieldName, String newValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String transferReservation(String managerID, String passengerID, String currentCity, String otherCity) {
		// TODO Auto-generated method stub
		return null;
	}

}
