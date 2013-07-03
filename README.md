jnano
=====

Java binding for the [nanomsg][1] library.


What is this?
-------------

This package contains a JNI-based Java binding for `nanomsg`, the
communications and synchronization library.

How to Use
----------

You will need the following:

1. A copy of the [JDK][2].  The binding has been tested on `v1.7.0_21`
   running on a Windows 7 machine.

2. A copy of [ant][3].  I use `v1.8.4`.

3. A C compiler.  I use MSVC 2010.

4. A copy of `cpptasks`, an `ant` helper that automates compilation of
   C code.  I use `v1.0b5`.  You can find a copy in the `lib/ant`
   directory; therefore, you can run ant with `ant -lib
   lib/ant/cpptasks-1.0b5.jar jar` to use the included `cpptasks`.

5. Look at the `build.<OS>.properties` file for your OS.  Pay
   particular attention to the value of `dir.jni.headers`.


Once your environment is set up and you have downloaded `jnano`, you
can do any of these:

* `ant jar` -- create all `JAR`s.

* `ant test` -- run all unit tests (none for now).

* `ant run` -- run a specific class in one of the `JAR`s, such as
  `Tester`.  See below.

* `ant perf` -- run performance tests.  See below.


To run a basic tester class, run this on a command window:

`ant -Dcn=org.nanomsg.Tester run`


To run the latency performance test for `inproc` sockets, run this on
a command window:

`ant -Dcn=org.nanomsg.inproc_lat -Dargs="1 100000" perf`


To run the throughput performance test for `inproc` sockets, run this
on a command window:

`ant -Dcn=org.nanomsg.inproc_thr -Dargs="100 100000" perf`


To run the latency performance test for `TCP` sockets, run this on two
separate command windows:

`ant -Dcn=org.nanomsg.local_lat -Dargs="tcp://127.0.0.1:6789 1 100000" perf`
`ant -Dcn=org.nanomsg.remote_lat -Dargs="tcp://127.0.0.1:6789 1 100000" perf`


To run the throughput performance test for `TCP` sockets, run this on
two separate command windows:

`ant -Dcn=org.nanomsg.local_thr -Dargs="tcp://127.0.0.1:6789 100 100000" perf`
`ant -Dcn=org.nanomsg.remote_thr -Dargs="tcp://127.0.0.1:6789 100 100000" perf`


Status
------

*This is a work in progress.*

Everything works pretty well and performance is comparable to what you
get on C.

I am still looking for the right way to integrate the polling support
provided by `nanomsg` into the native Java way of doing
demultiplexing.


License
-------

This project is released under the MIT license, as is the native
libnanomsg library.  See COPYING for more details.


[1]: http://nanomsg.org/                          "nanomsg"
[2]: http://en.wikipedia.org/wiki/JDK             "Java Development Kit"
[3]: http://en.wikipedia.org/wiki/Apache_Ant      "Apache Ant"
