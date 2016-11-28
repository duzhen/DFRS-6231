package dfrs.servers2;

import java.io.Serializable;
 

public class FlightRecord implements Serializable {
	
	private String recordID;
	
	private int economySeats;
	private int businessSeats;
	private int fitsSeats;
	private String departure;
	private String destination;
	
	private String dateOfFlight;
	
 
	
	public FlightRecord(String recordID, String departure, String destination, int eco, int busi, int fit,
			String dateOfFlight) {
		this.recordID = recordID;
		this.departure = departure;
		this.destination = destination;
		this.economySeats = eco;
		this.businessSeats = busi;
		this.fitsSeats = fit;
		this.dateOfFlight = dateOfFlight;
 
	}



	public String getRecordID() {
		return recordID;
	}



	public int getEconomySeats() {
		return economySeats;
	}



	public int getBusinessSeats() {
		return businessSeats;
	}



	public int getFitsSeats() {
		return fitsSeats;
	}



	public String getDeparture() {
		return departure;
	}



	public String getDestination() {
		return destination;
	}



	public String getDateOfFlight() {
		return dateOfFlight;
	}



	public void setEconomySeats(int economySeats) {
		this.economySeats = economySeats;
	}



	public void setBusinessSeats(int businessSeats) {
		this.businessSeats = businessSeats;
	}



	public void setFitsSeats(int fitsSeats) {
		this.fitsSeats = fitsSeats;
	}



	public void setRecordID(String recordID) {
		this.recordID = recordID;
	}



	public void setDeparture(String departure) {
		this.departure = departure;
	}



	public void setDestination(String destination) {
		this.destination = destination;
	}



	public void setDateOfFlight(String dateOfFlight) {
		this.dateOfFlight = dateOfFlight;
	}



	@Override
	public String toString() {
		return "FlightRecord [recordID=" + recordID + ", economySeats=" + economySeats + ", businessSeats="
				+ businessSeats + ", fitsSeats=" + fitsSeats + ", departure=" + departure + ", destination="
				+ destination + ", dateOfFlight=" + dateOfFlight + "]";
	}

	
	
	 
	 
}
