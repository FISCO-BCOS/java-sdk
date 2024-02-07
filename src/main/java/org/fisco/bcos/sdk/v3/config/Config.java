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

package org.fisco.bcos.sdk.v3.config;

import com.moandjiezana.toml.Toml;
import java.io.File;
import org.fisco.bcos.sdk.v3.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.v3.config.model.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Config is to load config file and verify.
 *
 * @author Maggie
 */
public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
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
            return new ConfigOption(configProperty);
        } catch (Exception e) {
            logger.error(
                    "parse Config {} failed, error info: {}", tomlConfigFile, e.getMessage(), e);
            throw new ConfigException(
                    "parse Config " + tomlConfigFile + " failed, please check the config file.");
        }
    }
}
