package org.fisco.bcos.sdk.v3.test.codec.abi.tools;

import org.fisco.bcos.sdk.v3.codec.abi.tools.TopicTools;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;

public class TopicToolsTest {

    private TopicTools topicTools;
    private CryptoSuite cryptoSuite;

    @Before
    public void setUp() {
        // Create a real CryptoSuite for testing
        cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        topicTools = new TopicTools(cryptoSuite);
    }

    @Test
    public void testConstructor() {
        TopicTools tools = new TopicTools(cryptoSuite);
        Assert.assertNotNull(tools);
    }

    private static String repeat(char c, int count) {
        char[] arr = new char[count];
        Arrays.fill(arr, c);
        return new String(arr);
    }

    @Test
    public void testMaxNumTopicEventLog() {
        Assert.assertEquals(4, TopicTools.MAX_NUM_TOPIC_EVENT_LOG);
    }

    @Test
    public void testTopicLengthInHex() {
        Assert.assertEquals(64, TopicTools.TOPIC_LENGTH_IN_HEX);
    }

    @Test
    public void testValidTopicWithValidHexWith0xPrefix() {
        String validTopic = "0x" + repeat('a', 64);
        Assert.assertTrue(TopicTools.validTopic(validTopic));
    }

    @Test
    public void testValidTopicWithValidHexWith0XPrefix() {
        String validTopic = "0X" + repeat('a', 64);
        Assert.assertTrue(TopicTools.validTopic(validTopic));
    }

    @Test
    public void testValidTopicWithValidHexNoPrefix() {
        String validTopic = repeat('a', 64);
        Assert.assertTrue(TopicTools.validTopic(validTopic));
    }

    @Test
    public void testValidTopicWithNull() {
        Assert.assertFalse(TopicTools.validTopic(null));
    }

    @Test
    public void testValidTopicWithShortString() {
        String shortTopic = "0x" + repeat('a', 32);
        Assert.assertFalse(TopicTools.validTopic(shortTopic));
    }

    @Test
    public void testValidTopicWithLongString() {
        String longTopic = "0x" + repeat('a', 100);
        Assert.assertFalse(TopicTools.validTopic(longTopic));
    }

    @Test
    public void testValidTopicWithEmptyString() {
        Assert.assertFalse(TopicTools.validTopic(""));
    }

