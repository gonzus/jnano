package org.nanomsg;

import java.nio.ByteBuffer;

public class remote_lat {
    public static void main(String [] args) {
        if (args.length != 3) {
            System.out.printf("argc was %d\n", args.length);
            System.out.printf("usage: remote_lat <connect-to> <message-size> <roundtrip-count>\n");
            return;
        }

        NanoLibrary nano = new NanoLibrary();

        String connect_to;
        int roundtrip_count;
        int message_size;
        int socket = 0;
        int rc;
        int i;

        long watch = 0;
        long begin = 0;
        long elapsed = 0;
        double latency = 0.0;

        connect_to = args[0];
        message_size = Integer.parseInt(args[1]);
        roundtrip_count = Integer.parseInt(args[2]);
        System.out.printf("args: %s | %d | %d\n",
                          connect_to, message_size, roundtrip_count);

        socket = nano.nn_socket(nano.AF_SP, nano.NN_PAIR);
        if (socket < 0) {
            System.out.printf("error in nn_socket: %s\n",
                              nano.nn_strerror(nano.nn_errno()));
            return;
        }
        System.out.printf("NANO PAIR socket created\n");

        rc = nano.nn_connect(socket, connect_to);
        if (rc < 0) {
            System.out.printf("error in nn_connect(%s): %s\n",
                              connect_to,
                              nano.nn_strerror(nano.nn_errno()));
            return;
        }
        System.out.printf("NANO PAIR socket connected to %s\n", connect_to);

        ByteBuffer bb = ByteBuffer.allocateDirect(message_size);
        byte bval = 111;
        for (i = 0; i < message_size; ++i) {
            bb.put(i, bval);
        }

        System.out.printf("NANO running %d iterations...\n", roundtrip_count);
        begin = System.nanoTime();
        for (i = 0; i != roundtrip_count; ++i) {
            bb.rewind();
            rc = nano.nn_send(socket, bb, 0);
            if (rc < 0) {
                System.out.printf("error in nn_send: %s\n",
                                  nano.nn_strerror(nano.nn_errno()));
                return;
            }
            if (rc != message_size) {
                System.out.printf("message of incorrect size sent\n");
                return;
            }

            bb.clear();
            rc = nano.nn_recv(socket, bb, 0);
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
