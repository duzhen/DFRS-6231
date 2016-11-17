package dfrs.servers1;
import dfrs.ServerInterfacePOA;
public class ServerImpl1  extends ServerInterfacePOA  {

	public static final String SERVER_HOST = "localhost";
	public static final String SERVER_MTL = "Montreal";
	public static final String SERVER_WST = "Washington";
	public static final String SERVER_NDL = "New Delhi";
	//CORBA
	public static final String SERVER_MTL_CORBA_PORT = "9050";
	public static final String SERVER_WST_CORBA_PORT = "9051";
	public static final String SERVER_NDL_CORBA_PORT = "9052";
	//HEARTBEAT
	public static final int RM_HEARTBEAT_MTL_PORT = 7211;
	public static final int RM_HEARTBEAT_WST_PORT = 7212;
	public static final int RM_HEARTBEAT_NDL_PORT = 7213;
	
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
