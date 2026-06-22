package org.fisco.bcos.sdk.v3.test.model;

import org.fisco.bcos.sdk.v3.model.ConstantConfig;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ConstantConfigTest {

    @Test
    public void testConfigFileName() {
        Assert.assertEquals("config.toml", ConstantConfig.CONFIG_FILE_NAME);
    }

    @Test
    public void testDefaultCharset() {
        Assert.assertEquals(StandardCharsets.UTF_8, ConstantConfig.DEFAULT_CHARSET);
    }

    @Test
    public void testConfigFileNameNotNull() {
        Assert.assertNotNull(ConstantConfig.CONFIG_FILE_NAME);
    }

    @Test
    public void testConfigFileNameNotEmpty() {
        Assert.assertFalse(ConstantConfig.CONFIG_FILE_NAME.isEmpty());
    }

    @Test
    public void testDefaultCharsetNotNull() {
        Assert.assertNotNull(ConstantConfig.DEFAULT_CHARSET);
    }

    @Test
    public void testDefaultCharsetIsUtf8() {
        Charset utf8 = StandardCharsets.UTF_8;
        Assert.assertEquals(utf8, ConstantConfig.DEFAULT_CHARSET);
        Assert.assertEquals("UTF-8", ConstantConfig.DEFAULT_CHARSET.name());
    }
}
