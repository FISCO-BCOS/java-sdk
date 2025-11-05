package org.fisco.bcos.sdk.v3.test.client.protocol.response;

import org.fisco.bcos.sdk.v3.client.protocol.response.BlockHash;
import org.junit.Assert;
import org.junit.Test;

public class BlockHashTest {

    @Test
    public void testGetBlockHashByNumber() {
        BlockHash blockHash = new BlockHash();
        String hash = "0x1234567890abcdef";
        blockHash.setResult(hash);
        
        String result = blockHash.getBlockHashByNumber();
        Assert.assertEquals(hash, result);
    }

    @Test
    public void testGetBlockHashByNumberWithNull() {
        BlockHash blockHash = new BlockHash();
        blockHash.setResult(null);
        
        String result = blockHash.getBlockHashByNumber();
        Assert.assertNull(result);
    }

    @Test
    public void testGetBlockHashByNumberWithEmptyString() {
        BlockHash blockHash = new BlockHash();
        blockHash.setResult("");
        
        String result = blockHash.getBlockHashByNumber();
        Assert.assertEquals("", result);
    }

    @Test
    public void testGetBlockHashByNumberWithLongHash() {
        BlockHash blockHash = new BlockHash();
        String hash = "0xabcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890";
        blockHash.setResult(hash);
        
        String result = blockHash.getBlockHashByNumber();
        Assert.assertEquals(hash, result);
    }
}
