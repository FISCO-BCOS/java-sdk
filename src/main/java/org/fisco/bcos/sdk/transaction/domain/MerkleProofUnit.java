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
package org.fisco.bcos.sdk.transaction.domain;

import java.util.List;
import java.util.Objects;
import org.fisco.bcos.sdk.client.protocol.response.TransactionReceiptWithProof;
import org.fisco.bcos.sdk.client.protocol.response.TransactionWithProof;

/**
 * MerkleProofUnit object used by both {@link TransactionReceiptWithProof} and {@link
 * TransactionWithProof}.
 */
public class MerkleProofUnit {
    private List<String> left;
    private List<String> right;

    public MerkleProofUnit() {}

    public MerkleProofUnit(List<String> left, List<String> right) {
        this.left = left;
        this.right = right;
    }

    public List<String> getLeft() {
        return left;
    }

    public void setLeft(List<String> left) {
        this.left = left;
    }

    public List<String> getRight() {
        return right;
    }

    public void setRight(List<String> right) {
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MerkleProofUnit)) {
            return false;
        }
        MerkleProofUnit that = (MerkleProofUnit) o;
        return Objects.equals(getLeft(), that.getLeft())
                && Objects.equals(getRight(), that.getRight());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLeft(), getRight());
    }

    @Override
    public String toString() {
        return "MerkleProofUnit{" + "left=" + left + ", right=" + right + '}';
    }
}
