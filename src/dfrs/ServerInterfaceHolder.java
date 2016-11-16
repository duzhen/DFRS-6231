package server;

/**
* server/ServerInterfaceHolder.java .
* 由IDL-to-Java 编译器 (可移植), 版本 "3.2"生成
* 从C:/Users/nathan/workspace/COMP6231project/src/server.idl
* 2016年11月16日 星期三 上午09时02分35秒 EST
*/

public final class ServerInterfaceHolder implements org.omg.CORBA.portable.Streamable
{
  public server.ServerInterface value = null;

  public ServerInterfaceHolder ()
  {
  }

  public ServerInterfaceHolder (server.ServerInterface initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = server.ServerInterfaceHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    server.ServerInterfaceHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return server.ServerInterfaceHelper.type ();
  }

}
