#include <stdio.h>
#include "NanoUtil.h"

#include "org_nanomsg_NanoLibrary.h"

JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_load_1symbols(JNIEnv* env,
                                                                  jobject obj,
                                                                  jobject map)
{
    jclass cmap = 0;
    jclass cint = 0;
    jmethodID mput = 0;
    jmethodID mnew = 0;
    jint count = 0;

    cmap = (*env)->GetObjectClass(env, map);
    NANO_ASSERT(cmap);

    mput = (*env)->GetMethodID(env, cmap,
                               "put",
                               "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
    NANO_ASSERT(mput);

    cint = (*env)->FindClass(env, "java/lang/Integer");
    NANO_ASSERT(cint);

    mnew = (*env)->GetMethodID(env, cint,
                               "<init>",
                               "(I)V");
    NANO_ASSERT(mnew);

    for(count = 0; ; ++count) {
        const char* ckey;
        int cval;
        jstring jkey =  0;
        jobject jval = 0;

        ckey = nn_symbol(count, &cval);
        if (ckey == 0)
            break;
        // fprintf(stderr, "Got symbol #%d: [%s] -> %d\n", count, ckey, cval);

        jkey = (*env)->NewStringUTF(env, ckey);
        NANO_ASSERT(jkey);
        // fprintf(stderr, "Created Java String for [%s]\n", ckey);

        jval = (*env)->NewObject(env, cint, mnew, cval);
        NANO_ASSERT(jval);
        // fprintf(stderr, "Created Java Integer for [%d]\n", cval);

        (*env)->CallObjectMethod(env, map, mput, jkey, jval);
        // fprintf(stderr, "Inserted symbol in map: [%s] -> %d\n", ckey, cval);
    }

    return count;
}

JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1errno(JNIEnv* env,
                                                              jobject obj)
{
    return nn_errno();
}

JNIEXPORT jstring JNICALL Java_org_nanomsg_NanoLibrary_nn_1strerror(JNIEnv* env,
                                                                    jobject obj,
                                                                    jint errnum)
{
    const char* cerr = 0;
    jstring jerr = 0;

    cerr = nn_strerror(errnum);
    if (cerr == 0)
        cerr = "";

    jerr = (*env)->NewStringUTF(env, cerr);
    NANO_ASSERT(jerr);
    return jerr;
}

JNIEXPORT void JNICALL Java_org_nanomsg_NanoLibrary_nn_1term(JNIEnv* env,
                                                             jobject obj)
{
    nn_term();
}

JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1socket(JNIEnv* env,
                                                               jobject obj,
                                                               jint domain,
                                                               jint protocol)
{
    return nn_socket(domain, protocol);
}

JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1close(JNIEnv* env,
                                                              jobject obj,
                                                              jint socket)
{
    return nn_close(socket);
}

JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1bind(JNIEnv* env,
                                                             jobject obj,
                                                             jint socket,
                                                             jstring address)
{
    const char* cadd = 0;

    cadd = (*env)->GetStringUTFChars(env, address, NULL);
    NANO_ASSERT(cadd);

    return nn_bind(socket, cadd);
}

JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1connect(JNIEnv* env,
                                                                jobject obj,
                                                                jint socket,
                                                                jstring address)
{
    const char* cadd = 0;

    cadd = (*env)->GetStringUTFChars(env, address, NULL);
    NANO_ASSERT(cadd);

    return nn_connect(socket, cadd);
}

JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1shutdown(JNIEnv* env,
                                                                 jobject obj,
                                                                 jint socket,
                                                                 jint how)
{
    return nn_shutdown(socket, how);
}

JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1send(JNIEnv* env,
                                                             jobject obj,
                                                             jint socket,
                                                             jobject buffer,
                                                             jint offset,
                                                             jint length,
                                                             jint flags)
{
    jbyte* cbuf = 0;
    jint ret = 0;

    cbuf = (jbyte*) (*env)->GetDirectBufferAddress(env, buffer);
    NANO_ASSERT(cbuf);
    ret = nn_send(socket, cbuf + offset, length, flags);

    return ret;
}

JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1recv(JNIEnv* env,
                                                             jobject obj,
                                                             jint socket,
                                                             jobject buffer,
                                                             jint offset,
                                                             jint length,
                                                             jint flags)
{
    jbyte* cbuf = 0;
    jint ret = 0;

    cbuf = (jbyte*) (*env)->GetDirectBufferAddress(env, buffer);
    NANO_ASSERT(cbuf);
    ret = nn_recv(socket, cbuf + offset, length, flags);

    return ret;
}

JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1getsockopt_1int(JNIEnv* env,
                                                                        jobject obj,
                                                                        jint socket,
                                                                        jint level,
                                                                        jint optidx,
                                                                        jobject optval)
{
    jint ret = -1;

    switch (optidx) {
    case NN_DOMAIN:
    case NN_PROTOCOL:
    case NN_LINGER:
    case NN_SNDBUF:
    case NN_RCVBUF:
    case NN_SNDTIMEO:
    case NN_RCVTIMEO:
    case NN_RECONNECT_IVL:
    case NN_RECONNECT_IVL_MAX:
    case NN_SNDPRIO:
    case NN_SNDFD:
    case NN_RCVFD:
        do {
            int oval = 0;
            size_t olen = sizeof(oval);

            ret = nn_getsockopt(socket, level, optidx, &oval, &olen);
            if (ret >= 0) {
                jclass cval = 0;
                jfieldID ival = 0;

                ret = olen;

                cval = (*env)->GetObjectClass(env, optval);
                NANO_ASSERT(cval);

                ival = (*env)->GetFieldID(env, cval, "value", "I");
                NANO_ASSERT(ival);

                (*env)->SetIntField(env, optval, ival, oval);
            }
        } while (0);
        break;
    }

    return ret;
}

JNIEXPORT jint JNICALL Java_org_nanomsg_NanoLibrary_nn_1setsockopt_1int(JNIEnv* env,
                                                                        jobject obj,
                                                                        jint socket,
                                                                        jint level,
                                                                        jint optidx,
                                                                        jint optval)
{
    jint ret = -1;

    switch (optidx) {
    case NN_LINGER:
    case NN_SNDBUF:
    case NN_RCVBUF:
    case NN_SNDTIMEO:
    case NN_RCVTIMEO:
    case NN_RECONNECT_IVL:
    case NN_RECONNECT_IVL_MAX:
    case NN_SNDPRIO:
        do {
            int oval = optval;
            size_t olen = sizeof(oval);

            ret = nn_setsockopt(socket, level, optidx, &oval, olen);
            if (ret >= 0) {
                ret = olen;
            }
        } while (0);
        break;
    }

    return ret;
}
