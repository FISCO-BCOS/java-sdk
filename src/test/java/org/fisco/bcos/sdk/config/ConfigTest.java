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

package org.fisco.bcos.sdk.config;

import static org.junit.Assert.fail;

import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.junit.Assert;
import org.junit.Test;

public class ConfigTest {
    @Test
    public void testLoadRightConfig() {
        try {
            ConfigOption configOption =
                    Config.load(
                            "src/test/resources/" + ConstantConfig.CONFIG_FILE_NAME,
                            CryptoInterface.ECDSA_TYPE);
            Assert.assertTrue(configOption.getAccountConfig() != null);
            System.out.println(
                    "configOption.getAccountConfig: "
                            + configOption.getAccountConfig().getKeyStoreDir());
            // assertEquals("ecdsa", config.getAlgorithm());
        } catch (ConfigException e) {
            System.out.println("testLoadRightConfig failed, error message: " + e.getMessage());
            fail("No exception is needed.");
        }
    }
}
