package dfrs.servers2;

import java.util.ArrayList;

public abstract class BaseObj {
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
}
