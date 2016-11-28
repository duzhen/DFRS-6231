package dfrs.servers2;

import java.io.Serializable;
import java.util.Date;

public class PassengerRecord implements Serializable {

	private String passengerID;
	private Passenger passenger;
	private String destination;
	private String flightClass;
	private String dateOfFlight;
	
	public PassengerRecord(Passenger passenger, String destination, String flightClass, String dateOfFlight) {
		this.passenger = passenger;
		this.destination = destination;
		this.flightClass = flightClass;
		this.dateOfFlight = dateOfFlight;
//		this.passengerID = Long.toString(System.currentTimeMillis() / 1000L);
		
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public String getDestination() {
		return destination;
	}

	public String getFlightClass() {
		return flightClass;
	}

	public String getDateOfFlight() {
		return dateOfFlight;
	}

	public String getPassengerID() {
		return passengerID;
	}
	
	public void setPassengerID(String id) {
		this.passengerID = id;
	}

	@Override
	public String toString() {
		return "PassengerRecord [passengerID=" + passengerID + ", passenger=" + passenger + ", destination=" + destination
				+ ", flightClass=" + flightClass + ", dateOfFlight=" + dateOfFlight + "]";
	}
	
	
	
	
	
}
