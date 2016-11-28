package dfrs.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dfrs.servers.BaseServerCluster;
import dfrs.servers4.Flight;
import dfrs.servers4.FlightData;

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
	
	public static boolean validDate(String input) {
		String pat = "\\d{8}" ;
        Pattern p = Pattern.compile(pat) ;
        Matcher m = p.matcher(input) ;
        return m.matches();
	}
	
	public static void printFlight(String server) {
//		System.out.println("["+server+"]-Flight Information:");
		List<Flight> flight = FlightData.getInstance().initData(server);
		System.out.println("ID\tDEP\t\tDES\t\tDATE\t\tF/B/E");
		for(Flight f:flight) {
			System.out.println(f.getRecordID()+"\t"+f.getDeparture()+"\t"+f.getDestination()+"\t"+f.getDepartureDate()
			+"\t"+f.getBalanceFirstTickets()+"/"+f.getBalanceBusinessTickets()+"/"+f.getBalanceEconomyTickets());
		}
	}
	
	public static byte[] convert(Object ts) {
		byte[] data = null;
		try {
			data = null;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(bos));
			oos.writeObject(ts);
			oos.flush();
			data = bos.toByteArray();
			oos.close();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	public static Object resolve(byte[] data) {
		Object ts = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(data)));
			ts = ois.readObject();
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return ts;
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
        pat = "(WST|WDC|wst|Washington|washington)" ;
        if(Pattern.compile(pat).matcher(input).matches()) {
        	return BaseServerCluster.SERVER_WST;
        }
		pat = "(NDL|ndl|New Delhi|new Delhi|new delhi|New delhi|NewDelhi)" ;
		if(Pattern.compile(pat).matcher(input).matches()) {
        	return BaseServerCluster.SERVER_NDL;
        }
		return "";
	}
	
	// Read lines from file
	public static List<String> readLinesFromFile(String fileName) {
        String line = "";
        List<String> lines = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
            	lines.add(line);
            }
            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" +fileName + "'");
        }
        catch(IOException ex) {
            System.out.println("Error reading file '"+ fileName + "'");
        }
		return lines;
    }
	
	// write one line to file
	public static void writeLineToFile(String fileName, String oneLine) {
		 try {
	            FileWriter fileWriter = new FileWriter(fileName, true);
	            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
	            bufferedWriter.write(oneLine);
	            bufferedWriter.newLine();
	            bufferedWriter.close();
	        }
	        catch(IOException ex) {
	            System.out.println("Error writing to file '"+ fileName + "'");
	        }
	} 
	
	
}
