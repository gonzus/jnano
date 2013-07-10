package org.nanomsg;

// import static org.junit.Assert.assertArrayEquals;
// import static org.junit.Assert.assertEquals;
// import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
// import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

// import java.nio.ByteBuffer;
// import java.nio.ByteOrder;
// import java.nio.charset.CharacterCodingException;

import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
// import org.zeromq.ZMQ.Context;
// import org.zeromq.ZMQ.Poller;
// import org.zeromq.ZMQ.Socket;

/**
* @author Gonzalo Diethelm
*/
public class NanoTest
{
  @BeforeClass
  public static void initTest()
  {
    System.out.printf("Creating NanoTest\n");
    nano = new NanoLibrary();
  }

  @AfterClass
  public static void termTest()
  {
    nano = null;
    System.out.printf("Destroying NanoTest\n");
  }

  /**
   * Test method for existence of JNI native library.
   */
  @Test
  public void testNanoNotNull()
  {
    assertNotNull(nano);
  }

  /**
   * Test method for {@link org.nanomsg.NanoLibrary#get_version()}.
   */
  @Test
  public void testVersion()
  {
    int version = -1;
    version = nano.get_version();
    assertTrue("version is negative", version >= 0);
  }

  /**
   * Test method for symbols.
   */
  @Test
  public void testSymbols()
  {
    String[] names = {
      "NN_VERSION_MAJOR",
      "NN_VERSION_MINOR",
      "NN_VERSION_PATCH",
      "NN_VERSION",
      "AF_SP",
      "AF_SP_RAW",
      "NN_INPROC",
      "NN_IPC",
      "NN_TCP",
      "NN_PAIR",
      "NN_PUB",
      "NN_SUB",
      "NN_REP",
      "NN_REQ",
      "NN_SOURCE",
      "NN_SINK",
      "NN_PUSH",
      "NN_PULL",
      "NN_SURVEYOR",
      "NN_RESPONDENT",
      "NN_BUS",
      "NN_SOCKADDR_MAX",
      "NN_SOL_SOCKET",
      "NN_LINGER",
      "NN_SNDBUF",
      "NN_RCVBUF",
      "NN_SNDTIMEO",
      "NN_RCVTIMEO",
      "NN_RECONNECT_IVL",
      "NN_RECONNECT_IVL_MAX",
      "NN_SNDPRIO",
      "NN_SNDFD",
      "NN_RCVFD",
      "NN_DOMAIN",
      "NN_PROTOCOL",
      "NN_SUB_SUBSCRIBE",
      "NN_SUB_UNSUBSCRIBE",
      "NN_REQ_RESEND_IVL",
      "NN_SURVEYOR_DEADLINE",
      "NN_TCP_NODELAY",
      "NN_DONTWAIT",
      "EADDRINUSE",
      "EADDRNOTAVAIL",
      "EAFNOSUPPORT",
      "EAGAIN",
      "EBADF",
      "ECONNREFUSED",
      "EFAULT",
      "EFSM",
      "EINPROGRESS",
      "EINTR",
      "EINVAL",
      "EMFILE",
      "ENAMETOOLONG",
      "ENETDOWN",
      "ENOBUFS",
      "ENODEV",
      "ENOMEM",
      "ENOPROTOOPT",
      "ENOTSOCK",
      "ENOTSUP",
      "EPROTO",
      "EPROTONOSUPPORT",
      "ETERM",
      "ETIMEDOUT",
    };
    for (String sym: names) {
      int val = nano.get_symbol(sym);
      if (val >= -99999)
        continue;
      
      String err = "symbol " + sym + " has invalid value";
      fail(err);
    }
  }

  /**
   * Test method for symbol {@link org.nanomsg.NanoLibrary#NN_INPROC}.
   */
  @Test
  public void testSym_NN_INPROC()
  {
    assertTrue("NN_INPROC is negative", nano.NN_INPROC >= -99999);
  }

  /**
   * Test method for symbol {@link org.nanomsg.NanoLibrary#NN_IPC}.
   */
  @Test
  public void testSym_NN_IPC()
  {
    assertTrue("NN_IPC is negative", nano.NN_IPC >= -99999);
  }

  /**
   * Test method for symbol {@link org.nanomsg.NanoLibrary#NN_TCP}.
   */
  @Test
  public void testSym_NN_TCP()
  {
    assertTrue("NN_TCP is negative", nano.NN_TCP >= -99999);
  }

  /**
   * Test method for symbol {@link org.nanomsg.NanoLibrary#AF_SP}.
   */
  @Test
  public void testSym_AF_SP()
  {
    assertTrue("AF_SP is negative", nano.AF_SP >= -99999);
  }

  /**
   * Test method for symbol {@link org.nanomsg.NanoLibrary#NN_PAIR}.
   */
  @Test
  public void testSym_NN_PAIR()
  {
    assertTrue("NN_PAIR is negative", nano.NN_PAIR >= -99999);
  }

  /**
   * Test method for symbol {@link org.nanomsg.NanoLibrary#NN_SOL_SOCKET}.
   */
  @Test
  public void testSym_NN_SOL_SOCKET()
  {
    assertTrue("NN_SOL_SOCKET is negative", nano.NN_SOL_SOCKET >= -99999);
  }

  private static NanoLibrary nano = null;
}
