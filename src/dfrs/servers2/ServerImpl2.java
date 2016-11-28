package dfrs.servers2;

import dfrs.ServerInterfacePOA;
import dfrs.servers.BaseServerCluster;
import dfrs.utils.Config;
import dfrs.utils.Utils;

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
		String des = Utils.getServer(destination);
		if(BaseServerCluster.SERVER_MTL.equals(des)) {
			des = StartMontrealServer.SERVER_NAME;
		} else if(BaseServerCluster.SERVER_WST.equals(des)) {
			des = StartWashingtonServer.SERVER_NAME;
		} else if(BaseServerCluster.SERVER_NDL.equals(des)) {
			des = StartNewDelhiServer.SERVER_NAME;
		}
		if(Config.FIRST_CLASS.equals(flightClass)) {
			flightClass = "fit";
		} else if(Config.BUSINESS_CLASS.equals(flightClass)) {
			flightClass = "business";
		} else if(Config.ECONOMY_CLASS.equals(flightClass)) {
			flightClass = "economy";
		}
		//flightDate must be yyyy/MM/dd
		boolean result = impl.bookFlight(firstName, lastName, address, phoneNumber, des, flightClass, flightDate);
		if(result) {
			return Config.SUCCESS;
		} else {
			return Config.FAIL;
		}
	}

	@Override
	public String getBookedFlightCount(String managerID, String recordType) {
		//didn't implement by type
//		int type = 0;
//		if(Config.FIRST_CLASS.equals(recordType)) {
//			type = 1;
//		} else if(Config.BUSINESS_CLASS.equals(recordType)) {
//			type = 2;
//		} else if(Config.ECONOMY_CLASS.equals(recordType)) {
//			type = 3;
//		} else if(Config.ALL_CLASS.equals(recordType)) {
//			type = 0;
//		}
		//return WST$0$WDC$0$NDL$0
		return impl.getBookedFlightCount();
	}

	@Override
	public String editFlightRecord(String managerID, String recordID, String fieldName, String newValue) {
		String value[] = new String[3];//departure, destination, date
		int seats[] = new int[3];//eco, busi, fit
		int index = impl.findFlightRecordsByID(recordID);
		String departure = "";
		if(impl instanceof MontrealServerObj) {
			departure = StartMontrealServer.SERVER_NAME;
		} else if(impl instanceof WashingtonServerObj) {
			departure = StartWashingtonServer.SERVER_NAME;
		} else if(impl instanceof NewDelhiServerObj) {
			departure = StartNewDelhiServer.SERVER_NAME;
		}
		if(index == -1) {
			value[0] = departure;
			value[1] = "";
			value[2] = Config.DATE;
			seats[0] = 0;
			seats[1] = 0;
			seats[2] = 0;
		} else {
			FlightRecord record = impl.getRecordsByIndex(index);
			value[0] = record.getDeparture();
			value[1] = record.getDestination();
			value[2] = record.getDateOfFlight();
			seats[0] = record.getEconomySeats();
			seats[1] = record.getBusinessSeats();
			seats[2] = record.getFitsSeats();
		}
		if(Config.DEPARTURE.equals(fieldName)) {
			String des = Utils.getServer(newValue);
			if(BaseServerCluster.SERVER_MTL.equals(des)) {
				value[0] = StartMontrealServer.SERVER_NAME;
			} else if(BaseServerCluster.SERVER_WST.equals(des)) {
				value[0] = StartWashingtonServer.SERVER_NAME;
			} else if(BaseServerCluster.SERVER_NDL.equals(des)) {
				value[0] = StartNewDelhiServer.SERVER_NAME;
			}
		} else if(Config.DATE.equals(fieldName)) {
			value[2] = newValue;
		} else if(Config.DESTINATION.equals(fieldName)) {
			String des = Utils.getServer(newValue);
			if(BaseServerCluster.SERVER_MTL.equals(des)) {
				value[1] = StartMontrealServer.SERVER_NAME;
			} else if(BaseServerCluster.SERVER_WST.equals(des)) {
				value[1] = StartWashingtonServer.SERVER_NAME;
			} else if(BaseServerCluster.SERVER_NDL.equals(des)) {
				value[1] = StartNewDelhiServer.SERVER_NAME;
			}
		} else if(Config.FIRST_CLASS.equals(fieldName)) {
			seats[2] = Integer.valueOf(newValue);
		} else if(Config.BUSINESS_CLASS.equals(fieldName)) {
			seats[1] = Integer.valueOf(newValue);
		} else if(Config.ECONOMY_CLASS.equals(fieldName)) {
			seats[0] =Integer.valueOf(newValue);
		}
		boolean result = impl.editFlightRecord(recordID, value[0], value[1], seats[0], seats[1], seats[2], value[2]);
		if(result) {
			return Config.SUCCESS;
		} else {
			return Config.FAIL;
		}
	}

	@Override
	public String transferReservation(String managerID, String passengerID, String currentCity, String otherCity) {
		String c = Utils.getServer(currentCity);
		String o = Utils.getServer(currentCity);
		if(BaseServerCluster.SERVER_MTL.equals(c)) {
			currentCity = StartMontrealServer.SERVER_NAME;
		} else if(BaseServerCluster.SERVER_WST.equals(c)) {
			currentCity = StartWashingtonServer.SERVER_NAME;
		} else if(BaseServerCluster.SERVER_NDL.equals(c)) {
			currentCity = StartNewDelhiServer.SERVER_NAME;
		}
		if(BaseServerCluster.SERVER_MTL.equals(o)) {
			otherCity = StartMontrealServer.SERVER_NAME;
		} else if(BaseServerCluster.SERVER_WST.equals(o)) {
			otherCity = StartWashingtonServer.SERVER_NAME;
		} else if(BaseServerCluster.SERVER_NDL.equals(o)) {
			otherCity = StartNewDelhiServer.SERVER_NAME;
		}
		boolean result = impl.transferReservation(passengerID, currentCity, otherCity);
		if(result) {
			return Config.SUCCESS;
		} else {
			return Config.FAIL;
		}
	}

}
