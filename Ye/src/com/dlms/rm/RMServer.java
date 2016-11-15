package com.dlms.rm;

import com.dlms.server.BankServer;
import com.dlms.model.CustomerAccount;
import com.dlms.model.Loan;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import net.rudp.ReliableServerSocket;
import net.rudp.ReliableSocket;
import net.rudp.ReliableSocketOutputStream;

/**
 *
 * @author yucunli
 */
public class RMServer {
    public HashMap<String, Integer> port_map;
    
    public HashMap<String, BankServer> BankServantMap;
    
    private int[] other_rm;
    private String[] other_rm_host;
    
    private int RM_port, FE_port;
    
    public RMServer(int bankA_port, int bankB_port, int bankC_port, int rm_port, int[] other_rm_port, int fe_port) {
        //set each bank port
        port_map = new HashMap<String, Integer>();
        port_map.put("A", bankA_port);
        port_map.put("B", bankB_port);
        port_map.put("C", bankC_port);
        //set other rm array
        other_rm = other_rm_port;

        RM_port = rm_port;
        FE_port = fe_port;

        //3 bank servants
        BankServantMap = new HashMap<String, BankServer>();
        //BankServant para: Bank port, RM_port, FE_port
        BankServantMap.put("A", new BankServer("A", port_map, RM_port, FE_port, "A0"));
        BankServantMap.put("B", new BankServer("B", port_map, RM_port, FE_port, "B0"));
        BankServantMap.put("C", new BankServer("C", port_map, RM_port, FE_port, "C0"));
        
        Server server = new Server(RM_port);
        server.start();
    }
    
