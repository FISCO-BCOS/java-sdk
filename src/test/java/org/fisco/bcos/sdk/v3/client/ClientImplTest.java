package org.fisco.bcos.sdk.v3.client;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import org.fisco.bcos.sdk.jni.rpc.RpcJniObj;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.junit.Test;
import org.objenesis.ObjenesisStd;

public class ClientImplTest {

    private static ClientImpl allocateClientImpl() {
        return new ObjenesisStd().newInstance(ClientImpl.class);
    }

    @Test
    public void testStopIsIdempotent() throws Exception {
        ClientImpl client = allocateClientImpl();
        RpcJniObj rpcJniObj = mock(RpcJniObj.class);
        setField(client, "rpcJniObj", rpcJniObj);
        setBooleanField(client, "started", true);

        client.stop();
        client.stop();

        verify(rpcJniObj, times(1)).stop();
    }

    @Test
    public void testDestroyIsIdempotent() throws Exception {
        ClientImpl client = allocateClientImpl();
        CryptoSuite cryptoSuite = mock(CryptoSuite.class);
        // rpcJniObj is left null so BcosSDKJniObj.destroy() static native call is skipped
        setField(client, "cryptoSuite", cryptoSuite);
        setBooleanField(client, "started", true);

        client.destroy();
        client.destroy();

        verify(cryptoSuite, times(1)).destroy();
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static void setBooleanField(Object target, String fieldName, boolean value)
            throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.setBoolean(target, value);
    }
}
