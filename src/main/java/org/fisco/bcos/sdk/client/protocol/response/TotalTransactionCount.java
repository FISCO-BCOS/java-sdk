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

import java.util.Objects;

/** getTotalTransactionCount */
public class TotalTransactionCount
        extends JsonRpcResponse<TotalTransactionCount.TransactionCountInfo> {
    public TransactionCountInfo getTotalTransactionCount() {
        return getResult();
    }

    public static class TransactionCountInfo {
        private String txSum;
        private String blockNumber;
        private String failedTxSum;

        public String getTxSum() {
            return txSum;
        }

        public void setTxSum(String txSum) {
            this.txSum = txSum;
        }

        public String getBlockNumber() {
            return blockNumber;
        }

        public void setBlockNumber(String blockNumber) {
            this.blockNumber = blockNumber;
        }

        public String getFailedTxSum() {
            return failedTxSum;
        }

        public void setFailedTxSum(String failedTxSum) {
            this.failedTxSum = failedTxSum;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TransactionCountInfo that = (TransactionCountInfo) o;
            return Objects.equals(txSum, that.txSum)
                    && Objects.equals(blockNumber, that.blockNumber)
                    && Objects.equals(failedTxSum, that.failedTxSum);
        }

        @Override
        public int hashCode() {
            return Objects.hash(txSum, blockNumber, failedTxSum);
        }

        @Override
        public String toString() {
            return "TransactionCountInfo{"
                    + "txSum='"
                    + txSum
                    + '\''
                    + ", blockNumber='"
                    + blockNumber
                    + '\''
                    + ", failedTxSum='"
                    + failedTxSum
                    + '\''
                    + '}';
        }
    }
}
