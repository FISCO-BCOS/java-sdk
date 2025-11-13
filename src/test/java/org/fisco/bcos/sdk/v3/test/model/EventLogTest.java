package org.fisco.bcos.sdk.v3.test.model;

import org.fisco.bcos.sdk.v3.model.EventLog;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class EventLogTest {

    @Test
    public void testEventLogConstructorFull() {
        List<String> topics = Arrays.asList("topic1", "topic2");
        EventLog eventLog = new EventLog(
            "0x1",
            "0x2",
            "0xabc123",
            "0x64",
            "0x1234567890abcdef1234567890abcdef12345678",
            "0xdata",
            topics
        );

        Assert.assertEquals("0x1", eventLog.getLogIndexRaw());
        Assert.assertEquals("0x2", eventLog.getTransactionIndexRaw());
        Assert.assertEquals("0xabc123", eventLog.getTransactionHash());
        Assert.assertEquals("0x64", eventLog.getBlockNumberRaw());
        Assert.assertEquals("0x1234567890abcdef1234567890abcdef12345678", eventLog.getAddress());
        Assert.assertEquals("0xdata", eventLog.getData());
        Assert.assertEquals(topics, eventLog.getTopics());
    }

    @Test
    public void testEventLogConstructorSimple() {
        List<String> topics = Arrays.asList("topic1", "topic2");
        EventLog eventLog = new EventLog("0xdata", topics);

        Assert.assertEquals("0xdata", eventLog.getData());
        Assert.assertEquals(topics, eventLog.getTopics());
    }

    @Test
    public void testEventLogDefaultConstructor() {
        EventLog eventLog = new EventLog();
        Assert.assertNotNull(eventLog);
    }

    @Test
    public void testGettersAndSetters() {
        EventLog eventLog = new EventLog();

        eventLog.setLogIndex("0x1");
        Assert.assertEquals("0x1", eventLog.getLogIndexRaw());
        Assert.assertEquals(BigInteger.ONE, eventLog.getLogIndex());

        eventLog.setTransactionIndex("0x5");
        Assert.assertEquals("0x5", eventLog.getTransactionIndexRaw());
        Assert.assertEquals(new BigInteger("5"), eventLog.getTransactionIndex());

        eventLog.setTransactionHash("0xhash");
        Assert.assertEquals("0xhash", eventLog.getTransactionHash());

        eventLog.setBlockNumber("0xa");
        Assert.assertEquals("0xa", eventLog.getBlockNumberRaw());
        Assert.assertEquals(new BigInteger("10"), eventLog.getBlockNumber());

        eventLog.setAddress("0xaddress");
        Assert.assertEquals("0xaddress", eventLog.getAddress());

        eventLog.setData("0xdata");
        Assert.assertEquals("0xdata", eventLog.getData());

        List<String> topics = Arrays.asList("topic1");
        eventLog.setTopics(topics);
        Assert.assertEquals(topics, eventLog.getTopics());
    }

    @Test
    public void testEqualsAndHashCode() {
        List<String> topics = Arrays.asList("topic1", "topic2");
        EventLog log1 = new EventLog(
            "0x1", "0x2", "0xhash", "0x64", "0xaddr", "0xdata", topics
        );

        EventLog log2 = new EventLog(
            "0x1", "0x2", "0xhash", "0x64", "0xaddr", "0xdata", topics
        );

        Assert.assertEquals(log1, log2);
        Assert.assertEquals(log1.hashCode(), log2.hashCode());

        // Test same object
        Assert.assertEquals(log1, log1);

        // Test different log index
        EventLog log3 = new EventLog(
            "0x3", "0x2", "0xhash", "0x64", "0xaddr", "0xdata", topics
        );
        Assert.assertNotEquals(log1, log3);
    }

    @Test
    public void testToString() {
        List<String> topics = Arrays.asList("topic1");
        EventLog eventLog = new EventLog(
            "0x1", "0x2", "0xhash", "0x64", "0xaddr", "0xdata", topics
        );

        String result = eventLog.toString();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("EventLog"));
    }

    @Test
    public void testNullValues() {
        EventLog eventLog = new EventLog();

        // Test getters with null values
        Assert.assertNull(eventLog.getLogIndex());
        Assert.assertNull(eventLog.getTransactionIndex());
        Assert.assertNull(eventLog.getBlockNumber());
    }
}

