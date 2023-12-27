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
package org.fisco.bcos.sdk.v3.model;

import static org.fisco.bcos.sdk.v3.utils.Numeric.toBytesPadded;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.keypair.KeyPairJniObj;
import org.fisco.bcos.sdk.jni.utilities.receipt.ReceiptBuilderJniObj;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionVersion;
import org.fisco.bcos.sdk.v3.client.exceptions.ClientException;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.hash.Hash;
import org.fisco.bcos.sdk.v3.utils.AddressUtils;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.ObjectMapperFactory;

public class TransactionReceipt {
    private Integer version;
    private String contractAddress;
    private String checksumContractAddress;
    private String gasUsed;
    private int status = -1;
    private BigInteger blockNumber;
    private String output;
    private String transactionHash;

    @JsonProperty("hash")
    private String receiptHash;

    private List<Logs> logEntries;
    private String input;
    private String from;
    private String to;
    @Deprecated private List<MerkleProofUnit> transactionProof;
    @Deprecated private List<MerkleProofUnit> receiptProof;
    private List<String> txProof;
    private List<String> txReceiptProof;
    private String extraData;
    private String message;

    // Fields of v1 transaction
    private String effectiveGasPrice = "";

    public boolean isStatusOK() {
        return this.status == 0;
    }

    public static class Logs {
        @JsonProperty("address")
        private String address;

        @JsonProperty("topics")
        private List<String> topics;

        @JsonProperty("data")
        private String data;

        private String blockNumber;

        public String getAddress() {
            return this.address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public List<String> getTopics() {
            return this.topics;
        }

        public void setTopics(List<String> topics) {
            this.topics = topics;
        }

        public String getData() {
            return this.data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getBlockNumber() {
            return this.blockNumber;
        }

        public void setBlockNumber(String blockNumber) {
            this.blockNumber = blockNumber;
        }

        public EventLog toEventLog() {
            EventLog eventLog = new EventLog();
            eventLog.setAddress(this.address);
            eventLog.setTopics(this.topics);
            eventLog.setData(this.data);
            eventLog.setBlockNumber(this.blockNumber);
            return eventLog;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            Logs logs = (Logs) o;
            return Objects.equals(this.address, logs.address)
                    && Objects.equals(this.topics, logs.topics)
                    && Objects.equals(this.data, logs.data);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.address, this.topics, this.data);
        }

        @Override
        public String toString() {
            return "Logs{"
                    + "address='"
                    + this.address
                    + '\''
                    + ", topics="
                    + this.topics
                    + ", data='"
                    + this.data
                    + '\''
                    + '}';
        }
    }

    public static TransactionReceipt readFromHexString(String hexString)
            throws JniException, IOException {
        String jsonObj = ReceiptBuilderJniObj.decodeReceiptDataToJsonObj(hexString);
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        return objectMapper.readValue(jsonObj.getBytes(), TransactionReceipt.class);
    }

    public String writeToHexString() throws JsonProcessingException, JniException {
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        String json = objectMapper.writeValueAsString(this);
        long receiptDataWithJson = ReceiptBuilderJniObj.createReceiptDataWithJson(json);
        String encodeReceiptData = ReceiptBuilderJniObj.encodeReceiptData(receiptDataWithJson);
        ReceiptBuilderJniObj.destroyReceiptData(receiptDataWithJson);
        return encodeReceiptData;
    }

    @Deprecated
    public List<MerkleProofUnit> getReceiptProof() {
        return this.receiptProof;
    }

    @Deprecated
    public void setReceiptProof(List<MerkleProofUnit> receiptProof) {
        this.receiptProof = receiptProof;
    }

