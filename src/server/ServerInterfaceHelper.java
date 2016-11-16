package server;


/**
* server/ServerInterfaceHelper.java .
* 由IDL-to-Java 编译器 (可移植), 版本 "3.2"生成
* 从C:/Users/nathan/workspace/COMP6231project/src/server.idl
* 2016年11月16日 星期三 上午09时02分35秒 EST
*/

abstract public class ServerInterfaceHelper
{
  private static String  _id = "IDL:server/ServerInterface:1.0";

  public static void insert (org.omg.CORBA.Any a, server.ServerInterface that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static server.ServerInterface extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (server.ServerInterfaceHelper.id (), "ServerInterface");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static server.ServerInterface read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_ServerInterfaceStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, server.ServerInterface value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static server.ServerInterface narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof server.ServerInterface)
      return (server.ServerInterface)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      server._ServerInterfaceStub stub = new server._ServerInterfaceStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static server.ServerInterface unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof server.ServerInterface)
      return (server.ServerInterface)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      server._ServerInterfaceStub stub = new server._ServerInterfaceStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
