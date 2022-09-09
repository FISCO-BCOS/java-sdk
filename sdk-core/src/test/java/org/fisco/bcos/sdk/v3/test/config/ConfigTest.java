/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package org.fisco.bcos.sdk.v3.test.config;

import org.fisco.bcos.sdk.v3.config.Config;
import org.fisco.bcos.sdk.v3.config.ConfigOption;
import org.fisco.bcos.sdk.v3.config.exceptions.ConfigException;
import org.junit.Assert;
import org.junit.Test;

public class ConfigTest {
    @Test
    public void testLoadRightConfig() throws ConfigException {
        ConfigOption configOption =
                Config.load(
                        "src/test/resources/config/config-example.toml");
        Assert.assertFalse(configOption.getCryptoMaterialConfig().getUseSmCrypto());
        Assert.assertNotNull(configOption.getAccountConfig());
        Assert.assertEquals("group0", configOption.getNetworkConfig().getDefaultGroup());
        Assert.assertEquals(2, configOption.getNetworkConfig().getPeers().size());

        configOption.reloadConfig();
        Assert.assertFalse(configOption.getCryptoMaterialConfig().getUseSmCrypto());
        Assert.assertNotNull(configOption.getAccountConfig());
        Assert.assertEquals("group0", configOption.getNetworkConfig().getDefaultGroup());
        Assert.assertEquals(2, configOption.getNetworkConfig().getPeers().size());
    }

    @Test
    public void testSMConfig() throws ConfigException {
        ConfigOption configOption =
                Config.load(
                        "src/test/resources/config/config-example-gm.toml");
        Assert.assertTrue(configOption.getCryptoMaterialConfig().getUseSmCrypto());
        Assert.assertNotNull(configOption.getAccountConfig());
        Assert.assertEquals("group0", configOption.getNetworkConfig().getDefaultGroup());
        Assert.assertEquals(2, configOption.getNetworkConfig().getPeers().size());
    }
}
