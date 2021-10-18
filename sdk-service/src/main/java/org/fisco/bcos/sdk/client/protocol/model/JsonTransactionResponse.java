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

import com.qq.tars.protocol.tars.TarsOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import org.fisco.bcos.sdk.client.exceptions.ClientException;
import org.fisco.bcos.sdk.client.protocol.model.tars.TransactionData;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.model.MerkleProofUnit;
import org.fisco.bcos.sdk.utils.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonTransactionResponse {
    private static final Logger logger = LoggerFactory.getLogger(JsonTransactionResponse.class);

    // the fields related to get-transaction
    private Integer version;
    private String hash;
    private String nonce;
    private Long blockLimit;
    private String to;
    private String from;
    private String input;
    private String chainID;
    private String groupID;
    private String signature;
    private Long importTime;
    private List<MerkleProofUnit> transactionProof;

    public JsonTransactionResponse() {}

    public List<MerkleProofUnit> getTransactionProof() {
        return this.transactionProof;
    }

    public void setTransactionProof(List<MerkleProofUnit> transactionProof) {
        this.transactionProof = transactionProof;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getFrom() {
        return this.from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getHash() {
        return this.hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getInput() {
        return this.input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getNonce() {
        return this.nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getTo() {
        return this.to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Long getBlockLimit() {
        return this.blockLimit;
    }

    public void setBlockLimit(Long blockLimit) {
        this.blockLimit = blockLimit;
    }

    public String getChainID() {
        return this.chainID;
    }

    public void setChainID(String chainId) {
        this.chainID = chainId;
    }

    public String getGroupID() {
        return this.groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    // calculate the hash for the transaction
    public String calculateHash(CryptoSuite cryptoSuite) throws ClientException {
        try {
            TransactionData rawTransaction =
                    new TransactionData(
                            0,
                            this.chainID,
                            this.groupID,
                            this.blockLimit,
                            this.nonce,
                            this.to,
                            Base64.getDecoder().decode(this.input));
            TarsOutputStream tarsOutputStream = new TarsOutputStream();
            rawTransaction.writeTo(tarsOutputStream);
            byte[] encodedTransaction = cryptoSuite.hash(tarsOutputStream.toByteArray());
            return "0x" + Hex.toHexString(cryptoSuite.hash(encodedTransaction));
        } catch (Exception e) {
            logger.warn(
                    "calculate hash for the transaction failed, version: {}, transactionHash: {}, error info: {}",
                    this.version,
                    this.hash,
                    e);
            throw new ClientException(
                    "calculate hash for transaction " + this.hash + " failed for " + e.getMessage(),
                    e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        JsonTransactionResponse that = (JsonTransactionResponse) o;
        return Objects.equals(this.version, that.version)
                && Objects.equals(this.from, that.from)
                && Objects.equals(this.hash, that.hash)
                && Objects.equals(this.transactionProof, that.transactionProof)
                && Objects.equals(this.input, that.input)
                && Objects.equals(this.nonce, that.nonce)
                && Objects.equals(this.to, that.to)
                && Objects.equals(this.blockLimit, that.blockLimit)
                && Objects.equals(this.chainID, that.chainID)
                && Objects.equals(this.groupID, that.groupID)
                && Objects.equals(this.signature, that.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.version,
                this.from,
                this.hash,
                this.input,
                this.nonce,
                this.to,
                this.blockLimit,
                this.chainID,
                this.groupID,
                this.signature);
    }

    @Override
    public String toString() {
        return "{"
                + "version='"
                + this.version
                + '\''
                + ", from='"
                + this.from
                + '\''
                + ", hash='"
                + this.hash
                + '\''
                + ", input='"
                + this.input
                + '\''
                + ", nonce='"
                + this.nonce
                + '\''
                + ", to='"
                + this.to
                + '\''
                + ", blockLimit='"
                + this.blockLimit
                + '\''
                + ", chainId='"
                + this.chainID
                + '\''
                + ", groupID='"
                + this.groupID
                + '\''
                + ", transactionProof='"
                + this.transactionProof
                + '\''
                + ", signature="
                + this.signature
                + '}';
    }

    public Long getImportTime() {
        return this.importTime;
    }

    public void setImportTime(Long importTime) {
        this.importTime = importTime;
    }
}
