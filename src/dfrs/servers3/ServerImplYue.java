package dfrs.servers3;
import java.util.HashMap;
import java.util.Iterator;

import dfrs.ServerInterfacePOA;
import dfrs.utils.Config;

public class ServerImplYue {// extends ServerInterfacePOA  {

	public  HashMap<Character,recordLIst> recordTable = new HashMap<Character,recordLIst>();
	public  HashMap<String,managerRecord> planeMap = new HashMap<String,managerRecord>();
	public String location = "";
	public int portone=0;
	public int porttwo=0;
	public int counter=0;
	
//	public ServerImplYue(String new_location, int new_portone,int new_porttwo) {
//		super();
//		location = new_location;
//		portone = new_portone;
//		porttwo = new_porttwo;
//	}
	public ServerImplYue() {}
//	@Override
	public String bookFlight(String currentCity, String firstName, String lastName, String address, String phoneNumber,
			String destination, String flightClass, String flightDate) {
		passengerRecord record = new passengerRecord();
		Character lNameFLetter = lastName.substring(0, 1).toCharArray()[0];
		//EDIT
		record.RecordID=++counter;
		record.firstName = firstName;
		record.lastName = lastName;
		record.address =address;
		record.phoneNumber = phoneNumber;
		record.destination = destination ;
		record.flightClass = flightClass;
		record.flightDate = flightDate;
		String keyManager = record.destination+record.flightDate ;
		//EDIT
//		counter++;
		
		if(recordTable.containsKey(lNameFLetter))
		  {
				if(planeMap.containsKey(keyManager))
				{
					if(record.flightClass.equals("economy")&&(planeMap.get(keyManager).economyLeft>0))
						{
						//not responding the original data
							synchronized(planeMap.get(keyManager))
							{
								planeMap.get(keyManager).economyLeft--;
							}
							synchronized(recordTable.get(lNameFLetter).recordList)
							{
								recordTable.get(lNameFLetter).recordList.add(record);
							
								//thread concurrency test case
								for (recordLIst list : recordTable.values()) 
				  				  {  				  
					  					   for(int i = 0;i<list.recordList.size();i++)
					  					   {
					  						   System.out.println(list.recordList.get(i));
					  					   } 
				  				  }
								System.out.println("");
							}
			  				//thread concurrency test case
							return "success adding"+record.lastName;
							
						}else 
					if(record.flightClass.equals("business")&&(planeMap.get(keyManager).businessLeft>0))
						{
							synchronized(planeMap.get(keyManager))
							{
								planeMap.get(keyManager).businessLeft--;
							}
							synchronized(recordTable.get(lNameFLetter).recordList)
							{
								recordTable.get(lNameFLetter).recordList.add(record);
							}
							return "success adding"+record.lastName;
							
						}else  
					if(record.flightClass.equals("firstclass")&&(planeMap.get(keyManager).firstclassLeft>0))
						{
							synchronized(planeMap.get(keyManager))
							{
								planeMap.get(keyManager).firstclassLeft--;
							}
							synchronized(recordTable.get(lNameFLetter).recordList)
							{
								recordTable.get(lNameFLetter).recordList.add(record);
							}
							return "success adding"+record.lastName;
							
						}else{

							return "there is no seat available"+record.lastName;
						}
				}
				else
				{
					return "no  such a flight"+record.lastName;
				}
			 	
			}else
			{
				
				recordLIst recordList = new recordLIst();
				recordTable.put(lNameFLetter, recordList);
			
				if(planeMap.containsKey(keyManager))
				{
					if(record.flightClass.equals("economy")&&(planeMap.get(keyManager).economyLeft>0))
						{
							synchronized(planeMap.get(keyManager))
							{	
								planeMap.get(keyManager).economyLeft--;
							}
							synchronized(recordTable.get(lNameFLetter).recordList)
							{
								recordTable.get(lNameFLetter).recordList.add(record);
							
								//thread concurrency test case
								for (recordLIst list : recordTable.values()) 
				  				  {  				  
				  					   for(int i = 0;i<list.recordList.size();i++)
				  					   {
				  						   System.out.println(list.recordList.get(i));
				  					   } 
				  				  }
								System.out.println("");
				  				//thread concurrency test case
							}
							return "success adding"+record.lastName;
						}else 
					if(record.flightClass.equals("business")&&(planeMap.get(keyManager).businessLeft>0))
						{
							synchronized(planeMap.get(keyManager))
							{	
								planeMap.get(keyManager).businessLeft--;
							}
							synchronized(recordTable.get(lNameFLetter).recordList)
							{
								recordTable.get(lNameFLetter).recordList.add(record);
							}
							return "success adding"+record.lastName;
							
						}else  
					if(record.flightClass.equals("firstclass")&&(planeMap.get(keyManager).firstclassLeft>0))
						{
							synchronized(planeMap.get(keyManager))
							{		
								planeMap.get(keyManager).firstclassLeft--;
							}
							synchronized(recordTable.get(lNameFLetter).recordList)
							{
								recordTable.get(lNameFLetter).recordList.add(record);
							}
							return "success adding"+record.lastName;
							
						}else{
							return "there is no seat available"+record.lastName;
						}
				}
				else
				{
					return "no  such a flight"+record.lastName;
				}
			}
	}

//	@Override
	public String getBookedFlightCount(String currentCity, String managerID) {
	
		if(!managerID.substring(0,3).equals(location))
			return "enter to wrong server";
		
		
		int num = 0;
		for (recordLIst list : recordTable.values()) 
		  {  
			  synchronized(list)
			  {
				   for(int i = 0;i<list.recordList.size();i++)
				   {
					   num++;
				   } 
			  }
		  }  
		 NumberFlight query = new NumberFlight();
		 System.out.println("MTLnum "+num);
		 if(location.equals("MTL")){
		  synchronized(query)
		  {
			  query.MTL=num;
		  }
		  }else if(location.equals("WST")){
			  synchronized(query)
			  {
				  query.WST=num;
			  }
		  }else if(location.equals("NDL")){
			  synchronized(query)
			  {
				  query.NDL=num;
			  }
		  }
		  
		 clientThread cTone= new clientThread(portone,query);//need to change port number and get the result in the thread
    	 clientThread cTtwo= new clientThread(porttwo,query);
	    	
	     try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

	     String bookedcount = "MTL$" + query.MTL +  "$WST$" + query.WST + "$NDL$"+ query.NDL +"$";
		 return bookedcount;
	}

//	@Override
	public String editFlightRecord(String currentCity, String managerID, String destination, String flightDate,
			int economy, int business, int firstclass) {

		if(!managerID.substring(0,3).equals(location))
			return "enter to wrong server";
		
		String keyManager = destination+flightDate ;
		managerRecord record = new managerRecord();
		record.managerID = "MTL0001";
		record.destination = destination;
		record.flightDate = flightDate;
		record.economy = economy;
		record.economyLeft =economy;
		record.business = business;
		record.businessLeft = business;
		record.firstclass = firstclass;
		record.firstclassLeft = firstclass;
		
			if(planeMap.containsKey(keyManager))
			  {
				synchronized(planeMap.get(keyManager))
				{	
					if(((planeMap.get(keyManager).economy-planeMap.get(keyManager).economyLeft)<=economy)&&
					((planeMap.get(keyManager).business-planeMap.get(keyManager).businessLeft)<=business)&&
					((planeMap.get(keyManager).firstclass-planeMap.get(keyManager).firstclassLeft)<=firstclass))
						{
						planeMap.put(keyManager, record);
						}else{
							return "false changing"+keyManager;
						}
				}
				System.out.println("the changing text are economy is "+
				record.economy+" business is "+record.business+
				" firstclass is "+record.firstclass);

				return "success changing"+keyManager;
				}
			else{
				return "do not containing the key";
				}
	}

	
	public passengerRecord findID(String PassengerID){
		
		System.out.println("findID PassengerID " + PassengerID);
		
		Iterator iter = recordTable.keySet().iterator();
		
		int pid = Integer.valueOf(PassengerID);
		
		while (iter.hasNext()) {
			Character key = (Character)iter.next();
			recordLIst a = recordTable.get(key);
			synchronized(a)
			{
				for(int i=0;i<a.recordList.size();i++)
					{
						if(pid == a.recordList.get(i).RecordID)
						{
							return a.recordList.get(i);
						}
					}
			}
		}
		return null;
	}
	
//	@Override
	public String transferReservation(String managerID, String PassengerID, String CurrentCity, String OtherCity) 
	{

		if(!managerID.substring(0,3).equals(location))
			return "enter to wrong server";
		
		passengerRecord pasRectrans = findID(PassengerID);
		if(pasRectrans == null){
			return "the record is not found.";
		}else{
//			 6789 6790 MTL
//			 6791 6792 WST
//			 6793 6794 NDL 
			//lastName.substring(0, 1).toCharArray()[0];
			String keyManager = pasRectrans.destination+pasRectrans.flightDate ;
			if(OtherCity.equals("MTL"))
			{
				transmodule tR = new transmodule(6789 ,pasRectrans );
				if(tR.execute()){
					synchronized(recordTable.get(pasRectrans.lastName.substring(0, 1).toCharArray()[0]))
					{
						recordTable.get(pasRectrans.lastName.substring(0, 1).toCharArray()[0]).recordList.remove(pasRectrans);
					}
					
					if("economy".equals(pasRectrans.flightClass))
					{
						planeMap.get(keyManager).economyLeft++;
					}else if("business".equals(pasRectrans.flightClass))
					{
						planeMap.get(keyManager).businessLeft++;
					}else if("firstclass".equals(pasRectrans.flightClass))
					{
						planeMap.get(keyManager).businessLeft++;
					}
					
					System.out.println("remove"+6789);
					return "MTL  transfer succeed";
				}
				return "MTL fail and do not remove";
			}else 
			if(OtherCity.equals("WST"))
			{
				transmodule tR = new transmodule(6791 ,pasRectrans );
				if(tR.execute()){
					synchronized(recordTable.get(pasRectrans.lastName.substring(0, 1).toCharArray()[0]))
					{
						recordTable.get(pasRectrans.lastName.substring(0, 1).toCharArray()[0]).recordList.remove(pasRectrans);
					}
					
					if("economy".equals(pasRectrans.flightClass))
					{
						planeMap.get(keyManager).economyLeft++;
					}else if("business".equals(pasRectrans.flightClass))
					{
						planeMap.get(keyManager).businessLeft++;
					}else if("firstclass".equals(pasRectrans.flightClass))
					{
						planeMap.get(keyManager).businessLeft++;
					}
					
					System.out.println("remove"+6791);
					System.out.println(recordTable.get(pasRectrans.lastName.substring(0, 1).toCharArray()[0]).recordList.size()); 
					return "WST transfer succeed";
				}
				return "WST fail and do not remove";
			}else 
			if(OtherCity.equals("NDL"))
			{
				transmodule tR = new transmodule(6793 ,pasRectrans );
				if(tR.execute()){
					synchronized(recordTable.get(pasRectrans.lastName.substring(0, 1).toCharArray()[0]))
					{
						recordTable.get(pasRectrans.lastName.substring(0, 1).toCharArray()[0]).recordList.remove(pasRectrans);
					}
					
					
					if("economy".equals(pasRectrans.flightClass))
					{
						planeMap.get(keyManager).economyLeft++;
					}else if("business".equals(pasRectrans.flightClass))
					{
						planeMap.get(keyManager).businessLeft++;
					}else if("firstclass".equals(pasRectrans.flightClass))
					{
						planeMap.get(keyManager).businessLeft++;
					}
					
					System.out.println("remove"+6793);
				return "NDL  transfer succeed";
				}
				return "NDL fail and do not remove";
			}
		}
		return "Can not find OtherCity";
	
	}
	
}
