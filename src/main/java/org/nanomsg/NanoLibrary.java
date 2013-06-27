package org.nanomsg;

import java.util.Map;
import java.util.HashMap;
import java.nio.ByteBuffer;

public class NanoLibrary {
    static
    {
        ensureNativeCode();
    }

    public NanoLibrary()
    {
        symbols = new HashMap<String, Integer>();
        load_symbols(symbols);

        AF_SP = get_symbol("AF_SP");
        AF_SP_RAW = get_symbol("AF_SP_RAW");

        NN_INPROC = get_symbol("NN_INPROC");
        NN_IPC = get_symbol("NN_IPC");
        NN_TCP = get_symbol("NN_TCP");

        NN_PAIR = get_symbol("NN_PAIR");
        NN_PUB = get_symbol("NN_PUB");
        NN_SUB = get_symbol("NN_SUB");
        NN_REP = get_symbol("NN_REP");
        NN_REQ = get_symbol("NN_REQ");
        NN_SOURCE = get_symbol("NN_SOURCE");
        NN_SINK = get_symbol("NN_SINK");
        NN_PUSH = get_symbol("NN_PUSH");
        NN_PULL = get_symbol("NN_PULL");
        NN_SURVEYOR = get_symbol("NN_SURVEYOR");
        NN_RESPONDENT = get_symbol("NN_RESPONDENT");
        NN_BUS = get_symbol("NN_BUS");
    }

    public native void nn_term();
    public native int nn_errno();
    public native String nn_strerror(int errnum);

    public native int nn_socket(int domain,
                                int protocol);
    public native int nn_close(int socket);
    public native int nn_bind(int socket,
                              String address);
    public native int nn_connect(int socket,
                                 String address);
    public native int nn_shutdown(int socket,
                                  int how);
    public native int nn_send(int socket,
                              ByteBuffer buffer,
                              int offset,
                              int length,
                              int flags);
    public native int nn_recv(int socket,
                              ByteBuffer buffer,
                              int offset,
                              int length,
                              int flags);

    public int get_version()
    {
        int maj = get_symbol("NN_VERSION_MAJOR");
        int min = get_symbol("NN_VERSION_MINOR");
        int pat = get_symbol("NN_VERSION_PATCH");
        int ver = maj * 10000 + min * 100 + pat;

        return ver;
    }

    public int get_symbol_count()
    {
        return symbols.size();
    }

    public int get_symbol(String name)
    {
        Integer value = symbols.get(name);
        if (value == null)
            return -1;
        return value.intValue();
    }

    public int AF_SP = -1;
    public int AF_SP_RAW = -1;

    public int NN_INPROC = -1;
    public int NN_IPC = -1;
    public int NN_TCP = -1;

    public int NN_PAIR = -1;
    public int NN_PUB = -1;
    public int NN_SUB = -1;
    public int NN_REP = -1;
    public int NN_REQ = -1;
    public int NN_SOURCE = -1;
    public int NN_SINK = -1;
    public int NN_PUSH = -1;
    public int NN_PULL = -1;
    public int NN_SURVEYOR = -1;
    public int NN_RESPONDENT = -1;
    public int NN_BUS = -1;


    private static void ensureNativeCode()
    {
        System.loadLibrary ("jnano");
    }

    private native int load_symbols(Map<String, Integer> map);

    private Map<String, Integer> symbols;
}
