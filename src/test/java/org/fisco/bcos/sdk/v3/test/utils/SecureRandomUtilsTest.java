package org.fisco.bcos.sdk.v3.test.utils;

import org.junit.Assert;
import org.junit.Test;
import java.lang.reflect.Method;
import java.security.SecureRandom;

public class SecureRandomUtilsTest {

    @Test
    public void testSecureRandomInstance() throws Exception {
        // Use reflection to access the package-private class and method
        Class<?> secureRandomUtilsClass = Class.forName("org.fisco.bcos.sdk.v3.utils.SecureRandomUtils");
        Method secureRandomMethod = secureRandomUtilsClass.getDeclaredMethod("secureRandom");
        secureRandomMethod.setAccessible(true);
        
        Object result = secureRandomMethod.invoke(null);
        
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof SecureRandom);
    }

    @Test
    public void testIsAndroidRuntime() throws Exception {
        // Use reflection to access the isAndroidRuntime method
        Class<?> secureRandomUtilsClass = Class.forName("org.fisco.bcos.sdk.v3.utils.SecureRandomUtils");
        Method isAndroidRuntimeMethod = secureRandomUtilsClass.getDeclaredMethod("isAndroidRuntime");
        isAndroidRuntimeMethod.setAccessible(true);
        
        Object result = isAndroidRuntimeMethod.invoke(null);
        
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof Boolean);
        // In a normal JVM environment, this should be false
        Assert.assertFalse((Boolean) result);
    }

    @Test
    public void testSecureRandomConsistency() throws Exception {
        // Test that multiple calls return the same instance
        Class<?> secureRandomUtilsClass = Class.forName("org.fisco.bcos.sdk.v3.utils.SecureRandomUtils");
        Method secureRandomMethod = secureRandomUtilsClass.getDeclaredMethod("secureRandom");
        secureRandomMethod.setAccessible(true);
        
        SecureRandom first = (SecureRandom) secureRandomMethod.invoke(null);
        SecureRandom second = (SecureRandom) secureRandomMethod.invoke(null);
        
        Assert.assertSame("SecureRandom instance should be the same", first, second);
    }

    @Test
    public void testSecureRandomGeneratesRandomBytes() throws Exception {
        // Test that the SecureRandom can generate random bytes
        Class<?> secureRandomUtilsClass = Class.forName("org.fisco.bcos.sdk.v3.utils.SecureRandomUtils");
        Method secureRandomMethod = secureRandomUtilsClass.getDeclaredMethod("secureRandom");
        secureRandomMethod.setAccessible(true);
        
        SecureRandom random = (SecureRandom) secureRandomMethod.invoke(null);
        
        byte[] bytes1 = new byte[16];
        byte[] bytes2 = new byte[16];
        
        random.nextBytes(bytes1);
        random.nextBytes(bytes2);
        
        Assert.assertNotNull(bytes1);
        Assert.assertNotNull(bytes2);
        Assert.assertEquals(16, bytes1.length);
        Assert.assertEquals(16, bytes2.length);
        
        // With high probability, the two random byte arrays should be different
        boolean different = false;
        for (int i = 0; i < bytes1.length; i++) {
            if (bytes1[i] != bytes2[i]) {
                different = true;
                break;
            }
        }
        Assert.assertTrue("Random bytes should be different", different);
    }
}
