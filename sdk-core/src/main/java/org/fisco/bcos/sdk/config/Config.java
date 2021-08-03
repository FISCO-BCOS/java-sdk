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

import com.moandjiezana.toml.Toml;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.config.model.ConfigProperty;

import java.io.File;

/**
 * Config is to load config file and verify.
 *
 * @author Maggie
 */
public class Config {
    /**
     * @param tomlConfigFile the toml configuration file path
     * @return ConfigOption the configuration object
     * @throws ConfigException the configuration exception
     */
    public static ConfigOption load(String tomlConfigFile) throws ConfigException {
        // Load a toml config file to an java object
        File configFile = new File(tomlConfigFile);
        try {
            ConfigProperty configProperty = new Toml().read(configFile).to(ConfigProperty.class);
            ConfigOption configOption = new ConfigOption(configProperty);
            return configOption;
        } catch (Exception e) {
            throw new ConfigException(
                    "parse Config " + tomlConfigFile + " failed, error info: " + e.getMessage(), e);
        }
    }
}
