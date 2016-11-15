package com.dlms.server;

import com.dlms.model.CustomerAccount;
import com.dlms.model.Loan;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import net.rudp.ReliableServerSocket;
import net.rudp.ReliableSocket;
import net.rudp.ReliableSocketOutputStream;

/**
 *
 * @author yucunli
 */
public class BankServer {
    public int fe_port;
    public String fe_host;
    public HashMap<String, Integer> port_map;
    public String bankName;
    public int rm_port;
    
    public String expectedSequence;
    
    public static final String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
        "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

    private static final int DEFAULT_CREDIT = 1000;
    private static final String DEFAULT_DUEDATE = "2016-1-1";

    public HashMap<String, ArrayList<CustomerAccount>> account_HashMap;
    public HashMap<String, Loan> loan_HashMap;
    
    public BankServer(String _bankName, HashMap<String, Integer> _port_map, int rm_port, int fe_port, String expectedSequence){
        account_HashMap = new HashMap<String, ArrayList<CustomerAccount>>();
        loan_HashMap = new HashMap<String, Loan>();
        
        for(String ch : alphabet){
            account_HashMap.put(ch, new ArrayList<CustomerAccount>());
        }
        
        this.bankName = _bankName;
        this.port_map = _port_map;
        this.rm_port = rm_port;
        this.fe_port = fe_port;
        this.expectedSequence = expectedSequence;
        
        BankAsReceiver receiver = new BankAsReceiver();
        receiver.start();
    }
    
    public void refreshHashMap(){
        account_HashMap.clear();
        loan_HashMap.clear();
        
        for(String ch : alphabet){
            account_HashMap.put(ch, new ArrayList<CustomerAccount>());
        }
    }
    
     public String openAccount(String bank, String firstName, String lastName, String emailAddress, String phoneNumber, String password) {
        CustomerAccount account = null;
     
        CustomerAccount foundAccount = null;
        ArrayList<CustomerAccount> list = account_HashMap.get(lastName.toLowerCase().substring(0, 1));
        for (CustomerAccount temp : list) {
            if (temp.firstName.equals(firstName) && temp.lastName.equals(lastName)) {
                foundAccount = temp;
            }
        }
        
        if (foundAccount == null) {
            account = new CustomerAccount(firstName, lastName, emailAddress, phoneNumber, password, DEFAULT_CREDIT);
            list.add(account);

            log(firstName + lastName + " " + " create account : " + account.accountNumber);
            logCustomer(account.accountNumber, "account created");
            return account.accountNumber;
        } else {
            return foundAccount.accountNumber;
        }
     }
     
    public String getLoan(String bank, String accountNumber, String password, String loanAmount, String sequenceId) {
        CustomerAccount foundAccount = null;
        Loan loan = null;

        for (ArrayList<CustomerAccount> account_list : account_HashMap.values()) {
            for (CustomerAccount account : account_list) {
                if (account.accountNumber.equals(accountNumber)) {
                    foundAccount = account;
                    break;
                }
            }
        }
        
        if (foundAccount != null && foundAccount.password.equals(password)) {
            
            int[] rest_port = new int[2];
            int count = 0;
            for(String s : port_map.keySet()){
                if(!s.equals(bankName)){
                    rest_port[count] = port_map.get(s);
                    count++;
                }
            }

            BankAsClient client0 = new BankAsClient("localhost", rest_port[0], "search" + ":" + foundAccount.firstName + "," + foundAccount.lastName + ":");
            BankAsClient client1 = new BankAsClient("localhost", rest_port[1], "search" + ":" + foundAccount.firstName + "," + foundAccount.lastName + ":");

            client0.start();
            client1.start();
            try {
                client0.join();
                client1.join();
            } catch (Exception ex) {
                return ex.getMessage();
            }

            //get foundAccount debt
            int debt = 0;
            for (Loan temp : loan_HashMap.values()) {
                if (temp.accountNumber.equals(foundAccount.accountNumber)) {
                    debt += Integer.parseInt(temp.amount);
                }
            }
            debt += Integer.parseInt(client0.result) + Integer.parseInt(client1.result);

            if (foundAccount.creditLimit - debt >= 0) {
                loan = new Loan(sequenceId, accountNumber, loanAmount, DEFAULT_DUEDATE);
                loan_HashMap.put(loan.ID, loan);
            }
        }
        
        if (loan == null) {
            return "FAIL";
        } else {
            logCustomer(accountNumber, "GetLoan performed \n" );
            log("Account " + accountNumber + " tried to get loan, and the result shows ");
            return loan.ID;
        }
    }
    
