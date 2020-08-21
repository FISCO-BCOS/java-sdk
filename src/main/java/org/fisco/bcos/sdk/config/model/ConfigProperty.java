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

package org.fisco.bcos.sdk.config.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * ConfigOption is the java object of the config file.
 *
 * @author Maggie
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigProperty {

    public Map<String, String> cryptoMaterial;
    public List<String> peers;

    @JsonProperty("AMOPKeys")
    public List<AmopTopic> amopConfig;

    @JsonProperty("Account")
    public Map<String, String> accountConfig;

    @JsonProperty("threadPool")
    public Map<String, String> threadPoolConfig;

    public Map<String, String> getCryptoMaterial() {
        return cryptoMaterial;
    }

    public void setCryptoMaterial(Map<String, String> cryptoMaterial) {
        this.cryptoMaterial = cryptoMaterial;
    }

    public List<String> getPeers() {
        return peers;
    }

    public void setPeers(List<String> peers) {
        this.peers = peers;
    }

    public List<AmopTopic> getAmopConfig() {
        return amopConfig;
    }

    public void setAmopConfig(List<AmopTopic> amopConfig) {
        this.amopConfig = amopConfig;
    }

    public Map<String, String> getAccountConfig() {
        return accountConfig;
    }

    public void setAccountConfig(Map<String, String> accountConfig) {
        this.accountConfig = accountConfig;
    }

    public Map<String, String> getThreadPoolConfig() {
        return threadPoolConfig;
    }

    public void setThreadPoolConfig(Map<String, String> threadPoolConfig) {
        this.threadPoolConfig = threadPoolConfig;
    }

    public static String getValue(Map<String, String> config, String key, String defaultValue) {
        if (config == null || config.get(key) == null) {
            return defaultValue;
        }
        return config.get(key);
    }
}