    public void renewBankServant(){
        try {
            //close listening port
            Client bankA = new Client("localhost", port_map.get("A"), "shutdown");
            Client bankB = new Client("localhost", port_map.get("B"), "shutdown");
            Client bankC = new Client("localhost", port_map.get("C"), "shutdown");
            
            bankA.start();
            bankB.start();
            bankC.start();
            
            bankA.join();
            bankB.join();
            bankC.join();
            
            sleep(10000);
            
            BankServantMap.remove("A");
            BankServantMap.remove("B");
            BankServantMap.remove("C");
            
            BankServantMap.put("A", new BankServer("A", port_map, RM_port, FE_port, "A0"));
            BankServantMap.put("B", new BankServer("B", port_map, RM_port, FE_port, "A0"));
            BankServantMap.put("C", new BankServer("C", port_map, RM_port, FE_port, "A0"));
        } catch (InterruptedException ex) {
            
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
                
                clientSocket.close();

            } catch (IOException ex) {

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
                    if (msg.contains("ASK")){
                        String hash_data = hashToString2(RMServer.this);
                        String sendData = hash_data;
                        
                        ReliableSocketOutputStream outToClient = (ReliableSocketOutputStream) connectionSocket.getOutputStream();
                        PrintWriter outputBuffer = new PrintWriter(outToClient);
                        outputBuffer.println(sendData);
                        outputBuffer.flush();
                    }else if(msg.equals("Restart")){
                        Client client = new Client(other_rm_host[0],other_rm[0],"ASK");
                        client.run();
                        
                        renewBankServant();
                        
                        stringToHash2(client.result, RMServer.this);
                        
                    }
                    
                    connectionSocket.close();
                }
                
                serverSocket.close();

            } catch (IOException ex) {

            } 
        }
    }
    
    
    public void stringToHash2(String s, RMServer rm) {
        String banks[] = s.split("!");

        if (!banks[0].equals("@")) {
            String elements[] = banks[0].split("@");
            String customers[] = elements[0].split(";");
            String loans[] = {};
            if (elements.length > 1) {
                loans = elements[1].split(";");
            }
            
            rm.BankServantMap.get("A").refreshHashMap();

            for (int i = 0; i < customers.length; i++) {
                String token[] = customers[i].split(",");
                CustomerAccount customer = new CustomerAccount(token[1].trim(), token[2].trim(), token[3].trim(), token[4].trim(), token[5].trim(), 1000);
                String key = Character.toString(token[1].trim().charAt(0));
                ArrayList<CustomerAccount> customerList = rm.BankServantMap.get("A").account_HashMap.get(key);
                customerList.add(customer);
                rm.BankServantMap.get("A").account_HashMap.put(key, customerList);
            }
            for (int i = 0; i < loans.length; i++) {
                String token[] = loans[i].split(",");
                Loan loan = new Loan(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim());
                String key = token[0].trim();
                rm.BankServantMap.get("A").loan_HashMap.put(key, loan);
            }
        }

        if (!banks[1].equals("@")) {
            String elements2[] = banks[1].split("@");
            String customers2[] = elements2[0].split(";");
            String loans2[] = {};
            if (elements2.length > 1){
                loans2 = elements2[1].split(";");
            }
            
            rm.BankServantMap.get("B").refreshHashMap();
            
            for (int i = 0; i < customers2.length; i++) {
                String token[] = customers2[i].split(",");
                CustomerAccount customer = new CustomerAccount(token[1].trim(), token[2].trim(), token[3].trim(), token[4].trim(), token[5].trim(), 1000);
                String key = Character.toString(token[1].trim().charAt(0));
                ArrayList<CustomerAccount> customerList = rm.BankServantMap.get("B").account_HashMap.get(key);
                customerList.add(customer);
                rm.BankServantMap.get("B").account_HashMap.put(key, customerList);
            }
            for (int i = 0; i < loans2.length; i++) {
                String token[] = loans2[i].split(",");
                Loan loan = new Loan(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim());
                String key = token[0].trim();
                rm.BankServantMap.get("B").loan_HashMap.put(key, loan);
            }
        }
        
        if(!banks[2].equals("@")){
            String elements3[] = banks[0].split("@");
            String customers3[] = elements3[0].split(";");
            String loans3[] = {};
            if (customers3.length > 1){
                loans3 = elements3[1].split(";");
            }
            
            rm.BankServantMap.get("C").refreshHashMap();

            for (int i = 0; i < customers3.length; i++) {
                String token[] = customers3[i].split(",");
                CustomerAccount customer = new CustomerAccount(token[1].trim(), token[2].trim(), token[3].trim(), token[4].trim(), token[5].trim(), 1000);
                String key = Character.toString(token[1].trim().charAt(0));
                ArrayList<CustomerAccount> customerList = rm.BankServantMap.get("C").account_HashMap.get(key);
                customerList.add(customer);
                rm.BankServantMap.get("C").account_HashMap.put(key, customerList);
            }
            for (int i = 0; i < loans3.length; i++) {
                String token[] = loans3[i].split(",");
                Loan loan = new Loan(token[0].trim(), token[1].trim(), token[2].trim(), token[3].trim());
                String key = token[0].trim();
                rm.BankServantMap.get("C").loan_HashMap.put(key, loan);
            }
        }
        
    }

    public String hashToString2(RMServer rm) {
        String result = "";

        for (ArrayList<CustomerAccount> account_list : rm.BankServantMap.get("A").account_HashMap.values()) {
            for (CustomerAccount account : account_list) {
                result += account.accountNumber + "," + account.firstName + "," + account.lastName + "," + account.emailAddress + "," + account.phoneNumber
                        + "," + account.password + ";";
            }
        }
        result += "@";
        for (Loan temp : rm.BankServantMap.get("A").loan_HashMap.values()) {
            result += temp.ID + "," + temp.accountNumber + "," + temp.amount + "," + temp.dueDate + ";";
        }

        result += "!";

        for (ArrayList<CustomerAccount> account_list : rm.BankServantMap.get("B").account_HashMap.values()) {
            for (CustomerAccount account : account_list) {
                result += account.accountNumber + "," + account.firstName + "," + account.lastName + "," + account.emailAddress + "," + account.phoneNumber
                        + "," + account.password + ";";
            }
        }
        result += "@";
        for (Loan temp : rm.BankServantMap.get("B").loan_HashMap.values()) {
            result += temp.ID + "," + temp.accountNumber + "," + temp.amount + "," + temp.dueDate + ";";
        }

        result += "!";

        for (ArrayList<CustomerAccount> account_list : rm.BankServantMap.get("C").account_HashMap.values()) {
            for (CustomerAccount account : account_list) {
                result += account.accountNumber + "," + account.firstName + "," + account.lastName + "," + account.emailAddress + "," + account.phoneNumber
                        + "," + account.password + ";";
            }
        }
        result += "@";
        for (Loan temp : rm.BankServantMap.get("C").loan_HashMap.values()) {
            result += temp.ID + "," + temp.accountNumber + "," + temp.amount + "," + temp.dueDate + ";";
        }

        //System.out.println(result); 
        return result;
    }
}
