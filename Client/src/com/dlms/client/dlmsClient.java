package com.dlms.client;

import DLMS.dlms;
import DLMS.dlmsHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.omg.CORBA.ORB;

/**
 *
 * @author yucunli
 */
public class dlmsClient {
    ORB orb;
    
    public static void main(String[] args) throws IOException{
        dlmsClient client = new dlmsClient(args);
        
        System.out.println("Welcome to our bank system customer client!");
        boolean login = true;
        while(login){
            client.console();
            System.out.println("Do you have any other operation to do?\n1. Yes\n2. No");
            
            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	    String choice = bufferRead.readLine();
            
            if(choice.equals("2")){
                login = false;
                System.out.println("Have a nice day!");
            }
            System.out.println("---------------------------");
        }
    }
    
    public dlmsClient(String[] args){
        orb = ORB.init(args, null);
    }
    
    public String sendRequest(String bank, String request, String[] infoArray){
        String result = null;
        
        try{
            String path = System.getProperty("user.dir");
            path = path.substring(0, path.lastIndexOf("/") + 1);
            
            File f = new File(path + "FEOR.txt");
            
            BufferedReader br = new BufferedReader(new FileReader(f));
            String IOR = br.readLine();
            br.close();
            
            org.omg.CORBA.Object obj = orb.string_to_object(IOR); 
            dlms aDLMS = dlmsHelper.narrow(obj);
            
            if(request.equals("openAccount")){
                result = aDLMS.openAccount(bank, infoArray[0], infoArray[1], infoArray[2], infoArray[3], infoArray[4]);
            }else if(request.equals("getLoan")){
                result = aDLMS.getLoan(bank, infoArray[0], infoArray[1], infoArray[2]);
            }else if(request.equals("transferLoan")){
                result = aDLMS.transferLoan(infoArray[0], bank, infoArray[1]);
            }
            
            System.out.println(result);
            
        }catch(Exception ex){
            System.out.println(ex.toString());
        }
        
        return result;
    }
    
    
    public void console() {
        System.out.println("Here are two available operation you can do on our system:");
        System.out.println("1. Open Account");
        System.out.println("2. Get Loan");
        System.out.println("3. Transfer Loan");

        try {
            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
            String choice = bufferRead.readLine();
            
            if (!(choice.equals("1") || choice.equals("2") || choice.equals("3")|| choice.equals("*")|| choice.equals("/")|| choice.equals("&"))) {
                System.out.println("Illegal Input, please choice 1 or 2 or 3");
                return;
            }

            System.out.println("Please choose a bank:");
            System.out.println("A. " + "Bank A" + "\nB. " + "Bank B"
                    + "\nC. " + "Bank C");

            bufferRead = new BufferedReader(new InputStreamReader(System.in));
            String bank = bufferRead.readLine();

            if (choice.equals("1")) {

                System.out.println("Please enter your information(FirstName, LastName, Email, Phone, Password)");
                bufferRead = new BufferedReader(new InputStreamReader(System.in));
                String info = bufferRead.readLine();
                String[] infoArray = info.split(",");

                sendRequest(bank, "openAccount", infoArray);

            } else if (choice.equals("2")) {

                System.out.println("Please enter your information(AccountNumber, Password, LoanAmount)");
                bufferRead = new BufferedReader(new InputStreamReader(System.in));
                String info = bufferRead.readLine();
                String[] infoArray = info.split(",");

                sendRequest(bank, "getLoan", infoArray);

            } else if (choice.equals("3")) {
                System.out.println("Please enter your information(loan ID, target bank)");
                bufferRead = new BufferedReader(new InputStreamReader(System.in));
                String info = bufferRead.readLine();
                String[] infoArray = info.split(",");

                sendRequest(bank, "transferLoan", infoArray);

            } else {
                System.out.println("Illegal Input, please choice 1 or 2 or 3");
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