    public String getTransactionHash() {
        return this.transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getReceiptHash() {
        return receiptHash;
    }

    public void setReceiptHash(String receiptHash) {
        this.receiptHash = receiptHash;
    }

    public BigInteger getBlockNumber() {
        return this.blockNumber;
    }

    public void setBlockNumber(BigInteger blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getFrom() {
        return this.from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return AddressUtils.addHexPrefixToAddress(this.to);
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getGasUsed() {
        return this.gasUsed;
    }

    public void setGasUsed(String gasUsed) {
        this.gasUsed = gasUsed;
    }

    public String getContractAddress() {
        return AddressUtils.addHexPrefixToAddress(contractAddress);
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getChecksumContractAddress() {
        return checksumContractAddress;
    }

    public void setChecksumContractAddress(String checksumContractAddress) {
        this.checksumContractAddress = checksumContractAddress;
    }

    public void setLogEntries(List<Logs> logEntries) {
        this.logEntries = logEntries;
    }

    public List<Logs> getLogEntries() {
        return this.logEntries;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInput() {
        return this.input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return this.output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    @Deprecated
    public List<MerkleProofUnit> getTransactionProof() {
        return this.transactionProof;
    }

    @Deprecated
    public void setTransactionProof(List<MerkleProofUnit> transactionProof) {
        this.transactionProof = transactionProof;
    }

    public List<String> getTxProof() {
        return txProof;
    }

    public void setTxProof(List<String> txProof) {
        this.txProof = txProof;
    }

    public List<String> getTxReceiptProof() {
        return txReceiptProof;
    }

    public void setTxReceiptProof(List<String> txReceiptProof) {
        this.txReceiptProof = txReceiptProof;
    }

    public String getEffectiveGasPrice() {
        return effectiveGasPrice;
    }

    public void setEffectiveGasPrice(String effectiveGasPrice) {
        this.effectiveGasPrice = effectiveGasPrice;
    }

    @Deprecated
    public String calculateReceiptHash(CryptoSuite cryptoSuite) throws ClientException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
            String json = objectMapper.writeValueAsString(this);

            long receiptData = ReceiptBuilderJniObj.createReceiptDataWithJson(json);
            long jniKeyPair = cryptoSuite.getCryptoKeyPair().getJniKeyPair();
            int jniKeyPairCryptoType = KeyPairJniObj.getJniKeyPairCryptoType(jniKeyPair);

            String receiptDataHash =
                    ReceiptBuilderJniObj.calcReceiptDataHash(jniKeyPairCryptoType, receiptData);
            ReceiptBuilderJniObj.destroyReceiptData(receiptData);
            return receiptDataHash;
        } catch (Exception e) {
            throw new ClientException(
                    "calculate hash for receipt "
                            + this.receiptHash
                            + " failed for "
                            + e.getMessage(),
                    e);
        }
    }

    /**
     * Calculate receipt hash in native code with specified hash implementation (keccak256 or sm3)
     *
     * @param hashImpl hash implementation
     * @return receipt hash hex string
     * @throws IOException throw exception when encodeTransactionReceipt() throws IOException
     */
    public String calculateReceiptHashInNative(Hash hashImpl) throws IOException {
        byte[] hashBytes = hashImpl.hash(encodeTransactionReceipt());
        return Hex.toHexStringWithPrefix(hashBytes);
    }

    /**
     * This method is used to encode the transaction receipt into a byte array. It first creates a
     * ByteArrayOutputStream, then writes the version, gas used, contract address, status, and
     * output of the transaction receipt into the stream. If the version of the transaction receipt
     * is V1, it also writes the effective gas price into the stream. Then, it iterates over the log
     * entries of the transaction receipt. For each log entry, it writes the address, topics, and
     * data into the stream. Finally, it writes the block number of the transaction receipt into the
     * stream and returns the byte array.
     *
     * @return byte array representing the encoded transaction receipt
     * @throws IOException if an I/O error occurs
     */
    public byte[] encodeTransactionReceipt() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(toBytesPadded(BigInteger.valueOf(getVersion()), 4));
        byteArrayOutputStream.write(getGasUsed().getBytes());
        byteArrayOutputStream.write(getContractAddress().getBytes());
        byteArrayOutputStream.write(toBytesPadded(BigInteger.valueOf(getStatus()), 4));
        byteArrayOutputStream.write(getOutput().getBytes());
        if (getVersion() == TransactionVersion.V1.getValue()) {
            byteArrayOutputStream.write(getEffectiveGasPrice().getBytes());
        }

        for (Logs logEntry : getLogEntries()) {
            byteArrayOutputStream.write(logEntry.getAddress().getBytes());
            for (String topic : logEntry.getTopics()) {
                byteArrayOutputStream.write(topic.getBytes());
            }
            byteArrayOutputStream.write(logEntry.getData().getBytes());
        }
        byteArrayOutputStream.write(toBytesPadded(getBlockNumber(), 8));
        return byteArrayOutputStream.toByteArray();
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        TransactionReceipt that = (TransactionReceipt) o;
        return Objects.equals(this.transactionHash, that.transactionHash)
                && Objects.equals(this.version, that.version)
                && Objects.equals(this.blockNumber, that.blockNumber)
                && Objects.equals(this.from, that.from)
                && Objects.equals(this.to, that.to)
                && Objects.equals(this.gasUsed, that.gasUsed)
                && Objects.equals(this.contractAddress, that.contractAddress)
                && Objects.equals(this.checksumContractAddress, that.checksumContractAddress)
                && Objects.equals(this.logEntries, that.logEntries)
                && Objects.equals(this.status, that.status)
                && Objects.equals(this.input, that.input)
                && Objects.equals(this.output, that.output)
                && Objects.equals(this.extraData, that.extraData)
                && Objects.equals(this.transactionProof, that.transactionProof)
                && Objects.equals(this.receiptProof, that.receiptProof);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.transactionHash,
                this.version,
                this.blockNumber,
                this.from,
                this.to,
                this.gasUsed,
                this.contractAddress,
                this.checksumContractAddress,
                this.logEntries,
                this.status,
                this.input,
                this.output,
                this.extraData,
                this.transactionProof,
                this.receiptProof);
    }

    @Override
    public String toString() {
        return "TransactionReceipt{"
                + "transactionHash='"
                + this.transactionHash
                + '\''
                + ", version='"
                + this.version
                + '\''
                + ", blockNumber='"
                + this.blockNumber
                + '\''
                + ", from='"
                + this.from
                + '\''
                + ", to='"
                + this.getTo()
                + '\''
                + ", gasUsed='"
                + this.gasUsed
                + '\''
                + ", contractAddress='"
                + this.contractAddress
                + ", checksumContractAddress='"
                + this.checksumContractAddress
                + '\''
                + ", logs="
                + this.logEntries
                + ", status='"
                + this.status
                + '\''
                + ", extraData='"
                + this.extraData
                + '\''
                + ", input='"
                + this.input
                + '\''
                + ", output='"
                + this.output
                + '\''
                + ", txProof="
                + this.txProof
                + ", txReceiptProof="
                + this.txReceiptProof
                + '}';
    }

    /** @return the message */
    public String getMessage() {
        return this.message;
    }

    /** @param message the message to set */
    public void setMessage(String message) {
        this.message = message;
    }
}
