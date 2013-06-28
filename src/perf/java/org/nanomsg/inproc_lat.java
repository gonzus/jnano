package org.nanomsg;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

public class inproc_lat {
    static class Worker
        implements Runnable {
        public Worker(NanoLibrary nano,
                      int roundtrip_count,
                      int message_size,
                      String addr,
                      CountDownLatch done) {
            this.nano = nano;
            this.roundtrip_count = roundtrip_count;
            this.message_size = message_size;
            this.addr = addr;
            this.done = done;
        }

        public void run() {
            try {
                do_run();
                done.countDown();
            } catch (InterruptedException ex) {
            }
        }

        private void do_run()
            throws InterruptedException {
            int socket;
            int rc;
            int i;

            socket = nano.nn_socket(nano.AF_SP, nano.NN_PAIR);
            if (socket < 0) {
                System.out.printf("error in nn_socket: %s\n",
                                  nano.nn_strerror(nano.nn_errno()));
                return;
            }
            System.out.printf("NANO PAIR socket created\n");

            rc = nano.nn_connect(socket, addr);
            if (rc < 0) {
                System.out.printf("error in nn_connect(%s): %s\n",
                                  addr,
                                  nano.nn_strerror(nano.nn_errno()));
                return;
            }
            System.out.printf("NANO PAIR socket connected to %s\n",
                              addr);

            ByteBuffer bb = ByteBuffer.allocateDirect(message_size);

            System.out.printf("NANO running %d iterations...\n",
                              roundtrip_count);
            for (i = 0; i != roundtrip_count; ++i) {
                rc = nano.nn_recv(socket, bb, 0, message_size, 0);
                if (rc < 0) {
                    System.out.printf("error in nn_recv: %s\n",
                                      nano.nn_strerror(nano.nn_errno()));
                    return;
                }
                if (rc != message_size) {
                    System.out.printf("message of incorrect size received\n");
                    return;
                }

                rc = nano.nn_send(socket, bb, 0, message_size, 0);
                if (rc < 0) {
                    System.out.printf("error in nn_send: %s\n",
                                      nano.nn_strerror(nano.nn_errno()));
                    return;
                }
                if (rc != message_size) {
                    System.out.printf("message of incorrect size sent\n");
                    return;
                }
            }

            rc = nano.nn_close(socket);
            if (rc < 0) {
                System.out.printf("error in nn_close: %s\n",
                                  nano.nn_strerror(nano.nn_errno()));
                return;
            }
        }

        private NanoLibrary nano;
        private int roundtrip_count;
        private int message_size;
        private String addr;
        private CountDownLatch done;
    }

    public static void main(String [] args)
        throws InterruptedException {
        if (args.length != 2) {
            System.out.printf("argc was %d\n", args.length);
            System.out.printf("usage: inproc_lat <message-size> <roundtrip-count>\n");
            return;
        }

        NanoLibrary nano = new NanoLibrary();

        int roundtrip_count;
        int message_size;
        String addr = "inproc://lat_test";
        int socket = 0;
        int rc;
        int i;
        long begin = 0;
        long elapsed = 0;
        double latency = 0;

        message_size = Integer.parseInt(args[0]);
        roundtrip_count = Integer.parseInt(args[1]);
        System.out.printf("args: %d | %d\n",
                          message_size, roundtrip_count);

        socket = nano.nn_socket(nano.AF_SP, nano.NN_PAIR);
        if (socket < 0) {
            System.out.printf("error in nn_socket: %s\n",
                              nano.nn_strerror(nano.nn_errno()));
            return;
        }
        System.out.printf("NANO PAIR socket created\n");

        rc = nano.nn_bind(socket, addr);
        if (rc < 0) {
            System.out.printf("error in nn_bind(%s): %s\n",
                              addr,
                              nano.nn_strerror(nano.nn_errno()));
            return;
        }
        System.out.printf("NANO PAIR socket bound to %s\n", addr);

        ByteBuffer bb = ByteBuffer.allocateDirect(message_size);
        byte bval = 111;
        for (i = 0; i < message_size; ++i) {
            bb.put(i, bval);
        }
        CountDownLatch done = new CountDownLatch(1);

        new Thread(new Worker(nano,
                              roundtrip_count,
                              message_size,
                              addr,
                              done)).start();

        System.out.printf("NANO running %d iterations...\n", roundtrip_count);
        begin = System.nanoTime();
        for (i = 0; i != roundtrip_count; ++i) {
            rc = nano.nn_send(socket, bb, 0, message_size, 0);
            if (rc < 0) {
                System.out.printf("error in nn_send: %s\n",
                                  nano.nn_strerror(nano.nn_errno()));
                return;
            }
            if (rc != message_size) {
                System.out.printf("message of incorrect size sent\n");
                return;
            }

            rc = nano.nn_recv(socket, bb, 0, message_size, 0);
            if (rc < 0) {
                System.out.printf("error in nn_recv: %s\n",
                                  nano.nn_strerror(nano.nn_errno()));
                return;
            }
            if (rc != message_size) {
                System.out.printf("message of incorrect size received\n");
                return;
            }
        }
        elapsed = System.nanoTime() - begin;
        done.await();

        latency = (double) elapsed / (roundtrip_count * 2 * 1000);

        System.out.printf("message size: %d [B]\n", message_size);
        System.out.printf("roundtrip count: %d\n", roundtrip_count);
        System.out.printf("average latency: %.3f [us]\n", latency);

        rc = nano.nn_close(socket);
        if (rc < 0) {
            System.out.printf("error in nn_close: %s\n",
                              nano.nn_strerror(nano.nn_errno()));
            return;
        }

        System.out.printf("NANO done running\n");
    }
}
