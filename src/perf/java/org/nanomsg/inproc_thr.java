package org.nanomsg;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

public class inproc_thr {
    static class Worker
        implements Runnable {
        public Worker(NanoLibrary nano,
                      int message_count,
                      int message_size,
                      String addr,
                      CountDownLatch done) {
            this.nano = nano;
            this.message_count = message_count;
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
            byte bval = 111;
            for (i = 0; i < message_size; ++i) {
                bb.put(i, bval);
            }

            /*  First message is used to start the stopwatch. */
            rc = nano.nn_send(socket, bb, 0, message_size, 0);
            if (rc < 0) {
                System.out.printf("INITIAL error in nn_send: %s\n",
                                  nano.nn_strerror(nano.nn_errno()));
                return;
            }
            if (rc != message_size) {
                System.out.printf("INITIAL message of incorrect size sent\n");
                return;
            }

            System.out.printf("NANO running %d iterations...\n",
                              message_count);
            for (i = 0; i != message_count; ++i) {
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
        private int message_count;
        private int message_size;
        private String addr;
        private CountDownLatch done;
    }

    public static void main(String [] args)
        throws InterruptedException {
        if (args.length != 2) {
            System.out.printf("argc was %d\n", args.length);
            System.out.printf("usage: inproc_thr <message-size> <message-count>\n");
            return;
        }

        NanoLibrary nano = new NanoLibrary();

        int message_count;
        int message_size;
        String addr = "inproc://thr_test";
        int socket = 0;
        int rc;
        int i;
        long begin = 0;
        long elapsed = 0;
        long throughput = 0;
        double megabits = 0;

        message_size = Integer.parseInt(args[0]);
        message_count = Integer.parseInt(args[1]);
        System.out.printf("args: %d | %d\n",
                          message_size, message_count);

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
        CountDownLatch done = new CountDownLatch(1);

        new Thread(new Worker(nano,
                              message_count,
                              message_size,
                              addr,
                              done)).start();

        /*  First message is used to start the stopwatch. */
        rc = nano.nn_recv(socket, bb, 0, message_size, 0);
        if (rc < 0) {
            System.out.printf("INITIAL error in nn_recv: %s\n",
                              nano.nn_strerror(nano.nn_errno()));
            return;
        }
        if (rc != message_size) {
            System.out.printf("INITIAL message of incorrect size received\n");
            return;
        }

        System.out.printf("NANO running %d iterations...\n", message_count);
        begin = System.nanoTime();
        for (i = 0; i != message_count; ++i) {
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

        if (elapsed == 0)
            elapsed = 1;
        throughput = (int) (1000000000.0 * (double) message_count / (double) elapsed);
        megabits = (double) (throughput * message_size * 8) / 1000000.0;

        System.out.printf("message size: %d [B]\n", message_size);
        System.out.printf("message count: %d\n", message_count);
        System.out.printf("mean throughput: %d [msg/s]\n", throughput);
        System.out.printf("mean throughput: %.3f [Mb/s]\n", megabits);

        rc = nano.nn_close(socket);
        if (rc < 0) {
            System.out.printf("error in nn_close: %s\n",
                              nano.nn_strerror(nano.nn_errno()));
            return;
        }

        System.out.printf("NANO done running\n");
    }
}
