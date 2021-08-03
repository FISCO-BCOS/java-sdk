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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.config.model.*;

/**
 * ConfigOption is the java object of the config file.
 *
 * @author Maggie
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigOption {
    private CryptoMaterialConfig cryptoMaterialConfig;
    private AccountConfig accountConfig;
    private AmopConfig amopConfig;
    private NetworkConfig networkConfig;
    private ThreadPoolConfig threadPoolConfig;
    private ConfigProperty configProperty;

    public ConfigOption(ConfigProperty configProperty) throws ConfigException {
        // load cryptoMaterialConfig
        this.cryptoMaterialConfig = new CryptoMaterialConfig(configProperty);
        // load accountConfig
        this.accountConfig = new AccountConfig(configProperty);
        // load AmopConfig
        this.amopConfig = new AmopConfig(configProperty);
        // load networkConfig
        this.networkConfig = new NetworkConfig(configProperty);
        // load threadPoolConfig
        this.threadPoolConfig = new ThreadPoolConfig(configProperty);
        // init configProperty
        this.configProperty = configProperty;
    }

    public void reloadConfig(int cryptoType) throws ConfigException {
        this.cryptoMaterialConfig = new CryptoMaterialConfig(this.configProperty);
    }

    public CryptoMaterialConfig getCryptoMaterialConfig() {
        return this.cryptoMaterialConfig;
    }

    public void setCryptoMaterialConfig(CryptoMaterialConfig cryptoMaterialConfig) {
        this.cryptoMaterialConfig = cryptoMaterialConfig;
    }

    public AccountConfig getAccountConfig() {
        return this.accountConfig;
    }

    public void setAccountConfig(AccountConfig accountConfig) {
        this.accountConfig = accountConfig;
    }

    public AmopConfig getAmopConfig() {
        return this.amopConfig;
    }

    public void setAmopConfig(AmopConfig amopConfig) {
        this.amopConfig = amopConfig;
    }

    public NetworkConfig getNetworkConfig() {
        return this.networkConfig;
    }

    public void setNetworkConfig(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
    }

    public ThreadPoolConfig getThreadPoolConfig() {
        return this.threadPoolConfig;
    }

    public void setThreadPoolConfig(ThreadPoolConfig threadPoolConfig) {
        this.threadPoolConfig = threadPoolConfig;
    }
}
