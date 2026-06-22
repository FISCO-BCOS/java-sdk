package org.fisco.bcos.sdk.v3.test.utils;

import org.fisco.bcos.sdk.v3.utils.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StringUtilsTest {

    @Test
    public void testToCsv() {
        List<String> list = Arrays.asList("apple", "banana", "cherry");
        String result = StringUtils.toCsv(list);
        Assert.assertEquals("apple, banana, cherry", result);

        // Test single element
        List<String> single = Collections.singletonList("single");
        Assert.assertEquals("single", StringUtils.toCsv(single));

        // Test empty list
        List<String> empty = Collections.emptyList();
        Assert.assertEquals("", StringUtils.toCsv(empty));

        // Test null
        Assert.assertNull(StringUtils.toCsv(null));
    }

    @Test
    public void testJoin() {
        List<String> list = Arrays.asList("one", "two", "three");
        String result = StringUtils.join(list, "-");
        Assert.assertEquals("one-two-three", result);

        // Test with different delimiter
        String resultComma = StringUtils.join(list, ",");
        Assert.assertEquals("one,two,three", resultComma);

        // Test with empty delimiter
        String resultEmpty = StringUtils.join(list, "");
        Assert.assertEquals("onetwothree", resultEmpty);

        // Test null list
        Assert.assertNull(StringUtils.join(null, ","));
    }

    @Test
    public void testCapitaliseFirstLetter() {
        Assert.assertEquals("Hello", StringUtils.capitaliseFirstLetter("hello"));
        Assert.assertEquals("World", StringUtils.capitaliseFirstLetter("World"));
        Assert.assertEquals("A", StringUtils.capitaliseFirstLetter("a"));

        // Test empty string
        Assert.assertEquals("", StringUtils.capitaliseFirstLetter(""));

        // Test null
        Assert.assertNull(StringUtils.capitaliseFirstLetter(null));

        // Test single character
        Assert.assertEquals("X", StringUtils.capitaliseFirstLetter("x"));

        // Test already capitalised
        Assert.assertEquals("Already", StringUtils.capitaliseFirstLetter("Already"));
    }

    @Test
    public void testLowercaseFirstLetter() {
        Assert.assertEquals("hello", StringUtils.lowercaseFirstLetter("Hello"));
        Assert.assertEquals("world", StringUtils.lowercaseFirstLetter("world"));
        Assert.assertEquals("a", StringUtils.lowercaseFirstLetter("A"));

        // Test empty string
        Assert.assertEquals("", StringUtils.lowercaseFirstLetter(""));

        // Test null
        Assert.assertNull(StringUtils.lowercaseFirstLetter(null));

        // Test single character
        Assert.assertEquals("x", StringUtils.lowercaseFirstLetter("X"));

        // Test already lowercase
        Assert.assertEquals("already", StringUtils.lowercaseFirstLetter("already"));
    }

    @Test
    public void testZeros() {
        Assert.assertEquals("000", StringUtils.zeros(3));
        Assert.assertEquals("0000000000", StringUtils.zeros(10));
        Assert.assertEquals("", StringUtils.zeros(0));
        Assert.assertEquals("0", StringUtils.zeros(1));
    }

    @Test
    public void testRepeat() {
        Assert.assertEquals("aaa", StringUtils.repeat('a', 3));
        Assert.assertEquals("xxxxx", StringUtils.repeat('x', 5));
        Assert.assertEquals("", StringUtils.repeat('z', 0));
        Assert.assertEquals("1", StringUtils.repeat('1', 1));
        Assert.assertEquals("##########", StringUtils.repeat('#', 10));
    }

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(StringUtils.isEmpty(null));
        Assert.assertTrue(StringUtils.isEmpty(""));
        Assert.assertFalse(StringUtils.isEmpty("test"));
        Assert.assertFalse(StringUtils.isEmpty(" "));
        Assert.assertFalse(StringUtils.isEmpty("a"));
    }

    @Test
    public void testFromUTF8ByteArray() {
        // Test simple ASCII
        byte[] asciiBytes = "Hello".getBytes();
        String result = StringUtils.fromUTF8ByteArray(asciiBytes);
        Assert.assertNotNull(result);

        // Test empty array
        byte[] empty = new byte[0];
        String resultEmpty = StringUtils.fromUTF8ByteArray(empty);
        Assert.assertEquals("", resultEmpty);

        // Test single byte
        byte[] single = new byte[]{0x41}; // 'A'
        String resultSingle = StringUtils.fromUTF8ByteArray(single);
        Assert.assertNotNull(resultSingle);
    }

    @Test
    public void testToUTF8ByteArray() {
        // Test simple ASCII
        String simple = "Hello";
        byte[] result = StringUtils.toUTF8ByteArray(simple);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.length > 0);

        // Test empty string
        String empty = "";
        byte[] resultEmpty = StringUtils.toUTF8ByteArray(empty);
        Assert.assertNotNull(resultEmpty);
        Assert.assertEquals(0, resultEmpty.length);

        // Test with special characters
        String special = "Test123";
        byte[] resultSpecial = StringUtils.toUTF8ByteArray(special);
        Assert.assertNotNull(resultSpecial);
    }
}

