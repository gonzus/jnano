package org.nanomsg;

import java.nio.ByteBuffer;

public class local_thr {
    public static void main(String [] args) {
        if (args.length != 3) {
            System.out.printf("argc was %d\n", args.length);
            System.out.printf("usage: local_thr <bind-to> <message-size> <message-count>\n");
            return;
        }

        NanoLibrary nano = new NanoLibrary();

        String bind_to;
        int message_count;
        int message_size;
        int socket = 0;
        int rc;
        int i;
        long begin = 0;
        long elapsed = 0;
        long throughput = 0;
        double megabits = 0;

        bind_to = args[0];
        message_size = Integer.parseInt(args[1]);
        message_count = Integer.parseInt(args[2]);
        System.out.printf("args: %s | %d | %d\n",
                          bind_to, message_size, message_count);

        socket = nano.nn_socket(nano.AF_SP, nano.NN_PAIR);
        if (socket < 0) {
            System.out.printf("error in nn_socket: %s\n",
                              nano.nn_strerror(nano.nn_errno()));
            return;
        }
        System.out.printf("NANO PAIR socket created\n");

        rc = nano.nn_bind(socket, bind_to);
        if (rc < 0) {
            System.out.printf("error in nn_bind(%s): %s\n",
                              bind_to,
                              nano.nn_strerror(nano.nn_errno()));
            return;
        }
        System.out.printf("NANO PAIR socket bound to %s\n", bind_to);

        ByteBuffer bb = ByteBuffer.allocateDirect(message_size);

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
        for (i = 0; i != message_count; i++) {
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
