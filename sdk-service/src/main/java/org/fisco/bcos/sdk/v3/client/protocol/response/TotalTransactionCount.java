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

package org.fisco.bcos.sdk.v3.client.protocol.response;

import java.util.Objects;
import org.fisco.bcos.sdk.v3.model.JsonRpcResponse;

/** getTotalTransactionCount */
public class TotalTransactionCount
        extends JsonRpcResponse<TotalTransactionCount.TransactionCountInfo> {
    public TransactionCountInfo getTotalTransactionCount() {
        return this.getResult();
    }

    public static class TransactionCountInfo {
        private String transactionCount;
        private String blockNumber;
        private String failedTransactionCount;

        public String getTransactionCount() {
            return this.transactionCount;
        }

        public void setTransactionCount(String transactionCount) {
            this.transactionCount = transactionCount;
        }

        public String getBlockNumber() {
            return this.blockNumber;
        }

        public void setBlockNumber(String blockNumber) {
            this.blockNumber = blockNumber;
        }

        public String getFailedTransactionCount() {
            return this.failedTransactionCount;
        }

        public void setFailedTransactionCount(String failedTransactionCount) {
            this.failedTransactionCount = failedTransactionCount;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            TransactionCountInfo that = (TransactionCountInfo) o;
            return Objects.equals(this.transactionCount, that.transactionCount)
                    && Objects.equals(this.blockNumber, that.blockNumber)
                    && Objects.equals(this.failedTransactionCount, that.failedTransactionCount);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    this.transactionCount, this.blockNumber, this.failedTransactionCount);
        }

        @Override
        public String toString() {
            return "TransactionCountInfo{"
                    + "transactionCount='"
                    + this.transactionCount
                    + '\''
                    + ", blockNumber='"
                    + this.blockNumber
                    + '\''
                    + ", failedTransactionCount='"
                    + this.failedTransactionCount
                    + '\''
                    + '}';
        }
    }
}