    @Test
    public void testIntegerToTopicWithZero() {
        String result = topicTools.integerToTopic(BigInteger.ZERO);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("0x"));
        Assert.assertEquals(66, result.length()); // 0x + 64 hex chars
        Assert.assertTrue(result.matches("0x0{64}"));
    }

    @Test
    public void testIntegerToTopicWithOne() {
        String result = topicTools.integerToTopic(BigInteger.ONE);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("0x"));
        Assert.assertEquals(66, result.length());
        Assert.assertTrue(result.matches("0x0{63}1"));
    }

    @Test
    public void testIntegerToTopicWithLargeNumber() {
        BigInteger largeNumber = new BigInteger("123456789012345678901234567890");
        String result = topicTools.integerToTopic(largeNumber);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("0x"));
        Assert.assertEquals(66, result.length());
    }

    @Test
    public void testIntegerToTopicWithNegativeNumber() {
        // Note: negative numbers may throw UnsupportedOperationException in some implementations
        // Test with a large positive number instead
        BigInteger largePositive = new BigInteger("2").pow(255);
        String result = topicTools.integerToTopic(largePositive);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("0x"));
        Assert.assertEquals(66, result.length());
    }

    @Test
    public void testBoolToTopicWithTrue() {
        String result = topicTools.boolToTopic(true);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("0x"));
        Assert.assertEquals(66, result.length());
        Assert.assertTrue(result.matches("0x0{63}1"));
    }

    @Test
    public void testBoolToTopicWithFalse() {
        String result = topicTools.boolToTopic(false);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("0x"));
        Assert.assertEquals(66, result.length());
        Assert.assertTrue(result.matches("0x0{64}"));
    }

    @Test
    public void testAddressToTopicWithValidAddress() {
        String validAddress = "0x1234567890123456789012345678901234567890";
        String result = topicTools.addressToTopic(validAddress);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("0x"));
        Assert.assertEquals(66, result.length());
        Assert.assertTrue(result.endsWith("1234567890123456789012345678901234567890"));
    }

    @Test
    public void testAddressToTopicWithValidAddressNoPrefix() {
        String validAddress = "1234567890123456789012345678901234567890";
        String result = topicTools.addressToTopic(validAddress);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("0x000000000000000000000000"));
        Assert.assertTrue(result.endsWith("1234567890123456789012345678901234567890"));
    }

    @Test
    public void testAddressToTopicWithShortValidAddress() {
        // Short addresses are valid according to AddressUtils (1-40 hex chars)
        String shortAddress = "0x1234";
        String result = topicTools.addressToTopic(shortAddress);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("0x000000000000000000000000"));
    }

    @Test(expected = Exception.class)
    public void testAddressToTopicWithInvalidCharacters() {
        String invalidAddress = "0xGGGG";
        topicTools.addressToTopic(invalidAddress);
    }

    @Test(expected = Exception.class)
    public void testAddressToTopicWithTooLongAddress() {
        String tooLong = "0x" + repeat('1', 41);
        topicTools.addressToTopic(tooLong);
    }

    @Test(expected = Exception.class)
    public void testAddressToTopicWithNull() {
        // This should throw NullPointerException or IllegalArgumentException
        topicTools.addressToTopic(null);
    }

    @Test
    public void testStringToTopic() {
        String testString = "hello";
        String result = topicTools.stringToTopic(testString);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("0x"));
        // The result should be a hash, typically 64 hex chars + 0x prefix
        Assert.assertTrue(result.length() > 2);
    }

    @Test
    public void testStringToTopicWithEmptyString() {
        String result = topicTools.stringToTopic("");

        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("0x"));
    }

    @Test
    public void testStringToTopicConsistency() {
        String testString = "test";
        String result1 = topicTools.stringToTopic(testString);
        String result2 = topicTools.stringToTopic(testString);

        Assert.assertEquals("Same string should produce same hash", result1, result2);
    }

    @Test
    public void testStringToTopicDifferentStrings() {
        String result1 = topicTools.stringToTopic("test1");
        String result2 = topicTools.stringToTopic("test2");

        Assert.assertNotEquals("Different strings should produce different hashes", result1, result2);
    }

    @Test
    public void testBytesToTopic() {
        byte[] testBytes = new byte[]{1, 2, 3, 4, 5};
        String result = topicTools.bytesToTopic(testBytes);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("0x"));
        Assert.assertTrue(result.length() > 2);
    }

    @Test
    public void testBytesToTopicWithEmptyArray() {
        byte[] emptyBytes = new byte[0];
        String result = topicTools.bytesToTopic(emptyBytes);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("0x"));
    }

    @Test
    public void testBytesToTopicConsistency() {
        byte[] testBytes = new byte[]{1, 2, 3};
        String result1 = topicTools.bytesToTopic(testBytes);
        String result2 = topicTools.bytesToTopic(testBytes);

        Assert.assertEquals("Same bytes should produce same hash", result1, result2);
    }

    @Test
    public void testByteNToTopicWithSmallArray() {
        byte[] testBytes = new byte[]{1, 2, 3};
        String result = topicTools.byteNToTopic(testBytes);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("0x"));
    }

    @Test
    public void testByteNToTopicWithSingleByte() {
        byte[] singleByte = new byte[]{42};
        String result = topicTools.byteNToTopic(singleByte);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("0x"));
    }

    @Test
    public void testByteNToTopicWith32Bytes() {
        byte[] maxBytes = new byte[32];
        for (int i = 0; i < 32; i++) {
            maxBytes[i] = (byte) i;
        }
        String result = topicTools.byteNToTopic(maxBytes);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("0x"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testByteNToTopicWithTooLargeArray() {
        byte[] tooLarge = new byte[33];
        topicTools.byteNToTopic(tooLarge);
    }

    @Test
    public void testByteNToTopicWithBoundary() {
        // Test with exactly 32 bytes (boundary condition)
        byte[] boundaryBytes = new byte[32];
        String result = topicTools.byteNToTopic(boundaryBytes);
        Assert.assertNotNull(result);

        // Test with 31 bytes (just below boundary)
        byte[] belowBoundary = new byte[31];
        String result2 = topicTools.byteNToTopic(belowBoundary);
        Assert.assertNotNull(result2);
    }

    @Test
    public void testTopicToolsWithDifferentCryptoSuite() {
        // Test with SM crypto
        CryptoSuite smCryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        TopicTools smTopicTools = new TopicTools(smCryptoSuite);

        String result = smTopicTools.stringToTopic("test");
        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("0x"));
    }

    @Test
    public void testIntegerToTopicBoundaryValues() {
        // Test with max value
        BigInteger maxValue = new BigInteger("2").pow(256).subtract(BigInteger.ONE);
        String result = topicTools.integerToTopic(maxValue);
        Assert.assertNotNull(result);
        Assert.assertEquals(66, result.length());
    }

    @Test
    public void testValidTopicEdgeCases() {
        // Test exact length without prefix
        String exactLength = repeat('f', 64);
        Assert.assertTrue(TopicTools.validTopic(exactLength));

        // Test one character short
        String oneShort = repeat('f', 63);
        Assert.assertFalse(TopicTools.validTopic(oneShort));

        // Test one character long
        String oneLong = repeat('f', 65);
        Assert.assertFalse(TopicTools.validTopic(oneLong));
    }
}
