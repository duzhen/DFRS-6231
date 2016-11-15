package serverApp;
import java.util.HashMap;
import java.util.Iterator;

import server.ServerInterfacePOA;
public class FEImpl  extends ServerInterfacePOA  {

	@Override
	public String bookFlight(String firstName, String lastName, String address, String phoneNumber, String destination,
			String flightClass, String flightDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBookedFlightCount(String managerID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String editFlightRecord(String managerID, String destination, String flightDate, int economy, int business,
			int firstclass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String transferReservation(String managerID, String PassengerID, String CurrentCity, String OtherCity) {
		// TODO Auto-generated method stub
		return null;
	}

}
