package server;


/**
* server/ServerInterfaceOperations.java .
* 由IDL-to-Java 编译器 (可移植), 版本 "3.2"生成
* 从C:/Users/nathan/workspace/COMP6231project/src/server.idl
* 2016年11月15日 星期二 下午02时42分26秒 EST
*/

public interface ServerInterfaceOperations 
{
  String bookFlight (String firstName, String lastName, String address, String phoneNumber, String destination, String flightClass, String flightDate);
  String getBookedFlightCount (String managerID);
  String editFlightRecord (String managerID, String destination, String flightDate, int economy, int business, int firstclass);
  String transferReservation (String managerID, String PassengerID, String CurrentCity, String OtherCity);
} // interface ServerInterfaceOperations
