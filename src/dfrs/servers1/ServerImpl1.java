package dfrs.servers1;

import dfrs.ServerInterfacePOA;

public class ServerImpl1 extends ServerInterfacePOA {

	public ServerImpl1(int i) {
		// TODO Auto-generated constructor stub
	}

	public static ServerInterfacePOA getServerImpl(int i) {
		return new ServerImpl1(i);
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
