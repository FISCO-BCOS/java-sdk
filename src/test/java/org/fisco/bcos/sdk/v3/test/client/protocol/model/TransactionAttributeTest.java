package org.fisco.bcos.sdk.v3.test.client.protocol.model;

import org.fisco.bcos.sdk.v3.client.protocol.model.TransactionAttribute;
import org.junit.Assert;
import org.junit.Test;

public class TransactionAttributeTest {

    @Test
    public void testEVM_ABI_CODEC() {
        Assert.assertEquals(0x1, TransactionAttribute.EVM_ABI_CODEC);
    }

    @Test
    public void testLIQUID_SCALE_CODEC() {
        Assert.assertEquals(0x2, TransactionAttribute.LIQUID_SCALE_CODEC);
    }

    @Test
    public void testDAG() {
        Assert.assertEquals(0x4, TransactionAttribute.DAG);
    }

    @Test
    public void testLIQUID_CREATE() {
        Assert.assertEquals(0x8, TransactionAttribute.LIQUID_CREATE);
    }

    @Test
    public void testAttributeValues() {
        // Verify that the constants are unique
        Assert.assertNotEquals(TransactionAttribute.EVM_ABI_CODEC, TransactionAttribute.LIQUID_SCALE_CODEC);
        Assert.assertNotEquals(TransactionAttribute.EVM_ABI_CODEC, TransactionAttribute.DAG);
        Assert.assertNotEquals(TransactionAttribute.EVM_ABI_CODEC, TransactionAttribute.LIQUID_CREATE);
        Assert.assertNotEquals(TransactionAttribute.LIQUID_SCALE_CODEC, TransactionAttribute.DAG);
        Assert.assertNotEquals(TransactionAttribute.LIQUID_SCALE_CODEC, TransactionAttribute.LIQUID_CREATE);
        Assert.assertNotEquals(TransactionAttribute.DAG, TransactionAttribute.LIQUID_CREATE);
    }

    @Test
    public void testAttributesBitFlags() {
        // Test that attributes can be used as bit flags
        int combined = TransactionAttribute.EVM_ABI_CODEC | TransactionAttribute.DAG;
        Assert.assertEquals(0x5, combined);
        
        Assert.assertTrue((combined & TransactionAttribute.EVM_ABI_CODEC) != 0);
        Assert.assertTrue((combined & TransactionAttribute.DAG) != 0);
        Assert.assertFalse((combined & TransactionAttribute.LIQUID_SCALE_CODEC) != 0);
    }
}
