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
import io.netty.util.NetUtil;
import java.io.File;
import java.io.IOException;
import org.fisco.bcos.sdk.utils.Host;

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

        if (null == confOpts.getCryptoMaterial()) {
            throw new ConfigException(
                    "Invalid configuration, Crypto material not configured, please config cryptoMaterial in yaml config file.");
        }

        if (null == confOpts.getCaCert()) {
            throw new ConfigException(
                    "Invalid configuration, Ca certificate not configured, please config caCert in yaml config file.");
        }

        File caCertFile = new File(confOpts.getCaCert());
        if (!caCertFile.exists()) {
            throw new ConfigException(
                    "Invalid configuration, " + confOpts.getCaCert() + " file not exist");
        }

        if (null == confOpts.getSslCert()) {
            throw new ConfigException(
                    "Invalid configuration, SSL certificate not configured, please config sslCert in yaml config file.");
        }

        File sslCertFile = new File(confOpts.getSslCert());
        if (!sslCertFile.exists()) {
            throw new ConfigException(
                    "Invalid configuration, " + confOpts.getSslCert() + " file not exist");
        }

        if (null == confOpts.getSslKey()) {
            throw new ConfigException(
                    "Invalid configuration, SSL key not configured, please config sslKey in yaml config file.");
        }

        File sslKeyFile = new File(confOpts.getSslKey());
        if (!sslKeyFile.exists()) {
            throw new ConfigException(
                    "Invalid configuration, " + confOpts.getSslKey() + " file not exist");
        }

        // Check peer configuration
        if (null == confOpts.getPeers()) {
            throw new ConfigException(
                    "Invalid configuration, peers not configured, please config peers in yaml config file.");
        }

        for (String peer : confOpts.getPeers()) {
            int index = peer.lastIndexOf(':');
            if (index == -1) {
                throw new ConfigException(
                        " Invalid configuration, the peer value should in IP:Port format(eg: 127.0.0.1:1111), value: "
                                + peer);
            }
            String IP = peer.substring(0, index);
            String port = peer.substring(index + 1);

            if (!(NetUtil.isValidIpV4Address(IP) || NetUtil.isValidIpV6Address(IP))) {
                throw new ConfigException(
                        " Invalid configuration, invalid IP string format, value: " + IP);
            }

            if (!Host.validPort(port)) {
                throw new ConfigException(
                        " Invalid configuration, tcp port should from 1 to 65535, value: " + port);
            }
        }

        // Check sm material configuration
        if (null == confOpts.getAlgorithm()) {
            return;
        }
        if (!confOpts.getAlgorithm().equals("sm")) {
            if (!confOpts.getAlgorithm().equals("ecdsa")) {
                throw new ConfigException("Invalid configuration, algorithm must be ecdsa or sm.");
            }
            return;
        }

        // Special config check of sm algorithm set.
        if (null == confOpts.getEnSslCert()) {
            throw new ConfigException(
                    "Invalid configuration, encrypt ssl certificate not configured. You algorithm is sm, enSslCert is required, please config enSslCert in yaml config file.");
        }

        File enSslCertFile = new File(confOpts.getEnSslCert());
        if (!enSslCertFile.exists()) {
            throw new ConfigException(
                    "Invalid configuration, " + confOpts.getEnSslCert() + " file not exist");
        }

        if (null == confOpts.getEnSslKey()) {
            throw new ConfigException(
                    "Invalid configuration,encrypt ssl key not configured. You algorithm is sm, enSslKey is required, please config enSslKey in yaml config file.");
        }

        File enSslKeyFile = new File(confOpts.getEnSslKey());
        if (!enSslKeyFile.exists()) {
            throw new ConfigException(
                    "Invalid configuration, " + confOpts.getEnSslKey() + " file not exist");
        }
    }
}
