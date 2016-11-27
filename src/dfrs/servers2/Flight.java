package dfrs.servers2;

public class Flight {

	public static final String FIRST_CLASS = "First";
	public static final String BUSINESS_CLASS = "Business";
	public static final String ECONOMY_CLASS = "Economy";
	public static final String ALL_CLASS = "All";
	
	public static final String DEPARTURE = "DEPARTURE";
	public static final String DATE = "DATE";
	public static final String DESTINATION = "DESTINATION";
	public static final String F_SEATS = "First";
	public static final String B_SEATS = "Business";
	public static final String E_SEATS = "Economy";
	
	private int recordID = 0;
	
	private String flightName = "";

	private String departure = "[No Value]";
	private String departureDate = "[No Value]";
	private String destination = "[No Value]";
	private String achieveDate = "";
	
	private int totalBusinessTickets = 0;
	private int totalFirstTickets = 0;
	private int totalEconomyTickets = 0;
	private int balanceBusinessTickets = 0;
	private int balanceFirstTickets = 0;
	private int balanceEconomyTickets = 0;
	
	public String getFlightName() {
		return flightName;
	}

	public void setFlightName(String flightName) {
		this.flightName = flightName;
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

	public String getAchieveDate() {
		return achieveDate;
	}

	public void setAchieveDate(String achieveDate) {
		this.achieveDate = achieveDate;
	}

	public int getTotalBusinessTickets() {
		return totalBusinessTickets;
	}

	public synchronized boolean setTotalBusinessTickets(int totalBusinessTickets) {
		int add = totalBusinessTickets - this.totalBusinessTickets;
		if(editBalanceBusinessTickets(add)) {
			this.totalBusinessTickets = totalBusinessTickets;
			return true;
		}
		return false;
	}

	public int getTotalFirstTickets() {
		return totalFirstTickets;
	}

	public synchronized boolean setTotalFirstTickets(int totalFirstTickets) {
		int add = totalFirstTickets - this.totalFirstTickets;
		if(editBalanceFirstTickets(add)) {
			this.totalFirstTickets = totalFirstTickets;
			return true;
		}
		return false;
	}

	public int getTotalEconomyTickets() {
		return totalEconomyTickets;
	}

	public synchronized boolean setTotalEconomyTickets(int totalEconomyTickets) {
		int add = totalEconomyTickets - this.totalEconomyTickets;
		if(editBalanceEconomyTickets(add)) {
			this.totalEconomyTickets = totalEconomyTickets;
			return true;
		}
		return false;
	}
	
	public synchronized boolean sellTicket(String type, boolean sell) {
		int add = 0;
		if(sell) {
			add = -1;
		} else {
			add = 1;
		}
		if (BUSINESS_CLASS.equals(type)) {
			return editBalanceBusinessTickets(add);
		} else if (FIRST_CLASS.equals(type)) {
			return editBalanceFirstTickets(add);
		} else if (ECONOMY_CLASS.equals(type)) {
			return editBalanceEconomyTickets(add);
		}
		return false;
	}
	
	public int getRecordID() {
		return recordID;
	}

	public void setRecordID(int recordID) {
		this.recordID = recordID;
	}

	public int getBalanceBusinessTickets() {
		return balanceBusinessTickets;
	}

	private synchronized boolean editBalanceBusinessTickets(int v) {
		if(this.balanceBusinessTickets + v < 0)
			return false;
		this.balanceBusinessTickets += v;
		return true;
	}

	public int getBalanceFirstTickets() {
		return balanceFirstTickets;
	}

	private synchronized boolean editBalanceFirstTickets(int v) {
		if(this.balanceFirstTickets + v < 0)
			return false;
		this.balanceFirstTickets += v;
		return true;
	}

	public int getBalanceEconomyTickets() {
		return balanceEconomyTickets;
	}

	private synchronized boolean editBalanceEconomyTickets(int v) {
		if(this.balanceEconomyTickets + v < 0)
			return false;
		this.balanceEconomyTickets += v;
		return true;
	}

	@Override
	public String toString() {
		return "Flight [recordID=" + recordID + ", departure=" + departure + ", departureDate=" + departureDate
				+ ", destination=" + destination + ", totalBusinessTickets=" + totalBusinessTickets
				+ ", totalFirstTickets=" + totalFirstTickets + ", totalEconomyTickets=" + totalEconomyTickets
				+ ", balanceBusinessTickets=" + balanceBusinessTickets + ", balanceFirstTickets=" + balanceFirstTickets
				+ ", balanceEconomyTickets=" + balanceEconomyTickets + "]";
	}
	
}
