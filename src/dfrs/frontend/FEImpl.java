package dfrs.frontend;

import java.util.HashMap;

import dfrs.ServerInterfacePOA;
import dfrs.net.Client;
import dfrs.utils.Config;


public class FEImpl  extends ServerInterfacePOA  {

	public HashMap<String,HashMap<String,String>> recPac;
	public CompareResult CR;
	public CompareCount CC;
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
		cMServer1 = new FEServer(Config.FE_RECEIVE_SERVER_PORT_1,CR,CC);
		cMServer2 = new FEServer(Config.FE_RECEIVE_SERVER_PORT_2,CR,CC);
		cMServer3 = new FEServer(Config.FE_RECEIVE_SERVER_PORT_3,CR,CC);
		cMServer4 = new FEServer(Config.FE_RECEIVE_SERVER_PORT_4,CR,CC);
		
		cMServer1.start();
		cMServer2.start();
		cMServer3.start();
		cMServer4.start();
		clear();
		System.out.println("FEImpl afterinitial ...");
	}
	
	@Override
	public String bookFlight(String departure, String firstName, String lastName, String address, String phoneNumber,
			String destination, String flightClass, String flightDate) {
		String content ="1"+"$"+departure
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
	public String getBookedFlightCount(String managerID, String recordType) {
		String content ="3"+"$"+managerID
				+"$"+recordType+"$";
		
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
	     
	     String CM1 = "";
	     String CM2 = "";
	     String CM3 = "";
	     String CM4 = "";
	     String[] RMReply = new String[4];
	     
	     //ArrayList<Integer> crashList = new ArrayList<Integer>();
	     for(int i=0;i<CC.recordCount.length;i++)
	     {
	    	 if(CC.recordCount[i][0]==-1)
	    	 {
		    	 System.out.println("CC.recordCount"+i+" crash");
		    	 //crashList.add(i);
		    	 RMReply[i]= "crash";
		     }
	     }
	     

	     
	     for(int i=0;i<CC.recordCount[0].length;i++)
	     {
	    	 CM1=Integer.toString(CC.recordCount[0][i])+"$";
	     }
	     
	     for(int i=0;i<CC.recordCount[0].length;i++)
	     {
	    	 CM2=Integer.toString(CC.recordCount[1][i])+"$";
	     }
	     
	     for(int i=0;i<CC.recordCount[0].length;i++)
	     {
	    	 CM3=Integer.toString(CC.recordCount[2][i])+"$";
	     }
	     
	     for(int i=0;i<CC.recordCount[0].length;i++)
	     {
	    	 CM4=Integer.toString(CC.recordCount[3][i])+"$";
	     }
	     
	     HashMap<String,Integer> hCount = new HashMap<String,Integer>();
	     
	     if(!RMReply[0].equals("crash"))
	     {
	    	 hCount.put(CM1, 1);
	     }
	     if(!RMReply[1].equals("crash")){
		     if(hCount.containsKey(CM2)){
		    	 hCount.put(CM2,( hCount.get(CM2)+1));
		     }else{
		    	 hCount.put(CM2, 1);
		     }
	     }
	     if(!RMReply[2].equals("crash")){
		     if(hCount.containsKey(CM3)){
		    	 hCount.put(CM3,( hCount.get(CM3)+1));
		     }else{
		    	 hCount.put(CM3, 1);
		     }
	     }
	     if(!RMReply[3].equals("crash")){
		     if(hCount.containsKey(CM4)){
		    	 hCount.put(CM4,( hCount.get(CM4)+1));
		     }else{
		    	 hCount.put(CM4, 1);
		     }
	     }
	     
	     int max = 0;
	     String answer="";
	     
	     for (String key : hCount.keySet()) {  
	    	 if(hCount.get(key)>max){
	    		 max = hCount.get(key);
	    		 answer = key;
	    	 }
	      System.out.println("key= "+ key + " and value= " + hCount.get(key));  
	     }  
	     
	     String RMMessage="";
	     
	     if(RMReply[0].equals("crash")){
	     RMMessage=RMMessage+"crash"+"$"+0+"$" ;
	     }else 
	     if(CM1.equals(answer)){
	    	 RMMessage=RMMessage+"correct"+"$"+0+"$" ;
	     }else{
	    	 RMMessage=RMMessage+"wrong"+"$"+0+"$" ;
	     }
	     
	     if(RMReply[1].equals("crash")){
	     RMMessage=RMMessage+"crash"+"$"+1+"$" ;
	     }else 
	     if(CM2.equals(answer)){
	    	 RMMessage=RMMessage+"correct"+"$"+1+"$" ;
	     }else{
	    	 RMMessage=RMMessage+"wrong"+"$"+1+"$" ;
	     }
	     
	     if(RMReply[2].equals("crash")){
	     RMMessage=RMMessage+"crash"+"$"+2+"$" ;
	     }else 
	     if(CM3.equals(answer)){
	    	 RMMessage=RMMessage+"correct"+"$"+2+"$" ;
	     }else{
	    	 RMMessage=RMMessage+"wrong"+"$"+2+"$" ;
	     }
	     
	     if(RMReply[3].equals("crash")){
	     RMMessage=RMMessage+"crash"+"$"+3+"$" ;
	     }else 
	     if(CM4.equals(answer)){
	    	 RMMessage=RMMessage+"correct"+"$"+3+"$" ;
	     }else{
	    	 RMMessage=RMMessage+"wrong"+"$"+3+"$" ;
	     }
	     
    	     FE2RMMulticast mc = new FE2RMMulticast(RMMessage);
             mc.initial();
             mc.execute();
             clear();
             
             for(int i = 0; i <CC.recordCount.length;i++){
            	 for(int j = 0; j <CC.recordCount[0].length;j++){
            		 CC.recordCount[i][j]=-1;
            	 }
             }

    	 return answer;

	}

	@Override
	public String editFlightRecord(String managerID, String recordID, String fieldName, String newValue) {
		//EDIT
		String content ="2"+"$"+managerID
				+"$"+recordID
				+"$"+fieldName
				+"$"+newValue
				+"$";
//		String content ="2"+"$"+currentCity
//				+"$"+managerID
//				+"$"+destination
//				+"$"+flightDate
//				+"$"+Integer.toString(economy)
//				+"$"+Integer.toString(business)
//				+"$"+Integer.toString(firstclass)+"$";
		//END
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
	public String transferReservation(String managerID, String passengerID, String currentCity, String otherCity) {
		String content ="4"+"$"+managerID
				+"$"+passengerID
				+"$"+currentCity
				+"$"+otherCity+"$";
		
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
