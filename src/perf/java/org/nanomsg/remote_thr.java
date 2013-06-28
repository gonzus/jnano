package org.nanomsg;

import java.nio.ByteBuffer;

public class remote_thr {
    public static void main(String [] args) {
        if (args.length != 3) {
            System.out.printf("argc was %d\n", args.length);
            System.out.printf("usage: remote_thr <connect-to> <message-size> <message-count>\n");
            return;
        }

        NanoLibrary nano = new NanoLibrary();

        String connect_to;
        int message_count;
        int message_size;
        int socket = 0;
        int rc;
        int i;

        connect_to = args[0];
        message_size = Integer.parseInt(args[1]);
        message_count = Integer.parseInt(args[2]);
        System.out.printf("args: %s | %d | %d\n",
                          connect_to, message_size, message_count);

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

        System.out.printf("NANO running %d iterations...\n", message_count);
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

        System.out.printf("NANO done running\n");
    }
}
