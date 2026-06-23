package org.fisco.bcos.sdk.v3.test.utils;

import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.exceptions.DecoderException;
import org.junit.Assert;
import org.junit.Test;

public class HexTest {

    @Test
    public void testHasHexPrefix() {
        Assert.assertTrue(Hex.hasHexPrefix("0x1234"));
        Assert.assertTrue(Hex.hasHexPrefix("0X1234"));
        Assert.assertFalse(Hex.hasHexPrefix("1234"));
        Assert.assertFalse(Hex.hasHexPrefix(""));
        Assert.assertFalse(Hex.hasHexPrefix(null));
    }

    @Test
    public void testAddPrefix() {
        Assert.assertEquals("0x1234", Hex.addPrefix("1234"));
        Assert.assertEquals("0x1234", Hex.addPrefix("0x1234"));
        Assert.assertNull(Hex.addPrefix(null));
        Assert.assertEquals("0x", Hex.addPrefix(""));
    }

    @Test
    public void testTrimPrefix() {
        Assert.assertEquals("1234", Hex.trimPrefix("0x1234"));
        Assert.assertEquals("1234", Hex.trimPrefix("1234"));
        Assert.assertEquals("", Hex.trimPrefix("0x"));
        Assert.assertNull(Hex.trimPrefix(null));
    }

    @Test
    public void testToHexString() {
        byte[] data = new byte[]{0x01, 0x02, 0x03};
        String result = Hex.toHexString(data);
        Assert.assertEquals("010203", result);

        // Test empty array
        byte[] empty = new byte[0];
        String resultEmpty = Hex.toHexString(empty);
        Assert.assertEquals("", resultEmpty);

        // Test single byte
        byte[] single = new byte[]{(byte) 0xff};
        String resultSingle = Hex.toHexString(single);
        Assert.assertEquals("ff", resultSingle);
    }

    @Test
    public void testToHexStringWithPrefix() {
        byte[] data = new byte[]{0x01, 0x02, 0x03};
        String result = Hex.toHexStringWithPrefix(data);
        Assert.assertEquals("0x010203", result);
        Assert.assertTrue(result.startsWith("0x"));
    }

    @Test
    public void testToHexStringWithPrefixNullable() {
        byte[] data = new byte[]{0x01, 0x02};
        String result = Hex.toHexStringWithPrefixNullable(data, "default");
        Assert.assertEquals("0x0102", result);

        // Test null data
        String resultNull = Hex.toHexStringWithPrefixNullable(null, "default");
        Assert.assertEquals("0xdefault", resultNull);
    }

    @Test
    public void testToHexStringMaybeNullData() {
        byte[] data = new byte[]{0x0a, 0x0b};
        String result = Hex.toHexStringMaybeNullData(data, "default");
        Assert.assertEquals("0a0b", result);

        // Test null data
        String resultNull = Hex.toHexStringMaybeNullData(null, "default");
        Assert.assertEquals("default", resultNull);
    }

    @Test
    public void testEncode() {
        byte[] data = new byte[]{0x12, 0x34, 0x56};
        byte[] encoded = Hex.encode(data);
        Assert.assertNotNull(encoded);
        Assert.assertTrue(encoded.length > 0);
    }

    @Test
    public void testDecode() {
        String hexString = "123456";
        byte[] decoded = Hex.decode(hexString);
        Assert.assertNotNull(decoded);
        Assert.assertEquals(3, decoded.length);
        Assert.assertEquals(0x12, decoded[0]);
        Assert.assertEquals(0x34, decoded[1]);
        Assert.assertEquals(0x56, decoded[2]);
    }

    @Test
    public void testDecodeBytes() {
        byte[] hexBytes = "1234".getBytes();
        byte[] decoded = Hex.decode(hexBytes);
        Assert.assertNotNull(decoded);
        Assert.assertEquals(2, decoded.length);
    }

    @Test(expected = DecoderException.class)
    public void testDecodeInvalidHex() {
        Hex.decode("xyz");
    }

    @Test
    public void testEncodeAndDecode() {
        byte[] original = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05};
        byte[] encoded = Hex.encode(original);
        String hexString = new String(encoded);
        byte[] decoded = Hex.decode(hexString);

        Assert.assertArrayEquals(original, decoded);
    }

    @Test
    public void testToHexStringWithOffset() {
        byte[] data = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05};
        String result = Hex.toHexString(data, 1, 3);
        Assert.assertEquals("020304", result);
    }
}

