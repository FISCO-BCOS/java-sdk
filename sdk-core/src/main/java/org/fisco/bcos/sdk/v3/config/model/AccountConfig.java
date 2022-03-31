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

import java.util.Objects;
import org.fisco.bcos.sdk.v3.config.exceptions.ConfigException;

/** Account configuration */
public class AccountConfig {
    private String keyStoreDir;
    private String accountAddress;
    private String accountFileFormat;
    private String accountPassword;
    private String accountFilePath;

    public AccountConfig() {}

    public AccountConfig(ConfigProperty configProperty) throws ConfigException {
        this.keyStoreDir =
                ConfigProperty.getConfigFilePath(
                        ConfigProperty.getValue(
                                configProperty.getAccount(), "keyStoreDir", "account"));
        this.accountAddress =
                ConfigProperty.getValue(configProperty.getAccount(), "accountAddress", "");
        this.accountFileFormat =
                ConfigProperty.getValue(configProperty.getAccount(), "accountFileFormat", "pem");
        this.accountPassword = ConfigProperty.getValue(configProperty.getAccount(), "password", "");
        this.accountFilePath =
                ConfigProperty.getValue(configProperty.getAccount(), "accountFilePath", "");
        if (!this.accountFilePath.equals("")) {
            this.accountFilePath = ConfigProperty.getConfigFilePath(this.accountFilePath);
        }
        checkAccountConfig();
    }

    private void checkAccountConfig() throws ConfigException {
        if (this.accountAddress.equals("")) {
            return;
        }
        // check account format
        if ("pem".compareToIgnoreCase(accountFileFormat) != 0
                && "p12".compareToIgnoreCase(accountFileFormat) != 0) {
            throw new ConfigException(
                    "load account failed, only support pem and p12 account file format, current configurated account file format is "
                            + accountFileFormat);
        }
    }

    public String getAccountFilePath() {
        return accountFilePath;
    }

    public void setAccountFilePath(String accountFilePath) {
        this.accountFilePath = accountFilePath;
    }

    public String getKeyStoreDir() {
        return keyStoreDir;
    }

    public void setKeyStoreDir(String keyStoreDir) {
        this.keyStoreDir = keyStoreDir;
    }

    public String getAccountAddress() {
        return accountAddress;
    }

    public void setAccountAddress(String accountAddress) {
        this.accountAddress = accountAddress;
    }

    public String getAccountFileFormat() {
        return accountFileFormat;
    }

    public void setAccountFileFormat(String accountFileFormat) {
        this.accountFileFormat = accountFileFormat;
    }

    public String getAccountPassword() {
        return accountPassword;
    }

    public void setAccountPassword(String accountPassword) {
        this.accountPassword = accountPassword;
    }

    @Override
    public String toString() {
        return "AccountConfig{"
                + "keyStoreDir='"
                + keyStoreDir
                + '\''
                + ", accountAddress='"
                + accountAddress
                + '\''
                + ", accountFileFormat='"
                + accountFileFormat
                + '\''
                + ", accountPassword='"
                + accountPassword
                + '\''
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountConfig that = (AccountConfig) o;
        return Objects.equals(keyStoreDir, that.keyStoreDir)
                && Objects.equals(accountAddress, that.accountAddress)
                && Objects.equals(accountFileFormat, that.accountFileFormat)
                && Objects.equals(accountPassword, that.accountPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyStoreDir, accountAddress, accountFileFormat, accountPassword);
    }

    public void clearAccount() {
        this.accountFilePath = "";
        this.accountAddress = "";
        this.accountPassword = "";
    }

    public boolean isAccountConfigured() {
        if (accountFilePath != null && !accountFilePath.equals("")) {
            return true;
        }
        if (accountAddress != null && !accountAddress.equals("")) {
            return true;
        }
        return false;
    }
}
