package org.fisco.bcos.sdk.v3.client.protocol.response;

import org.junit.Assert;
import org.junit.Test;
import java.math.BigInteger;

public class PendingTxSizeTest {

    @Test
    public void testGetPendingTxSize() {
        PendingTxSize pendingTxSize = new PendingTxSize();
        pendingTxSize.setResult("0x14");
        
        BigInteger result = pendingTxSize.getPendingTxSize();
        Assert.assertEquals(BigInteger.valueOf(20), result);
    }

    @Test
    public void testGetPendingTxSizeWithZero() {
        PendingTxSize pendingTxSize = new PendingTxSize();
        pendingTxSize.setResult("0x0");
        
        BigInteger result = pendingTxSize.getPendingTxSize();
        Assert.assertEquals(BigInteger.ZERO, result);
    }

    @Test
    public void testGetPendingTxSizeWithLargeValue() {
        PendingTxSize pendingTxSize = new PendingTxSize();
        pendingTxSize.setResult("0x3E8");
        
        BigInteger result = pendingTxSize.getPendingTxSize();
        Assert.assertEquals(BigInteger.valueOf(1000), result);
    }

    @Test
    public void testGetPendingTxSizeWithHexValue() {
        PendingTxSize pendingTxSize = new PendingTxSize();
        pendingTxSize.setResult("0xFF");
        
        BigInteger result = pendingTxSize.getPendingTxSize();
        Assert.assertEquals(BigInteger.valueOf(255), result);
    }
}
