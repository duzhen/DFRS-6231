package dfrs.frontend;

import java.util.HashMap;

import dfrs.ServerInterfacePOA;
import dfrs.net.Client;
import dfrs.utils.Config;
import net.rudp.ReliableSocket;


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
		CC = new CompareCount();
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
	public synchronized String bookFlight(String departure, String firstName, String lastName, String address, String phoneNumber,
			String destination, String flightDate, String flightClass) {
		String content ="1"+"$"+departure
				+"$"+firstName
				+"$"+lastName
				+"$"+address
				+"$"+phoneNumber
				+"$"+destination
				+"$"+flightDate
				+"$"+flightClass+"$";
		
		System.out.println("Request:"+content);
		ReliableSocket socket = FESender.getInstance().getSocket(host, port);
        Client client = new Client(socket, content);
        client.run();
        try {
			client.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        int count = 0;
        for(int i=0;i<100;i++) {
        	for(int j=0;j<CR.CM.length;j++){
   	    	 if(!CR.CM[i].equals("")){
   	    		count++;
   		     }
   	     }
    	if(count >= 3) {
    		break;
    	}
        	try {
				Thread.sleep(2*60*10);
			} catch (InterruptedException e) {
				e.printStackTrace();
		}
        }
	     for(int i=0;i<CR.CM.length;i++){
	    	 if(CR.CM[i].equals("")||CR.CM[i].equals("error")){
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
    		     }else if(CR.CM[i].equals("")||CR.CM[i].equals("error")){
    		    	 RMMessage=RMMessage+"crash"+"$"+Integer.toString(i)+"$" ;
    		     }
    	     }
    	     FE2RMMulticast mc = new FE2RMMulticast(RMMessage);
             mc.initial();
             mc.execute();
             clear();
    	     return "success";
	     }else 
    	 if(successCount<failCount)
    	 {
    		 String RMMessage="";
    	     for(int i=0;i<CR.CM.length;i++){
    	    	 if(CR.CM[i].equals("fail")){
    	    		 RMMessage=RMMessage+"correct"+"$"+Integer.toString(i)+"$" ;
    		     }else if(CR.CM[i].equals("success")){
    		    	 RMMessage=RMMessage+"wrong"+"$"+Integer.toString(i)+"$" ;
    		     }else if(CR.CM[i].equals("")||CR.CM[i].equals("error")){
    		    	 RMMessage=RMMessage+"crash"+"$"+Integer.toString(i)+"$" ;
    		     }
    	     }
    	     FE2RMMulticast mc = new FE2RMMulticast(RMMessage);
             mc.initial();
             mc.execute();
             clear();
    	 return "fail";
	     }
	     
		return "WRONG END";
	}

	@Override
	public synchronized String getBookedFlightCount(String managerID, String recordType) {
		String content ="3"+"$"+managerID
				+"$"+recordType+"$";
		
		System.out.println("Request:"+content);
		ReliableSocket socket = FESender.getInstance().getSocket(host, port);
        Client client = new Client(socket, content);
        client.run();
        try {
			client.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        int count = 0;
        for(int i=0;i<100;i++) {
        	for(int j=0;j<4;j++){
   	    	 if(CC.recordCount[j][0]!=-1){
   	    		count++;
   		     }
   	     }
    	if(count >= 3) {
    		break;
    	}
        	try {
				Thread.sleep(2*60*10);
			} catch (InterruptedException e) {
				e.printStackTrace();
		}
        }
	     
	     String CM1 = "";
	     String CM2 = "";
	     String CM3 = "";
	     String CM4 = "";
	     String[] RMReply = new String[4];
	     
	     //ArrayList<Integer> crashList = new ArrayList<Integer>();
	     //print all result
	     
	     for(int i=0;i<CC.recordCount.length;i++)
	     {
	    	 for(int j=0;j<CC.recordCount[0].length;j++)
	    	 {
		    	 System.out.print(CC.recordCount[i][j]+" ");
		    	 //crashList.add(i);
		    	 //RMReply[i]= "crash";
		     }
	    	 System.out.println(" ");
	     }
	     
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
	    	 CM1=CM1+ Integer.toString(CC.recordCount[0][i])+"$";
	     }
	     
	     for(int i=0;i<CC.recordCount[0].length;i++)
	     {
	    	 CM2=CM2+ Integer.toString(CC.recordCount[1][i])+"$";
	     }
	     
	     for(int i=0;i<CC.recordCount[0].length;i++)
	     {
	    	 CM3=CM3+Integer.toString(CC.recordCount[2][i])+"$";
	     }
	     
	     for(int i=0;i<CC.recordCount[0].length;i++)
	     {
	    	 CM4=CM4+Integer.toString(CC.recordCount[3][i])+"$";
	     }
	     
	     HashMap<String,Integer> hCount = new HashMap<String,Integer>();
	     
	     if(!"crash".equals(RMReply[0]))
	     {
	    	 hCount.put(CM1, 1);
	     }
	     
	     if(!"crash".equals(RMReply[1])){
		     if(hCount.containsKey(CM2)){
		    	 int sum=hCount.get(CM2)+1;
		    	 hCount.put(CM2,sum);
		     }else{
		    	 hCount.put(CM2, 1);
		     }
	     }
	     if(!"crash".equals(RMReply[2])){
		     if(hCount.containsKey(CM3)){
		    	 int sum=hCount.get(CM3)+1;
		    	 hCount.put(CM3,sum);
		     }else{
		    	 hCount.put(CM3, 1);
		     }
	     }
	     if(!"crash".equals(RMReply[3])){
		     if(hCount.containsKey(CM4)){
		    	 int sum=hCount.get(CM4)+1;
		    	 hCount.put(CM4,sum);
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
	     String[] answerSplit = answer.split("\\$");
	     if(answerSplit.length<3) {
	    	 System.out.println("answer:"+answer);
	     }
	     String reply = "MTL "+answerSplit[0] + ",WST "+answerSplit[1] +",NDL "  +answerSplit[2]; 
	     
	     if("crash".equals(RMReply[0])){
	     RMMessage=RMMessage+"crash"+"$"+0+"$" ;
	     }else 
	     if(CM1.equals(answer)){
	    	 RMMessage=RMMessage+"correct"+"$"+0+"$" ;
	     }else{
	    	 RMMessage=RMMessage+"wrong"+"$"+0+"$" ;
	     }
	     
	     if("crash".equals(RMReply[1])){
	     RMMessage=RMMessage+"crash"+"$"+1+"$" ;
	     }else 
	     if(CM2.equals(answer)){
	    	 RMMessage=RMMessage+"correct"+"$"+1+"$" ;
	     }else{
	    	 RMMessage=RMMessage+"wrong"+"$"+1+"$" ;
	     }
	     
	     if("crash".equals(RMReply[2])){
	     RMMessage=RMMessage+"crash"+"$"+2+"$" ;
	     }else 
	     if(CM3.equals(answer)){
	    	 RMMessage=RMMessage+"correct"+"$"+2+"$" ;
	     }else{
	    	 RMMessage=RMMessage+"wrong"+"$"+2+"$" ;
	     }
	     
	     if("crash".equals(RMReply[3])){
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

    	 return reply;

	}

	@Override
	public synchronized String editFlightRecord(String managerID, String recordID, String fieldName, String newValue) {
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
		System.out.println("Request:"+content);
		ReliableSocket socket = FESender.getInstance().getSocket(host, port);
        Client client = new Client(socket, content);
        client.run();
        try {
			client.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        int count = 0;
        for(int i=0;i<100;i++) {
        	for(int j=0;j<CR.CM.length;j++){
   	    	 if(!CR.CM[i].equals("")){
   	    		count++;
   		     }
   	     }
    	if(count >= 3) {
    		break;
    	}
        	try {
				Thread.sleep(2*60*10);
			} catch (InterruptedException e) {
				e.printStackTrace();
		}
        }
	     //begin to receive
	     for(int i=0;i<CR.CM.length;i++){
	    	 if(CR.CM[i].equals("")||CR.CM[i].equals("error")){
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
	    	 return "success";
	     }else 
    	 if(failCount==4){
    		 clear();
	    	 return "fail";
	     }else 
    	 if(successCount>=failCount)
    	 {
    		 String RMMessage="";
    	     for(int i=0;i<CR.CM.length;i++){
    	    	 if(CR.CM[i].equals("success")){
    	    		 RMMessage=RMMessage+"correct"+"$"+Integer.toString(i)+"$" ;
    		     }else if(CR.CM[i].equals("fail")){
    		    	 RMMessage=RMMessage+"wrong"+"$"+Integer.toString(i)+"$" ;
    		     }else if(CR.CM[i].equals("")||CR.CM[i].equals("error")){
    		    	 RMMessage=RMMessage+"crash"+"$"+Integer.toString(i)+"$" ;
    		     }
    	     }
    	     FE2RMMulticast mc = new FE2RMMulticast(RMMessage);
             mc.initial();
             mc.execute();
             clear();
    	     return "success";
	     }else 
    	 if(successCount<failCount)
    	 {
    		 String RMMessage="";
    	     for(int i=0;i<CR.CM.length;i++){
    	    	 if(CR.CM[i].equals("fail")){
    	    		 RMMessage=RMMessage+"correct"+"$"+Integer.toString(i)+"$" ;
    		     }else if(CR.CM[i].equals("success")){
    		    	 RMMessage=RMMessage+"wrong"+"$"+Integer.toString(i)+"$" ;
    		     }else if(CR.CM[i].equals("")||CR.CM[i].equals("error")){
    		    	 RMMessage=RMMessage+"crash"+"$"+Integer.toString(i)+"$" ;
    		     }
    	     }
    	     FE2RMMulticast mc = new FE2RMMulticast(RMMessage);
             mc.initial();
             mc.execute();
             clear();
    	 return "fail";
	     }
	     
		return "WRONG END";
	}

	@Override
	public synchronized String transferReservation(String managerID, String passengerID, String currentCity, String otherCity) {
		String content ="4"+"$"+managerID
				+"$"+passengerID
				+"$"+currentCity
				+"$"+otherCity+"$";
		
		System.out.println("Request:"+content);
		ReliableSocket socket = FESender.getInstance().getSocket(host, port);
        Client client = new Client(socket, content);
        client.run();
        try {
			client.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        int count = 0;
        for(int i=0;i<100;i++) {
        	for(int j=0;j<CR.CM.length;j++){
   	    	 if(!CR.CM[i].equals("")){
   	    		count++;
   		     }
   	     }
    	if(count >= 3) {
    		break;
    	}
        	try {
				Thread.sleep(2*60*10);
			} catch (InterruptedException e) {
				e.printStackTrace();
		}
        }
	     for(int i=0;i<CR.CM.length;i++){
	    	 if(CR.CM[i].equals("")||CR.CM[i].equals("error")){
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
	    	 return "success";
	     }else 
    	 if(failCount==4){
    		 clear();
	    	 return "fail";
	     }else 
    	 if(successCount>=failCount)
    	 {
    		 String RMMessage="";
    	     for(int i=0;i<CR.CM.length;i++){
    	    	 if(CR.CM[i].equals("success")){
    	    		 RMMessage=RMMessage+"correct"+"$"+Integer.toString(i)+"$" ;
    		     }else if(CR.CM[i].equals("fail")){
    		    	 RMMessage=RMMessage+"wrong"+"$"+Integer.toString(i)+"$" ;
    		     }else if(CR.CM[i].equals("")||CR.CM[i].equals("error")){
    		    	 RMMessage=RMMessage+"crash"+"$"+Integer.toString(i)+"$" ;
    		     }
    	     }
    	     FE2RMMulticast mc = new FE2RMMulticast(RMMessage);
             mc.initial();
             mc.execute();
             clear();
    	     return "success";
	     }else 
    	 if(successCount<failCount)
    	 {
    		 String RMMessage="";
    	     for(int i=0;i<CR.CM.length;i++){
    	    	 if(CR.CM[i].equals("fail")){
    	    		 RMMessage=RMMessage+"correct"+"$"+Integer.toString(i)+"$" ;
    		     }else if(CR.CM[i].equals("success")){
    		    	 RMMessage=RMMessage+"wrong"+"$"+Integer.toString(i)+"$" ;
    		     }else if(CR.CM[i].equals("")||CR.CM[i].equals("error")){
    		    	 RMMessage=RMMessage+"crash"+"$"+Integer.toString(i)+"$" ;
    		     }
    	     }
    	     FE2RMMulticast mc = new FE2RMMulticast(RMMessage);
             mc.initial();
             mc.execute();
             clear();
    	 return "fail";
	     }
	     
		return "WRONG END";
	}
	
	private synchronized void clear(){
		successCount=0;
		failCount=0;
		crashCount=0;
		
	     for(int i=0;i<CR.CM.length;i++){
	    	 CR.CM[i]="";
	     }
	}
}
