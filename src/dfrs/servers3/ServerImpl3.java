package dfrs.servers3;

import java.util.HashMap;

import dfrs.ServerInterfacePOA;
import dfrs.servers.BaseServerCluster;
import dfrs.utils.Config;
import dfrs.utils.Utils;

public class ServerImpl3 extends ServerInterfacePOA {

	private ServerImplYue impl;
	public  HashMap<String,String> flight = new HashMap<String,String>();
	/*
	 6789 6790 MTL
	 6791 6792 WST
	 6793 6794 NDL
	 */
	public ServerImpl3(int i) {
		if(i==0) {
			impl = new ServerImplYue();
			impl.portone = 6791;
			impl.porttwo = 6793;
			impl.location="MTL";
			//create flight to wst
			managerRecord a = new managerRecord();
			a.managerID="MTL0001";
			a.destination = "WST";
			a.flightDate =Config.DATE;
			a.business = 100;
			a.businessLeft = 100;
			a.firstclass = 100;
			a.firstclassLeft = 100;
			a.economy = 100;
			a.economyLeft = 100;
			flight.put(GenerateID.getInstance().getFlightID()+"", a.destination+a.flightDate);
			impl.planeMap.put((a.destination+a.flightDate) , a);
			//create flight to ndl
			a = new managerRecord();
			a.managerID="MTL0002";
			a.destination = "NDL";
			a.flightDate =Config.DATE;
			a.business = 100;
			a.businessLeft = 100;
			a.firstclass = 100;
			a.firstclassLeft = 100;
			a.economy = 100;
			a.economyLeft = 100;
			flight.put(GenerateID.getInstance().getFlightID()+"", a.destination+a.flightDate);
			impl.planeMap.put((a.destination+a.flightDate) , a);
			
			serverThread sTone = new serverThread(6789,impl);
	    	serverThread sTtwo = new serverThread(6790,impl);
	    	System.out.println("mtlServer ready and waiting ...");
		} else if(i==1) {
			impl = new ServerImplYue();
			impl.portone = 6789;
			impl.porttwo = 6794;
			impl.location="WST";
			//create flight to mtl
			managerRecord a = new managerRecord();
			a.managerID="WST0001";
			a.destination = "MTL";
			a.flightDate =Config.DATE;
			a.business = 100;
			a.businessLeft = 100;
			a.firstclass = 100;
			a.firstclassLeft = 100;
			a.economy = 100;
			a.economyLeft = 100;
			flight.put(GenerateID.getInstance().getFlightID()+"", a.destination+a.flightDate);
			impl.planeMap.put((a.destination+a.flightDate) , a);
			
			//create flight to NDL
			managerRecord b = new managerRecord();
			b.managerID="WST0002";
			b.destination = "NDL";
			a.flightDate =Config.DATE;
			a.business = 100;
			a.businessLeft = 100;
			a.firstclass = 100;
			a.firstclassLeft = 100;
			a.economy = 100;
			a.economyLeft = 100;
			flight.put(GenerateID.getInstance().getFlightID()+"", a.destination+a.flightDate);
			impl.planeMap.put((b.destination+b.flightDate) , b);
			
			serverThread sTone = new serverThread(6791,impl);
	    	serverThread sTtwo = new serverThread(6792,impl);
	    	System.out.println("ndlServer ready and waiting ...");
		} else if(i==2) {
			impl = new ServerImplYue();
			impl.portone = 6790;
			impl.porttwo = 6792;
			impl.location="NDL";
			//create a planeMap record
			managerRecord a = new managerRecord();
			a.managerID="NDL0001";
			a.destination = "MTL";
			a.flightDate =Config.DATE;
			a.business = 100;
			a.businessLeft = 100;
			a.firstclass = 100;
			a.firstclassLeft = 100;
			a.economy = 100;
			a.economyLeft = 100;
			flight.put(GenerateID.getInstance().getFlightID()+"", a.destination+a.flightDate);
			impl.planeMap.put((a.destination+a.flightDate) , a);
			//create a planeMap record
			a = new managerRecord();
			a.managerID="NDL0002";
			a.destination = "WST";
			a.flightDate =Config.DATE;
			a.business = 100;
			a.businessLeft = 100;
			a.firstclass = 100;
			a.firstclassLeft = 100;
			a.economy = 100;
			a.economyLeft = 100;
			flight.put(GenerateID.getInstance().getFlightID()+"", a.destination+a.flightDate);
			impl.planeMap.put((a.destination+a.flightDate) , a);
			
			serverThread sTone = new serverThread(6793,impl);
	    	serverThread sTtwo = new serverThread(6794,impl);
	    	System.out.println("wstServer ready and waiting ...");
		}
	}