    public String transferLoan(String loanID, String currentBank, String otherBank){
        if (loan_HashMap.get(loanID) == null) {
            return "NotFoundLoan";
        }

        Loan loan = loan_HashMap.get(loanID);
        CustomerAccount foundAccount = null;
        
        for (ArrayList<CustomerAccount> account_list : account_HashMap.values()) {
            for (CustomerAccount account : account_list) {
                if (account.accountNumber.equals(loan.accountNumber)) {
                    foundAccount = account;
                    break;
                }
            }
        }
        
        synchronized (foundAccount){
            try {
                String content = "transfer" + ":" + loan.ID + "," + loan.accountNumber + "," + loan.dueDate + "," + loan.amount
                        + "#" + foundAccount.accountNumber + "," + foundAccount.firstName + "," + foundAccount.lastName + "," + foundAccount.emailAddress + "," + foundAccount.phoneNumber + "," + foundAccount.password + "," + foundAccount.creditLimit
                        + ":";
                BankAsClient client = new BankAsClient("localhost", Integer.valueOf(port_map.get(otherBank)), content);
                client.run();

                // return result Yes/True
                // operate on local database
                if (client.result.equals("No")) {
                    //do nothing, just return
                    return "FAIL";
                }

                if (client.result.equals("Yes")) {
                    //if operation done well
                    //if not well -> roll back
                    loan_HashMap.remove(loan.ID);
                    if (loan_HashMap.get(loan.ID) != null) {
                        content = "rollback" + ":" + foundAccount.lastName + "," + foundAccount.accountNumber + "," + loan.ID + ":";
                        client = new BankAsClient("localhost", Integer.valueOf(port_map.get(otherBank)), content);
                        client.run();

                        if (client.result.equals("No")) {
                            //do nothing, just return
                            return "FAIL";
                        }
                    } else {
                        
                        content = "transferDone" + ":" + loan.ID + "," + ":";
                        client = new BankAsClient("localhost", Integer.valueOf(port_map.get(otherBank)), content);
                        client.start();
                        client.join();
                    }
                }

            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
        }
        
        return "DONE";
    }
    
    
    public String delayPayment(String bank, String loanID, String currentDueDate, String newDueDate) {
        Loan loan = loan_HashMap.get(loanID);

        if (loan == null) {
            return "FAIL";
        }

        synchronized (loan) {
            loan.dueDate = newDueDate;
        }

        log("Loan " + loanID + " has been delayed from " + currentDueDate + " to " + newDueDate);
        logManager("Loan " + loanID + " has been delayed from " + currentDueDate + " to " + newDueDate);
        return newDueDate;
    }
    
    public String printCustomerInfo(String bank) {
        StringBuilder result = new StringBuilder();
        for (String ch : alphabet) {
            ArrayList<CustomerAccount> list = account_HashMap.get(ch);
            for (CustomerAccount account : list) {
                result.append(account.toString());
            }
        }

        log("printCustomerInfo has been called");
        logManager("printCustomerInfo has been called");
        return result.toString();
    }
     
    class BankAsClient extends Thread{
        String host;
        int port;
        String content;
        public String result;
        
        public BankAsClient(String host, int port, String content){
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
    
    class BankAsReceiver extends Thread{
        @Override
        public void run(){
            try {
                ReliableServerSocket serverSocket = new ReliableServerSocket(rm_port);
                
                boolean isStopped = false;
                while(!isStopped){
                    Socket connectionSocket = serverSocket.accept();
                    
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    String msg = inFromClient.readLine();
                    
                    if(msg.equals("shutdown")){
                        break;
                    }else if(msg.contains("%")){
                        String[] message = msg.split("%");
                        String sequenceId = message[0];
                        
                        if(sequenceId.equals(expectedSequence)){
                            String[] request = message[1].split("#");
                            String requestType = request[0];
                            String[] requestPara = request[1].split(",");

                            String result = "";

                            switch(requestType){
                                case "openAccount":
                                    result = sequenceId + "%" + rm_port + "#" + openAccount(requestPara[0], requestPara[1], requestPara[2], requestPara[3], requestPara[4], requestPara[5]) + "#";
                                    break;
                                case "getLoan":
                                    result = sequenceId + "%" + rm_port + "#" + getLoan(requestPara[0], requestPara[1], requestPara[2], requestPara[3], sequenceId) + "#";
                                    break;
                                case "transferLoan":
                                    result = sequenceId + "%" + rm_port + "#" + transferLoan(requestPara[0], requestPara[1], requestPara[2]) + "#";

                                    break;
                                case "delayLoan":
                                    result = sequenceId + "%" + rm_port + "#" + delayPayment(requestPara[0], requestPara[1], requestPara[2], requestPara[3]) + "#";
                                    break;
                                case "printCustomerInfo":
                                    result = sequenceId + "%" + rm_port + "#" + printCustomerInfo(requestPara[0]) + "#";
                                    break;
                            }

                            // send result back to front end
                            BankAsClient client = new BankAsClient(fe_host,fe_port, result);
                            client.start();
                            
                            expectedSequence = sequenceId.substring(0,1) + Integer.parseInt(sequenceId.substring(1));
                            
                            // message send back to client
                            ReliableSocketOutputStream outToClient = (ReliableSocketOutputStream) connectionSocket.getOutputStream();
                            PrintWriter outputBuffer = new PrintWriter(outToClient);
                            outputBuffer.println("success");
                            outputBuffer.flush();
                        }else{
                            // message send back to client
                            ReliableSocketOutputStream outToClient = (ReliableSocketOutputStream) connectionSocket.getOutputStream();
                            PrintWriter outputBuffer = new PrintWriter(outToClient);
                            outputBuffer.println("Wrong Sequence ID");
                            outputBuffer.flush();
                            
                            continue;
                        }
                    }else{
                        String sendData = "";
                        
                        String[] request_array = sendData.split(":");
                        if (request_array[0].equals("search")) {
                            String[] content_array = request_array[1].split(",");
                            ArrayList<CustomerAccount> list = account_HashMap.get(content_array[0].toLowerCase().substring(0, 1));
                            CustomerAccount foundAccount = null;
                            for (CustomerAccount account : list) {
                                if (account.emailAddress.equals(content_array[1])) {
                                    foundAccount = account;
                                    break;
                                }
                            }

                            if (foundAccount == null) {
                                sendData = "0";
                            } else {
                                int debt = 0;
                                for (Loan loan : loan_HashMap.values()) {
                                    if (loan.accountNumber == foundAccount.accountNumber) {
                                        debt += Integer.parseInt(loan.amount);
                                    }
                                }

                                sendData = (debt + "");
                            }

                        } else if (request_array[0].equals("transfer")) {
                            String[] content_array = request_array[1].split("#");
                            String[] loan_info = content_array[0].split(",");
                            Loan loan = new Loan();
                            loan.ID = loan_info[0];
                            loan.accountNumber = loan_info[1];
                            loan.dueDate = loan_info[2];
                            loan.amount = loan_info[3];

                            String[] account_info = content_array[1].split(",");
                            CustomerAccount account = new CustomerAccount();
                            account.accountNumber = account_info[0];
                            account.firstName = account_info[1];
                            account.lastName = account_info[2];
                            account.emailAddress = account_info[3];
                            account.phoneNumber = account_info[4];
                            account.password = account_info[5];
                            account.creditLimit = Integer.valueOf(account_info[6]);

                            loan_HashMap.put(loan.ID, loan);
                            List<CustomerAccount> list = account_HashMap.get(account.lastName.toLowerCase().substring(0, 1));
                            list.add(account);

                            if (loan_HashMap.get(loan.ID) != null && list.contains(account)) {
                                sendData = "Yes";
                            } else {
                                if (loan_HashMap.get(loan.ID) != null) {
                                    loan_HashMap.remove(loan.ID);
                                }
                                if (list.contains(account)) {
                                    list.remove(account);
                                }

                                sendData = "No";
                            }

                            // thread used to lock loan object
                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                    synchronized (loan) {
                                        try {
                                            loan.wait();
                                        } catch (InterruptedException ex) {
                                            System.out.println(ex.toString());
                                        }
                                    }

                                }
                            };
                            thread.start();

                        } else if (request_array[0].equals("rollback")) {
                            String[] content_array = request_array[1].split(",");
                            CustomerAccount foundAccount = null;
                            List<CustomerAccount> account_list = account_HashMap.get(content_array[0].toLowerCase().substring(0, 1));
                            for (CustomerAccount account : account_list) {
                                if (account.accountNumber.equals(content_array[1])) {
                                    foundAccount = account;
                                    break;
                                }
                            }
                            if (foundAccount != null) {
                                account_list.remove(foundAccount);
                            }
                            if (loan_HashMap.get(content_array[2]) != null) {
                                Loan loan = loan_HashMap.get(content_array[2]);

                                //unlock loan object
                                synchronized (loan) {
                                    loan_HashMap.remove(content_array[2]);
                                    loan.notify();
                                }

                            }
                        } else if (request_array[0].equals("transferDone")) {
                            String[] content_array = request_array[1].split(",");
                            Loan loan = loan_HashMap.get(content_array[0]);
                            //unlock loan object
                            synchronized (loan) {
                                loan_HashMap.remove(content_array[0]);
                                loan.notify();
                            }
                        }
                        
                        ReliableSocketOutputStream outToClient = (ReliableSocketOutputStream) connectionSocket.getOutputStream();
                        PrintWriter outputBuffer = new PrintWriter(outToClient);
                        outputBuffer.println(sendData);
                        outputBuffer.flush();
                    }
                    
                    connectionSocket.close();
                }
                
                serverSocket.close();

            } catch (IOException ex) {

            }
        }
    }
    
     
     
     
     private void log(String content){
        
        File dir = new File(bankName+""); 
        dir.mkdir();
        
        String path = "./"+ bankName +"/log.txt";
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, true)))) {
            out.println(dateFormat.format(date) + "    " + content);
            
        }catch (IOException e) {
            
        }
    }
    
    private void logCustomer(String id, String content){
        File dir = new File(bankName+""); 
        dir.mkdir();
        
        String path = "./" + bankName + "/Customer"+ id +".txt";
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, true)))) {
            out.println(dateFormat.format(date) + "    " + content);
            
        }catch (IOException e) {
            
        }
    }
    
    private void logManager(String content){
        File dir = new File(bankName+""); 
        dir.mkdir();
        
        String path = "./"+bankName+"/Manager.txt";
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, true)))) {
            out.println(dateFormat.format(date) + "    " + content);
            
        }catch (IOException e) {
            
        }
    }
}
