package dfrs.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.regex.Pattern;

import dfrs.servers.BaseServerCluster;

public class Utils {
	public static String exec(String command) {
        StringBuilder sb = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            process.getOutputStream().close();
            reader.close();
            process.destroy();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return sb.toString();
    }
	
	public static int validInputOption(Scanner keyboard, int max) {
		int userChoice = 0;
		boolean valid = false;

		// Enforces a valid integer input.
		while (!valid) {
			try {
				userChoice = keyboard.nextInt();
				if(userChoice >=1 && userChoice <=max)
					valid = true;
				else {
					throw new Exception();
				}
			} catch (Exception e) {
				System.out.println("Invalid Input, please enter an Integer (1 - "+max+")\n");
				valid = false;
				keyboard.nextLine();
			}
		}
		return userChoice;
	}
	
	public static void println(String content) {
		if(Config.TEST) {
			System.out.println(content);
		}
	}
	
	public static String getServer(String input) {
		String pat = "(MTL)\\d{4}" ;
        if(Pattern.compile(pat).matcher(input).matches()) {
        	return BaseServerCluster.SERVER_MTL;
        }
        pat = "(WST)\\d{4}" ;
        if(Pattern.compile(pat).matcher(input).matches()) {
        	return BaseServerCluster.SERVER_WST;
        }
		pat = "(NDL)\\d{4}" ;
		if(Pattern.compile(pat).matcher(input).matches()) {
        	return BaseServerCluster.SERVER_NDL;
        }
		pat = "(MTL|mtl|Montreal|montreal)" ;
        if(Pattern.compile(pat).matcher(input).matches()) {
        	return BaseServerCluster.SERVER_MTL;
        }
        pat = "(WST|wst|Washington|washtington)" ;
        if(Pattern.compile(pat).matcher(input).matches()) {
        	return BaseServerCluster.SERVER_WST;
        }
		pat = "(NDL|ndl|New Delhi|new Delhi|new delhi|New delhi)" ;
		if(Pattern.compile(pat).matcher(input).matches()) {
        	return BaseServerCluster.SERVER_NDL;
        }
		return "";
	}
}
