package org.fisco.bcos.sdk.v3.client.protocol.request;

import org.junit.Assert;
import org.junit.Test;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

public class LogFilterRequestTest {

    @Test
    public void testDefaultConstructor() {
        LogFilterRequest request = new LogFilterRequest();
        
        Assert.assertNotNull(request);
        Assert.assertNull(request.getFromBlock());
        Assert.assertNull(request.getToBlock());
        Assert.assertNull(request.getBlockHash());
        Assert.assertNull(request.getAddress());
    }

    @Test
    public void testConstructorWithFromAndToBlock() {
        DefaultBlockParameter fromBlock = DefaultBlockParameter.valueOf(BigInteger.valueOf(100));
        DefaultBlockParameter toBlock = DefaultBlockParameter.valueOf(BigInteger.valueOf(200));
        
        LogFilterRequest request = new LogFilterRequest(fromBlock, toBlock);
        
        Assert.assertEquals(fromBlock, request.getFromBlock());
        Assert.assertEquals(toBlock, request.getToBlock());
    }

    @Test
    public void testConstructorWithBlocksAndAddressList() {
        DefaultBlockParameter fromBlock = DefaultBlockParameter.valueOf(BigInteger.valueOf(100));
        DefaultBlockParameter toBlock = DefaultBlockParameter.valueOf(BigInteger.valueOf(200));
        java.util.List<String> addresses = Arrays.asList("0x1234", "0x5678");
        
        LogFilterRequest request = new LogFilterRequest(fromBlock, toBlock, addresses);
        
        Assert.assertEquals(fromBlock, request.getFromBlock());
        Assert.assertEquals(toBlock, request.getToBlock());
        Assert.assertEquals(addresses, request.getAddress());
    }

    @Test
    public void testConstructorWithBlocksAndSingleAddress() {
        DefaultBlockParameter fromBlock = DefaultBlockParameter.valueOf(BigInteger.valueOf(100));
        DefaultBlockParameter toBlock = DefaultBlockParameter.valueOf(BigInteger.valueOf(200));
        String address = "0x1234";
        
        LogFilterRequest request = new LogFilterRequest(fromBlock, toBlock, address);
        
        Assert.assertEquals(fromBlock, request.getFromBlock());
        Assert.assertEquals(toBlock, request.getToBlock());
        Assert.assertEquals(Collections.singletonList(address), request.getAddress());
    }

    @Test
    public void testConstructorWithBlockHash() {
        String blockHash = "0xabcdef";
        
        LogFilterRequest request = new LogFilterRequest(blockHash);
        
        Assert.assertEquals(blockHash, request.getBlockHash());
    }

    @Test
    public void testConstructorWithBlockHashAndAddress() {
        String blockHash = "0xabcdef";
        String address = "0x1234";
        
        LogFilterRequest request = new LogFilterRequest(blockHash, address);
        
        Assert.assertEquals(blockHash, request.getBlockHash());
        Assert.assertEquals(Collections.singletonList(address), request.getAddress());
    }

    @Test
    public void testSetFromBlock() {
        LogFilterRequest request = new LogFilterRequest();
        BigInteger from = BigInteger.valueOf(100);
        
        LogFilterRequest result = request.setFromBlock(from);
        
        Assert.assertEquals(request, result);
        Assert.assertNotNull(request.getFromBlock());
    }

    @Test
    public void testSetToBlock() {
        LogFilterRequest request = new LogFilterRequest();
        BigInteger to = BigInteger.valueOf(200);
        
        LogFilterRequest result = request.setToBlock(to);
        
        Assert.assertEquals(request, result);
        Assert.assertNotNull(request.getToBlock());
    }

    @Test
    public void testCheckParamsWithLatestBlocks() {
        DefaultBlockParameter latest = DefaultBlockParameterName.LATEST;
        LogFilterRequest request = new LogFilterRequest(latest, latest);
        
        Assert.assertTrue(request.checkParams());
    }

    @Test
    public void testCheckParamsWithValidRange() {
        DefaultBlockParameter from = DefaultBlockParameter.valueOf(BigInteger.valueOf(100));
        DefaultBlockParameter to = DefaultBlockParameter.valueOf(BigInteger.valueOf(200));
        LogFilterRequest request = new LogFilterRequest(from, to);
        
        Assert.assertTrue(request.checkParams());
    }

    @Test
    public void testCheckParamsWithInvalidRange() {
        DefaultBlockParameter from = DefaultBlockParameter.valueOf(BigInteger.valueOf(200));
        DefaultBlockParameter to = DefaultBlockParameter.valueOf(BigInteger.valueOf(100));
        LogFilterRequest request = new LogFilterRequest(from, to);
        
        Assert.assertFalse(request.checkParams());
    }

    @Test
    public void testAddSingleTopic() {
        LogFilterRequest request = new LogFilterRequest();
        String topic = "0xtopic1";
        
        request.addSingleTopic(topic);
        
        Assert.assertEquals(1, request.getTopics().size());
    }

    @Test
    public void testAddNullTopic() {
        LogFilterRequest request = new LogFilterRequest();
        
        request.addNullTopic();
        
        Assert.assertEquals(1, request.getTopics().size());
    }

    @Test
    public void testAddOptionalTopics() {
        LogFilterRequest request = new LogFilterRequest();
        
        request.addOptionalTopics("0xtopic1", "0xtopic2");
        
        Assert.assertEquals(1, request.getTopics().size());
    }
}
