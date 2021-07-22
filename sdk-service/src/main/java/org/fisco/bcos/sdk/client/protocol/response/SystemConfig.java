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

package org.fisco.bcos.sdk.client.protocol.response;

import org.fisco.bcos.sdk.model.JsonRpcResponse;

import java.util.Objects;

/**
 * getSystemConfigByKey
 */
public class SystemConfig extends JsonRpcResponse<SystemConfig.Config> {
    public SystemConfig.Config getSystemConfig() {
        return this.getResult();
    }

    public static class Config {
        private Integer blockNumber;
        private String value;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            SystemConfig.Config that = (SystemConfig.Config) o;
            return Objects.equals(this.blockNumber, that.blockNumber)
                    && Objects.equals(this.value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    this.blockNumber,
                    this.value);
        }

        @Override
        public String toString() {
            return "SystemConfig{"
                    + "blockNumber='"
                    + this.blockNumber
                    + '\''
                    + ", value='"
                    + this.value
                    + '\''
                    + '}';
        }

        public Integer getBlockNumber() {
            return this.blockNumber;
        }

        public void setBlockNumber(Integer blockNumber) {
            this.blockNumber = blockNumber;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
