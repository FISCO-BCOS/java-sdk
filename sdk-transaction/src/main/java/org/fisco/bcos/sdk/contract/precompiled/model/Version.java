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

package org.fisco.bcos.sdk.contract.precompiled.model;

import org.fisco.bcos.sdk.model.EnumNodeVersion;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Version {
    private static Logger logger = LoggerFactory.getLogger(Version.class);
    private final String minVersion;
    private String maxVersion;
    private String interfaceName;

    public Version(String interfaceName, String minVersion) {
        this.interfaceName = interfaceName;
        this.minVersion = minVersion;
    }

    public void checkVersion(String currentVersion) throws ContractException {
        try {
            EnumNodeVersion.Version minSupportVersion = EnumNodeVersion.getClassVersion(minVersion);
            EnumNodeVersion.Version supportedVersion =
                    EnumNodeVersion.getClassVersion(currentVersion);
            String errorMessage =
                    "The fisco bcos node with supported_version lower than "
                            + minSupportVersion.toVersionString()
                            + " does not support the interface "
                            + interfaceName
                            + ", current fisco-bcos supported_version:"
                            + supportedVersion.toVersionString();

            if (supportedVersion.getMajor() < minSupportVersion.getMajor()) {
                logger.error(errorMessage);
                throw new ContractException(errorMessage);
            }
            if (supportedVersion.getMajor() == minSupportVersion.getMajor()
                    && supportedVersion.getMinor() < minSupportVersion.getMinor()) {
                logger.error(errorMessage);
                throw new ContractException(errorMessage);
            }
            if (maxVersion == null || maxVersion.equals("")) {
                return;
            }
            // check maxVersion
            EnumNodeVersion.Version maxSupportedVersion =
                    EnumNodeVersion.getClassVersion(maxVersion);
            errorMessage =
                    "The fisco bcos node with supported_version larger than "
                            + maxSupportedVersion.toVersionString()
                            + " does not support the interface "
                            + interfaceName
                            + ", current fisco-bcos supported_version:"
                            + supportedVersion.toVersionString();
            if (supportedVersion.getMajor() > maxSupportedVersion.getMajor()) {
                throw new ContractException(errorMessage);
            }
            if (supportedVersion.getMajor() == maxSupportedVersion.getMajor()
                    && supportedVersion.getMinor() > maxSupportedVersion.getMinor()) {
                throw new ContractException(errorMessage);
            }
        } catch (Exception e) {
            logger.error(
                    "checkVersion for interface "
                            + interfaceName
                            + " failed, error info: "
                            + e.getMessage());
            throw new ContractException(
                    "checkVersion for interface "
                            + interfaceName
                            + " failed, error info: "
                            + e.getMessage());
        }
    }

    public String getMinVersion() {
        return minVersion;
    }

    public String getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(String maxVersion) {
        this.maxVersion = maxVersion;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }
}
