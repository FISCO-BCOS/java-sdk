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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.fisco.bcos.sdk.jni.common.JniConfig;
import org.fisco.bcos.sdk.v3.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.v3.config.model.AccountConfig;
import org.fisco.bcos.sdk.v3.config.model.AmopConfig;
import org.fisco.bcos.sdk.v3.config.model.ConfigProperty;
import org.fisco.bcos.sdk.v3.config.model.CryptoMaterialConfig;
import org.fisco.bcos.sdk.v3.config.model.NetworkConfig;
import org.fisco.bcos.sdk.v3.config.model.ThreadPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ConfigOption is the java object of the config file.
 *
 * @author Maggie
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigOption {

    private static final Logger logger = LoggerFactory.getLogger(ConfigOption.class);
    /** if disable ssl connection */
    private static boolean DISABLE_SSL = false;

    static {
        String value = System.getProperty("org.fisco.bcos.jni.disableSsl");
        if (value != null) {
            DISABLE_SSL = "true".equals(value);
            logger.info("-Dorg.fisco.bcos.jni.disableSsl is set, value: {}", value);
        }
    }

    private CryptoMaterialConfig cryptoMaterialConfig;
    private AccountConfig accountConfig;
    private AmopConfig amopConfig;
    private NetworkConfig networkConfig;
    private ThreadPoolConfig threadPoolConfig;
    private ConfigProperty configProperty;
    private JniConfig jniConfig;

    public ConfigOption() {}

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
        // generate jni config
        this.jniConfig = generateJniConfig();
        // init configProperty
        this.configProperty = configProperty;
    }

    public void reloadConfig(int cryptoType) throws ConfigException {
        this.cryptoMaterialConfig = new CryptoMaterialConfig(this.configProperty);
    }

    public JniConfig generateJniConfig() {
        // init jni config
        JniConfig jniConfig = new JniConfig();
        jniConfig.setPeers(networkConfig.getPeers());

        boolean disableSsl = DISABLE_SSL;
        // if disable ssl, default false
        jniConfig.setDisableSsl(disableSsl);
        jniConfig.setThreadPoolSize(threadPoolConfig.getThreadPoolSize());
        jniConfig.setMessageTimeoutMs(networkConfig.getTimeout());

        if (disableSsl) {
            logger.info(" ==>> java sdk work in disable ssl model !!!");
            return jniConfig;
        }

        if (cryptoMaterialConfig.getUseSmCrypto()) {
            JniConfig.SMCertConfig smCertConfig = new JniConfig.SMCertConfig();

            smCertConfig.setCaCert(cryptoMaterialConfig.getCaCert());
            smCertConfig.setNodeCert(cryptoMaterialConfig.getSdkCert());
            smCertConfig.setNodeKey(cryptoMaterialConfig.getSdkPrivateKey());
            smCertConfig.setEnNodeCert(cryptoMaterialConfig.getEnSdkCert());
            smCertConfig.setEnNodeKey(cryptoMaterialConfig.getEnSdkPrivateKey());

            jniConfig.setSslType("sm_ssl");
            jniConfig.setSmCertConfig(smCertConfig);
        } else { // ssl cert config items
            JniConfig.CertConfig certConfig = new JniConfig.CertConfig();
            certConfig.setCaCert(cryptoMaterialConfig.getCaCert());
            certConfig.setNodeCert(cryptoMaterialConfig.getSdkCert());
            certConfig.setNodeKey(cryptoMaterialConfig.getSdkPrivateKey());

            jniConfig.setCertConfig(certConfig);
            jniConfig.setSslType("ssl");
        }

        return jniConfig;
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

    public JniConfig getJniConfig() {
        return jniConfig;
    }

    public void setJniConfig(JniConfig jniConfig) {
        this.jniConfig = jniConfig;
    }

    public ConfigProperty getConfigProperty() {
        return configProperty;
    }

    public void setConfigProperty(ConfigProperty configProperty) {
        this.configProperty = configProperty;
    }
}
