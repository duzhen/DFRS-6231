package dfrs.servers1;

import dfrs.ServerInterfacePOA;
import dfrs.servers.BaseServerCluster;
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
		return new ServerImpl11(i);
	}
	
	@Override
	public String bookFlight(String departure, String firstName, String lastName, String address, String phoneNumber,
			String destination, String flightDate, String flightClass) {
		String des = Utils.getServer(destination);
		if(BaseServerCluster.SERVER_MTL.equals(des)) {
			des = "MTL";
		} else if(BaseServerCluster.SERVER_WST.equals(des)) {
			des = "WDC";
		} else if(BaseServerCluster.SERVER_NDL.equals(des)) {
			des = "NDL";
		}
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
		String f = "";
		if(Config.DEPARTURE.equals(fieldName)) {
			f = "departure";//no this field
			String des = Utils.getServer(newValue);
			if(BaseServerCluster.SERVER_MTL.equals(des)) {
				newValue = "MTL";
			} else if(BaseServerCluster.SERVER_WST.equals(des)) {
				newValue = "WDC";
			} else if(BaseServerCluster.SERVER_NDL.equals(des)) {
				newValue = "NDL";
			}
		} else if(Config.DATE.equals(fieldName)) {
			f = "date";
		} else if(Config.DESTINATION.equals(fieldName)) {
			f = "destination";
			String des = Utils.getServer(newValue);
			if(BaseServerCluster.SERVER_MTL.equals(des)) {
				newValue = "MTL";
			} else if(BaseServerCluster.SERVER_WST.equals(des)) {
				newValue = "WDC";
			} else if(BaseServerCluster.SERVER_NDL.equals(des)) {
				newValue = "NDL";
			}
		} else if(Config.FIRST_CLASS.equals(fieldName)) {
			f = "first";
		} else if(Config.BUSINESS_CLASS.equals(fieldName)) {
			f = "bussiness";
		} else if(Config.ECONOMY_CLASS.equals(fieldName)) {
			f = "economy";
		}
		//split("$")-this method maybe wrong, should split("\\$")
		String result = impl.editRecord(recordID, "edit"+f, newValue);
		if(result != null&&result.startsWith("RIGHT-")) {
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
			currentCity = "MTL";
		} else if(BaseServerCluster.SERVER_WST.equals(c)) {
			currentCity = "WDC";
		} else if(BaseServerCluster.SERVER_NDL.equals(c)) {
			currentCity = "NDL";
		}
		if(BaseServerCluster.SERVER_MTL.equals(o)) {
			otherCity = "MTL";
		} else if(BaseServerCluster.SERVER_WST.equals(o)) {
			otherCity = "WDC";
		} else if(BaseServerCluster.SERVER_NDL.equals(o)) {
			otherCity = "NDL";
		}
		String result = impl.transferReservation(passengerID, currentCity, otherCity);
		if(result != null&&result.contains("Completed")) {
			return Config.SUCCESS;
		} else {
			return Config.FAIL;
		}
	}

}
