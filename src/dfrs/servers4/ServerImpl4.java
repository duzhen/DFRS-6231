package dfrs.servers4;
import dfrs.ServerInterfacePOA;
import dfrs.utils.Config;
public class ServerImpl4  extends ServerInterfacePOA  {

	public String host = "";
	public int port=0;
	
	public ServerImpl4(String host, int portone) {
		super();
		this.host = host;
		this.port = portone;
	}
	public ServerImpl4() {}
	@Override
	public String bookFlight(String currentCity, String firstName, String lastName, String address, String phoneNumber,
			String destination, String flightClass, String flightDate) {
		System.out.println("ServerImpl4 bookFlight Success");
		return Config.SUCCESS;
	}

	@Override
	public String getBookedFlightCount(String currentCity, String managerID) {
		System.out.println("ServerImpl4 getBookedFlightCount Success");		
		return Config.SUCCESS;
	}

	@Override
	public String editFlightRecord(String currentCity, String managerID, String destination, String flightDate,
			int economy, int business, int firstclass) {
		System.out.println("ServerImpl4 editFlightRecord Success");
		return Config.SUCCESS;
	}

	@Override
	public String transferReservation(String managerID, String PassengerID, String CurrentCity, String OtherCity) {
		System.out.println("ServerImpl4 transferReservation Fail");
		return Config.FAIL;
	}
	
}
