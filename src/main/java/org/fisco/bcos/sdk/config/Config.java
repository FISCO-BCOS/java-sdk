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
    static ConfigOption load(String yamlConfigFile) throws ConfigException {
        // Load a yaml config file to an java object
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        File configFile = new File(yamlConfigFile);

        try {
            ConfigOption option = mapper.readValue(configFile, ConfigOption.class);
            checkValid(option);
            return option;
        } catch (IOException e) {
            throw new ConfigException(e);
        }
    }

    /**
     * Check whether configure is right
     *
     * @param confOpts
     * @throws ConfigException
     */
    static void checkValid(ConfigOption confOpts) throws ConfigException {

        if (null == confOpts.getCryptoMateral()) {
            throw new ConfigException(
                    "Crypto material not configured, please config cryptoMaterial in yaml config file.");
        }

        if (null == confOpts.getCaCert()) {
            throw new ConfigException(
                    "Ca certificate not configured, please config caCert in yaml config file.");
        }

        File caCertFile = new File(confOpts.getCaCert());
        if (!caCertFile.exists()) {
            throw new ConfigException(confOpts.getCaCert() + " file not exist");
        }

        if (null == confOpts.getSslCert()) {
            throw new ConfigException(
                    "SSL certificate not configured, please config sslCert in yaml config file.");
        }

        File sslCertFile = new File(confOpts.getSslCert());
        if (!sslCertFile.exists()) {
            throw new ConfigException(confOpts.getSslCert() + " file not exist");
        }

        if (null == confOpts.getSslKey()) {
            throw new ConfigException(
                    "SSL key not configured, please config sslKey in yaml config file.");
        }

        File sslKeyFile = new File(confOpts.getSslKey());
        if (!sslKeyFile.exists()) {
            throw new ConfigException(confOpts.getSslKey() + " file not exist");
        }

        if (null == confOpts.getPeers()) {
            throw new ConfigException(
                    "Peers not configured, please config peers in yaml config file.");
        }

        if (!confOpts.getAlgorithm().equals("guomi")) {
            return;
        }

        // Special config check of guomi algorithm set.
        if (null == confOpts.getEnSslCert()) {
            throw new ConfigException(
                    "Encrypt ssl certificate not configured. You algorithm is guomi, enSslCert is required, please config enSslCert in yaml config file.");
        }

        File enSslCertFile = new File(confOpts.getEnSslCert());
        if (!enSslCertFile.exists()) {
            throw new ConfigException(confOpts.getEnSslCert() + " file not exist");
        }

        if (null == confOpts.getEnSslKey()) {
            throw new ConfigException(
                    "Encrypt ssl key not configured. You algorithm is guomi, enSslKey is required, please config enSslKey in yaml config file.");
        }

        File enSslKeyFile = new File(confOpts.getEnSslKey());
        if (!enSslKeyFile.exists()) {
            throw new ConfigException(confOpts.getEnSslKey() + " file not exist");
        }
    }
}
