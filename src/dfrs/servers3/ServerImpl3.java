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
		return impl.bookFlight(departure, firstName, lastName, address, phoneNumber, des, flightClass, flightDate);
	}

	@Override
	public String getBookedFlightCount(String managerID, String recordType) {
		// TODO Auto-generated method stub
		return null;
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
