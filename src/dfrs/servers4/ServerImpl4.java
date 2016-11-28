package dfrs.servers4;

import dfrs.ServerInterfacePOA;
import dfrs.utils.Config;

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
			String destination, String flightDate, String flightClass) {
		Result result = imple.bookFlight(firstName, lastName, address, phoneNumber, destination, flightDate, flightClass);
		if(result.success) {
			return Config.SUCCESS;
		} else {
			return Config.FAIL;
		}
	}

	@Override
	public String getBookedFlightCount(String managerID, String recordType) {
		//MTL 0,WST 0,NDL 0
		String result = imple.getBookedFlightCount(recordType);
		//MTL$0$WST$0$NDL$0
		return result.replace(" ", "$").replace(",", "$");
	}

	@Override
	public String editFlightRecord(String managerID, String recordID, String fieldName, String newValue) {
		Result result = imple.editFlightRecord(Integer.valueOf(recordID), fieldName, newValue);
		if(result.success) {
			return Config.SUCCESS;
		} else {
			return Config.FAIL;
		}
	}

	@Override
	public String transferReservation(String managerID, String passengerID, String currentCity, String otherCity) {
		Result result = imple.transferReservation(Integer.valueOf(passengerID), currentCity, otherCity);
		if(result.success) {
			return Config.SUCCESS;
		} else {
			return Config.FAIL;
		}
	}

}
