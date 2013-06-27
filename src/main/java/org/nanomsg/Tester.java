package org.nanomsg;

import java.nio.ByteBuffer;

public class Tester {
    public static void main(String [] args) {
        Tester tester = new Tester();
        tester.allocate();
        tester.testLibrary();
        tester.testSymbols();
        tester.testBind();
        tester.testConnect();
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

    private void testBind() {
        System.out.printf("=== Testing Bind =======\n");

        int ret = 0;
        int domain_af_sp = nano.get_symbol("AF_SP");
        int protocol_pair = nano.get_symbol("NN_PAIR");

        int socket = nano.nn_socket(domain_af_sp,
                                   protocol_pair);
        System.out.printf("Created socket 1 (%d, %d) => %d\n",
                          domain_af_sp, protocol_pair, socket);
        if (socket < 0) {
            int errn = nano.nn_errno();
            System.out.printf("*** Error: %d - %s\n",
                              errn, nano.nn_strerror(errn));
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
        /*
        ret = nano.nn_shutdown(socket, ep1);
        System.out.printf("Shutdown endpoint 1 (%d) in socket 1 (%d) => %d\n",
                          ep1, socket, ret);
        if (ret < 0) {
            int errn = nano.nn_errno();
            System.out.printf("*** Error: %d - %s\n",
                              errn, nano.nn_strerror(errn));
        }
        */
        ret = nano.nn_close(socket);
        System.out.printf("Closed socket 1 %d => %d\n",
                          socket, ret);
        if (ret < 0) {
            int errn = nano.nn_errno();
            System.out.printf("*** Error: %d - %s\n",
                              errn, nano.nn_strerror(errn));
        }
    }

    private void testConnect() {
        System.out.printf("=== Testing Connect ====\n");

        int ret = 0;
        int domain_af_sp = nano.get_symbol("AF_SP");
        int protocol_pair = nano.get_symbol("NN_PAIR");
        int socket = nano.nn_socket(domain_af_sp,
                                    protocol_pair);
        System.out.printf("Created socket (%d, %d) => %d\n",
                          domain_af_sp, protocol_pair, socket);
        if (socket < 0) {
            int errn = nano.nn_errno();
            System.out.printf("*** Error: %d - %s\n",
                              errn, nano.nn_strerror(errn));
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
        }
    }


    private NanoLibrary nano = null;
}
