package com.dlms.frontend;

import DLMS.dlmsPOA;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.HashMap;
import java.util.Hashtable;
import net.rudp.ReliableServerSocket;
import net.rudp.ReliableSocket;
import net.rudp.ReliableSocketOutputStream;

/**
 *
 * @author yucunli
 */
public class Frontend extends dlmsPOA{
    
    private String SequencerHost;
    private int SequencerPortNumber;
    private HashMap<String, Integer> RMPort_map;
    private HashMap<String, String> RMHost_map;
    private int FEport;
    
    public Hashtable<String, Hashtable<String, String>> ResultMap = new Hashtable<String, Hashtable<String, String>>();
    
    public Frontend(String SequencerHost, int sequencer_port, HashMap<String, Integer> RMPort_map, HashMap<String, String> RMHost_map, int fe_port){
        this.SequencerHost = SequencerHost;
        this.SequencerPortNumber = sequencer_port;
        this.RMPort_map = RMPort_map;
        this.RMHost_map = RMHost_map;
        this.FEport = fe_port;
        
        Server receiver = new Server(FEport);
        receiver.start();
    }

    @Override
    public String openAccount(String Bank, String fName, String lName, String email, String phoneNumber, String password) {
        Client client = new Client(SequencerHost, SequencerPortNumber, "openAccount#" + Bank + "," + fName + "," + lName + "," + email + ","
                + phoneNumber + "," + password + "#");
        client.run();
        
        String request_id = client.result;
        
        return process(request_id);
    }

    @Override
    public String getLoan(String Bank, String accountNumber, String password, String loanAmount) {
        Client client = new Client(SequencerHost, SequencerPortNumber, "getLoan#" + Bank + "," + accountNumber + "," + password + "," + loanAmount + "#");
        client.run();
        
        String request_id = client.result;
        
        return process(request_id);
    }
    
    @Override
    public String transferLoan(String loanID, String currentBank, String otherBank) {
        Client client = new Client(SequencerHost, SequencerPortNumber, "transferLoan#" + loanID + "," + currentBank + "," + otherBank + "#");
        client.run();
        
        String request_id = client.result;
        
        return process(request_id);
    }

    @Override
    public String delayPayment(String Bank, String loanID, String currentD, String newD) {
        Client client = new Client(SequencerHost, SequencerPortNumber, "delayPayment#" + Bank + "," + loanID + "," + currentD + "," + newD + "#");
        client.run();
        
        String request_id = client.result;
        
        return process(request_id);
    }

    @Override
    public String printCustomerInfo(String Bank) {
        Client client = new Client(SequencerHost, SequencerPortNumber, "printCustomerInfo#" + Bank + "#");
        client.run();
        
        String request_id = client.result;
        
        return process(request_id);
    }
    
    
    public class Server extends Thread{
        int port;
        
        public Server(int port){
            this.port = port;
        }
        
        @Override
        public void run(){
            try {
                ReliableServerSocket serverSocket = new ReliableServerSocket(port);
                
                boolean isStopped = false;
                while(!isStopped){
                    Socket connectionSocket = serverSocket.accept();
                    
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    String sentence = inFromClient.readLine();
                    System.out.println("Front End Server: " + sentence);
                    
                    //***************************
                    //message processing...
                    //***************************
                    String[] responseArr = sentence.split("%");
                    String sequenceId = responseArr[0];
                    String[] rmArr = responseArr[1].split("#");
                    String rm_port = rmArr[0];
                    String result = rmArr[1];

                    if (ResultMap.get(sequenceId) == null) {
                        Hashtable<String, String> rm_result_map = new Hashtable<String, String>();
                        rm_result_map.put(rm_port, result);
                        ResultMap.put(sequenceId, rm_result_map);
                    } else {
                        ResultMap.get(sequenceId).put(rm_port, result);
                    }
                    
                    connectionSocket.close();
                }
                
                serverSocket.close();

            } catch (IOException ex) {

            } 
        }
    }
    
    public class Client extends Thread{
        
        String host;
        int port;
        String content;
        
        public String result;
        
        public Client(String host, int port, String content){
            this.host = host;
            this.port = port;
            this.content = content;
        }
        
         @Override
        public void run(){
            try {
                ReliableSocket clientSocket = new ReliableSocket(host, port);
                ReliableSocketOutputStream outToServer = (ReliableSocketOutputStream) clientSocket.getOutputStream();
                PrintWriter outputBuffer = new PrintWriter(outToServer);
                
                if(content.equals("Restart")){
                    
                    outputBuffer.println(content);
                    outputBuffer.flush();
                }else if(content.startsWith("openAccount") || content.startsWith("transferLoan") ||
                        content.startsWith("getLoan") || content.startsWith("delayPayment") ||
                        content.startsWith("printCustomerInfo")){
                
                    outputBuffer.println(content);
                    outputBuffer.flush();
                    
                    // message receive from server
                    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String sentence = inFromServer.readLine();
                    System.out.println("FrontEnd Client: " + sentence);
                    result = sentence;
                }else{
                    System.out.println("Wrong Front End Sending Messages");
                }
                
                clientSocket.close();
            } catch (IOException ex) {

            }
        }
    }
    
    public String process(String request_id){
        long startTime = System.currentTimeMillis();
        //There is no result from bank server
        while (ResultMap.get(request_id) == null && (System.currentTimeMillis() - startTime) < 60000) {
            try {
                sleep(5000);

            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
        
        while (ResultMap.get(request_id).size() < 4 && (System.currentTimeMillis() - startTime) < 60000) {
        }
        
        // Time out problem
        if ((System.currentTimeMillis() - startTime) > 60000){
            Hashtable<String, String> re_map = ResultMap.get(request_id);
            String result = "";
            if(re_map.get(Integer.toString(RMPort_map.get("RM1"))) == null){
                Client rm1_client = new Client(RMHost_map.get("RM1"), RMPort_map.get("RM1"), "Restart");
                rm1_client.start();
            }else{
                result = re_map.get(Integer.toString(RMPort_map.get("RM1")));
            }
            
            if(re_map.get(Integer.toString(RMPort_map.get("RM2"))) == null){
                Client rm2_client = new Client(RMHost_map.get("RM2"), RMPort_map.get("RM2"), "Restart");
                rm2_client.start();
            }else{
                result = re_map.get(Integer.toString(RMPort_map.get("RM2")));
            }
            
            if(re_map.get(Integer.toString(RMPort_map.get("RM3"))) == null){
                Client rm3_client = new Client(RMHost_map.get("RM3"), RMPort_map.get("RM3"), "Restart");
                rm3_client.start();
            }else{
                result = re_map.get(Integer.toString(RMPort_map.get("RM3")));
            }
            
            return result;
        }else{
            Hashtable<String, String> re_map = ResultMap.get(request_id);
            if(re_map.get(Integer.toString(RMPort_map.get("RM1"))).equals(re_map.get(Integer.toString(RMPort_map.get("RM2"))))
                    && re_map.get(Integer.toString(RMPort_map.get("RM2"))).equals(re_map.get(Integer.toString(RMPort_map.get("RM3"))))){
                return re_map.get(Integer.toString(RMPort_map.get("RM1")));
            }else{
                return " Inconsistence with result from bank server";
            }
        }
    }

}
