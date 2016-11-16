package dfrs.servers1;
import java.util.HashMap;
import java.util.Iterator;

import dfrs.ServerInterfacePOA;
public class FEImpl  extends ServerInterfacePOA  {

	public String host = "";
	public int port=0;
	
	public FEImpl(String host, int portone) {
		super();
		this.host = host;
		this.port = portone;
	}

	@Override
	public String bookFlight(String currentCity, String firstName, String lastName, String address, String phoneNumber,
			String destination, String flightClass, String flightDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBookedFlightCount(String currentCity, String managerID) {
		
		
		return null;
	}

	@Override
	public String editFlightRecord(String currentCity, String managerID, String destination, String flightDate,
			int economy, int business, int firstclass) {
		
		ChangeModule cM = new ChangeModule(host,port,currentCity,managerID,destination,flightDate,
				economy,business,firstclass	);
		cM.execute();
		return null;
	}

	@Override
	public String transferReservation(String managerID, String PassengerID, String CurrentCity, String OtherCity) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
