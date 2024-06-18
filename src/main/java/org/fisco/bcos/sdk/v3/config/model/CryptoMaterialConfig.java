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

package org.fisco.bcos.sdk.v3.config.model;

import java.util.Map;
import org.fisco.bcos.sdk.v3.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Crypto material configuration, include certs and keys */
public class CryptoMaterialConfig {
    private static final Logger logger = LoggerFactory.getLogger(CryptoMaterialConfig.class);

    private Boolean useSmCrypto = false;
    private Boolean disableSsl = false;
    private Boolean enableHsm = false;
    private String certPath = "conf";

    private String caCertPath;
    private String sdkCertPath;
    private String sdkPrivateKeyPath;
    private String enSdkCertPath;
    private String enSdkPrivateKeyPath;

    private String caCert;
    private String sdkCert;
    private String sdkPrivateKey;
    private String enSdkCert;
    private String enSdkPrivateKey;

    private String hsmLibPath;
    private String hsmKeyIndex;
    private String hsmPassword;

    public CryptoMaterialConfig() {}

    public CryptoMaterialConfig(ConfigProperty configProperty) throws ConfigException {

        Map<String, Object> cryptoMaterialProperty = configProperty.getCryptoMaterial();
        String useSMCrypto = (String) cryptoMaterialProperty.get("useSMCrypto");
        Object disableSsl = cryptoMaterialProperty.get("disableSsl");
        Object enableSsl = cryptoMaterialProperty.get("enableSsl");
        String enableHsm = (String) cryptoMaterialProperty.get("enableHsm");

        this.useSmCrypto = Boolean.valueOf(useSMCrypto);
        if (disableSsl != null) {
            this.disableSsl = Boolean.parseBoolean((String) disableSsl);
        }
        if (enableSsl != null) {
            // if enableSsl is set, disableSsl will be ignored
            this.disableSsl = !Boolean.parseBoolean((String) enableSsl);
        }

        this.enableHsm = Boolean.valueOf(enableHsm);

        if (this.enableHsm) {
            this.hsmLibPath = (String) cryptoMaterialProperty.get("hsmLibPath");
            this.hsmKeyIndex = (String) cryptoMaterialProperty.get("hsmKeyIndex");
            this.hsmPassword = (String) cryptoMaterialProperty.get("hsmPassword");

            if (this.hsmLibPath == null || this.hsmKeyIndex == null || this.hsmPassword == null) {
                throw new ConfigException(
                        "hsmLibPath hsmKeyIndex and hsmPassword, must be set in HSM model");
            }
        }

        if (this.disableSsl) {
            logger.info("Load cryptoMaterial, disableSsl has been set");
            return;
        }

        int cryptoType =
                this.useSmCrypto
                        ? (this.enableHsm ? CryptoType.HSM_TYPE : CryptoType.SM_TYPE)
                        : CryptoType.ECDSA_TYPE;
        this.certPath =
                ConfigProperty.getConfigFilePath(
                        ConfigProperty.getValue(cryptoMaterialProperty, "certPath", this.certPath));
        CryptoMaterialConfig defaultCryptoMaterialConfig =
                this.getDefaultCaCertPath(cryptoType, this.certPath);

        if (cryptoType == CryptoType.ECDSA_TYPE) {
            this.caCert =
                    ConfigProperty.getConfigFileContent(
                            ConfigProperty.getValue(
                                    cryptoMaterialProperty,
                                    "caCert",
                                    defaultCryptoMaterialConfig.getCaCertPath()));
            this.sdkCert =
                    ConfigProperty.getConfigFileContent(
                            ConfigProperty.getValue(
                                    cryptoMaterialProperty,
                                    "sslCert",
                                    defaultCryptoMaterialConfig.getSdkCertPath()));
            this.sdkPrivateKey =
                    ConfigProperty.getConfigFileContent(
                            ConfigProperty.getValue(
                                    cryptoMaterialProperty,
                                    "sslKey",
                                    defaultCryptoMaterialConfig.getSdkPrivateKeyPath()));
        } else {
            this.caCert =
                    ConfigProperty.getConfigFileContent(
                            ConfigProperty.getValue(
                                    cryptoMaterialProperty,
                                    "caCert",
                                    defaultCryptoMaterialConfig.getCaCertPath()));
            this.sdkCert =
                    ConfigProperty.getConfigFileContent(
                            ConfigProperty.getValue(
                                    cryptoMaterialProperty,
                                    "sslCert",
                                    defaultCryptoMaterialConfig.getSdkCertPath()));
            this.sdkPrivateKey =
                    ConfigProperty.getConfigFileContent(
                            ConfigProperty.getValue(
                                    cryptoMaterialProperty,
                                    "sslKey",
                                    defaultCryptoMaterialConfig.getSdkPrivateKeyPath()));
            this.enSdkCert =
                    ConfigProperty.getConfigFileContent(
                            ConfigProperty.getValue(
                                    cryptoMaterialProperty,
                                    "enSslCert",
                                    defaultCryptoMaterialConfig.getEnSdkCertPath()));
            this.enSdkPrivateKey =
                    ConfigProperty.getConfigFileContent(
                            ConfigProperty.getValue(
                                    cryptoMaterialProperty,
                                    "enSslKey",
                                    defaultCryptoMaterialConfig.getEnSdkPrivateKeyPath()));
        }

        logger.debug(
                "Load cryptoMaterial, useSmCrypto: {}, useHSMCrypto: {}, cryptoType: {}, caCertPath: {}, sdkCertPath: {}, sdkPrivateKeyPath:{}, enSSLCertPath: {}, enSSLPrivateKeyPath:{}",
                this.useSmCrypto,
                this.enableHsm,
                cryptoType,
                this.getCaCertPath(),
                this.getSdkCertPath(),
                this.getSdkPrivateKeyPath(),
                this.getEnSdkCertPath(),
                this.getEnSdkPrivateKeyPath());
    }

