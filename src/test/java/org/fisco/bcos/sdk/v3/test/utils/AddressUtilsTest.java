package org.fisco.bcos.sdk.v3.test.utils;

import org.fisco.bcos.sdk.v3.utils.AddressUtils;
import org.junit.Assert;
import org.junit.Test;

public class AddressUtilsTest {

    @Test
    public void testIsValidAddress() {
        // Valid addresses
        Assert.assertTrue(AddressUtils.isValidAddress("1234567890abcdef"));
        Assert.assertTrue(AddressUtils.isValidAddress("0x1234567890abcdef"));
        Assert.assertTrue(AddressUtils.isValidAddress("ABCDEF1234567890"));
        Assert.assertTrue(AddressUtils.isValidAddress("a"));
        Assert.assertTrue(AddressUtils.isValidAddress("0xa"));
        Assert.assertTrue(AddressUtils.isValidAddress("1234567890abcdef1234567890abcdef12345678"));
        Assert.assertTrue(AddressUtils.isValidAddress("0x1234567890abcdef1234567890abcdef12345678"));

        // Invalid addresses
        Assert.assertFalse(AddressUtils.isValidAddress(""));
        Assert.assertFalse(AddressUtils.isValidAddress("0x"));
        Assert.assertFalse(AddressUtils.isValidAddress("0xg123"));
        Assert.assertFalse(AddressUtils.isValidAddress("xyz"));
        Assert.assertFalse(AddressUtils.isValidAddress("1234567890abcdef1234567890abcdef123456789")); // 41 chars
        Assert.assertFalse(AddressUtils.isValidAddress("test@address"));
    }

    @Test
    public void testIsValidFullAddress() {
        // Valid full addresses (exactly 40 hex chars)
        Assert.assertTrue(AddressUtils.isValidFullAddress("1234567890abcdef1234567890abcdef12345678"));
        Assert.assertTrue(AddressUtils.isValidFullAddress("0x1234567890abcdef1234567890abcdef12345678"));
        Assert.assertTrue(AddressUtils.isValidFullAddress("ABCDEF1234567890ABCDEF1234567890ABCDEF12"));

        // Invalid full addresses
        Assert.assertFalse(AddressUtils.isValidFullAddress("1234567890abcdef")); // too short
        Assert.assertFalse(AddressUtils.isValidFullAddress("1234567890abcdef1234567890abcdef123456789")); // 41 chars
        Assert.assertFalse(AddressUtils.isValidFullAddress("a"));
        Assert.assertFalse(AddressUtils.isValidFullAddress(""));
        Assert.assertFalse(AddressUtils.isValidFullAddress("0xg234567890abcdef1234567890abcdef12345678")); // contains 'g'
    }

    @Test
    public void testAddHexPrefixToAddress() {
        // Should add prefix to valid full address without prefix
        Assert.assertEquals("0x1234567890abcdef1234567890abcdef12345678",
            AddressUtils.addHexPrefixToAddress("1234567890abcdef1234567890abcdef12345678"));

        // Should not change address that already has prefix
        Assert.assertEquals("0x1234567890abcdef1234567890abcdef12345678",
            AddressUtils.addHexPrefixToAddress("0x1234567890abcdef1234567890abcdef12345678"));

        Assert.assertEquals("0X1234567890abcdef1234567890abcdef12345678",
            AddressUtils.addHexPrefixToAddress("0X1234567890abcdef1234567890abcdef12345678"));

        // Should not change invalid addresses
        Assert.assertEquals("123", AddressUtils.addHexPrefixToAddress("123"));
        Assert.assertEquals("0xabc", AddressUtils.addHexPrefixToAddress("0xabc"));

        // Should handle null
        Assert.assertNull(AddressUtils.addHexPrefixToAddress(null));
    }
}

