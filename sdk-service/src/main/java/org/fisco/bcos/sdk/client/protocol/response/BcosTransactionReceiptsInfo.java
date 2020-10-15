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

import java.util.List;
import org.fisco.bcos.sdk.model.JsonRpcResponse;
import org.fisco.bcos.sdk.model.TransactionReceipt;

public class BcosTransactionReceiptsInfo
        extends JsonRpcResponse<BcosTransactionReceiptsInfo.TransactionReceiptsInfo> {
    public TransactionReceiptsInfo getTransactionReceiptsInfo() {
        return getResult();
    }

    public static class BlockInfo {
        private String receiptRoot;
        private String blockNumber;
        private String blockHash;
        private String receiptsCount;

        public String getReceiptRoot() {
            return receiptRoot;
        }

        public void setReceiptRoot(String receiptRoot) {
            this.receiptRoot = receiptRoot;
        }

        public String getBlockNumber() {
            return blockNumber;
        }

        public void setBlockNumber(String blockNumber) {
            this.blockNumber = blockNumber;
        }

        public String getBlockHash() {
            return blockHash;
        }

        public void setBlockHash(String blockHash) {
            this.blockHash = blockHash;
        }

        public String getReceiptsCount() {
            return receiptsCount;
        }

        public void setReceiptsCount(String receiptsCount) {
            this.receiptsCount = receiptsCount;
        }

        @Override
        public String toString() {
            return "BlockInfo{"
                    + "receiptRoot='"
                    + receiptRoot
                    + '\''
                    + ", blockNumber='"
                    + blockNumber
                    + '\''
                    + ", blockHash='"
                    + blockHash
                    + '\''
                    + ", receiptsCount='"
                    + receiptsCount
                    + '\''
                    + '}';
        }
    }

    public static class TransactionReceiptsInfo {
        private BlockInfo blockInfo;
        private List<TransactionReceipt> transactionReceipts;

        public BlockInfo getBlockInfo() {
            return blockInfo;
        }

        public void setBlockInfo(BlockInfo blockInfo) {
            this.blockInfo = blockInfo;
        }

        public List<TransactionReceipt> getTransactionReceipts() {
            return transactionReceipts;
        }

        public void setTransactionReceipts(List<TransactionReceipt> transactionReceipts) {
            this.transactionReceipts = transactionReceipts;
        }

        @Override
        public String toString() {
            return "TransactionReceiptsInfo{"
                    + "blockInfo="
                    + blockInfo
                    + ", transactionReceipts="
                    + transactionReceipts
                    + '}';
        }
    }
}
