package org.nanomsg;

import java.nio.ByteBuffer;

public class Tester {
    public static void main(String [] args) {
        Tester tester = new Tester();
        tester.allocate();
        if (tester.testLibrary()) {
            tester.testSymbols();
            tester.testOptions();
            tester.testBind();
            tester.testConnect();
        }
        tester.dispose();
    }

    public Tester() {
    }

    private void allocate() {
        System.out.printf("Starting Tester\n");
        nano = new NanoLibrary();
    }

    private void dispose() {
        nano.nn_term();
        nano = null;
        System.out.printf("Finished Tester\n");
    }

    private boolean testLibrary() {
        System.out.printf("=== Testing Library ====\n");

        if (nano == null) {
            System.out.printf("nano library is null\n");
            return false;
        }

        int version = -1;
        version = nano.get_version();

        System.out.printf("nano library version %06d loaded OK (%d symbols)\n",
                          version, nano.get_symbol_count());

        int errn = nano.nn_errno();
        String errs = nano.nn_strerror(errn);
        System.out.printf("nano library errno (probably meaningless) is %d - [%s]\n",
                          errn, errs);
        return true;
    }

    private void testSymbols() {
        System.out.printf("=== Testing Symbols ====\n");

        String key_enomem = "ENOMEM";
        int val_enomem = nano.get_symbol(key_enomem);
        String err_enomem = nano.nn_strerror(val_enomem);

        System.out.printf("Symbol [%s] => %d\n",
                          key_enomem, val_enomem);
        System.out.printf("Error for [%s] => [%s]\n",
                          key_enomem, err_enomem);
    }

