package org.fisco.bcos.sdk.v3.test.utils;

import org.fisco.bcos.sdk.v3.utils.ByteUtils;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

public class ByteUtilsTest {

    @Test
    public void testAppendByte() {
        byte[] original = new byte[]{1, 2, 3};
        byte[] result = ByteUtils.appendByte(original, (byte) 4);

        Assert.assertEquals(4, result.length);
        Assert.assertEquals(1, result[0]);
        Assert.assertEquals(2, result[1]);
        Assert.assertEquals(3, result[2]);
        Assert.assertEquals(4, result[3]);

        // Test appending to empty array
        byte[] empty = new byte[0];
        byte[] resultEmpty = ByteUtils.appendByte(empty, (byte) 5);
        Assert.assertEquals(1, resultEmpty.length);
        Assert.assertEquals(5, resultEmpty[0]);
    }

    @Test
    public void testBigIntegerToBytes() {
        // Test positive number
        BigInteger value = new BigInteger("256");
        byte[] result = ByteUtils.bigIntegerToBytes(value, 4);
        Assert.assertEquals(4, result.length);
        Assert.assertEquals(0, result[0]);
        Assert.assertEquals(0, result[1]);
        Assert.assertEquals(1, result[2]);
        Assert.assertEquals(0, result[3]);

        // Test zero
        BigInteger zero = BigInteger.ZERO;
        byte[] resultZero = ByteUtils.bigIntegerToBytes(zero, 4);
        Assert.assertEquals(4, resultZero.length);
        Assert.assertEquals(0, resultZero[0]);

        // Test null
        byte[] resultNull = ByteUtils.bigIntegerToBytes(null, 4);
        Assert.assertNull(resultNull);

        // Test large number
        BigInteger large = new BigInteger("65535");
        byte[] resultLarge = ByteUtils.bigIntegerToBytes(large, 4);
        Assert.assertEquals(4, resultLarge.length);
        Assert.assertEquals(0, resultLarge[0]);
        Assert.assertEquals(0, resultLarge[1]);
        Assert.assertEquals((byte) 0xFF, resultLarge[2]);
        Assert.assertEquals((byte) 0xFF, resultLarge[3]);
    }

    @Test
    public void testBigIntegerToBytesNoSize() {
        // Test positive number
        BigInteger value = new BigInteger("256");
        byte[] result = ByteUtils.bigIntegerToBytes(value);
        Assert.assertEquals(2, result.length);
        Assert.assertEquals(1, result[0]);
        Assert.assertEquals(0, result[1]);

        // Test null
        byte[] resultNull = ByteUtils.bigIntegerToBytes(null);
        Assert.assertNull(resultNull);

        // Test zero
        BigInteger zero = BigInteger.ZERO;
        byte[] resultZero = ByteUtils.bigIntegerToBytes(zero);
        Assert.assertEquals(1, resultZero.length);
        Assert.assertEquals(0, resultZero[0]);
    }

    @Test
    public void testBigIntegerToBytesSigned() {
        // Test positive number
        BigInteger value = new BigInteger("100");
        byte[] result = ByteUtils.bigIntegerToBytesSigned(value, 4);
        Assert.assertEquals(4, result.length);
        Assert.assertEquals(0, result[0]);
        Assert.assertEquals(0, result[1]);
        Assert.assertEquals(0, result[2]);
        Assert.assertEquals(100, result[3]);

        // Test negative number
        BigInteger negative = new BigInteger("-1");
        byte[] resultNeg = ByteUtils.bigIntegerToBytesSigned(negative, 4);
        Assert.assertEquals(4, resultNeg.length);
        Assert.assertEquals((byte) 0xFF, resultNeg[0]);
        Assert.assertEquals((byte) 0xFF, resultNeg[1]);
        Assert.assertEquals((byte) 0xFF, resultNeg[2]);
        Assert.assertEquals((byte) 0xFF, resultNeg[3]);

        // Test null
        byte[] resultNull = ByteUtils.bigIntegerToBytesSigned(null, 4);
        Assert.assertNull(resultNull);
    }

    @Test
    public void testBytesToBigInteger() {
        // Test normal byte array
        byte[] bytes = new byte[]{1, 2, 3};
        BigInteger result = ByteUtils.bytesToBigInteger(bytes);
        Assert.assertEquals(new BigInteger("66051"), result);

        // Test null
        BigInteger resultNull = ByteUtils.bytesToBigInteger(null);
        Assert.assertEquals(BigInteger.ZERO, resultNull);

        // Test empty array
        BigInteger resultEmpty = ByteUtils.bytesToBigInteger(new byte[0]);
        Assert.assertEquals(BigInteger.ZERO, resultEmpty);

        // Test single byte
        byte[] single = new byte[]{5};
        BigInteger resultSingle = ByteUtils.bytesToBigInteger(single);
        Assert.assertEquals(new BigInteger("5"), resultSingle);
    }

