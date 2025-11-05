package org.fisco.bcos.sdk.v3.test.client.protocol.request;

import org.fisco.bcos.sdk.v3.client.protocol.request.DefaultBlockParameter;
import org.fisco.bcos.sdk.v3.client.protocol.request.DefaultBlockParameterName;
import org.fisco.bcos.sdk.v3.client.protocol.request.DefaultBlockParameterNumber;
import org.junit.Assert;
import org.junit.Test;
import java.math.BigInteger;

public class DefaultBlockParameterTest {

    @Test
    public void testValueOfWithBigInteger() {
        BigInteger blockNumber = BigInteger.valueOf(100);
        DefaultBlockParameter parameter = DefaultBlockParameter.valueOf(blockNumber);
        
        Assert.assertNotNull(parameter);
        Assert.assertTrue(parameter instanceof DefaultBlockParameterNumber);
    }

    @Test
    public void testValueOfWithInt() {
        int blockNumber = 200;
        DefaultBlockParameter parameter = DefaultBlockParameter.valueOf(blockNumber);
        
        Assert.assertNotNull(parameter);
        Assert.assertTrue(parameter instanceof DefaultBlockParameterNumber);
    }

    @Test
    public void testValueOfWithString() {
        String blockName = "latest";
        DefaultBlockParameter parameter = DefaultBlockParameter.valueOf(blockName);
        
        Assert.assertNotNull(parameter);
        Assert.assertTrue(parameter instanceof DefaultBlockParameterName);
        Assert.assertEquals(DefaultBlockParameterName.LATEST, parameter);
    }

    @Test
    public void testValueOfWithNegativeNumber() {
        BigInteger negativeNumber = BigInteger.valueOf(-1);
        DefaultBlockParameter parameter = DefaultBlockParameter.valueOf(negativeNumber);
        
        Assert.assertNotNull(parameter);
        Assert.assertTrue(parameter instanceof DefaultBlockParameterNumber);
    }

    @Test
    public void testValueOfWithZero() {
        DefaultBlockParameter parameter = DefaultBlockParameter.valueOf(BigInteger.ZERO);
        
        Assert.assertNotNull(parameter);
        Assert.assertEquals("0x0", parameter.getValue());
    }

    @Test
    public void testValueOfWithEarliest() {
        DefaultBlockParameter parameter = DefaultBlockParameter.valueOf("earliest");
        
        Assert.assertNotNull(parameter);
        Assert.assertEquals(DefaultBlockParameterName.EARLIEST, parameter);
        Assert.assertTrue(parameter.isEarliest());
        Assert.assertFalse(parameter.isLatest());
    }

    @Test
    public void testValueOfWithLatest() {
        DefaultBlockParameter parameter = DefaultBlockParameter.valueOf("latest");
        
        Assert.assertNotNull(parameter);
        Assert.assertEquals(DefaultBlockParameterName.LATEST, parameter);
        Assert.assertTrue(parameter.isLatest());
        Assert.assertFalse(parameter.isEarliest());
    }
}
