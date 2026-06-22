package org.fisco.bcos.sdk.v3.test.model;

import org.fisco.bcos.sdk.v3.model.RetCode;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.junit.Assert;
import org.junit.Test;

public class RetCodeTest {

    @Test
    public void testDefaultConstructor() {
        RetCode retCode = new RetCode();
        Assert.assertNotNull(retCode);
        Assert.assertEquals(0, retCode.getCode());
        Assert.assertNull(retCode.getMessage());
        Assert.assertNull(retCode.getTransactionReceipt());
    }

    @Test
    public void testConstructorWithParameters() {
        RetCode retCode = new RetCode(200, "Success");
        Assert.assertEquals(200, retCode.getCode());
        Assert.assertEquals("Success", retCode.getMessage());
    }

    @Test
    public void testGettersAndSetters() {
        RetCode retCode = new RetCode();

        retCode.code = 404;
        Assert.assertEquals(404, retCode.getCode());

        TransactionReceipt receipt = new TransactionReceipt();
        retCode.setTransactionReceipt(receipt);
        Assert.assertEquals(receipt, retCode.getTransactionReceipt());
    }

    @Test
    public void testEquals() {
        RetCode retCode1 = new RetCode(100, "Test");
        RetCode retCode2 = new RetCode(100, "Test");
        RetCode retCode3 = new RetCode(200, "Test");
        RetCode retCode4 = new RetCode(100, "Different");

        // Test equality
        Assert.assertEquals(retCode1, retCode2);

        // Test same object
        Assert.assertEquals(retCode1, retCode1);

        // Test different code
        Assert.assertNotEquals(retCode1, retCode3);

        // Test different message
        Assert.assertNotEquals(retCode1, retCode4);

        // Test null
        Assert.assertNotEquals(retCode1, null);

        // Test different class
        Assert.assertNotEquals(retCode1, "String");
    }

    @Test
    public void testHashCode() {
        RetCode retCode1 = new RetCode(100, "Test");
        RetCode retCode2 = new RetCode(100, "Test");

        Assert.assertEquals(retCode1.hashCode(), retCode2.hashCode());

        // Different values should (likely) have different hash codes
        RetCode retCode3 = new RetCode(200, "Different");
        Assert.assertNotEquals(retCode1.hashCode(), retCode3.hashCode());
    }

    @Test
    public void testToString() {
        RetCode retCode = new RetCode(200, "Success");
        String result = retCode.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("200"));
        Assert.assertTrue(result.contains("Success"));
        Assert.assertTrue(result.contains("code"));
        Assert.assertTrue(result.contains("msg"));
    }

    @Test
    public void testToStringFormat() {
        RetCode retCode = new RetCode(404, "Not Found");
        String result = retCode.toString();

        // Should be in JSON-like format
        Assert.assertTrue(result.startsWith("{"));
        Assert.assertTrue(result.endsWith("}"));
    }

    @Test
    public void testNullMessage() {
        RetCode retCode = new RetCode(100, null);
        Assert.assertEquals(100, retCode.getCode());
        Assert.assertNull(retCode.getMessage());

        // toString should handle null message
        String result = retCode.toString();
        Assert.assertNotNull(result);
    }
}

