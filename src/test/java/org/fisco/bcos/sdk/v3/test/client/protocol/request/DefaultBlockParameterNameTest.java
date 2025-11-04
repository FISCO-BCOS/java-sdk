package org.fisco.bcos.sdk.v3.test.client.protocol.request;

import org.fisco.bcos.sdk.v3.client.protocol.request.DefaultBlockParameterName;
import org.junit.Assert;
import org.junit.Test;

public class DefaultBlockParameterNameTest {

    @Test
    public void testGetValue() {
        Assert.assertEquals("earliest", DefaultBlockParameterName.EARLIEST.getValue());
        Assert.assertEquals("latest", DefaultBlockParameterName.LATEST.getValue());
    }

    @Test
    public void testIsLatest() {
        Assert.assertTrue(DefaultBlockParameterName.LATEST.isLatest());
        Assert.assertFalse(DefaultBlockParameterName.EARLIEST.isLatest());
    }

    @Test
    public void testIsEarliest() {
        Assert.assertTrue(DefaultBlockParameterName.EARLIEST.isEarliest());
        Assert.assertFalse(DefaultBlockParameterName.LATEST.isEarliest());
    }

    @Test
    public void testFromString() {
        Assert.assertEquals(DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.fromString("earliest"));
        Assert.assertEquals(DefaultBlockParameterName.LATEST,
            DefaultBlockParameterName.fromString("latest"));

        // Test case insensitive
        Assert.assertEquals(DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.fromString("EARLIEST"));
        Assert.assertEquals(DefaultBlockParameterName.LATEST,
            DefaultBlockParameterName.fromString("LATEST"));
        Assert.assertEquals(DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.fromString("EaRlIeSt"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromStringInvalid() {
        DefaultBlockParameterName.fromString("invalid");
    }

    @Test
    public void testEnumValues() {
        DefaultBlockParameterName[] values = DefaultBlockParameterName.values();
        Assert.assertEquals(2, values.length);
        Assert.assertEquals(DefaultBlockParameterName.EARLIEST, values[0]);
        Assert.assertEquals(DefaultBlockParameterName.LATEST, values[1]);
    }

    @Test
    public void testValueOf() {
        Assert.assertEquals(DefaultBlockParameterName.EARLIEST,
            DefaultBlockParameterName.valueOf("EARLIEST"));
        Assert.assertEquals(DefaultBlockParameterName.LATEST,
            DefaultBlockParameterName.valueOf("LATEST"));
    }
}

