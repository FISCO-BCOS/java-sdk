package org.fisco.bcos.sdk.v3.test.transaction.gasProvider;

import org.fisco.bcos.sdk.v3.transaction.gasProvider.ContractGasProvider;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.DefaultGasProvider;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.StaticGasProvider;
import org.junit.Assert;
import org.junit.Test;
import java.math.BigInteger;

public class DefaultGasProviderTest {

    @Test
    public void testDefaultGasLimit() {
        Assert.assertEquals(BigInteger.valueOf(9_000_000), DefaultGasProvider.GAS_LIMIT);
    }

    @Test
    public void testDefaultGasPrice() {
        Assert.assertEquals(BigInteger.valueOf(4_100_000_000L), DefaultGasProvider.GAS_PRICE);
    }

    @Test
    public void testConstructor() {
        DefaultGasProvider provider = new DefaultGasProvider();
        
        Assert.assertNotNull(provider);
    }

    @Test
    public void testGetGasPrice() {
        DefaultGasProvider provider = new DefaultGasProvider();
        
        BigInteger gasPrice = provider.getGasPrice("test");
        Assert.assertEquals(DefaultGasProvider.GAS_PRICE, gasPrice);
    }

    @Test
    public void testGetGasLimit() {
        DefaultGasProvider provider = new DefaultGasProvider();
        
        BigInteger gasLimit = provider.getGasLimit("test");
        Assert.assertEquals(DefaultGasProvider.GAS_LIMIT, gasLimit);
    }

    @Test
    public void testGetGasPriceWithByteArray() {
        DefaultGasProvider provider = new DefaultGasProvider();
        
        byte[] methodId = {0x01, 0x02};
        BigInteger gasPrice = provider.getGasPrice(methodId);
        Assert.assertEquals(DefaultGasProvider.GAS_PRICE, gasPrice);
    }

    @Test
    public void testGetGasLimitWithByteArray() {
        DefaultGasProvider provider = new DefaultGasProvider();
        
        byte[] methodId = {0x01, 0x02};
        BigInteger gasLimit = provider.getGasLimit(methodId);
        Assert.assertEquals(DefaultGasProvider.GAS_LIMIT, gasLimit);
    }

    @Test
    public void testIsEIP1559Enabled() {
        DefaultGasProvider provider = new DefaultGasProvider();
        
        Assert.assertFalse(provider.isEIP1559Enabled());
    }

    @Test
    public void testExtendsStaticGasProvider() {
        DefaultGasProvider provider = new DefaultGasProvider();
        
        Assert.assertTrue(provider instanceof StaticGasProvider);
        Assert.assertTrue(provider instanceof ContractGasProvider);
    }
}