    private void testOptions() {
        System.out.printf("=== Testing Options ====\n");

        /*
         * WARNING
         *
         * Use new() for this variable or be a victim of Java's
         * misguided attempts to cache Integer values.  For more
         * information, visit
         * http://stackoverflow.com/questions/1995113/strangest-language-feature
         * and search for "Fun with auto boxing and the integer cache
         * in Java"...
         */
        Integer optval = new Integer(-1);
        int optnew = -1;
        int ret = 0;

        int socket = nano.nn_socket(nano.AF_SP,
                                    nano.NN_PAIR);
        System.out.printf("Created socket (%d, %d) => %d\n",
                          nano.AF_SP, nano.NN_PAIR, socket);
        if (socket < 0) {
            int errn = nano.nn_errno();
            System.out.printf("*** Error: %d - %s\n",
                              errn, nano.nn_strerror(errn));
            return;
        }

        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_DOMAIN, optval);
        System.out.printf("Socket %d, old domain is %d (%d)\n",
                          socket, optval.intValue(), ret);
        optnew = optval.intValue() + 1;
        ret = nano.nn_setsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_DOMAIN, optnew);
        System.out.printf("Socket %d, set domain to %d (%d)\n",
                          socket, optnew, ret);
        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_DOMAIN, optval);
        System.out.printf("Socket %d, new domain is %d (%d)\n",
                          socket, optval.intValue(), ret);

        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_PROTOCOL, optval);
        System.out.printf("Socket %d, old protocol is %d (%d)\n",
                          socket, optval.intValue(), ret);
        optnew = optval.intValue() + 1;
        ret = nano.nn_setsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_PROTOCOL, optnew);
        System.out.printf("Socket %d, set protocol to %d (%d)\n",
                          socket, optnew, ret);
        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_PROTOCOL, optval);
        System.out.printf("Socket %d, new protocol is %d (%d)\n",
                          socket, optval.intValue(), ret);

        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_LINGER, optval);
        System.out.printf("Socket %d, old linger is %d (%d)\n",
                          socket, optval.intValue(), ret);
        optnew = optval.intValue() * 2;
        ret = nano.nn_setsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_LINGER, optnew);
        System.out.printf("Socket %d, set linger to %d (%d)\n",
                          socket, optnew, ret);
        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_LINGER, optval);
        System.out.printf("Socket %d, new linger is %d (%d)\n",
                          socket, optval.intValue(), ret);

        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_SNDBUF, optval);
        System.out.printf("Socket %d, old sndbuf is %d (%d)\n",
                          socket, optval.intValue(), ret);
        optnew = optval.intValue() * 2;
        ret = nano.nn_setsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_SNDBUF, optnew);
        System.out.printf("Socket %d, set sndbuf to %d (%d)\n",
                          socket, optnew, ret);
        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_SNDBUF, optval);
        System.out.printf("Socket %d, new sndbuf is %d (%d)\n",
                          socket, optval.intValue(), ret);

        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_RCVBUF, optval);
        System.out.printf("Socket %d, old rcvbuf is %d (%d)\n",
                          socket, optval.intValue(), ret);
        optnew = optval.intValue() * 2;
        ret = nano.nn_setsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_RCVBUF, optnew);
        System.out.printf("Socket %d, set rcvbuf to %d (%d)\n",
                          socket, optnew, ret);
        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_RCVBUF, optval);
        System.out.printf("Socket %d, new rcvbuf is %d (%d)\n",
                          socket, optval.intValue(), ret);

        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_SNDTIMEO, optval);
        System.out.printf("Socket %d, old sndtimeo is %d (%d)\n",
                          socket, optval.intValue(), ret);
        optnew = 500;
        ret = nano.nn_setsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_SNDTIMEO, optnew);
        System.out.printf("Socket %d, set sndtimeo to %d (%d)\n",
                          socket, optnew, ret);
        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_SNDTIMEO, optval);
        System.out.printf("Socket %d, new sndtimeo is %d (%d)\n",
                          socket, optval.intValue(), ret);

        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_RCVTIMEO, optval);
        System.out.printf("Socket %d, old rcvtimeo is %d (%d)\n",
                          socket, optval.intValue(), ret);
        optnew = 500;
        ret = nano.nn_setsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_RCVTIMEO, optnew);
        System.out.printf("Socket %d, set rcvtimeo to %d (%d)\n",
                          socket, optnew, ret);
        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_RCVTIMEO, optval);
        System.out.printf("Socket %d, new rcvtimeo is %d (%d)\n",
                          socket, optval.intValue(), ret);

        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_RECONNECT_IVL, optval);
        System.out.printf("Socket %d, old reconnect_ivl is %d (%d)\n",
                          socket, optval.intValue(), ret);
        optnew = optval.intValue() * 2;
        ret = nano.nn_setsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_RECONNECT_IVL, optnew);
        System.out.printf("Socket %d, set reconnect_ivl to %d (%d)\n",
                          socket, optnew, ret);
        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_RECONNECT_IVL, optval);
        System.out.printf("Socket %d, new reconnect_ivl is %d (%d)\n",
                          socket, optval.intValue(), ret);

        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_RECONNECT_IVL_MAX, optval);
        System.out.printf("Socket %d, old reconnect_ivl_max is %d (%d)\n",
                          socket, optval.intValue(), ret);
        optnew = 2;
        ret = nano.nn_setsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_RECONNECT_IVL_MAX, optnew);
        System.out.printf("Socket %d, set reconnect_ivl_max to %d (%d)\n",
                          socket, optnew, ret);
        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_RECONNECT_IVL_MAX, optval);
        System.out.printf("Socket %d, new reconnect_ivl_max is %d (%d)\n",
                          socket, optval.intValue(), ret);

        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_SNDPRIO, optval);
        System.out.printf("Socket %d, old sndprio is %d (%d)\n",
                          socket, optval.intValue(), ret);
        optnew = optval.intValue() + 1;
        ret = nano.nn_setsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_SNDPRIO, optnew);
        System.out.printf("Socket %d, set sndprio to %d (%d)\n",
                          socket, optnew, ret);
        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_SNDPRIO, optval);
        System.out.printf("Socket %d, new sndprio is %d (%d)\n",
                          socket, optval.intValue(), ret);

        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_SNDFD, optval);
        System.out.printf("Socket %d, old sndfd is %d (%d)\n",
                          socket, optval.intValue(), ret);
        optnew = optval.intValue() + 1;
        ret = nano.nn_setsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_SNDFD, optnew);
        System.out.printf("Socket %d, set sndfd to %d (%d)\n",
                          socket, optnew, ret);
        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_SNDFD, optval);
        System.out.printf("Socket %d, new sndfd is %d (%d)\n",
                          socket, optval.intValue(), ret);

        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_RCVFD, optval);
        System.out.printf("Socket %d, old rcvfd is %d (%d)\n",
                          socket, optval.intValue(), ret);
        optnew = optval.intValue() + 1;
        ret = nano.nn_setsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_RCVFD, optnew);
        System.out.printf("Socket %d, set rcvfd to %d (%d)\n",
                          socket, optnew, ret);
        ret = nano.nn_getsockopt_int(socket, nano.NN_SOL_SOCKET, nano.NN_RCVFD, optval);
        System.out.printf("Socket %d, new rcvfd is %d (%d)\n",
                          socket, optval.intValue(), ret);

        ret = nano.nn_close(socket);
        System.out.printf("Closed socket %d => %d\n",
                          socket, ret);
        if (ret < 0) {
            int errn = nano.nn_errno();
            System.out.printf("*** Error: %d - %s\n",
                              errn, nano.nn_strerror(errn));
            return;
        }
    }

    private void testBind() {
        System.out.printf("=== Testing Bind =======\n");
        int ret = 0;

        int socket = nano.nn_socket(nano.AF_SP,
                                    nano.NN_PAIR);
        System.out.printf("Created socket 1 (%d, %d) => %d\n",
                          nano.AF_SP, nano.NN_PAIR, socket);
        if (socket < 0) {
            int errn = nano.nn_errno();
            System.out.printf("*** Error: %d - %s\n",
                              errn, nano.nn_strerror(errn));
            return;
        }

        String addr1 = "tcp://127.0.0.1:9991";
        int ep1 = nano.nn_bind(socket, addr1);
        System.out.printf("Bound socket 1 (%d) to [%s] => %d\n",
                          socket, addr1, ep1);
        if (ep1 < 0) {
            int errn = nano.nn_errno();
            System.out.printf("*** Error: %d - %s\n",
                              errn, nano.nn_strerror(errn));
        }

        String addr2 = "tcp://127.0.0.1:9992";
        int ep2 = nano.nn_bind(socket, addr2);
        System.out.printf("Bound socket 1 (%d) to [%s] => %d\n",
                          socket, addr2, ret);
        if (ep2 < 0) {
            int errn = nano.nn_errno();
            System.out.printf("*** Error: %d - %s\n",
                              errn, nano.nn_strerror(errn));
        }

        ret = nano.nn_close(socket);
        System.out.printf("Closed socket 1 %d => %d\n",
                          socket, ret);
        if (ret < 0) {
            int errn = nano.nn_errno();
            System.out.printf("*** Error: %d - %s\n",
                              errn, nano.nn_strerror(errn));
            return;
        }
    }

    private void testConnect() {
        System.out.printf("=== Testing Connect ====\n");
        int ret = 0;

        int socket = nano.nn_socket(nano.AF_SP,
                                    nano.NN_PAIR);
        System.out.printf("Created socket (%d, %d) => %d\n",
                          nano.AF_SP, nano.NN_PAIR, socket);
        if (socket < 0) {
            int errn = nano.nn_errno();
            System.out.printf("*** Error: %d - %s\n",
                              errn, nano.nn_strerror(errn));
            return;
        }

        String addr1 = "tcp://127.0.0.1:9991";
        int ep1 = nano.nn_connect(socket, addr1);
        System.out.printf("Connected socket 1 (%d) to [%s] => %d\n",
                          socket, addr1, ep1);
        if (ep1 < 0) {
            int errn = nano.nn_errno();
            System.out.printf("*** Error: %d - %s\n",
                              errn, nano.nn_strerror(errn));
        }

        String addr2 = "tcp://127.0.0.1:9992";
        int ep2 = nano.nn_connect(socket, addr2);
        System.out.printf("Connected socket 1 (%d) to [%s] => %d\n",
                          socket, addr2, ret);
        if (ep2 < 0) {
            int errn = nano.nn_errno();
            System.out.printf("*** Error: %d - %s\n",
                              errn, nano.nn_strerror(errn));
        }

        ret = nano.nn_close(socket);
        System.out.printf("Closed socket %d => %d\n",
                          socket, ret);
        if (ret < 0) {
            int errn = nano.nn_errno();
            System.out.printf("*** Error: %d - %s\n",
                              errn, nano.nn_strerror(errn));
            return;
        }
    }


    private NanoLibrary nano = null;
}