    @Test
    public void testMatchingNibbleLength() {
        byte[] a = new byte[]{1, 2, 3, 4};
        byte[] b = new byte[]{1, 2, 5, 6};

        int result = ByteUtils.matchingNibbleLength(a, b);
        Assert.assertEquals(2, result);

        // Test completely matching
        byte[] c = new byte[]{1, 2, 3};
        byte[] d = new byte[]{1, 2, 3};
        Assert.assertEquals(3, ByteUtils.matchingNibbleLength(c, d));

        // Test no match
        byte[] e = new byte[]{1, 2, 3};
        byte[] f = new byte[]{4, 5, 6};
        Assert.assertEquals(0, ByteUtils.matchingNibbleLength(e, f));

        // Test different lengths
        byte[] g = new byte[]{1, 2};
        byte[] h = new byte[]{1, 2, 3, 4};
        Assert.assertEquals(2, ByteUtils.matchingNibbleLength(g, h));
    }

    @Test
    public void testLongToBytes() {
        long value = 256L;
        byte[] result = ByteUtils.longToBytes(value);

        Assert.assertEquals(8, result.length);
        Assert.assertEquals(1, result[6]);
        Assert.assertEquals(0, result[7]);

        // Test zero
        byte[] resultZero = ByteUtils.longToBytes(0L);
        Assert.assertEquals(8, resultZero.length);

        // Test negative
        byte[] resultNeg = ByteUtils.longToBytes(-1L);
        Assert.assertEquals(8, resultNeg.length);
    }

    @Test
    public void testLongToBytesNoLeadZeroes() {
        long value = 256L;
        byte[] result = ByteUtils.longToBytesNoLeadZeroes(value);
        Assert.assertEquals(2, result.length);
        Assert.assertEquals(1, result[0]);
        Assert.assertEquals(0, result[1]);

        // Test zero
        byte[] resultZero = ByteUtils.longToBytesNoLeadZeroes(0L);
        Assert.assertEquals(0, resultZero.length);

        // Test small value
        byte[] resultSmall = ByteUtils.longToBytesNoLeadZeroes(5L);
        Assert.assertEquals(1, resultSmall.length);
        Assert.assertEquals(5, resultSmall[0]);
    }

    @Test
    public void testIntToBytes() {
        int value = 256;
        byte[] result = ByteUtils.intToBytes(value);

        Assert.assertEquals(4, result.length);
        Assert.assertEquals(0, result[0]);
        Assert.assertEquals(0, result[1]);
        Assert.assertEquals(1, result[2]);
        Assert.assertEquals(0, result[3]);

        // Test zero
        byte[] resultZero = ByteUtils.intToBytes(0);
        Assert.assertEquals(4, resultZero.length);
    }

    @Test
    public void testIntToBytesNoLeadZeroes() {
        int value = 256;
        byte[] result = ByteUtils.intToBytesNoLeadZeroes(value);
        Assert.assertEquals(2, result.length);
        Assert.assertEquals(1, result[0]);
        Assert.assertEquals(0, result[1]);

        // Test zero
        byte[] resultZero = ByteUtils.intToBytesNoLeadZeroes(0);
        Assert.assertEquals(0, resultZero.length);

        // Test single byte value
        byte[] resultSmall = ByteUtils.intToBytesNoLeadZeroes(5);
        Assert.assertEquals(1, resultSmall.length);
        Assert.assertEquals(5, resultSmall[0]);

        // Test max value
        byte[] resultMax = ByteUtils.intToBytesNoLeadZeroes(Integer.MAX_VALUE);
        Assert.assertTrue(resultMax.length <= 4);
    }

    @Test
    public void testCalcPacketLength() {
        byte[] msg = new byte[1024];
        byte[] result = ByteUtils.calcPacketLength(msg);

        Assert.assertEquals(4, result.length);
        Assert.assertEquals(0, result[0]);
        Assert.assertEquals(0, result[1]);
        Assert.assertEquals(4, result[2]);
        Assert.assertEquals(0, result[3]);

        // Test small message
        byte[] smallMsg = new byte[10];
        byte[] resultSmall = ByteUtils.calcPacketLength(smallMsg);
        Assert.assertEquals(4, resultSmall.length);
    }
}

