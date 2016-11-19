package dfrs.servers1;
import dfrs.ServerInterfacePOA;
public class ServerImpl1  extends ServerInterfacePOA  {

	public String host = "";
	public int port=0;
	
	public ServerImpl1(String host, int portone) {
		super();
		this.host = host;
		this.port = portone;
	}

	public ServerImpl1() {}
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
		System.out.println("editFlightRecord Success");
		return "Success";
	}

	@Override
	public String transferReservation(String managerID, String PassengerID, String CurrentCity, String OtherCity) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