    public CryptoMaterialConfig getDefaultCaCertPath(int cryptoType, String certPath)
            throws ConfigException {
        CryptoMaterialConfig cryptoMaterialConfig = new CryptoMaterialConfig();
        cryptoMaterialConfig.setCertPath(certPath);
        if (cryptoType == CryptoType.ECDSA_TYPE) {
            cryptoMaterialConfig.setCaCertPath(certPath + "/" + "ca.crt");
            cryptoMaterialConfig.setSdkCertPath(certPath + "/" + "sdk.crt");
            cryptoMaterialConfig.setSdkPrivateKeyPath(certPath + "/" + "sdk.key");
        } else if (cryptoType == CryptoType.SM_TYPE || cryptoType == CryptoType.HSM_TYPE) {
            cryptoMaterialConfig.setCaCertPath(certPath + "/" + "sm_ca.crt");
            cryptoMaterialConfig.setSdkCertPath(certPath + "/" + "sm_sdk.crt");
            cryptoMaterialConfig.setSdkPrivateKeyPath(certPath + "/" + "sm_sdk.key");
            cryptoMaterialConfig.setEnSdkCertPath(certPath + "/" + "sm_ensdk.crt");
            cryptoMaterialConfig.setEnSdkPrivateKeyPath(certPath + "/" + "sm_ensdk.key");
        } else {
            throw new ConfigException(
                    "load CryptoMaterialConfig failed, only support ecdsa and sm now, expected 0 or 1, but provided "
                            + cryptoType);
        }
        return cryptoMaterialConfig;
    }

