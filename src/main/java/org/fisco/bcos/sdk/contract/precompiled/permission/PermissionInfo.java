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
package org.fisco.bcos.sdk.contract.precompiled.permission;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class PermissionInfo {
    @JsonProperty("table_name")
    private String tableName;

    private String address;

    @JsonProperty("enable_num")
    private String enableNum;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEnableNum() {
        return enableNum;
    }

    public void setEnableNum(String enableNum) {
        this.enableNum = enableNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionInfo that = (PermissionInfo) o;
        return Objects.equals(tableName, that.tableName)
                && Objects.equals(address, that.address)
                && Objects.equals(enableNum, that.enableNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName, address, enableNum);
    }

    @Override
    public String toString() {
        return "PermissionInfo{"
                + "tableName='"
                + tableName
                + '\''
                + ", address='"
                + address
                + '\''
                + ", enableNum='"
                + enableNum
                + '\''
                + '}';
    }
}
