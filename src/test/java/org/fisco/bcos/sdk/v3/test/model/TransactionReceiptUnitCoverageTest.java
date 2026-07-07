/**
 * Copyright 2014-2020 [fisco-dev]
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.sdk.v3.test.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.model.EventLog;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.junit.Assert;
import org.junit.Test;

/**
 * Pure-Java (no node) coverage tests for {@link TransactionReceipt} and its nested {@link
 * TransactionReceipt.Logs} class. Exercises getters/setters, status logic, the message field, the
 * {@code toEventLog} conversion and equals/hashCode/toString.
 */
public class TransactionReceiptUnitCoverageTest {

    private TransactionReceipt.Logs buildLogs() {
        TransactionReceipt.Logs logs = new TransactionReceipt.Logs();
        logs.setAddress("0x1234567890123456789012345678901234567890");
        logs.setTopics(Arrays.asList("0xtopic1", "0xtopic2"));
        logs.setData("0xdeadbeef");
        logs.setBlockNumber("0x10");
        return logs;
    }

    @Test
    public void testLogsGettersSetters() {
        TransactionReceipt.Logs logs = buildLogs();
        Assert.assertEquals("0x1234567890123456789012345678901234567890", logs.getAddress());
        Assert.assertEquals(2, logs.getTopics().size());
        Assert.assertEquals("0xtopic1", logs.getTopics().get(0));
        Assert.assertEquals("0xdeadbeef", logs.getData());
        Assert.assertEquals("0x10", logs.getBlockNumber());
    }

    @Test
    public void testLogsToEventLog() {
        TransactionReceipt.Logs logs = buildLogs();
        EventLog eventLog = logs.toEventLog();
        Assert.assertEquals(logs.getAddress(), eventLog.getAddress());
        Assert.assertEquals(logs.getTopics(), eventLog.getTopics());
        Assert.assertEquals(logs.getData(), eventLog.getData());
        Assert.assertEquals(logs.getBlockNumber(), eventLog.getBlockNumberRaw());
    }

    @Test
    public void testLogsEqualsHashCodeToString() {
        TransactionReceipt.Logs logs = buildLogs();
        TransactionReceipt.Logs same = buildLogs();
        // equals() does not compare blockNumber, so these are equal
        Assert.assertEquals(logs, logs);
        Assert.assertEquals(logs, same);
        Assert.assertEquals(logs.hashCode(), same.hashCode());
        Assert.assertNotEquals(logs, null);
        Assert.assertNotEquals(logs, "not a logs");

        TransactionReceipt.Logs different = buildLogs();
        different.setData("0xdifferent");
        Assert.assertNotEquals(logs, different);

        Assert.assertTrue(logs.toString().contains("0xdeadbeef"));
        Assert.assertTrue(logs.toString().startsWith("Logs{"));
    }

    @Test
    public void testStatusLogic() {
        TransactionReceipt receipt = new TransactionReceipt();
        // default status is -1 (not ok)
        Assert.assertEquals(-1, receipt.getStatus());
        Assert.assertFalse(receipt.isStatusOK());

        receipt.setStatus(0);
        Assert.assertTrue(receipt.isStatusOK());
        Assert.assertEquals(0, receipt.getStatus());

        receipt.setStatus(16);
        Assert.assertFalse(receipt.isStatusOK());
    }

    @Test
    public void testMessageField() {
        TransactionReceipt receipt = new TransactionReceipt();
        Assert.assertNull(receipt.getMessage());
        receipt.setMessage("execution reverted");
        Assert.assertEquals("execution reverted", receipt.getMessage());
    }

    @Test
    public void testAddressPrefixGetters() {
        TransactionReceipt receipt = new TransactionReceipt();
        // setTo / setContractAddress store the raw value, getters add the hex prefix
        receipt.setTo("1234567890123456789012345678901234567890");
        receipt.setContractAddress("abcdefabcdefabcdefabcdefabcdefabcdefabcd");
        Assert.assertTrue(receipt.getTo().startsWith("0x"));
        Assert.assertTrue(receipt.getContractAddress().startsWith("0x"));
        Assert.assertTrue(receipt.getTo().contains("1234567890"));
        Assert.assertTrue(receipt.getContractAddress().contains("abcdef"));
    }

    @Test
    public void testAllGettersSetters() {
        TransactionReceipt receipt = new TransactionReceipt();
        receipt.setVersion(1);
        receipt.setTransactionHash("0xtxhash");
        receipt.setReceiptHash("0xreceipthash");
        receipt.setBlockNumber(BigInteger.valueOf(42));
        receipt.setFrom("0xfrom");
        receipt.setTo("0xto");
        receipt.setGasUsed("21000");
        receipt.setChecksumContractAddress("0xChecksum");
        receipt.setStatus(0);
        receipt.setInput("0xinput");
        receipt.setOutput("0xoutput");
        receipt.setExtraData("0xextra");
        receipt.setEffectiveGasPrice("0x1");
        List<String> txProof = Collections.singletonList("0xproof");
        List<String> txReceiptProof = Collections.singletonList("0xrproof");
        receipt.setTxProof(txProof);
        receipt.setTxReceiptProof(txReceiptProof);
        receipt.setTransactionProof(new ArrayList<>());
        receipt.setReceiptProof(new ArrayList<>());
        List<TransactionReceipt.Logs> logEntries = Collections.singletonList(buildLogs());
        receipt.setLogEntries(logEntries);

        Assert.assertEquals(Integer.valueOf(1), receipt.getVersion());
        Assert.assertEquals("0xtxhash", receipt.getTransactionHash());
        Assert.assertEquals("0xreceipthash", receipt.getReceiptHash());
        Assert.assertEquals(BigInteger.valueOf(42), receipt.getBlockNumber());
        Assert.assertEquals("0xfrom", receipt.getFrom());
        Assert.assertEquals("21000", receipt.getGasUsed());
        Assert.assertEquals("0xChecksum", receipt.getChecksumContractAddress());
        Assert.assertEquals("0xinput", receipt.getInput());
        Assert.assertEquals("0xoutput", receipt.getOutput());
        Assert.assertEquals("0xextra", receipt.getExtraData());
        Assert.assertEquals("0x1", receipt.getEffectiveGasPrice());
        Assert.assertEquals(txProof, receipt.getTxProof());
        Assert.assertEquals(txReceiptProof, receipt.getTxReceiptProof());
        Assert.assertNotNull(receipt.getTransactionProof());
        Assert.assertNotNull(receipt.getReceiptProof());
        Assert.assertEquals(1, receipt.getLogEntries().size());
    }

    @Test
    public void testEqualsHashCodeToString() {
        TransactionReceipt a = new TransactionReceipt();
        a.setTransactionHash("0xhash");
        a.setVersion(0);
        a.setBlockNumber(BigInteger.ONE);
        a.setFrom("0xfrom");
        a.setTo("0xto");
        a.setStatus(0);

        TransactionReceipt b = new TransactionReceipt();
        b.setTransactionHash("0xhash");
        b.setVersion(0);
        b.setBlockNumber(BigInteger.ONE);
        b.setFrom("0xfrom");
        b.setTo("0xto");
        b.setStatus(0);

        Assert.assertEquals(a, a);
        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
        Assert.assertNotEquals(a, null);
        Assert.assertNotEquals(a, "string");

        b.setStatus(1);
        Assert.assertNotEquals(a, b);

        Assert.assertTrue(a.toString().startsWith("TransactionReceipt{"));
        Assert.assertTrue(a.toString().contains("0xhash"));
    }
}
