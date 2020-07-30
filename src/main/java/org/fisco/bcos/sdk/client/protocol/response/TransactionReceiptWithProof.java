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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import org.fisco.bcos.sdk.model.JsonRpcResponse;
import org.fisco.bcos.sdk.model.MerkleProofUnit;
import org.fisco.bcos.sdk.model.TransactionReceipt;

/** getTransactionReceiptWithProof. */
public class TransactionReceiptWithProof
        extends JsonRpcResponse<TransactionReceiptWithProof.ReceiptAndProof> {
    public ReceiptAndProof getTransactionReceiptWithProof() {
        return getResult();
    }

    public static class ReceiptAndProof {
        @JsonProperty("transactionReceipt")
        private TransactionReceipt receipt;

        @JsonProperty("receiptProof")
        private List<MerkleProofUnit> receiptProof;

        public TransactionReceipt getReceipt() {
            return receipt;
        }

        public void setReceipt(TransactionReceipt receipt) {
            this.receipt = receipt;
        }

        public List<MerkleProofUnit> getReceiptProof() {
            return receiptProof;
        }

        public void setReceiptProof(List<MerkleProofUnit> receiptProof) {
            this.receiptProof = receiptProof;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ReceiptAndProof that = (ReceiptAndProof) o;
            return Objects.equals(receipt, that.receipt)
                    && Objects.equals(receiptProof, that.receiptProof);
        }

        @Override
        public int hashCode() {
            return Objects.hash(receipt, receiptProof);
        }

        @Override
        public String toString() {
            return "ReceiptAndProof{"
                    + "receipt="
                    + receipt
                    + ", receiptProof="
                    + receiptProof
                    + '}';
        }
    }
}
