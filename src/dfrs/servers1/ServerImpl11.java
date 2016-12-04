package dfrs.servers1;

import dfrs.ServerInterfacePOA;
import dfrs.servers.IServerManager;
import dfrs.servers4.Result;
import dfrs.utils.Config;

public class ServerImpl11 extends ServerInterfacePOA implements IServerManager {

	public static final String[] SERVERS = new String[] {"Montreal","Washington","New Delhi"};
	public static final String[] SERVER_NAME = {"MTL","WST","NDL"};
	public static final int[] UDP_PORT_NUM = {2001,2002,2003};
	public static final int[] T_UDP_PORT_NUM = {2004,2005,2006};
	
	private ServerImpl imple;
	
	public ServerImpl11(int i) {
		imple = new ServerImpl(SERVER_NAME[i], SERVERS[i],
				UDP_PORT_NUM[i], T_UDP_PORT_NUM[i]);
	}
	
	public static ServerInterfacePOA getServerImpl(int i) {
		return new ServerImpl11(i);
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

	@Override
	public void shutdown() {
		imple.shutdown();
	}

	@Override
	public void printAllTicket() {
		imple.printAllTicket();
	}
}
