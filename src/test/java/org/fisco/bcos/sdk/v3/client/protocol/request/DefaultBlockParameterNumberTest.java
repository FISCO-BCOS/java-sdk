package org.fisco.bcos.sdk.v3.client.protocol.request;

import org.junit.Assert;
import org.junit.Test;
import java.math.BigInteger;

public class DefaultBlockParameterNumberTest {

    @Test
    public void testConstructorWithBigInteger() {
        BigInteger blockNumber = BigInteger.valueOf(100);
        DefaultBlockParameterNumber parameter = new DefaultBlockParameterNumber(blockNumber);
        
        Assert.assertEquals(blockNumber, parameter.getBlockNumber());
    }

    @Test
    public void testConstructorWithLong() {
        long blockNumber = 200L;
        DefaultBlockParameterNumber parameter = new DefaultBlockParameterNumber(blockNumber);
        
        Assert.assertEquals(BigInteger.valueOf(blockNumber), parameter.getBlockNumber());
    }

    @Test
    public void testGetValueReturnsHexString() {
        DefaultBlockParameterNumber parameter = new DefaultBlockParameterNumber(BigInteger.valueOf(100));
        
        String value = parameter.getValue();
        Assert.assertNotNull(value);
        Assert.assertTrue(value.startsWith("0x"));
    }

    @Test
    public void testIsLatest() {
        DefaultBlockParameterNumber parameter = new DefaultBlockParameterNumber(BigInteger.valueOf(100));
        
        Assert.assertFalse(parameter.isLatest());
    }

    @Test
    public void testIsEarliest() {
        DefaultBlockParameterNumber parameter = new DefaultBlockParameterNumber(BigInteger.valueOf(100));
        
        Assert.assertFalse(parameter.isEarliest());
    }

    @Test
    public void testWithZeroBlockNumber() {
        DefaultBlockParameterNumber parameter = new DefaultBlockParameterNumber(BigInteger.ZERO);
        
        Assert.assertEquals(BigInteger.ZERO, parameter.getBlockNumber());
        Assert.assertEquals("0x0", parameter.getValue());
    }

    @Test
    public void testWithLargeBlockNumber() {
        BigInteger largeNumber = new BigInteger("999999999999999999");
        DefaultBlockParameterNumber parameter = new DefaultBlockParameterNumber(largeNumber);
        
        Assert.assertEquals(largeNumber, parameter.getBlockNumber());
    }
}
