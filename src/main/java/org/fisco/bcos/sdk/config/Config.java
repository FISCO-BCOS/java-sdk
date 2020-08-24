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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.config.model.ConfigProperty;

/**
 * Config is to load config file and verify.
 *
 * @author Maggie
 */
public class Config {
    /**
     * @param yamlConfigFile
     * @return ConfigOption
     * @throws IOException
     */
    public static ConfigOption load(String yamlConfigFile, int cryptoType) throws ConfigException {
        // Load a yaml config file to an java object
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        File configFile = new File(yamlConfigFile);
        try {
            ConfigProperty configProperty = mapper.readValue(configFile, ConfigProperty.class);
            ConfigOption configOption = new ConfigOption(configProperty, cryptoType);
            return configOption;
        } catch (IOException e) {
            throw new ConfigException(
                    "parse Config " + yamlConfigFile + " failed, error info: " + e.getMessage(), e);
        }
    }
}
