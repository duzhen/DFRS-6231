package com.dlms.sequencer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.rudp.ReliableServerSocket;
import net.rudp.ReliableSocket;
import net.rudp.ReliableSocketOutputStream;

/**
 *
 * @author yucunli
 */
public class Sequencer {
    private HashMap<String, Integer> bankA_portMap;
    private HashMap<String, Integer> bankB_portMap;
    private HashMap<String, Integer> bankC_portMap;
    
    private HashMap<String, String> bankA_hostMap;
    private HashMap<String, String> bankB_hostMap;
    private HashMap<String, String> bankC_hostMap;
    
    private int Sequencer_port;
    
    int aSequence = 0;
    int bSequence = 0;
    int cSequence = 0;
    Queue<String> aSeqQueue = new LinkedList<String>();
    Queue<String> bSeqQueue = new LinkedList<String>();
    Queue<String> cSeqQueue = new LinkedList<String>();
    
    public Sequencer(HashMap<String, Integer> bankA_portMap, HashMap<String, Integer> bankB_portMap, 
            HashMap<String, Integer> bankC_portMap, HashMap<String, String> bankA_hostMap, 
            HashMap<String, String> bankB_hostMap, HashMap<String, String> bankC_hostMap, int sequencer_port){
        this.bankA_portMap = bankA_portMap;
        this.bankB_portMap = bankB_portMap;
        this.bankC_portMap = bankC_portMap;
        
        this.bankA_hostMap = bankA_hostMap;
        this.bankB_hostMap = bankB_hostMap;
        this.bankC_hostMap = bankC_hostMap;

        this.Sequencer_port = sequencer_port;
    }
    
    public class SequenceQueueConsumer extends Thread{
        String bank;
        public SequenceQueueConsumer(String bank){
            this.bank = bank;
        }
        
        @Override
        public void run() {
            Queue<String> seqQueue;
            while (true) {
                
                if (bank.equals("A")){
                    seqQueue = aSeqQueue;
                }else if(bank.equals("B")){
                    seqQueue = bSeqQueue;
                }else if(bank.equals("C")){
                    seqQueue = cSeqQueue;
                }else{
                    break;
                }
                
                if (seqQueue.size() != 0){
                    String sendMessage = seqQueue.peek();
                    String bankName = extractBank(sendMessage.split("%")[1]);
                    
                    if(bankName.equals("A")){
                        try {
                            Client rm1_client = new Client(bankA_hostMap.get("RM1"), bankA_portMap.get("RM1"), sendMessage);
                            Client rm2_client = new Client(bankA_hostMap.get("RM2"), bankA_portMap.get("RM2"), sendMessage);
                            Client rm3_client = new Client(bankA_hostMap.get("RM3"), bankA_portMap.get("RM3"), sendMessage);
                            
                            rm1_client.start();
                            rm2_client.start();
                            rm3_client.start();
                            
                            rm1_client.join();
                            rm2_client.join();
                            rm3_client.join();
                            
                            if(rm1_client.equals("success") && rm1_client.equals("success") && rm1_client.equals("success")){
                                seqQueue.poll();
                            }
                        } catch (InterruptedException ex) {
                            
                        }
                        
                    }else if(bankName.equals("B")){
                        try {
                            Client rm1_client = new Client(bankB_hostMap.get("RM1"), bankB_portMap.get("RM1"), sendMessage);
                            Client rm2_client = new Client(bankB_hostMap.get("RM2"), bankB_portMap.get("RM2"), sendMessage);
                            Client rm3_client = new Client(bankB_hostMap.get("RM3"), bankB_portMap.get("RM3"), sendMessage);
                            
                            rm1_client.start();
                            rm2_client.start();
                            rm3_client.start();
                            
                            rm1_client.join();
                            rm2_client.join();
                            rm3_client.join();
                            
                            if(rm1_client.equals("success") && rm1_client.equals("success") && rm1_client.equals("success")){
                                seqQueue.poll();
                            }
                        } catch (InterruptedException ex) {
                            
                        }
                    }else if(bankName.equals("C")){
                        try {
                            Client rm1_client = new Client(bankC_hostMap.get("RM1"), bankC_portMap.get("RM1"), sendMessage);
                            Client rm2_client = new Client(bankC_hostMap.get("RM2"), bankC_portMap.get("RM2"), sendMessage);
                            Client rm3_client = new Client(bankC_hostMap.get("RM3"), bankC_portMap.get("RM3"), sendMessage);
                            
                            rm1_client.start();
                            rm2_client.start();
                            rm3_client.start();
                            
                            rm1_client.join();
                            rm2_client.join();
                            rm3_client.join();
                            
                            if(rm1_client.equals("success") && rm1_client.equals("success") && rm1_client.equals("success")){
                                seqQueue.poll();
                            }
                        } catch (InterruptedException ex) {
                            
                        }
                    }
                }
            }
        }
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
                    String msg = inFromClient.readLine();
                    System.out.println("Sequencer: "+msg);
                    
                    String bankName = extractBank(msg);
                    
                    String sequenceId = "";
                    if(bankName.equals("A")){
                        aSequence++;
                        sequenceId = "A" + aSequence;
                        synchronized(aSeqQueue){
                            aSeqQueue.add(sequenceId+"%"+msg);
                        }
                    }else if(bankName.equals("B")){
                        bSequence++;
                        sequenceId = "B" + aSequence;
                        synchronized(bSeqQueue){
                            bSeqQueue.add(sequenceId+"%"+msg);
                        }
                    }else if(bankName.equals("C")){
                        cSequence++;
                        sequenceId = "C" + cSequence;
                        synchronized(cSeqQueue){
                            cSeqQueue.add(sequenceId+"%"+msg);
                        }
                    }else{
                        System.out.println("Wrong bank name");
                    }
                    
                    // message send back to client
                    ReliableSocketOutputStream outToClient = (ReliableSocketOutputStream) connectionSocket.getOutputStream();
                    PrintWriter outputBuffer = new PrintWriter(outToClient);
                    outputBuffer.println(sequenceId);
                    outputBuffer.flush();
                    
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
                outputBuffer.println(content);
                outputBuffer.flush();
                
                // message receive from server
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                result = inFromServer.readLine();

                clientSocket.close();

            } catch (IOException ex) {

            }
        }
    }
    
    public String extractBank(String msg){
        String[] requestArr = msg.split("#");
        String requestName = requestArr[0];
        String[] requestParaArr = requestArr[1].split(",");

        String bankName = "";

        if(requestName.equals("openAccount")){
            bankName = requestParaArr[0];
        }else if(requestName.equals("getLoan")){
            bankName = requestParaArr[0];
        }else if(requestName.equals("transferLoan")){
            bankName = requestParaArr[1];
        }else if(requestName.equals("delayPayment")){
            bankName = requestParaArr[0];
        }else if(requestName.equals("printCustomerInfo")){
            bankName = requestParaArr[0];
        }
        
        return bankName;
    }
}
