package server;


/**
* server/_ServerInterfaceStub.java .
* 由IDL-to-Java 编译器 (可移植), 版本 "3.2"生成
* 从C:/Users/nathan/workspace/COMP6231project/src/server.idl
* 2016年11月16日 星期三 上午09时02分35秒 EST
*/

public class _ServerInterfaceStub extends org.omg.CORBA.portable.ObjectImpl implements server.ServerInterface
{

  public String bookFlight (String currentCity, String firstName, String lastName, String address, String phoneNumber, String destination, String flightClass, String flightDate)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("bookFlight", true);
                $out.write_string (currentCity);
                $out.write_string (firstName);
                $out.write_string (lastName);
                $out.write_string (address);
                $out.write_string (phoneNumber);
                $out.write_string (destination);
                $out.write_string (flightClass);
                $out.write_string (flightDate);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return bookFlight (currentCity, firstName, lastName, address, phoneNumber, destination, flightClass, flightDate        );
            } finally {
                _releaseReply ($in);
            }
  } // bookFlight

  public String getBookedFlightCount (String currentCity, String managerID)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getBookedFlightCount", true);
                $out.write_string (currentCity);
                $out.write_string (managerID);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getBookedFlightCount (currentCity, managerID        );
            } finally {
                _releaseReply ($in);
            }
  } // getBookedFlightCount

  public String editFlightRecord (String currentCity, String managerID, String destination, String flightDate, int economy, int business, int firstclass)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("editFlightRecord", true);
                $out.write_string (currentCity);
                $out.write_string (managerID);
                $out.write_string (destination);
                $out.write_string (flightDate);
                $out.write_long (economy);
                $out.write_long (business);
                $out.write_long (firstclass);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return editFlightRecord (currentCity, managerID, destination, flightDate, economy, business, firstclass        );
            } finally {
                _releaseReply ($in);
            }
  } // editFlightRecord

  public String transferReservation (String managerID, String PassengerID, String CurrentCity, String OtherCity)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("transferReservation", true);
                $out.write_string (managerID);
                $out.write_string (PassengerID);
                $out.write_string (CurrentCity);
                $out.write_string (OtherCity);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return transferReservation (managerID, PassengerID, CurrentCity, OtherCity        );
            } finally {
                _releaseReply ($in);
            }
  } // transferReservation

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:server/ServerInterface:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     org.omg.CORBA.Object obj = orb.string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
   } finally {
     orb.destroy() ;
   }
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     String str = orb.object_to_string (this);
     s.writeUTF (str);
   } finally {
     orb.destroy() ;
   }
  }
} // class _ServerInterfaceStub
