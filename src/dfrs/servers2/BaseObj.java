package dfrs.servers2;

public abstract class BaseObj {

	public abstract boolean bookFlight(String firstName, String lastName, String address, String phoneNumber,
			String destination, String flightClass, String dateOfFlight);
	public abstract String getBookedFlightCount();
	public abstract boolean editFlightRecord(String recordID, String departure, String destination, int eco,
			int busi, int fit, String dateOfFlight);
	public abstract boolean transferReservation(String passengerID, String currentCity, String otherCity);

}
