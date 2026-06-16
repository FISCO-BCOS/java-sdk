package org.fisco.bcos.sdk.v3.client;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import org.fisco.bcos.sdk.jni.rpc.RpcJniObj;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.junit.Test;
import sun.misc.Unsafe;

public class ClientImplTest {

    @Test
    public void testStopAndDestroyAreIdempotent() throws Exception {
        ClientImpl client = (ClientImpl) getUnsafe().allocateInstance(ClientImpl.class);
        RpcJniObj rpcJniObj = mock(RpcJniObj.class);
        when(rpcJniObj.getNativePointer()).thenReturn(0L);
        CryptoSuite cryptoSuite = mock(CryptoSuite.class);

        setField(client, "rpcJniObj", rpcJniObj);
        setField(client, "cryptoSuite", cryptoSuite);
        setBooleanField(client, "started", true);

        client.stop();
        client.stop();
        client.destroy();
        client.destroy();

        verify(rpcJniObj, times(1)).stop();
        verify(cryptoSuite, times(1)).destroy();
    }

    private static Unsafe getUnsafe() throws Exception {
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        return (Unsafe) field.get(null);
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
