package org.fisco.bcos.sdk.v3.test.codec.datatypes;

import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.junit.Assert;
import org.junit.Test;

public class BoolTest {

    @Test
    public void testBoolWithPrimitiveTrue() {
        Bool bool = new Bool(true);
        Assert.assertTrue(bool.getValue());
    }

    @Test
    public void testBoolWithPrimitiveFalse() {
        Bool bool = new Bool(false);
        Assert.assertFalse(bool.getValue());
    }

    @Test
    public void testBoolWithBooleanTrue() {
        Bool bool = new Bool(Boolean.TRUE);
        Assert.assertTrue(bool.getValue());
    }

    @Test
    public void testBoolWithBooleanFalse() {
        Bool bool = new Bool(Boolean.FALSE);
        Assert.assertFalse(bool.getValue());
    }

    @Test
    public void testGetTypeAsString() {
        Bool bool = new Bool(true);
        Assert.assertEquals("bool", bool.getTypeAsString());
    }

    @Test
    public void testEquals() {
        Bool bool1 = new Bool(true);
        Bool bool2 = new Bool(true);
        Bool bool3 = new Bool(false);

        Assert.assertEquals(bool1, bool2);
        Assert.assertNotEquals(bool1, bool3);
        Assert.assertEquals(bool1, bool1);
        Assert.assertNotEquals(bool1, null);
        Assert.assertNotEquals(bool1, "String");
    }

    @Test
    public void testHashCode() {
        Bool boolTrue1 = new Bool(true);
        Bool boolTrue2 = new Bool(true);
        Bool boolFalse = new Bool(false);

        Assert.assertEquals(boolTrue1.hashCode(), boolTrue2.hashCode());
        Assert.assertNotEquals(boolTrue1.hashCode(), boolFalse.hashCode());
        Assert.assertEquals(1, boolTrue1.hashCode());
        Assert.assertEquals(0, boolFalse.hashCode());
    }

    @Test
    public void testDefaultBool() {
        Assert.assertNotNull(Bool.DEFAULT);
        Assert.assertFalse(Bool.DEFAULT.getValue());
    }

    @Test
    public void testTypeName() {
        Assert.assertEquals("bool", Bool.TYPE_NAME);
    }
}

