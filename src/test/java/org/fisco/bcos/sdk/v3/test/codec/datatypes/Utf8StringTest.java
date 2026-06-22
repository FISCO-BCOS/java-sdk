package org.fisco.bcos.sdk.v3.test.codec.datatypes;

import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.junit.Assert;
import org.junit.Test;

public class Utf8StringTest {

    @Test
    public void testUtf8StringConstructor() {
        Utf8String str = new Utf8String("Hello World");
        Assert.assertEquals("Hello World", str.getValue());
    }

    @Test
    public void testGetValue() {
        Utf8String str = new Utf8String("Test String");
        Assert.assertEquals("Test String", str.getValue());
    }

    @Test
    public void testGetTypeAsString() {
        Utf8String str = new Utf8String("test");
        Assert.assertEquals("string", str.getTypeAsString());
    }

    @Test
    public void testToString() {
        Utf8String str = new Utf8String("Test");
        Assert.assertEquals("Test", str.toString());
    }

    @Test
    public void testEquals() {
        Utf8String str1 = new Utf8String("Hello");
        Utf8String str2 = new Utf8String("Hello");
        Utf8String str3 = new Utf8String("World");

        Assert.assertEquals(str1, str2);
        Assert.assertNotEquals(str1, str3);
        Assert.assertEquals(str1, str1);
        Assert.assertNotEquals(str1, null);
        Assert.assertNotEquals(str1, "Hello");
    }

    @Test
    public void testHashCode() {
        Utf8String str1 = new Utf8String("Test");
        Utf8String str2 = new Utf8String("Test");

        Assert.assertEquals(str1.hashCode(), str2.hashCode());
    }

    @Test
    public void testEmptyString() {
        Utf8String str = new Utf8String("");
        Assert.assertEquals("", str.getValue());
        Assert.assertEquals("", str.toString());
    }

    @Test
    public void testDefaultUtf8String() {
        Assert.assertNotNull(Utf8String.DEFAULT);
        Assert.assertEquals("", Utf8String.DEFAULT.getValue());
    }

    @Test
    public void testTypeName() {
        Assert.assertEquals("string", Utf8String.TYPE_NAME);
    }

    @Test
    public void testBytes32PaddedLengthEmpty() {
        Utf8String emptyStr = new Utf8String("");
        int length = emptyStr.bytes32PaddedLength();
        Assert.assertEquals(32, length);
    }

    @Test
    public void testBytes32PaddedLengthNonEmpty() {
        Utf8String str = new Utf8String("test");
        int length = str.bytes32PaddedLength();
        Assert.assertEquals(64, length);
    }

    @Test
    public void testWithSpecialCharacters() {
        Utf8String str = new Utf8String("Hello\nWorld\t!");
        Assert.assertEquals("Hello\nWorld\t!", str.getValue());
    }

    @Test
    public void testWithUnicodeCharacters() {
        Utf8String str = new Utf8String("你好世界");
        Assert.assertEquals("你好世界", str.getValue());
    }

    @Test
    public void testEqualsWithNull() {
        Utf8String str1 = new Utf8String("test");
        Utf8String str2 = new Utf8String(null);
        Utf8String str3 = new Utf8String(null);

        Assert.assertNotEquals(str1, str2);
        Assert.assertEquals(str2, str3);
    }
}