    public String getCertPath() {
        return this.certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public String getCaCert() {
        return this.caCert;
    }

    public void setCaCert(String caCert) {
        this.caCert = caCert;
    }

    public String getSdkCert() {
        return this.sdkCert;
    }

    public void setSdkCert(String sdkCert) {
        this.sdkCert = sdkCert;
    }

    public String getSdkPrivateKey() {
        return this.sdkPrivateKey;
    }

    public void setSdkPrivateKey(String sdkPrivateKey) {
        this.sdkPrivateKey = sdkPrivateKey;
    }

    public String getEnSdkCert() {
        return this.enSdkCert;
    }

    public void setEnSdkCert(String enSdkCert) {
        this.enSdkCert = enSdkCert;
    }

    public String getEnSdkPrivateKey() {
        return this.enSdkPrivateKey;
    }

    public void setEnSdkPrivateKey(String enSdkPrivateKey) {
        this.enSdkPrivateKey = enSdkPrivateKey;
    }

    public Boolean getUseSmCrypto() {
        return this.useSmCrypto;
    }

    public Boolean getDisableSsl() {
        return disableSsl;
    }

    public void setDisableSsl(Boolean disableSsl) {
        this.disableSsl = disableSsl;
    }

    public void setUseSmCrypto(Boolean useSmCrypto) {
        this.useSmCrypto = useSmCrypto;
    }

    public int getSslCryptoType() {
        return this.useSmCrypto ? CryptoType.SM_TYPE : CryptoType.ECDSA_TYPE;
    }

    public boolean isUseSmCrypto() {
        return useSmCrypto;
    }

    public Boolean getEnableHsm() {
        return enableHsm;
    }

    public void setEnableHsm(Boolean enableHsm) {
        this.enableHsm = enableHsm;
    }

    public String getHsmLibPath() {
        return hsmLibPath;
    }

    public void setHsmLibPath(String hsmLibPath) {
        this.hsmLibPath = hsmLibPath;
    }

    public String getHsmKeyIndex() {
        return hsmKeyIndex;
    }

    public void setHsmKeyIndex(String hsmKeyIndex) {
        this.hsmKeyIndex = hsmKeyIndex;
    }

    public String getHsmPassword() {
        return hsmPassword;
    }

    public void setHsmPassword(String hsmPassword) {
        this.hsmPassword = hsmPassword;
    }

    public String getCaCertPath() {
        return caCertPath;
    }

    public void setCaCertPath(String caCertPath) {
        this.caCertPath = caCertPath;
    }

    public String getSdkCertPath() {
        return sdkCertPath;
    }

    public void setSdkCertPath(String sdkCertPath) {
        this.sdkCertPath = sdkCertPath;
    }

    public String getSdkPrivateKeyPath() {
        return sdkPrivateKeyPath;
    }

    public void setSdkPrivateKeyPath(String sdkPrivateKeyPath) {
        this.sdkPrivateKeyPath = sdkPrivateKeyPath;
    }

    public String getEnSdkCertPath() {
        return enSdkCertPath;
    }

    public void setEnSdkCertPath(String enSdkCertPath) {
        this.enSdkCertPath = enSdkCertPath;
    }

    public String getEnSdkPrivateKeyPath() {
        return enSdkPrivateKeyPath;
    }

    public void setEnSdkPrivateKeyPath(String enSdkPrivateKeyPath) {
        this.enSdkPrivateKeyPath = enSdkPrivateKeyPath;
    }

    @Override
    public String toString() {
        return "CryptoMaterialConfig{"
                + "useSmCrypto="
                + useSmCrypto
                + "useHSMCrypto="
                + enableHsm
                + ", certPath='"
                + certPath
                + '\''
                + ", caCertPath='"
                + caCertPath
                + '\''
                + ", sdkCertPath='"
                + sdkCertPath
                + '\''
                + ", sdkPrivateKeyPath='"
                + sdkPrivateKeyPath
                + '\''
                + ", enSdkCertPath='"
                + enSdkCertPath
                + '\''
                + ", enSdkPrivateKeyPath='"
                + enSdkPrivateKeyPath
                + '\''
                + '}';
    }
}
