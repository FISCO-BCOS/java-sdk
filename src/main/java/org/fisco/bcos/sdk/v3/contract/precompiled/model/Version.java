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

package org.fisco.bcos.sdk.v3.contract.precompiled.model;

import org.fisco.bcos.sdk.v3.model.EnumNodeVersion;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

public class Version {
    private final String minVersion;
    private String maxVersion;
    private String interfaceName;

    public Version(String interfaceName, String minVersion) {
        this.interfaceName = interfaceName;
        this.minVersion = minVersion;
    }

    public Version(String interfaceName, String minVersion, String maxVersion) {
        this.interfaceName = interfaceName;
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
    }

    public void checkVersion(long currentVersion) throws ContractException {
        EnumNodeVersion.Version minSupportVersion = EnumNodeVersion.getClassVersion(minVersion);
        EnumNodeVersion.Version supportedVersion =
                EnumNodeVersion.valueOf((int) currentVersion).toVersionObj();
        String errorMessage =
                "The fisco bcos node with supported_version lower than "
                        + minSupportVersion.toVersionString()
                        + " does not support the interface "
                        + interfaceName
                        + ", current fisco-bcos supported_version:"
                        + supportedVersion.toVersionString();

        if (supportedVersion.compareTo(minSupportVersion) < 0) {
            throw new ContractException(errorMessage);
        }
        if (maxVersion == null || maxVersion.equals("")) {
            return;
        }
        // check maxVersion
        EnumNodeVersion.Version maxSupportedVersion = EnumNodeVersion.getClassVersion(maxVersion);
        errorMessage =
                "The fisco bcos node with supported_version larger than "
                        + maxSupportedVersion.toVersionString()
                        + " does not support the interface "
                        + interfaceName
                        + ", current fisco-bcos supported_version:"
                        + supportedVersion.toVersionString();

        if (supportedVersion.compareTo(maxSupportedVersion) > 0) {
            throw new ContractException(errorMessage);
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
