package dfrs;

/**
* server/ServerInterfaceHolder.java .
* 由IDL-to-Java 编译器 (可移植), 版本 "3.2"生成
* 从C:/Users/nathan/workspace/COMP6231project/src/server.idl
* 2016年11月16日 星期三 上午09时02分35秒 EST
*/

public final class ServerInterfaceHolder implements org.omg.CORBA.portable.Streamable
{
  public ServerInterface value = null;

  public ServerInterfaceHolder ()
  {
  }

  public ServerInterfaceHolder (ServerInterface initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = ServerInterfaceHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    ServerInterfaceHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return ServerInterfaceHelper.type ();
  }

}
