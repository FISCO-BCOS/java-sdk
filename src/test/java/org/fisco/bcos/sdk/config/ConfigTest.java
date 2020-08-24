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
import org.junit.Test;

public class ConfigTest {
    @Test(expected = ConfigException.class)
    public void testLoadConfig() throws ConfigException {
        Config.load("src/test/resources/config/config-bad.yaml", CryptoInterface.ECDSA_TYPE);
    }

    @Test
    public void testLoadRightConfig() {
        try {
            Config.load("src/test/resources/config-example.yaml", CryptoInterface.ECDSA_TYPE);
            // assertEquals("ecdsa", config.getAlgorithm());
        } catch (ConfigException e) {
            System.out.println("testLoadRightConfig failed, error message: " + e.getMessage());
            fail("No exception is needed.");
        }
    }
}
