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
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import org.fisco.bcos.sdk.config.exceptions.ConfigException;

/**
 * ConfigOption is the java object of the config file.
 *
 * @author Maggie
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigProperty {
    public Map<String, Object> cryptoMaterial;
    public Map<String, Object> network;
    public List<AmopTopic> amop;
    public Map<String, Object> account;

    public Map<String, Object> threadPool;

    public Map<String, Object> getCryptoMaterial() {
        return cryptoMaterial;
    }

    public void setCryptoMaterial(Map<String, Object> cryptoMaterial) {
        this.cryptoMaterial = cryptoMaterial;
    }

    public Map<String, Object> getNetwork() {
        return network;
    }

    public void setNetwork(Map<String, Object> network) {
        this.network = network;
    }

    public List<AmopTopic> getAmop() {
        return amop;
    }

    public void setAmop(List<AmopTopic> amop) {
        this.amop = amop;
    }

    public Map<String, Object> getAccount() {
        return account;
    }

    public void setAccount(Map<String, Object> account) {
        this.account = account;
    }

    public Map<String, Object> getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(Map<String, Object> threadPool) {
        this.threadPool = threadPool;
    }

    public static String getValue(Map<String, Object> config, String key, String defaultValue) {
        if (config == null || config.get(key) == null) {
            return defaultValue;
        }
        return (String) config.get(key);
    }

    public static String getConfigFilePath(String configFilePath) throws ConfigException {
        try {
            if (configFilePath == null) {
                return null;
            }
            File file = new File(configFilePath);
            if (file.exists()) {
                return configFilePath;
            }
            // try to load from the resource path
            URL url = Thread.currentThread().getContextClassLoader().getResource(configFilePath);
            if (url == null) {
                return configFilePath;
            }
            String resourceCertPath = URLDecoder.decode(url.getPath(), "utf-8");
            if (new File(resourceCertPath).exists()) {
                return resourceCertPath;
            }
            return configFilePath;
        } catch (UnsupportedEncodingException e) {
            throw new ConfigException(e);
        }
    }
}
