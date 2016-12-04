package dfrs.servers3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

import dfrs.servers.IServerManager;

public class serverThread  extends Thread implements IServerManager {

	private boolean running = true;
	private int port = 0;
	private ServerImplYue serverIm;
	private  HashMap<Character,recordLIst> recordTable;
	DatagramSocket aSocket;
	public serverThread(int new_port,ServerImplYue new_serverIm )
	{
		serverIm=new_serverIm;
		port=new_port;
		recordTable=serverIm.recordTable;
		this.start();
	}

	public void run()
	{
		 
//    	DatagramSocket aSocket = null;
		try{
	    	aSocket = new DatagramSocket(port);
			
	    	byte[] buffer = new byte[1000];
			System.out.println("server "+port+" begin running");
 			while(running){
 				
 				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
  				aSocket.receive(request);     
  				String receiver =new String(request.getData(), 0, request.getLength()).trim();
  				System.out.println("receiver is " + receiver);
  				
  				if(receiver.charAt(0)== '1') //getbookednumber
  				{
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
  				  System.out.println(num);
				String t = Integer.toString(num);
				byte [] m = t.getBytes();
	
				DatagramPacket reply = new DatagramPacket(m, t.length(), 
					request.getAddress(), request.getPort());
				
				aSocket.send(reply);
  				}//if 1
  				else if(receiver.charAt(0)== '2')
  				{	
  					System.out.println("going to the second");
  					passengerRecord record = new passengerRecord();
  					//EDIT
//  					record.RecordID=++serverIm.counter;
  					//END
  					int j =1;
  					int cal=0;
  					for(int i=2;i<receiver.length();i++){
  							if(receiver.charAt(i)=='$'){
  								if(cal==0){
  									record.firstName = receiver.substring(j+1,i);
  									cal++;
  								}else if(cal==1){
  									record.lastName = receiver.substring(j+1,i);
  									cal++;
  								}else if(cal==2){
  									record.address = receiver.substring(j+1,i);
  									cal++;
  								}else if(cal==3){
  									record.phoneNumber = receiver.substring(j+1,i);
  									cal++;
  								}else if(cal==4){
  									record.destination = receiver.substring(j+1,i);
  									cal++;
  								}else if(cal==5){
  									record.flightClass = receiver.substring(j+1,i);
  									cal++;
  								}else if(cal==6){
  									record.flightDate = receiver.substring(j+1,i);
  									cal++;
  								}else if(cal==7) {
  									record.RecordID = Integer.valueOf(receiver.substring(j+1,i));
  									cal++;
  								}
  								j=i;
  							}//if $
  					}//for
  					
  					System.out.println("record.flightDate is " + record.flightDate);
  					//l
  					Character lNameFLetter = record.lastName.substring(0, 1).toCharArray()[0];
  					System.out.println(lNameFLetter.toString());
  					String keyManager = record.destination+record.flightDate ;
  					//EDIT
//  					serverIm.counter++;
  					//END
  					String reply=null;
  					if(recordTable.containsKey(lNameFLetter))
  					  {
  							if(serverIm.planeMap.containsKey(keyManager))
  							{
  								if(record.flightClass.equals("economy")&&(serverIm.planeMap.get(keyManager).economyLeft>0))
  									{
  									//not responding the original data
  									synchronized(serverIm.planeMap.get(keyManager))
  									{	
  										serverIm.planeMap.get(keyManager).economyLeft--;
  									}
  									synchronized(recordTable.get(lNameFLetter).recordList)
  									{
  										recordTable.get(lNameFLetter).recordList.add(record);
  									}
									reply= "success";
  										
  									}else 
  								if(record.flightClass.equals("business")&&(serverIm.planeMap.get(keyManager).businessLeft>0))
  									{
  									synchronized(serverIm.planeMap.get(keyManager))
  									{	
  										serverIm.planeMap.get(keyManager).businessLeft--;
  									}
  									synchronized(recordTable.get(lNameFLetter).recordList)
  									{
  										recordTable.get(lNameFLetter).recordList.add(record);
  									}	
									reply= "success";
  										
  									}else  
  								if(record.flightClass.equals("firstclass")&&(serverIm.planeMap.get(keyManager).firstclassLeft>0))
  									{
  									synchronized(serverIm.planeMap.get(keyManager))
  									{
  										serverIm.planeMap.get(keyManager).firstclassLeft--;
  									}
  									synchronized(recordTable.get(lNameFLetter).recordList)
  									{
  										recordTable.get(lNameFLetter).recordList.add(record);
  									}
									reply= "success";
  										
  									}else{
  										reply= "fail";//the class has problem
  									}
  							}
  							else
  							{
  								reply= "fail";// no such flight
  							}
  						 	
  						}else
  						{
  							recordLIst recordList = new recordLIst();
  							recordTable.put(lNameFLetter, recordList);
  							System.out.println("new list");
  							if(serverIm.planeMap.containsKey(keyManager))
  							{
  								System.out.println("containsKey");
  								if(record.flightClass.equals("economy")&&(serverIm.planeMap.get(keyManager).economyLeft>0))
  									{
  									System.out.println("into economy");
  									synchronized(serverIm.planeMap.get(keyManager))
  									{
  										serverIm.planeMap.get(keyManager).economyLeft--;
  									}
  									synchronized(recordTable.get(lNameFLetter).recordList)
  									{
  										recordTable.get(lNameFLetter).recordList.add(record);
  									}
									System.out.println("the record is "+recordTable.get(lNameFLetter).recordList.size());
									reply= "success";
  										
  									}else 
  								if(record.flightClass.equals("business")&&(serverIm.planeMap.get(keyManager).businessLeft>0))
  									{
  									synchronized(serverIm.planeMap.get(keyManager))
  									{
  										serverIm.planeMap.get(keyManager).businessLeft--;
  									}
  									synchronized(recordTable.get(lNameFLetter).recordList)
  									{
  										recordTable.get(lNameFLetter).recordList.add(record);
  									}
  									reply= "success";
  										
  									}else  
  								if(record.flightClass.equals("firstclass")&&(serverIm.planeMap.get(keyManager).firstclassLeft>0))
  									{
  									synchronized(serverIm.planeMap.get(keyManager))
  									{
  										serverIm.planeMap.get(keyManager).firstclassLeft--;
  									}
  									synchronized(recordTable.get(lNameFLetter).recordList)
  									{
  										recordTable.get(lNameFLetter).recordList.add(record);
  									}
									reply= "success";
  										
  									}else{
  										reply= "fail";//the class has problem
  									}
  							}
  							else
  							{
  								reply= "fail";// no such flight
  							}
  						}
					
					byte [] m = reply.getBytes();
		
					DatagramPacket result = new DatagramPacket(m, reply.length(), 
						request.getAddress(), request.getPort());
					
					aSocket.send(result);
  					
  				}//2
  				
    		}//while
		}catch (SocketException e){
			System.out.println("Socket: " + e.getMessage());
		}catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}finally {
			if(aSocket != null) 
				aSocket.close();
		}
	}

	@Override
	public void shutdown() {
		running = false;
		if(aSocket!=null)aSocket.close();
	}

	@Override
	public void printAllTicket() {
		
	}
}
