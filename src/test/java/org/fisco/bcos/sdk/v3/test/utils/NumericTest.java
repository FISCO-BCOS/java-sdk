package org.fisco.bcos.sdk.v3.test.utils;

import org.fisco.bcos.sdk.v3.utils.Numeric;
import org.fisco.bcos.sdk.v3.utils.exceptions.MessageDecodingException;
import org.fisco.bcos.sdk.v3.utils.exceptions.MessageEncodingException;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumericTest {

    @Test
    public void testEncodeQuantity() {
        BigInteger value = new BigInteger("255");
        String result = Numeric.encodeQuantity(value);
        Assert.assertEquals("0xff", result);

        BigInteger zero = BigInteger.ZERO;
        String resultZero = Numeric.encodeQuantity(zero);
        Assert.assertEquals("0x0", resultZero);

        BigInteger large = new BigInteger("1000");
        String resultLarge = Numeric.encodeQuantity(large);
        Assert.assertEquals("0x3e8", resultLarge);
    }

    @Test(expected = MessageEncodingException.class)
    public void testEncodeQuantityNegative() {
        BigInteger negative = new BigInteger("-1");
        Numeric.encodeQuantity(negative);
    }

    @Test
    public void testDecodeQuantity() {
        String hexValue = "0xff";
        BigInteger result = Numeric.decodeQuantity(hexValue);
        Assert.assertEquals(new BigInteger("255"), result);

        String hexZero = "0x0";
        BigInteger resultZero = Numeric.decodeQuantity(hexZero);
        Assert.assertEquals(BigInteger.ZERO, resultZero);

        // Test decimal string
        String decimalValue = "100";
        BigInteger resultDecimal = Numeric.decodeQuantity(decimalValue);
        Assert.assertEquals(new BigInteger("100"), resultDecimal);

        // Test null
        BigInteger resultNull = Numeric.decodeQuantity(null);
        Assert.assertEquals(BigInteger.ZERO, resultNull);
    }

    @Test(expected = MessageDecodingException.class)
    public void testDecodeQuantityInvalid() {
        Numeric.decodeQuantity("0xinvalid");
    }

    @Test
    public void testCleanHexPrefix() {
        Assert.assertEquals("1234", Numeric.cleanHexPrefix("0x1234"));
        Assert.assertEquals("1234", Numeric.cleanHexPrefix("1234"));
        Assert.assertEquals("", Numeric.cleanHexPrefix("0x"));
        Assert.assertEquals("ABCD", Numeric.cleanHexPrefix("0xABCD"));
    }

    @Test
    public void testPrependHexPrefix() {
        Assert.assertEquals("0x1234", Numeric.prependHexPrefix("1234"));
        Assert.assertEquals("0x1234", Numeric.prependHexPrefix("0x1234"));
        Assert.assertEquals("0x", Numeric.prependHexPrefix(""));
    }

    @Test
    public void testContainsHexPrefix() {
        Assert.assertTrue(Numeric.containsHexPrefix("0x1234"));
        Assert.assertTrue(Numeric.containsHexPrefix("0X1234"));
        Assert.assertFalse(Numeric.containsHexPrefix("1234"));
        Assert.assertFalse(Numeric.containsHexPrefix(""));
        Assert.assertFalse(Numeric.containsHexPrefix(null));
    }

    @Test
    public void testToBigIntFromBytes() {
        byte[] bytes = new byte[]{0x01, 0x02};
        BigInteger result = Numeric.toBigInt(bytes);
        Assert.assertEquals(new BigInteger("258"), result);

        byte[] single = new byte[]{0x05};
        BigInteger resultSingle = Numeric.toBigInt(single);
        Assert.assertEquals(new BigInteger("5"), resultSingle);

        byte[] empty = new byte[0];
        BigInteger resultEmpty = Numeric.toBigInt(empty);
        Assert.assertEquals(BigInteger.ZERO, resultEmpty);
    }

    @Test
    public void testToBigIntFromBytesWithOffset() {
        byte[] bytes = new byte[]{0x00, 0x01, 0x02, 0x03};
        BigInteger result = Numeric.toBigInt(bytes, 1, 2);
        Assert.assertEquals(new BigInteger("258"), result);
    }

    @Test
    public void testToBigIntFromString() {
        String hexValue = "0xff";
        BigInteger result = Numeric.toBigInt(hexValue);
        Assert.assertEquals(new BigInteger("255"), result);

        String hexWithoutPrefix = "ff";
        BigInteger resultNoPrefix = Numeric.toBigInt(hexWithoutPrefix);
        Assert.assertEquals(new BigInteger("255"), resultNoPrefix);

        String hexZero = "0x0";
        BigInteger resultZero = Numeric.toBigInt(hexZero);
        Assert.assertEquals(BigInteger.ZERO, resultZero);
    }

    @Test
    public void testToBigIntNoPrefix() {
        String hexValue = "ff";
        BigInteger result = Numeric.toBigIntNoPrefix(hexValue);
        Assert.assertEquals(new BigInteger("255"), result);

        String hexLarge = "1234";
        BigInteger resultLarge = Numeric.toBigIntNoPrefix(hexLarge);
        Assert.assertEquals(new BigInteger("4660"), resultLarge);
    }

    @Test
    public void testToHexString() {
        BigInteger value = new BigInteger("255");
        String result = Numeric.toHexString(value);
        Assert.assertNotNull(result);

        BigInteger zero = BigInteger.ZERO;
        String resultZero = Numeric.toHexString(zero);
        Assert.assertNotNull(resultZero);
    }

    @Test
    public void testToHexStringWithPrefix() {
        BigInteger value = new BigInteger("255");
        String result = Numeric.toHexStringWithPrefix(value);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("0x"));

        BigInteger zero = BigInteger.ZERO;
        String resultZero = Numeric.toHexStringWithPrefix(zero);
        Assert.assertTrue(resultZero.startsWith("0x"));
    }

    @Test
    public void testToHexStringNoPrefixZeroPadded() {
        BigInteger value = new BigInteger("255");
        String result = Numeric.toHexStringNoPrefixZeroPadded(value, 4);
        Assert.assertEquals(4, result.length());
        Assert.assertEquals("00ff", result);
    }

    @Test
    public void testIsIntegerValue() {
        Assert.assertTrue(Numeric.isIntegerValue(new BigDecimal("100")));
        Assert.assertTrue(Numeric.isIntegerValue(new BigDecimal("0")));
        Assert.assertFalse(Numeric.isIntegerValue(new BigDecimal("100.5")));
        Assert.assertFalse(Numeric.isIntegerValue(new BigDecimal("0.1")));
    }
}

