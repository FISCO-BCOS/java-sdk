package org.fisco.bcos.sdk.v3.test.utils;

import org.fisco.bcos.sdk.v3.utils.ByteUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ByteUtilsTest {

    private byte[] testBytes;
    private byte[] emptyBytes;

    @Before
    public void setUp() {
        testBytes = new byte[]{1, 2, 3, 4, 5};
        emptyBytes = new byte[0];
    }

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

    @Test
    public void testByteArrayToInt() {
        // Test normal conversion
        byte[] bytes = new byte[]{0, 0, 1, 0};
        int result = ByteUtils.byteArrayToInt(bytes);
        Assert.assertEquals(256, result);

        // Test null
        int resultNull = ByteUtils.byteArrayToInt(null);
        Assert.assertEquals(0, resultNull);

        // Test empty array
        int resultEmpty = ByteUtils.byteArrayToInt(new byte[0]);
        Assert.assertEquals(0, resultEmpty);

        // Test single byte
        byte[] single = new byte[]{5};
        int resultSingle = ByteUtils.byteArrayToInt(single);
        Assert.assertEquals(5, resultSingle);
    }

    @Test
    public void testByteArrayToLong() {
        // Test normal conversion
        byte[] bytes = new byte[]{0, 0, 0, 0, 0, 0, 1, 0};
        long result = ByteUtils.byteArrayToLong(bytes);
        Assert.assertEquals(256L, result);

        // Test null
        long resultNull = ByteUtils.byteArrayToLong(null);
        Assert.assertEquals(0L, resultNull);

        // Test empty array
        long resultEmpty = ByteUtils.byteArrayToLong(new byte[0]);
        Assert.assertEquals(0L, resultEmpty);

        // Test small value
        byte[] small = new byte[]{10};
        long resultSmall = ByteUtils.byteArrayToLong(small);
        Assert.assertEquals(10L, resultSmall);
    }

    @Test
    public void testNibblesToPrettyString() {
        byte[] nibbles = new byte[]{1, 2, 15};
        String result = ByteUtils.nibblesToPrettyString(nibbles);
        Assert.assertTrue(result.contains("\\x"));
        Assert.assertTrue(result.length() > 0);

        // Test empty array
        byte[] empty = new byte[0];
        String resultEmpty = ByteUtils.nibblesToPrettyString(empty);
        Assert.assertEquals("", resultEmpty);
    }

    @Test
    public void testOneByteToHexString() {
        // Test single digit hex
        String result1 = ByteUtils.oneByteToHexString((byte) 5);
        Assert.assertEquals("05", result1);

        // Test two digit hex
        String result2 = ByteUtils.oneByteToHexString((byte) 255);
        Assert.assertEquals("ff", result2);

        // Test zero
        String resultZero = ByteUtils.oneByteToHexString((byte) 0);
        Assert.assertEquals("00", resultZero);
    }

    @Test
    public void testNumBytes() {
        // Test small number
        int result1 = ByteUtils.numBytes("10");
        Assert.assertEquals(1, result1);

        // Test larger number
        int result2 = ByteUtils.numBytes("256");
        Assert.assertEquals(2, result2);

        // Test zero
        int resultZero = ByteUtils.numBytes("0");
        Assert.assertEquals(1, resultZero);
    }

    @Test
    public void testEncodeDataList() {
        // Test encoding multiple values
        byte[] result = ByteUtils.encodeDataList("100", "200");
        Assert.assertNotNull(result);
        Assert.assertEquals(64, result.length); // 32 bytes per value

        // Test single value
        byte[] resultSingle = ByteUtils.encodeDataList("42");
        Assert.assertEquals(32, resultSingle.length);
    }

    @Test(expected = RuntimeException.class)
    public void testEncodeDataListTooLarge() {
        // Test value too large (more than 32 bytes)
        String largeValue = new BigInteger("2").pow(300).toString();
        ByteUtils.encodeDataList(largeValue);
    }

    @Test
    public void testFirstNonZeroByte() {
        // Test array with leading zeros
        byte[] withZeros = new byte[]{0, 0, 0, 5, 6};
        int result = ByteUtils.firstNonZeroByte(withZeros);
        Assert.assertEquals(3, result);

        // Test array with no leading zeros
        byte[] noZeros = new byte[]{1, 2, 3};
        int resultNoZeros = ByteUtils.firstNonZeroByte(noZeros);
        Assert.assertEquals(0, resultNoZeros);

        // Test all zeros
        byte[] allZeros = new byte[]{0, 0, 0};
        int resultAllZeros = ByteUtils.firstNonZeroByte(allZeros);
        Assert.assertEquals(-1, resultAllZeros);
    }

    @Test
    public void testStripLeadingZeroes() {
        // Test with leading zeros
        byte[] withZeros = new byte[]{0, 0, 0, 1, 2, 3};
        byte[] result = ByteUtils.stripLeadingZeroes(withZeros);
        Assert.assertEquals(3, result.length);
        Assert.assertEquals(1, result[0]);

        // Test all zeros
        byte[] allZeros = new byte[]{0, 0, 0};
        byte[] resultAllZeros = ByteUtils.stripLeadingZeroes(allZeros);
        Assert.assertEquals(1, resultAllZeros.length);
        Assert.assertEquals(0, resultAllZeros[0]);

        // Test no leading zeros
        byte[] noZeros = new byte[]{1, 2, 3};
        byte[] resultNoZeros = ByteUtils.stripLeadingZeroes(noZeros);
        Assert.assertArrayEquals(noZeros, resultNoZeros);

        // Test null
        byte[] resultNull = ByteUtils.stripLeadingZeroes(null);
        Assert.assertNull(resultNull);
    }

    @Test
    public void testIncrement() {
        // Test normal increment
        byte[] bytes = new byte[]{0, 0, 5};
        boolean result = ByteUtils.increment(bytes);
        Assert.assertTrue(result);
        Assert.assertEquals(6, bytes[2]);

        // Test overflow in one byte
        byte[] overflow = new byte[]{0, 0, (byte) 255};
        boolean resultOverflow = ByteUtils.increment(overflow);
        Assert.assertTrue(resultOverflow);
        Assert.assertEquals(0, overflow[2]);
        Assert.assertEquals(1, overflow[1]);

        // Test all bytes at max
        byte[] allMax = new byte[]{(byte) 255, (byte) 255, (byte) 255};
        boolean resultAllMax = ByteUtils.increment(allMax);
        Assert.assertFalse(resultAllMax);
    }

    @Test
    public void testCopyToArray() {
        // Test normal BigInteger
        BigInteger value = new BigInteger("256");
        byte[] result = ByteUtils.copyToArray(value);
        Assert.assertEquals(32, result.length);

        // Test zero
        BigInteger zero = BigInteger.ZERO;
        byte[] resultZero = ByteUtils.copyToArray(zero);
        Assert.assertEquals(32, resultZero.length);

        // Test large value
        BigInteger large = new BigInteger("2").pow(100);
        byte[] resultLarge = ByteUtils.copyToArray(large);
        Assert.assertEquals(32, resultLarge.length);
    }

    @Test
    public void testSetBit() {
        byte[] data = new byte[]{0, 0, 0};
        
        // Set bit to 1
        byte[] result = ByteUtils.setBit(data, 0, 1);
        Assert.assertEquals(1, result[2]);

        // Set bit to 0
        byte[] result2 = ByteUtils.setBit(data, 0, 0);
        Assert.assertEquals(0, result2[2]);

        // Test higher bit position
        byte[] data2 = new byte[]{0, 0, 0};
        ByteUtils.setBit(data2, 8, 1);
        Assert.assertEquals(1, data2[1]);
    }

    @Test(expected = Error.class)
    public void testSetBitOutOfBounds() {
        byte[] data = new byte[]{0, 0};
        ByteUtils.setBit(data, 100, 1);
    }

    @Test
    public void testGetBit() {
        byte[] data = new byte[]{0, 0, 5}; // binary: ...00000101
        
        // Get bit at position 0 (LSB)
        int bit0 = ByteUtils.getBit(data, 0);
        Assert.assertEquals(1, bit0);

        // Get bit at position 1
        int bit1 = ByteUtils.getBit(data, 1);
        Assert.assertEquals(0, bit1);

        // Get bit at position 2
        int bit2 = ByteUtils.getBit(data, 2);
        Assert.assertEquals(1, bit2);
    }

    @Test(expected = Error.class)
    public void testGetBitOutOfBounds() {
        byte[] data = new byte[]{0, 0};
        ByteUtils.getBit(data, 100);
    }

    @Test
    public void testAnd() {
        byte[] b1 = new byte[]{(byte) 0xFF, (byte) 0x0F};
        byte[] b2 = new byte[]{(byte) 0xF0, (byte) 0xFF};
        byte[] result = ByteUtils.and(b1, b2);
        
        Assert.assertEquals(2, result.length);
        Assert.assertEquals((byte) 0xF0, result[0]);
        Assert.assertEquals((byte) 0x0F, result[1]);
    }

    @Test(expected = RuntimeException.class)
    public void testAndDifferentSizes() {
        byte[] b1 = new byte[]{1, 2};
        byte[] b2 = new byte[]{1, 2, 3};
        ByteUtils.and(b1, b2);
    }

    @Test
    public void testOr() {
        byte[] b1 = new byte[]{(byte) 0xF0, (byte) 0x0F};
        byte[] b2 = new byte[]{(byte) 0x0F, (byte) 0xF0};
        byte[] result = ByteUtils.or(b1, b2);
        
        Assert.assertEquals(2, result.length);
        Assert.assertEquals((byte) 0xFF, result[0]);
        Assert.assertEquals((byte) 0xFF, result[1]);
    }

    @Test(expected = RuntimeException.class)
    public void testOrDifferentSizes() {
        byte[] b1 = new byte[]{1, 2};
        byte[] b2 = new byte[]{1, 2, 3};
        ByteUtils.or(b1, b2);
    }

    @Test
    public void testXor() {
        byte[] b1 = new byte[]{(byte) 0xFF, (byte) 0x00};
        byte[] b2 = new byte[]{(byte) 0x0F, (byte) 0xFF};
        byte[] result = ByteUtils.xor(b1, b2);
        
        Assert.assertEquals(2, result.length);
        Assert.assertEquals((byte) 0xF0, result[0]);
        Assert.assertEquals((byte) 0xFF, result[1]);
    }

    @Test(expected = RuntimeException.class)
    public void testXorDifferentSizes() {
        byte[] b1 = new byte[]{1, 2};
        byte[] b2 = new byte[]{1, 2, 3};
        ByteUtils.xor(b1, b2);
    }

    @Test
    public void testXorAlignRight() {
        byte[] b1 = new byte[]{1, 2, 3};
        byte[] b2 = new byte[]{4, 5};
        byte[] result = ByteUtils.xorAlignRight(b1, b2);
        
        Assert.assertEquals(3, result.length);
        
        // Test reverse case
        byte[] result2 = ByteUtils.xorAlignRight(b2, b1);
        Assert.assertEquals(3, result2.length);

        // Test same size
        byte[] b3 = new byte[]{1, 2};
        byte[] b4 = new byte[]{3, 4};
        byte[] result3 = ByteUtils.xorAlignRight(b3, b4);
        Assert.assertEquals(2, result3.length);
    }

    @Test
    public void testMerge() {
        byte[] arr1 = new byte[]{1, 2};
        byte[] arr2 = new byte[]{3, 4};
        byte[] arr3 = new byte[]{5};
        
        byte[] result = ByteUtils.merge(arr1, arr2, arr3);
        
        Assert.assertEquals(5, result.length);
        Assert.assertEquals(1, result[0]);
        Assert.assertEquals(2, result[1]);
        Assert.assertEquals(3, result[2]);
        Assert.assertEquals(4, result[3]);
        Assert.assertEquals(5, result[4]);

        // Test single array
        byte[] resultSingle = ByteUtils.merge(arr1);
        Assert.assertArrayEquals(arr1, resultSingle);

        // Test empty arrays
        byte[] resultEmpty = ByteUtils.merge(new byte[0], new byte[0]);
        Assert.assertEquals(0, resultEmpty.length);
    }

    @Test
    public void testIsNullOrZeroArray() {
        // Test null
        Assert.assertTrue(ByteUtils.isNullOrZeroArray(null));

        // Test zero-length array
        Assert.assertTrue(ByteUtils.isNullOrZeroArray(new byte[0]));

        // Test non-empty array
        Assert.assertFalse(ByteUtils.isNullOrZeroArray(new byte[]{1, 2}));
    }

    @Test
    public void testIsSingleZero() {
        // Test single zero
        Assert.assertTrue(ByteUtils.isSingleZero(new byte[]{0}));

        // Test single non-zero
        Assert.assertFalse(ByteUtils.isSingleZero(new byte[]{1}));

        // Test multiple elements
        Assert.assertFalse(ByteUtils.isSingleZero(new byte[]{0, 0}));

        // Test empty array
        Assert.assertFalse(ByteUtils.isSingleZero(new byte[0]));
    }

    @Test
    public void testDifference() {
        Set<byte[]> setA = new HashSet<>();
        setA.add(new byte[]{1, 2});
        setA.add(new byte[]{3, 4});
        setA.add(new byte[]{5, 6});

        Set<byte[]> setB = new HashSet<>();
        setB.add(new byte[]{3, 4});

        Set<byte[]> result = ByteUtils.difference(setA, setB);
        
        Assert.assertEquals(2, result.size());
    }

    @Test
    public void testLength() {
        byte[] arr1 = new byte[]{1, 2, 3};
        byte[] arr2 = new byte[]{4, 5};
        
        int result = ByteUtils.length(arr1, arr2);
        Assert.assertEquals(5, result);

        // Test with null
        int resultWithNull = ByteUtils.length(arr1, null, arr2);
        Assert.assertEquals(5, resultWithNull);

        // Test empty
        int resultEmpty = ByteUtils.length();
        Assert.assertEquals(0, resultEmpty);
    }

    @Test
    public void testBytesToInts() {
        // Test big endian
        byte[] bytes = new byte[]{0, 0, 0, 1, 0, 0, 0, 2};
        int[] result = ByteUtils.bytesToInts(bytes, true);
        
        Assert.assertEquals(2, result.length);
        Assert.assertEquals(1, result[0]);
        Assert.assertEquals(2, result[1]);

        // Test little endian
        int[] resultLittle = ByteUtils.bytesToInts(bytes, false);
        Assert.assertEquals(2, resultLittle.length);
    }

    @Test
    public void testBytesToIntsArray() {
        byte[] bytes = new byte[]{0, 0, 0, 1, 0, 0, 0, 2};
        int[] arr = new int[2];
        
        ByteUtils.bytesToInts(bytes, arr, true);
        
        Assert.assertEquals(1, arr[0]);
        Assert.assertEquals(2, arr[1]);
    }

    @Test
    public void testIntsToBytes() {
        // Test big endian
        int[] ints = new int[]{1, 2};
        byte[] result = ByteUtils.intsToBytes(ints, true);
        
        Assert.assertEquals(8, result.length);

        // Test little endian
        byte[] resultLittle = ByteUtils.intsToBytes(ints, false);
        Assert.assertEquals(8, resultLittle.length);
    }

    @Test
    public void testIntsToBytesArray() {
        int[] ints = new int[]{1, 2};
        byte[] bytes = new byte[8];
        
        ByteUtils.intsToBytes(ints, bytes, true);
        
        Assert.assertEquals(8, bytes.length);
    }

    @Test
    public void testBigEndianToShort() {
        byte[] bytes = new byte[]{0, 5};
        short result = ByteUtils.bigEndianToShort(bytes);
        Assert.assertEquals(5, result);

        // Test with offset
        byte[] bytesWithOffset = new byte[]{1, 0, 10};
        short resultWithOffset = ByteUtils.bigEndianToShort(bytesWithOffset, 1);
        Assert.assertEquals(10, resultWithOffset);
    }

    @Test
    public void testShortToBytes() {
        short value = 256;
        byte[] result = ByteUtils.shortToBytes(value);
        
        Assert.assertEquals(2, result.length);
        Assert.assertEquals(1, result[0]);
        Assert.assertEquals(0, result[1]);
    }

    @Test
    public void testHexStringToBytes() {
        // Test with 0x prefix
        String hex1 = "0x0102";
        byte[] result1 = ByteUtils.hexStringToBytes(hex1);
        Assert.assertEquals(2, result1.length);
        Assert.assertEquals(1, result1[0]);
        Assert.assertEquals(2, result1[1]);

        // Test without prefix
        String hex2 = "0102";
        byte[] result2 = ByteUtils.hexStringToBytes(hex2);
        Assert.assertEquals(2, result2.length);

        // Test odd length
        String hex3 = "123";
        byte[] result3 = ByteUtils.hexStringToBytes(hex3);
        Assert.assertEquals(2, result3.length);

        // Test null
        byte[] resultNull = ByteUtils.hexStringToBytes(null);
        Assert.assertEquals(0, resultNull.length);
    }

    @Test
    public void testHostToBytes() {
        // Test localhost
        byte[] result = ByteUtils.hostToBytes("127.0.0.1");
        Assert.assertEquals(4, result.length);
        Assert.assertEquals(127, result[0]);
        Assert.assertEquals(0, result[1]);
        Assert.assertEquals(0, result[2]);
        Assert.assertEquals(1, result[3]);

        // Test invalid host
        byte[] resultInvalid = ByteUtils.hostToBytes("invalid.host.name.xyz");
        Assert.assertEquals(4, resultInvalid.length);
    }

    @Test
    public void testBytesToIp() {
        byte[] bytes = new byte[]{127, 0, 0, 1};
        String result = ByteUtils.bytesToIp(bytes);
        Assert.assertEquals("127.0.0.1", result);

        // Test different IP
        byte[] bytes2 = new byte[]{(byte) 192, (byte) 168, 1, 1};
        String result2 = ByteUtils.bytesToIp(bytes2);
        Assert.assertEquals("192.168.1.1", result2);
    }

    @Test
    public void testNumberOfLeadingZeros() {
        // Test with leading zeros
        byte[] withZeros = new byte[]{0, 0, 0, 1};
        int result = ByteUtils.numberOfLeadingZeros(withZeros);
        Assert.assertTrue(result >= 24);

        // Test all zeros
        byte[] allZeros = new byte[]{0, 0, 0};
        int resultAllZeros = ByteUtils.numberOfLeadingZeros(allZeros);
        Assert.assertEquals(24, resultAllZeros);

        // Test no leading zeros
        byte[] noZeros = new byte[]{(byte) 255, 0, 0};
        int resultNoZeros = ByteUtils.numberOfLeadingZeros(noZeros);
        Assert.assertEquals(0, resultNoZeros);
    }

    @Test
    public void testParseBytes() {
        byte[] input = new byte[]{1, 2, 3, 4, 5};
        
        // Test normal parsing
        byte[] result = ByteUtils.parseBytes(input, 1, 3);
        Assert.assertEquals(3, result.length);
        Assert.assertEquals(2, result[0]);
        Assert.assertEquals(3, result[1]);
        Assert.assertEquals(4, result[2]);

        // Test offset beyond array length
        byte[] resultBeyond = ByteUtils.parseBytes(input, 10, 3);
        Assert.assertEquals(0, resultBeyond.length);

        // Test zero length
        byte[] resultZero = ByteUtils.parseBytes(input, 0, 0);
        Assert.assertEquals(0, resultZero.length);
    }

    @Test
    public void testParseWord() {
        byte[] input = new byte[64]; // 2 words
        input[32] = 5;
        
        // Test first word
        byte[] word0 = ByteUtils.parseWord(input, 0);
        Assert.assertEquals(32, word0.length);

        // Test second word
        byte[] word1 = ByteUtils.parseWord(input, 1);
        Assert.assertEquals(32, word1.length);
        Assert.assertEquals(5, word1[0]);
    }

    @Test
    public void testParseWordWithOffset() {
        byte[] input = new byte[96]; // 3 words
        input[64] = 7;
        
        byte[] word = ByteUtils.parseWord(input, 32, 1);
        Assert.assertEquals(32, word.length);
        Assert.assertEquals(7, word[0]);
    }

    @Test
    public void testTrimLeadingBytes() {
        byte[] bytes = new byte[]{0, 0, 0, 1, 2, 3};
        byte[] result = ByteUtils.trimLeadingBytes(bytes, (byte) 0);
        
        Assert.assertEquals(3, result.length);
        Assert.assertEquals(1, result[0]);
        Assert.assertEquals(2, result[1]);
        Assert.assertEquals(3, result[2]);

        // Test no leading bytes to trim
        byte[] noTrim = new byte[]{1, 2, 3};
        byte[] resultNoTrim = ByteUtils.trimLeadingBytes(noTrim, (byte) 0);
        Assert.assertArrayEquals(noTrim, resultNoTrim);
    }

    @Test
    public void testTrimLeadingZeroes() {
        byte[] bytes = new byte[]{0, 0, 1, 2, 3};
        byte[] result = ByteUtils.trimLeadingZeroes(bytes);
        
        Assert.assertEquals(3, result.length);
        Assert.assertEquals(1, result[0]);
        Assert.assertEquals(2, result[1]);
        Assert.assertEquals(3, result[2]);
    }
}

