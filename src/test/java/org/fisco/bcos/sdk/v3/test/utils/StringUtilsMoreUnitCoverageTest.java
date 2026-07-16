/**
 * Copyright 2014-2020 [fisco-dev]
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.sdk.v3.test.utils;

import java.util.Arrays;
import java.util.List;
import org.fisco.bcos.sdk.v3.utils.StringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Additional pure-Java coverage tests for {@link StringUtils} branches not exercised by the existing
 * StringUtilsTest: split, locale-independent case conversion, byte/char array conversions, joinAll,
 * and UTF-8 round-tripping (including multi-byte code points).
 */
public class StringUtilsMoreUnitCoverageTest {

    @Test
    public void testSplit() {
        String[] parts = StringUtils.split("a,b,c", ',');
        Assert.assertEquals(3, parts.length);
        Assert.assertEquals("a", parts[0]);
        Assert.assertEquals("b", parts[1]);
        Assert.assertEquals("c", parts[2]);

        // no delimiter -> single element
        String[] none = StringUtils.split("abc", ',');
        Assert.assertEquals(1, none.length);
        Assert.assertEquals("abc", none[0]);

        // trailing delimiter handling: a leading delimiter (indexOf == 0) terminates the loop
        String[] leading = StringUtils.split(",abc", ',');
        Assert.assertEquals(1, leading.length);
        Assert.assertEquals(",abc", leading[0]);
    }

    @Test
    public void testToUpperCase() {
        Assert.assertEquals("HELLO", StringUtils.toUpperCase("hello"));
        Assert.assertEquals("HELLO123", StringUtils.toUpperCase("hello123"));
        // already uppercase -> same instance returned (no change branch)
        String already = "ALREADY";
        Assert.assertSame(already, StringUtils.toUpperCase(already));
        Assert.assertEquals("MIXEDcase".toUpperCase(), StringUtils.toUpperCase("MIXEDcase"));
    }

    @Test
    public void testToLowerCase() {
        Assert.assertEquals("hello", StringUtils.toLowerCase("HELLO"));
        Assert.assertEquals("hello123", StringUtils.toLowerCase("HELLO123"));
        // already lowercase -> same instance returned (no change branch)
        String already = "already";
        Assert.assertSame(already, StringUtils.toLowerCase(already));
        Assert.assertEquals("mixedcase", StringUtils.toLowerCase("mixedCASE"));
    }

    @Test
    public void testToByteArrayFromChars() {
        char[] chars = {'A', 'B', 'C'};
        byte[] bytes = StringUtils.toByteArray(chars);
        Assert.assertArrayEquals(new byte[] {65, 66, 67}, bytes);
    }

    @Test
    public void testToByteArrayFromString() {
        byte[] bytes = StringUtils.toByteArray("ABC");
        Assert.assertArrayEquals(new byte[] {65, 66, 67}, bytes);
        Assert.assertEquals(0, StringUtils.toByteArray("").length);
    }

    @Test
    public void testToByteArrayWithOffset() {
        byte[] buf = new byte[5];
        int count = StringUtils.toByteArray("AB", buf, 1);
        Assert.assertEquals(2, count);
        Assert.assertEquals(0, buf[0]);
        Assert.assertEquals(65, buf[1]);
        Assert.assertEquals(66, buf[2]);
    }

    @Test
    public void testFromByteArrayAndAsCharArray() {
        byte[] bytes = {65, 66, 67};
        Assert.assertEquals("ABC", StringUtils.fromByteArray(bytes));

        char[] chars = StringUtils.asCharArray(bytes);
        Assert.assertEquals(3, chars.length);
        Assert.assertEquals('A', chars[0]);
        Assert.assertEquals('C', chars[2]);

        // high-bit bytes are masked with 0xff
        byte[] high = {(byte) 0x80};
        char[] highChars = StringUtils.asCharArray(high);
        Assert.assertEquals(0x80, highChars[0]);
    }

    @Test
    public void testJoinAllArray() {
        String[] src = {"a", "b", "c"};
        Assert.assertEquals("a-b-c", StringUtils.joinAll("-", src));
        Assert.assertNull(StringUtils.joinAll("-", (String[]) null));
    }

    @Test
    public void testJoinAllList() {
        List<String> src = Arrays.asList("x", "y", "z");
        Assert.assertEquals("x|y|z", StringUtils.joinAll("|", src));
        Assert.assertNull(StringUtils.joinAll("|", (List<String>) null));
    }

    @Test
    public void testUtf8RoundTripAscii() {
        String original = "Hello, World!";
        byte[] bytes = StringUtils.toUTF8ByteArray(original);
        Assert.assertEquals(original, StringUtils.fromUTF8ByteArray(bytes));
    }

    @Test
    public void testUtf8RoundTripTwoByte() {
        // U+00E9 (e with acute) is encoded as two UTF-8 bytes
        String original = "café";
        byte[] bytes = StringUtils.toUTF8ByteArray(original);
        String decoded = StringUtils.fromUTF8ByteArray(bytes);
        Assert.assertEquals(original, decoded);
    }

    @Test
    public void testUtf8RoundTripThreeByte() {
        // CJK char encodes as three UTF-8 bytes
        String original = "中文";
        byte[] bytes = StringUtils.toUTF8ByteArray(original);
        String decoded = StringUtils.fromUTF8ByteArray(bytes);
        Assert.assertEquals(original, decoded);
    }

    @Test
    public void testUtf8RoundTripSurrogatePair() {
        // U+1F600 (emoji) is a surrogate pair / 4-byte UTF-8 sequence
        String original = new String(Character.toChars(0x1F600));
        byte[] bytes = StringUtils.toUTF8ByteArray(original);
        String decoded = StringUtils.fromUTF8ByteArray(bytes);
        Assert.assertEquals(original, decoded);
    }

    @Test
    public void testToUTF8ByteArrayFromChars() {
        char[] chars = "test".toCharArray();
        byte[] bytes = StringUtils.toUTF8ByteArray(chars);
        Assert.assertArrayEquals("test".getBytes(), bytes);
    }
}
