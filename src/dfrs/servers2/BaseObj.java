package dfrs.servers2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import dfrs.servers.IServerManager;
import dfrs.servers1.GenerateID;

public abstract class BaseObj implements IServerManager {
	protected boolean running = true;
	DatagramSocket aSocket;
	
	protected ArrayList<FlightRecord> flightRecords;
	
	public abstract boolean bookFlight(String firstName, String lastName, String address, String phoneNumber,
			String destination, String flightClass, String dateOfFlight);
	public abstract String getBookedFlightCount();
	public abstract boolean editFlightRecord(String recordID, String departure, String destination, int eco,
			int busi, int fit, String dateOfFlight);
	public abstract boolean transferReservation(String passengerID, String currentCity, String otherCity);

	protected int findFlightRecordsByID(String recordID) {
		for (FlightRecord fr : flightRecords) {
			if (fr != null) {
				if (fr.getRecordID().equals(recordID)) {
					return flightRecords.indexOf(fr);
				}
			}

		}
		return -1;
	}
	
	protected FlightRecord getRecordsByIndex(int index) {
		return flightRecords.get(index);
	}
	
	public String send(Object o, String host, int port) {
		try {
			DatagramSocket aSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(host);
			byte[] incomingData = new byte[1024];
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(outputStream);
			os.writeObject(o);
			byte[] data = outputStream.toByteArray();
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
			aSocket.send(sendPacket);
			 
			DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
			aSocket.receive(incomingPacket);
			aSocket.close();
			return new String(incomingPacket.getData(), 0, incomingPacket.getLength()).trim();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		} catch (SocketException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public void shutdown() {
		running = false;
		if(aSocket!=null)aSocket.close();
		GenerateID.getInstance().clearID();
	}

	@Override
	public void printAllTicket() {
	}
}
