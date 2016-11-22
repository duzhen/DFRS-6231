package dfrs.frontend;

import java.util.HashMap;

import dfrs.ServerInterfacePOA;
import dfrs.net.Client;
import dfrs.utils.Config;


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
		System.out.println("FEImpl initial ...");
		this.host = host;
		this.port = portone;
		CR = new CompareResult();
		// FE reveive cluster manager
		cMServer1 = new FEServer(Config.FE_RECEIVE_SERVER_PORT_1,CR);
		cMServer2 = new FEServer(Config.FE_RECEIVE_SERVER_PORT_2,CR);
		cMServer3 = new FEServer(Config.FE_RECEIVE_SERVER_PORT_3,CR);
		cMServer4 = new FEServer(Config.FE_RECEIVE_SERVER_PORT_4,CR);
		
		cMServer1.start();
		cMServer2.start();
		cMServer3.start();
		cMServer4.start();
		clear();
		System.out.println("FEImpl afterinitial ...");
	}

	@Override
	public String bookFlight(String currentCity, String firstName, String lastName, String address, String phoneNumber,
			String destination, String flightClass, String flightDate) {
		String content ="1"+"$"+currentCity
				+"$"+firstName
				+"$"+lastName
				+"$"+address
				+"$"+phoneNumber
				+"$"+destination
				+"$"+flightClass
				+"$"+flightDate+"$";
		
		System.out.println("client "+port+" connect string");
		System.out.println(content);
        Client client = new Client(host, port, content);
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
	     if(successCount==4){
	    	 clear();
	    	 return "success send the content";
	     }else 
    	 if(failCount==4){
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
    		     }else if(CR.CM[i].equals("")){
    		    	 RMMessage=RMMessage+"crash"+"$"+Integer.toString(i)+"$" ;
    		     }
    	     }
    	     FE2RMMulticast mc = new FE2RMMulticast(RMMessage);
             mc.initial();
             mc.execute();
             clear();
    	     return "success send the content";
	     }else 
    	 if(successCount<failCount)
    	 {
    		 String RMMessage="";
    	     for(int i=0;i<CR.CM.length;i++){
    	    	 if(CR.CM[i].equals("fail")){
    	    		 RMMessage=RMMessage+"correct"+"$"+Integer.toString(i)+"$" ;
    		     }else if(CR.CM[i].equals("success")){
    		    	 RMMessage=RMMessage+"wrong"+"$"+Integer.toString(i)+"$" ;
    		     }else if(CR.CM[i].equals("")){
    		    	 RMMessage=RMMessage+"crash"+"$"+Integer.toString(i)+"$" ;
    		     }
    	     }
    	     FE2RMMulticast mc = new FE2RMMulticast(RMMessage);
             mc.initial();
             mc.execute();
             clear();
    	 return "success send the content";
	     }
	     
		return "WRONG END";
	}

	@Override
	public String getBookedFlightCount(String currentCity, String managerID) {
		String content ="3"+"$"+currentCity
				+"$"+currentCity
				+"$"+managerID+"$";
		
		System.out.println("client "+port+" connect string");
		System.out.println(content);
        Client client = new Client(host, port, content);
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
	     if(successCount==4){
	    	 clear();
	    	 return "success send the content";
	     }else 
    	 if(failCount==4){
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
    		     }else if(CR.CM[i].equals("")){
    		    	 RMMessage=RMMessage+"crash"+"$"+Integer.toString(i)+"$" ;
    		     }
    	     }
    	     FE2RMMulticast mc = new FE2RMMulticast(RMMessage);
             mc.initial();
             mc.execute();
             clear();
    	     return "success send the content";
	     }else 
    	 if(successCount<failCount)
    	 {
    		 String RMMessage="";
    	     for(int i=0;i<CR.CM.length;i++){
    	    	 if(CR.CM[i].equals("fail")){
    	    		 RMMessage=RMMessage+"correct"+"$"+Integer.toString(i)+"$" ;
    		     }else if(CR.CM[i].equals("success")){
    		    	 RMMessage=RMMessage+"wrong"+"$"+Integer.toString(i)+"$" ;
    		     }else if(CR.CM[i].equals("")){
    		    	 RMMessage=RMMessage+"crash"+"$"+Integer.toString(i)+"$" ;
    		     }
    	     }
    	     FE2RMMulticast mc = new FE2RMMulticast(RMMessage);
             mc.initial();
             mc.execute();
             clear();
    	 return "success send the content";
	     }
	     
		return "WRONG END";
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
        Client client = new Client(host, port, content);
        client.run();
        try {
			client.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	     try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
		 }
	     //begin to receive
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
	     if(successCount==4){
	    	 clear();
	    	 return "success send the content";
	     }else 
    	 if(failCount==4){
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
    		     }else if(CR.CM[i].equals("")){
    		    	 RMMessage=RMMessage+"crash"+"$"+Integer.toString(i)+"$" ;
    		     }
    	     }
    	     FE2RMMulticast mc = new FE2RMMulticast(RMMessage);
             mc.initial();
             mc.execute();
             clear();
    	     return "success send the content";
	     }else 
    	 if(successCount<failCount)
    	 {
    		 String RMMessage="";
    	     for(int i=0;i<CR.CM.length;i++){
    	    	 if(CR.CM[i].equals("fail")){
    	    		 RMMessage=RMMessage+"correct"+"$"+Integer.toString(i)+"$" ;
    		     }else if(CR.CM[i].equals("success")){
    		    	 RMMessage=RMMessage+"wrong"+"$"+Integer.toString(i)+"$" ;
    		     }else if(CR.CM[i].equals("")){
    		    	 RMMessage=RMMessage+"crash"+"$"+Integer.toString(i)+"$" ;
    		     }
    	     }
    	     FE2RMMulticast mc = new FE2RMMulticast(RMMessage);
             mc.initial();
             mc.execute();
             clear();
    	 return "success send the content";
	     }
	     
		return "WRONG END";
	}

	@Override
	public String transferReservation(String managerID, String PassengerID, String CurrentCity, String OtherCity) {
		String content ="4"+"$"+managerID
				+"$"+PassengerID
				+"$"+CurrentCity
				+"$"+OtherCity+"$";
		
		System.out.println("client "+port+" connect string");
		System.out.println(content);
        Client client = new Client(host, port, content);
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
	     if(successCount==4){
	    	 clear();
	    	 return "success send the content";
	     }else 
    	 if(failCount==4){
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
    		     }else if(CR.CM[i].equals("")){
    		    	 RMMessage=RMMessage+"crash"+"$"+Integer.toString(i)+"$" ;
    		     }
    	     }
    	     FE2RMMulticast mc = new FE2RMMulticast(RMMessage);
             mc.initial();
             mc.execute();
             clear();
    	     return "success send the content";
	     }else 
    	 if(successCount<failCount)
    	 {
    		 String RMMessage="";
    	     for(int i=0;i<CR.CM.length;i++){
    	    	 if(CR.CM[i].equals("fail")){
    	    		 RMMessage=RMMessage+"correct"+"$"+Integer.toString(i)+"$" ;
    		     }else if(CR.CM[i].equals("success")){
    		    	 RMMessage=RMMessage+"wrong"+"$"+Integer.toString(i)+"$" ;
    		     }else if(CR.CM[i].equals("")){
    		    	 RMMessage=RMMessage+"crash"+"$"+Integer.toString(i)+"$" ;
    		     }
    	     }
    	     FE2RMMulticast mc = new FE2RMMulticast(RMMessage);
             mc.initial();
             mc.execute();
             clear();
    	 return "success send the content";
	     }
	     
		return "WRONG END";
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
