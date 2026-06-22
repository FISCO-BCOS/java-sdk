package org.fisco.bcos.sdk.v3.test.transaction.gasProvider;

import org.fisco.bcos.sdk.v3.transaction.gasProvider.EIP1559Struct;
import org.fisco.bcos.sdk.v3.transaction.gasProvider.StaticGasProvider;
import org.junit.Assert;
import org.junit.Test;
import java.math.BigInteger;

public class StaticGasProviderTest {

    @Test
    public void testConstructorAndGetters() {
        BigInteger gasPrice = BigInteger.valueOf(1000);
        BigInteger gasLimit = BigInteger.valueOf(21000);
        
        StaticGasProvider provider = new StaticGasProvider(gasPrice, gasLimit);
        
        Assert.assertEquals(gasPrice, provider.getGasPrice("test"));
        Assert.assertEquals(gasLimit, provider.getGasLimit("test"));
    }

    @Test
    public void testGetGasPriceWithString() {
        BigInteger gasPrice = BigInteger.valueOf(2000);
        BigInteger gasLimit = BigInteger.valueOf(30000);
        
        StaticGasProvider provider = new StaticGasProvider(gasPrice, gasLimit);
        
        Assert.assertEquals(gasPrice, provider.getGasPrice("function1"));
        Assert.assertEquals(gasPrice, provider.getGasPrice("function2"));
    }

    @Test
    public void testGetGasPriceWithByteArray() {
        BigInteger gasPrice = BigInteger.valueOf(3000);
        BigInteger gasLimit = BigInteger.valueOf(40000);
        
        StaticGasProvider provider = new StaticGasProvider(gasPrice, gasLimit);
        
        byte[] methodId = {0x01, 0x02, 0x03};
        Assert.assertEquals(gasPrice, provider.getGasPrice(methodId));
    }

    @Test
    public void testGetGasLimitWithString() {
        BigInteger gasPrice = BigInteger.valueOf(1000);
        BigInteger gasLimit = BigInteger.valueOf(50000);
        
        StaticGasProvider provider = new StaticGasProvider(gasPrice, gasLimit);
        
        Assert.assertEquals(gasLimit, provider.getGasLimit("function1"));
        Assert.assertEquals(gasLimit, provider.getGasLimit("function2"));
    }

    @Test
    public void testGetGasLimitWithByteArray() {
        BigInteger gasPrice = BigInteger.valueOf(1000);
        BigInteger gasLimit = BigInteger.valueOf(60000);
        
        StaticGasProvider provider = new StaticGasProvider(gasPrice, gasLimit);
        
        byte[] methodId = {0x04, 0x05, 0x06};
        Assert.assertEquals(gasLimit, provider.getGasLimit(methodId));
    }

    @Test
    public void testIsEIP1559Enabled() {
        StaticGasProvider provider = new StaticGasProvider(BigInteger.valueOf(1000), BigInteger.valueOf(21000));
        
        Assert.assertFalse(provider.isEIP1559Enabled());
    }

    @Test
    public void testGetEIP1559StructWithString() {
        BigInteger gasLimit = BigInteger.valueOf(70000);
        StaticGasProvider provider = new StaticGasProvider(BigInteger.valueOf(1000), gasLimit);
        
        EIP1559Struct struct = provider.getEIP1559Struct("test");
        
        Assert.assertEquals(BigInteger.ZERO, struct.getMaxFeePerGas());
        Assert.assertEquals(BigInteger.ZERO, struct.getMaxPriorityFeePerGas());
        Assert.assertEquals(gasLimit, struct.getGasLimit());
    }

    @Test
    public void testGetEIP1559StructWithByteArray() {
        BigInteger gasLimit = BigInteger.valueOf(80000);
        StaticGasProvider provider = new StaticGasProvider(BigInteger.valueOf(1000), gasLimit);
        
        byte[] methodId = {0x07, 0x08, 0x09};
        EIP1559Struct struct = provider.getEIP1559Struct(methodId);
        
        Assert.assertEquals(BigInteger.ZERO, struct.getMaxFeePerGas());
        Assert.assertEquals(BigInteger.ZERO, struct.getMaxPriorityFeePerGas());
        Assert.assertEquals(gasLimit, struct.getGasLimit());
    }

    @Test
    public void testWithZeroValues() {
        StaticGasProvider provider = new StaticGasProvider(BigInteger.ZERO, BigInteger.ZERO);
        
        Assert.assertEquals(BigInteger.ZERO, provider.getGasPrice("test"));
        Assert.assertEquals(BigInteger.ZERO, provider.getGasLimit("test"));
    }
}
