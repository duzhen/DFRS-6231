package dfrs;


/**
* server/ServerInterfaceOperations.java .
* 由IDL-to-Java 编译器 (可移植), 版本 "3.2"生成
* 从C:/Users/nathan/workspace/COMP6231project/src/server.idl
* 2016年11月16日 星期三 上午09时02分35秒 EST
*/

public interface ServerInterfaceOperations 
{
  String bookFlight (String currentCity, String firstName, String lastName, String address, String phoneNumber, String destination, String flightClass, String flightDate);
  String getBookedFlightCount (String currentCity, String managerID);
  String editFlightRecord (String currentCity, String managerID, String destination, String flightDate, int economy, int business, int firstclass);
  String transferReservation (String managerID, String PassengerID, String CurrentCity, String OtherCity);
} // interface ServerInterfaceOperations
