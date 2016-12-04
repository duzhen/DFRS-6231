package dfrs.servers4;

import java.io.Serializable;

public class Ticket implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3692494331856446999L;

	private int recordID = 0;
	
	private String firstName = "";
	private String lastName = "";
	private String address = "";
	private String phone = "";
	private String ticketClass = "";
	
	private String departure = "";
	private String departureDate = "";
	private String destination = "";
	
	public Ticket() {};
	
	public Ticket(String firstName, String lastName, String address, String phone, String destination, String departureDate,
			String ticketClass, String departure) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.phone = phone;
		this.ticketClass = ticketClass;
		this.departure = departure;
		this.departureDate = departureDate;
		this.destination = destination;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getTicketClass() {
		return ticketClass;
	}
	public void setTicketClass(String ticketClass) {
		this.ticketClass = ticketClass;
	}
	public String getDeparture() {
		return departure;
	}
	public void setDeparture(String departure) {
		this.departure = departure;
	}
	public String getDepartureDate() {
		return departureDate;
	}
	public void setDepartureDate(String departureDate) {
		this.departureDate = departureDate;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}

	public int getRecordID() {
		return recordID;
	}

	public void setRecordID(int recordID) {
		this.recordID = recordID;
	}

	@Override
	public String toString() {
		return "Ticket [recordID=" + recordID + ", firstName=" + firstName + ", lastName=" + lastName + ", address="
				+ address + ", phone=" + phone + ", ticketClass=" + ticketClass + ", departure=" + departure
				+ ", departureDate=" + departureDate + ", destination=" + destination + "]";
	}
	
}