	public static ServerInterfacePOA getServerImpl(int i) {
		return new ServerImpl3(i);
	}
	
	@Override
	public String bookFlight(String departure, String firstName, String lastName, String address, String phoneNumber,
			String destination, String flightClass, String flightDate) {
		String des = Utils.getServer(destination);
		if(BaseServerCluster.SERVER_MTL.equals(des)) {
			des = "MTL";
		} else if(BaseServerCluster.SERVER_WST.equals(des)) {
			des = "WST";
		} else if(BaseServerCluster.SERVER_NDL.equals(des)) {
			des = "NDL";
		}
		if(Config.FIRST_CLASS.equals(flightClass)) {
			flightClass = "firstclass";
		} else if(Config.BUSINESS_CLASS.equals(flightClass)) {
			flightClass = "business";
		} else if(Config.ECONOMY_CLASS.equals(flightClass)) {
			flightClass = "economy";
		}
		String result = impl.bookFlight(departure, firstName, lastName, address, phoneNumber, des, flightClass, flightDate);
		if(result != null&&result.contains("success")) {
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
		return impl.getBookedFlightCount(impl.location, managerID);
	}

	@Override
	public String editFlightRecord(String managerID, String recordID, String fieldName, String newValue) {
		String value[] = new String[2];//destination, date
		int seats[] = new int[3];//economy, business, firstclass
		String v = flight.get(recordID);
		if(v== null) {
			return Config.FAIL;
		} else {
			String destination = v.substring(0, 3);
			String flightDate = v.substring(3);
			String keyManager = destination+flightDate;
			if(impl.planeMap.containsKey(keyManager)) {
				managerRecord record = impl.planeMap.get(keyManager);
				value[0] = record.destination;
				value[1] = record.flightDate;
				seats[0] = record.economy;
				seats[1] = record.business;
				seats[2] = record.firstclass;
			}
		}
		//NOT Support edit destination and date
		if(Config.DATE.equals(fieldName)) {
			value[1] = newValue;
		} else if(Config.DESTINATION.equals(fieldName)) {
			String des = Utils.getServer(newValue);
			if(BaseServerCluster.SERVER_MTL.equals(des)) {
				value[0] = "MTL";
			} else if(BaseServerCluster.SERVER_WST.equals(des)) {
				value[0] = "WST";
			} else if(BaseServerCluster.SERVER_NDL.equals(des)) {
				value[0] = "NDL";
			}
		} else if(Config.FIRST_CLASS.equals(fieldName)) {
			seats[2] = Integer.valueOf(newValue);
		} else if(Config.BUSINESS_CLASS.equals(fieldName)) {
			seats[1] = Integer.valueOf(newValue);
		} else if(Config.ECONOMY_CLASS.equals(fieldName)) {
			seats[0] =Integer.valueOf(newValue);
		}
		String result = impl.editFlightRecord(impl.location, managerID, value[0], value[1], seats[0], seats[1], seats[2]);
		if(result != null&&result.contains("success")) {
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
			currentCity = "WST";
		} else if(BaseServerCluster.SERVER_NDL.equals(c)) {
			currentCity = "NDL";
		}
		if(BaseServerCluster.SERVER_MTL.equals(o)) {
			otherCity = "MTL";
		} else if(BaseServerCluster.SERVER_WST.equals(o)) {
			otherCity = "WST";
		} else if(BaseServerCluster.SERVER_NDL.equals(o)) {
			otherCity = "NDL";
		}
		String result = impl.transferReservation(managerID, passengerID, currentCity, otherCity);
		if(result != null&&result.contains("succeed")) {
			return Config.SUCCESS;
		} else {
			return Config.FAIL;
		}
	}

}
