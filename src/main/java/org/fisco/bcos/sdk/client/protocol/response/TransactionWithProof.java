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
import java.util.Objects;

import org.fisco.bcos.sdk.client.protocol.model.JsonTransactionResponse;
import org.fisco.bcos.sdk.model.JsonRpcResponse;
import org.fisco.bcos.sdk.model.MerkleProofUnit;

import com.fasterxml.jackson.annotation.JsonProperty;

/** getTransactionWithProof. */
public class TransactionWithProof
        extends JsonRpcResponse<TransactionWithProof.TransactionAndProof> {
    public TransactionWithProof.TransactionAndProof getTransactionWithProof() {
        return getResult();
    }

    public static class TransactionAndProof {
        @JsonProperty("transaction")
        private JsonTransactionResponse transaction;

        @JsonProperty("txProof")
        private List<MerkleProofUnit> transactionProof;

        public JsonTransactionResponse getTransaction() {
            return transaction;
        }

        public void setTransaction(JsonTransactionResponse transaction) {
            this.transaction = transaction;
        }

        public List<MerkleProofUnit> getTransactionProof() {
            return transactionProof;
        }

        public void setTransactionProof(List<MerkleProofUnit> transactionProof) {
            this.transactionProof = transactionProof;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TransactionAndProof that = (TransactionAndProof) o;
            return Objects.equals(transaction, that.transaction)
                    && Objects.equals(transactionProof, that.transactionProof);
        }

        @Override
        public int hashCode() {
            return Objects.hash(transaction, transactionProof);
        }

        @Override
        public String toString() {
            return "TransactionAndProof{"
                    + "transaction="
                    + transaction
                    + ", transactionProof="
                    + transactionProof
                    + '}';
        }
    }
}
