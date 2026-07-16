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
package org.fisco.bcos.sdk.v3.test.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.protocol.model.JsonTransactionResponse;
import org.junit.Assert;
import org.junit.Test;

/**
 * Pure-Java (no node) coverage tests for {@link JsonTransactionResponse}. Exercises all
 * getters/setters (including the v1 gas-price fields and the v2 extension bytes) plus
 * equals/hashCode/toString.
 */
public class JsonTransactionResponseUnitCoverageTest {

    private JsonTransactionResponse buildFull() {
        JsonTransactionResponse tx = new JsonTransactionResponse();
        tx.setVersion(1);
        tx.setHash("0xhash");
        tx.setNonce("0x01");
        tx.setBlockLimit(500L);
        tx.setTo("1234567890123456789012345678901234567890");
        tx.setFrom("0xfrom");
        tx.setAbi("[]");
        tx.setInput("0xinput");
        tx.setChainID("chain0");
        tx.setGroupID("group0");
        tx.setExtraData("0xextra");
        tx.setSignature("0xsig");
        tx.setImportTime(123456789L);
        tx.setTxProof(Collections.singletonList("0xproof"));
        tx.setTransactionProof(new ArrayList<>());
        // v1 fields
        tx.setValue("0x10");
        tx.setGasPrice("0x20");
        tx.setGasLimit(30000L);
        tx.setMaxFeePerGas("0x30");
        tx.setMaxPriorityFeePerGas("0x40");
        // v2 fields
        tx.setExtension(new byte[] {0x01, 0x02, 0x03});
        return tx;
    }

    @Test
    public void testDefaults() {
        JsonTransactionResponse tx = new JsonTransactionResponse();
        Assert.assertEquals(Integer.valueOf(0), tx.getVersion());
        Assert.assertEquals("", tx.getAbi());
        Assert.assertEquals("", tx.getValue());
        Assert.assertEquals("", tx.getGasPrice());
        Assert.assertEquals(0L, tx.getGasLimit());
        Assert.assertEquals("", tx.getMaxFeePerGas());
        Assert.assertEquals("", tx.getMaxPriorityFeePerGas());
        Assert.assertNull(tx.getExtension());
        Assert.assertNull(tx.getTransactionProof());
        Assert.assertNull(tx.getTxProof());
    }

    @Test
    public void testAllGettersSetters() {
        JsonTransactionResponse tx = buildFull();
        Assert.assertEquals(Integer.valueOf(1), tx.getVersion());
        Assert.assertEquals("0xhash", tx.getHash());
        Assert.assertEquals("0x01", tx.getNonce());
        Assert.assertEquals(500L, tx.getBlockLimit());
        // getTo() prepends the hex prefix to the stored value
        Assert.assertTrue(tx.getTo().startsWith("0x"));
        Assert.assertTrue(tx.getTo().contains("1234567890"));
        Assert.assertEquals("0xfrom", tx.getFrom());
        Assert.assertEquals("[]", tx.getAbi());
        Assert.assertEquals("0xinput", tx.getInput());
        Assert.assertEquals("chain0", tx.getChainID());
        Assert.assertEquals("group0", tx.getGroupID());
        Assert.assertEquals("0xextra", tx.getExtraData());
        Assert.assertEquals("0xsig", tx.getSignature());
        Assert.assertEquals(123456789L, tx.getImportTime());
        Assert.assertEquals(1, tx.getTxProof().size());
        Assert.assertNotNull(tx.getTransactionProof());
        Assert.assertEquals("0x10", tx.getValue());
        Assert.assertEquals("0x20", tx.getGasPrice());
        Assert.assertEquals(30000L, tx.getGasLimit());
        Assert.assertEquals("0x30", tx.getMaxFeePerGas());
        Assert.assertEquals("0x40", tx.getMaxPriorityFeePerGas());
        Assert.assertArrayEquals(new byte[] {0x01, 0x02, 0x03}, tx.getExtension());
    }

    @Test
    public void testEqualsHashCode() {
        JsonTransactionResponse a = buildFull();
        JsonTransactionResponse b = buildFull();
        Assert.assertEquals(a, a);
        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
        Assert.assertNotEquals(a, null);
        Assert.assertNotEquals(a, "not a tx");

        b.setHash("0xdifferent");
        Assert.assertNotEquals(a, b);
    }

    @Test
    public void testToString() {
        JsonTransactionResponse tx = buildFull();
        String str = tx.toString();
        Assert.assertTrue(str.startsWith("JsonTransactionResponse{"));
        Assert.assertTrue(str.contains("0xhash"));
        Assert.assertTrue(str.contains("group0"));
        // extension is rendered as hex
        Assert.assertTrue(str.contains("010203"));

        // toString tolerates a null extension
        JsonTransactionResponse empty = new JsonTransactionResponse();
        Assert.assertTrue(empty.toString().contains("extension='null'"));
    }

    @Test
    public void testExtensionMutation() {
        JsonTransactionResponse tx = new JsonTransactionResponse();
        byte[] ext = new byte[] {0x0a, 0x0b};
        tx.setExtension(ext);
        Assert.assertArrayEquals(ext, tx.getExtension());
        tx.setExtension(null);
        Assert.assertNull(tx.getExtension());
    }

    @Test
    public void testTxProofMutation() {
        JsonTransactionResponse tx = new JsonTransactionResponse();
        List<String> proof = new ArrayList<>();
        proof.add("0xa");
        proof.add("0xb");
        tx.setTxProof(proof);
        Assert.assertEquals(2, tx.getTxProof().size());
    }
}
