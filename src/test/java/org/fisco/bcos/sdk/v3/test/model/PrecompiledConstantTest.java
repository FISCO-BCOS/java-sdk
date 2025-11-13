package org.fisco.bcos.sdk.v3.test.model;

import org.fisco.bcos.sdk.v3.model.PrecompiledConstant;
import org.junit.Assert;
import org.junit.Test;

public class PrecompiledConstantTest {

    @Test
    public void testTableKeyMaxLength() {
        Assert.assertEquals(255, PrecompiledConstant.TABLE_KEY_MAX_LENGTH);
    }

    @Test
    public void testTableFieldNameMaxLength() {
        Assert.assertEquals(64, PrecompiledConstant.TABLE_FIELD_NAME_MAX_LENGTH);
    }

    @Test
    public void testUserTableNameMaxLength() {
        Assert.assertEquals(48, PrecompiledConstant.USER_TABLE_NAME_MAX_LENGTH);
    }

    @Test
    public void testTableValueFieldMaxLength() {
        Assert.assertEquals(1024, PrecompiledConstant.TABLE_VALUE_FIELD_MAX_LENGTH);
    }

    @Test
    public void testTableKeyValueMaxLength() {
        Assert.assertEquals(255, PrecompiledConstant.TABLE_KEY_VALUE_MAX_LENGTH);
    }

    @Test
    public void testUserTableFieldValueMaxLength() {
        Assert.assertEquals(16 * 1024 * 1024 - 1, PrecompiledConstant.USER_TABLE_FIELD_VALUE_MAX_LENGTH);
    }

    @Test
    public void testSyncKeepUpThreshold() {
        Assert.assertEquals(10, PrecompiledConstant.SYNC_KEEP_UP_THRESHOLD);
    }

    @Test
    public void testKeyOrderConstant() {
        Assert.assertEquals("key_order", PrecompiledConstant.KEY_ORDER);
    }

    @Test
    public void testKeyFieldNameConstant() {
        Assert.assertEquals("key_field", PrecompiledConstant.KEY_FIELD_NAME);
    }

    @Test
    public void testValueFieldNameConstant() {
        Assert.assertEquals("value_field", PrecompiledConstant.VALUE_FIELD_NAME);
    }

    @Test
    public void testConstantsAreConsistent() {
        // Ensure KEY_MAX_LENGTH and KEY_VALUE_MAX_LENGTH have the same value
        Assert.assertEquals(PrecompiledConstant.TABLE_KEY_MAX_LENGTH, 
                          PrecompiledConstant.TABLE_KEY_VALUE_MAX_LENGTH);
    }

    @Test
    public void testStringConstantsNotNull() {
        Assert.assertNotNull(PrecompiledConstant.KEY_ORDER);
        Assert.assertNotNull(PrecompiledConstant.KEY_FIELD_NAME);
        Assert.assertNotNull(PrecompiledConstant.VALUE_FIELD_NAME);
    }

    @Test
    public void testStringConstantsNotEmpty() {
        Assert.assertFalse(PrecompiledConstant.KEY_ORDER.isEmpty());
        Assert.assertFalse(PrecompiledConstant.KEY_FIELD_NAME.isEmpty());
        Assert.assertFalse(PrecompiledConstant.VALUE_FIELD_NAME.isEmpty());
    }
}
