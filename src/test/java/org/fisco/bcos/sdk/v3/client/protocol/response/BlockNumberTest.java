package org.fisco.bcos.sdk.v3.client.protocol.response;

import org.junit.Assert;
import org.junit.Test;
import java.math.BigInteger;

public class BlockNumberTest {

    @Test
    public void testGetBlockNumber() {
        BlockNumber blockNumber = new BlockNumber();
        blockNumber.setResult("0x64");
        
        BigInteger result = blockNumber.getBlockNumber();
        Assert.assertEquals(BigInteger.valueOf(100), result);
    }

    @Test
    public void testGetBlockNumberWithZero() {
        BlockNumber blockNumber = new BlockNumber();
        blockNumber.setResult("0x0");
        
        BigInteger result = blockNumber.getBlockNumber();
        Assert.assertEquals(BigInteger.ZERO, result);
    }

    @Test
    public void testGetBlockNumberWithLargeValue() {
        BlockNumber blockNumber = new BlockNumber();
        blockNumber.setResult("0xFFFFFF");
        
        BigInteger result = blockNumber.getBlockNumber();
        Assert.assertEquals(BigInteger.valueOf(16777215), result);
    }

    @Test
    public void testGetBlockNumberWithHexPrefix() {
        BlockNumber blockNumber = new BlockNumber();
        blockNumber.setResult("0xa");
        
        BigInteger result = blockNumber.getBlockNumber();
        Assert.assertEquals(BigInteger.valueOf(10), result);
    }
}
