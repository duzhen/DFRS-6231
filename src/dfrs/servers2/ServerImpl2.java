package dfrs.servers2;

import dfrs.ServerInterfacePOA;

public class ServerImpl2 extends ServerInterfacePOA {

	private BaseObj impl;
	
	public ServerImpl2(int i) {
		if(i==0) {
			impl = StartMontrealServer.main(null);
		} else if(i==1) {
			impl = StartWashingtonServer.main(null);
		} else if(i==2) {
			impl = StartNewDelhiServer.main(null);
		}
	}

	public static ServerInterfacePOA getServerImpl(int i) {
		return new ServerImpl2(i);
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
