package org.fisco.bcos.sdk.v3.test.utils;

import org.fisco.bcos.sdk.v3.utils.Collection;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CollectionTest {

    @Test
    public void testTail() {
        String[] args = new String[]{"first", "second", "third"};
        String[] result = Collection.tail(args);

        Assert.assertEquals(2, result.length);
        Assert.assertEquals("second", result[0]);
        Assert.assertEquals("third", result[1]);
    }

    @Test
    public void testTailWithSingleElement() {
        String[] args = new String[]{"only"};
        String[] result = Collection.tail(args);

        Assert.assertEquals(0, result.length);
    }

    @Test
    public void testTailWithEmptyArray() {
        String[] args = new String[]{};
        String[] result = Collection.tail(args);

        Assert.assertEquals(0, result.length);
    }

    @Test
    public void testCreate() {
        String[] result = Collection.create("one", "two", "three");

        Assert.assertEquals(3, result.length);
        Assert.assertEquals("one", result[0]);
        Assert.assertEquals("two", result[1]);
        Assert.assertEquals("three", result[2]);
    }

    @Test
    public void testCreateEmpty() {
        String[] result = Collection.create();
        Assert.assertEquals(0, result.length);
    }

    @Test
    public void testCreateWithDifferentTypes() {
        Integer[] result = Collection.create(1, 2, 3);
        Assert.assertEquals(3, result.length);
        Assert.assertEquals(Integer.valueOf(1), result[0]);
    }

    @Test
    public void testJoinWithFunction() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        String result = Collection.join(list, ", ", num -> "num" + num);

        Assert.assertEquals("num1, num2, num3", result);
    }

    @Test
    public void testJoinWithFunctionSingleElement() {
        List<Integer> list = Collections.singletonList(1);
        String result = Collection.join(list, ", ", num -> "value" + num);

        Assert.assertEquals("value1", result);
    }

    @Test
    public void testJoinWithFunctionEmpty() {
        List<Integer> list = Collections.emptyList();
        String result = Collection.join(list, ", ", num -> "value" + num);

        Assert.assertEquals("", result);
    }

    @Test
    public void testJoinStrings() {
        List<String> list = Arrays.asList("apple", "banana", "cherry");
        String result = Collection.join(list, ", ");

        Assert.assertEquals("apple, banana, cherry", result);
    }

    @Test
    public void testJoinStringsSingleElement() {
        List<String> list = Collections.singletonList("single");
        String result = Collection.join(list, "-");

        Assert.assertEquals("single", result);
    }

    @Test
    public void testJoinStringsEmpty() {
        List<String> list = Collections.emptyList();
        String result = Collection.join(list, ", ");

        Assert.assertEquals("", result);
    }

    @Test
    public void testJoinWithTrimming() {
        List<String> list = Arrays.asList("  apple  ", " banana ", "cherry  ");
        String result = Collection.join(list, ",");

        Assert.assertEquals("apple,banana,cherry", result);
    }

    @Test
    public void testJoinWithDifferentSeparators() {
        List<String> list = Arrays.asList("one", "two", "three");

        Assert.assertEquals("one-two-three", Collection.join(list, "-"));
        Assert.assertEquals("one|two|three", Collection.join(list, "|"));
        Assert.assertEquals("onetwothree", Collection.join(list, ""));
    }
}

