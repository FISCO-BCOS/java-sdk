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
package org.fisco.bcos.sdk.client.protocol.model;

import java.math.BigInteger;
import java.util.Objects;
import org.fisco.bcos.sdk.utils.Numeric;

public class JsonTransactionResponse {
    // the fields related to get-transaction
    private String blockHash;
    private String blockNumber;
    private String from;
    private String gas;
    private String hash;
    private String input;
    private String nonce;
    private String to;
    private String transactionIndex;
    private String value;

    // the fields related to get-block
    private String gasPrice;

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public BigInteger getBlockNumber() {
        return Numeric.decodeQuantity(blockNumber);
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getGas() {
        return gas;
    }

    public void setGas(String gas) {
        this.gas = gas;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(String transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonTransactionResponse that = (JsonTransactionResponse) o;
        return Objects.equals(blockHash, that.blockHash)
                && Objects.equals(
                        Numeric.decodeQuantity(blockNumber),
                        Numeric.decodeQuantity(that.blockNumber))
                && Objects.equals(from, that.from)
                && Objects.equals(gas, that.gas)
                && Objects.equals(hash, that.hash)
                && Objects.equals(input, that.input)
                && Objects.equals(nonce, that.nonce)
                && Objects.equals(to, that.to)
                && Objects.equals(transactionIndex, that.transactionIndex)
                && Objects.equals(value, that.value)
                && Objects.equals(gasPrice, that.gasPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                blockHash,
                Numeric.decodeQuantity(blockNumber),
                from,
                gas,
                hash,
                input,
                nonce,
                to,
                transactionIndex,
                value,
                gasPrice);
    }

    @Override
    public String toString() {
        return "JsonTransactionResponse{"
                + "blockHash='"
                + blockHash
                + '\''
                + ", blockNumber='"
                + blockNumber
                + '\''
                + ", from='"
                + from
                + '\''
                + ", gas='"
                + gas
                + '\''
                + ", hash='"
                + hash
                + '\''
                + ", input='"
                + input
                + '\''
                + ", nonce='"
                + nonce
                + '\''
                + ", to='"
                + to
                + '\''
                + ", transactionIndex='"
                + transactionIndex
                + '\''
                + ", value='"
                + value
                + '\''
                + ", gasPrice='"
                + gasPrice
                + '\''
                + '}';
    }
}
