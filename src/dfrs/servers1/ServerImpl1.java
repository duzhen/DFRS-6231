package dfrs.servers1;

import dfrs.ServerInterfacePOA;
import dfrs.utils.Config;
import dfrs.utils.Utils;

public class ServerImpl1 extends ServerInterfacePOA {

	private FlightServer impl;
	
	public ServerImpl1(int i) {
		if(i==0) {
			impl = MTLServer.main(null);
		} else if(i==1) {
			impl = WDCServer.main(null);
		} else if(i==2) {
			impl = NDLServer.main(null);
		}
	}

	public static ServerInterfacePOA getServerImpl(int i) {
		return new ServerImpl1(i);
	}
	
	@Override
	public String bookFlight(String departure, String firstName, String lastName, String address, String phoneNumber,
			String destination, String flightClass, String flightDate) {
		String des = Utils.getServer(destination);
		int seatClass = 0;
		if(Config.FIRST_CLASS.equals(flightClass)) {
			seatClass = 1;
		} else if(Config.BUSINESS_CLASS.equals(flightClass)) {
			seatClass = 2;
		} else if(Config.ECONOMY_CLASS.equals(flightClass)) {
			seatClass = 3;
		}
		//flightDate must be yyyy/MM/dd
		String result = impl.bookFlight(firstName, lastName, address, phoneNumber, des, flightDate, seatClass);
		if(result != null&&result.startsWith("Done-")) {
			return Config.SUCCESS;
		} else {
			return Config.FAIL;
		}
	}

	@Override
	public String getBookedFlightCount(String managerID, String recordType) {
		//didn't implement by type
		int type = 0;
		if(Config.FIRST_CLASS.equals(recordType)) {
			type = 1;
		} else if(Config.BUSINESS_CLASS.equals(recordType)) {
			type = 2;
		} else if(Config.ECONOMY_CLASS.equals(recordType)) {
			type = 3;
		} else if(Config.ALL_CLASS.equals(recordType)) {
			type = 0;
		}
		//WST: 0\tWDC: 0\tNDL: 0
		String result = impl.getBookedFlightCounts(type);
		//return WST$0$WDC$0$NDL$0
		return result.replace("\t", ": ").replace(": ", "$").replace("WDC", "WST");
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
