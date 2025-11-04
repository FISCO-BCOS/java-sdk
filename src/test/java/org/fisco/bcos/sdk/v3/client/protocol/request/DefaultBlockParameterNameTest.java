package org.fisco.bcos.sdk.v3.client.protocol.request;

import org.junit.Assert;
import org.junit.Test;

public class DefaultBlockParameterNameTest {

    @Test
    public void testEarliestValue() {
        Assert.assertEquals("earliest", DefaultBlockParameterName.EARLIEST.getValue());
    }

    @Test
    public void testLatestValue() {
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
    public void testFromStringWithEarliest() {
        DefaultBlockParameterName result = DefaultBlockParameterName.fromString("earliest");
        Assert.assertEquals(DefaultBlockParameterName.EARLIEST, result);
    }

    @Test
    public void testFromStringWithLatest() {
        DefaultBlockParameterName result = DefaultBlockParameterName.fromString("latest");
        Assert.assertEquals(DefaultBlockParameterName.LATEST, result);
    }

    @Test
    public void testFromStringCaseInsensitive() {
        DefaultBlockParameterName result1 = DefaultBlockParameterName.fromString("EARLIEST");
        Assert.assertEquals(DefaultBlockParameterName.EARLIEST, result1);
        
        DefaultBlockParameterName result2 = DefaultBlockParameterName.fromString("Latest");
        Assert.assertEquals(DefaultBlockParameterName.LATEST, result2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromStringWithInvalidValue() {
        DefaultBlockParameterName.fromString("invalid");
    }

    @Test(expected = NullPointerException.class)
    public void testFromStringWithNull() {
        DefaultBlockParameterName.fromString(null);
    }
}
