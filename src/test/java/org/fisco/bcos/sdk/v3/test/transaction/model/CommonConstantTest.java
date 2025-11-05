package org.fisco.bcos.sdk.v3.test.transaction.model;

import org.fisco.bcos.sdk.v3.transaction.model.CommonConstant;
import org.junit.Assert;
import org.junit.Test;

public class CommonConstantTest {

    @Test
    public void testBINConstant() {
        Assert.assertEquals("binary", CommonConstant.BIN);
    }

    @Test
    public void testABIConstant() {
        Assert.assertEquals("abi", CommonConstant.ABI);
    }

    @Test
    public void testABIConstructorConstant() {
        Assert.assertEquals("constructor", CommonConstant.ABI_CONSTRUCTOR);
    }

    @Test
    public void testABIFunctionConstant() {
        Assert.assertEquals("function", CommonConstant.ABI_FUNCTION);
    }

    @Test
    public void testAllConstantsAreUnique() {
        Assert.assertNotEquals(CommonConstant.BIN, CommonConstant.ABI);
        Assert.assertNotEquals(CommonConstant.ABI_CONSTRUCTOR, CommonConstant.ABI_FUNCTION);
        Assert.assertNotEquals(CommonConstant.BIN, CommonConstant.ABI_CONSTRUCTOR);
        Assert.assertNotEquals(CommonConstant.ABI, CommonConstant.ABI_FUNCTION);
    }
}
