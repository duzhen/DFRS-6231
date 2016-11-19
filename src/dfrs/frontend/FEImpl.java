package dfrs.frontend;

import java.util.HashMap;
import dfrs.ServerInterfacePOA;
import dfrs.net.Client;
import dfrs.sequencer.ClusterManagerSender;


public class FEImpl  extends ServerInterfacePOA  {

	public HashMap<String,HashMap<String,String>> recPac;
	public CompareResult CR;
	public String host = "";
	public int port=0;
	public FEServer cMServer1 ;
	public FEServer cMServer2 ;
	public FEServer cMServer3 ;
	public FEServer cMServer4 ;
	public int successCount=0,failCount=0,crashCount=0;
	public FEImpl(String host, int portone) {
		super();
		this.host = host;
		this.port = portone;
		CR = new CompareResult();
		
		cMServer1 = new FEServer(8101,CR);
		cMServer2 = new FEServer(8102,CR);
		cMServer3 = new FEServer(8103,CR);
		cMServer4 = new FEServer(8104,CR);
		
		cMServer1.run();
		cMServer2.run();
		cMServer3.run();
		cMServer4.run();
	}

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
		
		String content ="2"+"$"+currentCity
				+"$"+managerID
				+"$"+destination
				+"$"+flightDate
				+"$"+Integer.toString(economy)
				+"$"+Integer.toString(business)
				+"$"+Integer.toString(firstclass)+"$";
		
		System.out.println("client "+port+" connect string");
		System.out.println(content);
        Client client = new Client("localhost", 8888, content);
        client.run();
        try {
			client.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	     try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
		}
	     
	     for(int i=0;i<CR.CM.length;i++){
	    	 if(CR.CM[i].equals("")){
		    	 System.out.println("CR.CM"+i+" crash");
		    	 crashCount++;
		     }
	     }
	     
	     for(int i=0;i<CR.CM.length;i++){
	    	 if(CR.CM[i].equals("success")){
	    		 successCount++;
		     }else if(CR.CM[i].equals("fail")){
		    	 failCount++;
		     }
	     }
	     //whether crash should be noticed at the FE?
	     if(successCount==(4-crashCount)){
	    	 clear();
	    	 return "success send the content";
	     }else 
    	 if(failCount==(4-crashCount)){
    		 clear();
	    	 return "fail send the content";
	     }else 
    	 if(successCount>=failCount)
    	 {
    		 String RMMessage="";
    	     for(int i=0;i<CR.CM.length;i++){
    	    	 if(CR.CM[i].equals("success")){
    	    		 RMMessage=RMMessage+"correct"+"$"+Integer.toString(i)+"$" ;
    		     }else if(CR.CM[i].equals("fail")){
    		    	 RMMessage=RMMessage+"wrong"+"$"+Integer.toString(i)+"$" ;
    		     }
    	     }
//    	     FE2RMMulticast mc = new FE2RMMulticast(RMMessage);
//             mc.initial();
//             mc.execute();
    	    ClusterManagerSender cma1,cma2,cma3,cma4;
			cma1 = new ClusterManagerSender("localhost",8201,RMMessage);
			cma2 = new ClusterManagerSender("localhost",8202,RMMessage);
			cma3 = new ClusterManagerSender("localhost",8203,RMMessage);
			cma4 = new ClusterManagerSender("localhost",8204,RMMessage);
			cma1.run();
			cma2.run();
			cma3.run();
			cma4.run();
			try {
				cma1.join();
				cma2.join();
				cma3.join();
				cma4.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
             clear();
    	     return "success send the content";
	     }else 
    	 if(successCount<failCount){

    		 String RMMessage="";
    	     for(int i=0;i<CR.CM.length;i++){
    	    	 if(CR.CM[i].equals("fail")){
    	    		 RMMessage=RMMessage+"correct"+"$"+Integer.toString(i)+"$" ;
    		     }else if(CR.CM[i].equals("success")){
    		    	 RMMessage=RMMessage+"wrong"+"$"+Integer.toString(i)+"$" ;
    		     }
    	     }
//    	     FE2RMMulticast mc = new FE2RMMulticast(RMMessage);
//             mc.initial();
//             mc.execute();
    	    ClusterManagerSender cma1,cma2,cma3,cma4;
 			cma1 = new ClusterManagerSender("localhost",8201,RMMessage);
 			cma2 = new ClusterManagerSender("localhost",8202,RMMessage);
 			cma3 = new ClusterManagerSender("localhost",8203,RMMessage);
 			cma4 = new ClusterManagerSender("localhost",8204,RMMessage);
 			cma1.run();
 			cma2.run();
 			cma3.run();
 			cma4.run();
 			try {
 				cma1.join();
 				cma2.join();
 				cma3.join();
 				cma4.join();
 			} catch (InterruptedException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}
              clear();
    	 return "success send the content";
	     }
	     
		return "WRONG END";
	}

	@Override
	public String transferReservation(String managerID, String PassengerID, String CurrentCity, String OtherCity) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void clear(){
		successCount=0;
		failCount=0;
		crashCount=0;
		
	     for(int i=0;i<CR.CM.length;i++){
	    	 CR.CM[i]="";
	     }
	}
}
