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
package org.fisco.bcos.sdk.v3.client.protocol.model;

import static org.fisco.bcos.sdk.v3.utils.Numeric.toBytesPadded;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import org.fisco.bcos.sdk.jni.common.JniException;
import org.fisco.bcos.sdk.jni.utilities.tx.Transaction;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionBuilderJniObj;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionBuilderV2JniObj;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionData;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionDataV2;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionStructBuilderJniObj;
import org.fisco.bcos.sdk.jni.utilities.tx.TransactionVersion;
import org.fisco.bcos.sdk.v3.client.exceptions.ClientException;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.hash.Hash;
import org.fisco.bcos.sdk.v3.model.MerkleProofUnit;
import org.fisco.bcos.sdk.v3.utils.AddressUtils;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonTransactionResponse {
    private static final Logger logger = LoggerFactory.getLogger(JsonTransactionResponse.class);

    // the fields related to get-transaction
    private Integer version;
    private String hash;
    private String nonce;
    private long blockLimit;
    private String to;
    private String from;
    private String abi = "";
    private String input;
    private String chainID;
    private String groupID;
    private String extraData;
    private String signature;
    private long importTime;
    @Deprecated private List<MerkleProofUnit> transactionProof;
    private List<String> txProof;

    // Fields of v1 transaction
    private String value = "";
    private String gasPrice = "";
    private long gasLimit = 0;
    private String maxFeePerGas = "";
    private String maxPriorityFeePerGas = "";

    public JsonTransactionResponse() {}

    public String getAbi() {
        return abi;
    }

    public void setAbi(String abi) {
        this.abi = abi;
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
        return AddressUtils.addHexPrefixToAddress(this.to);
    }

    public void setTo(String to) {
        this.to = to;
    }

    public long getBlockLimit() {
        return this.blockLimit;
    }

    public void setBlockLimit(long blockLimit) {
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

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    public long getImportTime() {
        return this.importTime;
    }

    public void setImportTime(long importTime) {
        this.importTime = importTime;
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

    public long getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(long gasLimit) {
        this.gasLimit = gasLimit;
    }

    public String getMaxFeePerGas() {
        return maxFeePerGas;
    }

    public void setMaxFeePerGas(String maxFeePerGas) {
        this.maxFeePerGas = maxFeePerGas;
    }

    public String getMaxPriorityFeePerGas() {
        return maxPriorityFeePerGas;
    }

    public void setMaxPriorityFeePerGas(String maxPriorityFeePerGas) {
        this.maxPriorityFeePerGas = maxPriorityFeePerGas;
    }

    /**
     * This method is not correct, only can decode from method {@link #writeToHexString()}, which
     * not enable to send transaction to blockchain.
     *
     * @deprecated this method is deprecated, use {@link #decodeTransaction(String)} instead
     * @param hexString the hex string
     * @return the transaction response
     * @throws JniException the jni exception
     * @throws IOException the io exception
     */
    @Deprecated
    public static JsonTransactionResponse readFromHexString(String hexString)
            throws JniException, IOException {
        String jsonObj = TransactionBuilderJniObj.decodeTransactionDataToJsonObj(hexString);
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        return objectMapper.readValue(jsonObj.getBytes(), JsonTransactionResponse.class);
    }

    /**
     * This method only encode to transaction data hex string, use to sign transaction data.
     *
     * @deprecated this method is deprecated, use {@link #encodeTransactionData()} instead
     * @return the hex string
     * @throws JsonProcessingException the json processing exception
     * @throws JniException the jni exception
     */
    @Deprecated
    public String writeToHexString() throws JsonProcessingException, JniException {
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        String json = objectMapper.writeValueAsString(this);
        long transactionDataWithJson = TransactionBuilderJniObj.createTransactionDataWithJson(json);
        String encodeTransactionData =
                TransactionBuilderJniObj.encodeTransactionData(transactionDataWithJson);
        TransactionBuilderJniObj.destroyTransactionData(transactionDataWithJson);
        return encodeTransactionData;
    }

    /**
     * Calculate the hash for the transaction, only correct when blockchain node version < 3.3.0,
     *
     * @param cryptoSuite the crypto suite
     * @return tx hash hex string
     * @throws ClientException exception when calculateHash() throws ClientException
     */
    public String calculateHash(CryptoSuite cryptoSuite) throws ClientException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
            String json = objectMapper.writeValueAsString(this);
            return TransactionBuilderV2JniObj.calcTransactionDataHashWithJson(
                    cryptoSuite.cryptoTypeConfig, json);
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

    /**
     * Calculate the hash for the transaction, only correct when blockchain node version >= 3.3.0,
     *
     * @param cryptoSuiteType the crypto suite type, use for distinguish hash impl, 0 for Keccak256,
     *     1
     * @return tx hash hex string
     * @throws ClientException exception when calculateHash() throws ClientException
     */
    public String calculateHash(int cryptoSuiteType) throws ClientException {
        try {
            return TransactionBuilderV2JniObj.calcTransactionDataHashWithFullFields(
                    cryptoSuiteType,
                    (getVersion() == 0 ? TransactionVersion.V0 : TransactionVersion.V1),
                    getGroupID(),
                    getChainID(),
                    getTo(),
                    new String(Hex.decode(getNonce())),
                    Hex.decode(getInput()),
                    getAbi(),
                    getBlockLimit(),
                    getValue(),
                    getGasPrice(),
                    getGasLimit(),
                    getMaxFeePerGas(),
                    getMaxPriorityFeePerGas());
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

    /**
     * Calculate the hash for the transaction in java native, only correct when blockchain node
     * version >= 3.3.0, because the nonce output is changed in 3.3.0, you can calculateHex the
     * nonce to string
     *
     * @param hashImpl the hash implementation
     * @return tx hash hex string
     * @throws IOException exception when encodeTransactionData() throws IOException
     */
    public String calculateTxHashInNative(Hash hashImpl) throws IOException {
        byte[] hashBytes = hashImpl.hash(encodeTransactionData());
        return Hex.toHexStringWithPrefix(hashBytes);
    }

    /**
     * Encode the transaction data to byte array, witch is used to calculate the hash of the
     * transaction
     *
     * @return the byte array of the transaction data
     * @throws IOException exception when ByteArrayOutputStream throws IOException
     */
    public byte[] encodeTransactionData() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // version
        byteArrayOutputStream.write(toBytesPadded(BigInteger.valueOf(getVersion()), 4));
        // chainId
        byteArrayOutputStream.write(getChainID().getBytes());
        // groupId
        byteArrayOutputStream.write(getGroupID().getBytes());
        // blockLimit
        byteArrayOutputStream.write(toBytesPadded(BigInteger.valueOf(getBlockLimit()), 8));
        // nonce
        byteArrayOutputStream.write(Hex.decode(getNonce()));
        // to
        byteArrayOutputStream.write(getTo().getBytes());
        // input
        byteArrayOutputStream.write(Hex.decode(getInput()));
        // abi
        byteArrayOutputStream.write(getAbi().getBytes());

        if (getVersion() == TransactionVersion.V1.getValue()) {
            byteArrayOutputStream.write(getValue().getBytes());
            byteArrayOutputStream.write(getGasPrice().getBytes());
            byteArrayOutputStream.write(toBytesPadded(BigInteger.valueOf(getGasLimit()), 8));
            byteArrayOutputStream.write(getMaxFeePerGas().getBytes());
            byteArrayOutputStream.write(getMaxPriorityFeePerGas().getBytes());
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static JsonTransactionResponse decodeTransactionV1(String hexString)
            throws JniException {
        Transaction transactionV2 =
                TransactionStructBuilderJniObj.decodeTransactionStructV2(hexString);
        TransactionData transactionData = transactionV2.getTransactionData();
        JsonTransactionResponse jsonTransactionResponse = new JsonTransactionResponse();
        jsonTransactionResponse.setVersion(transactionData.getVersion());
        jsonTransactionResponse.setHash(
                Hex.toHexStringMaybeNullData(transactionV2.getDataHash(), ""));
        jsonTransactionResponse.setNonce(transactionData.getNonce());
        jsonTransactionResponse.setBlockLimit(transactionData.getBlockLimit());
        jsonTransactionResponse.setTo(transactionData.getTo());
        jsonTransactionResponse.setFrom(
                Hex.toHexStringMaybeNullData(transactionV2.getSender(), ""));
        jsonTransactionResponse.setAbi(transactionData.getAbi());
        jsonTransactionResponse.setInput(
                Hex.toHexStringMaybeNullData(transactionData.getInput(), ""));
        jsonTransactionResponse.setChainID(transactionData.getChainId());
        jsonTransactionResponse.setGroupID(transactionData.getGroupId());
        jsonTransactionResponse.setExtraData(transactionV2.getExtraData());
        jsonTransactionResponse.setSignature(
                Hex.toHexStringMaybeNullData(transactionV2.getSignature(), ""));
        jsonTransactionResponse.setImportTime(transactionV2.getImportTime());

        if (transactionData instanceof TransactionDataV2
                && transactionData.getVersion() == TransactionVersion.V1.getValue()) {
            TransactionDataV2 transactionDataV2 = (TransactionDataV2) transactionData;
            jsonTransactionResponse.setValue(transactionDataV2.getValue());
            jsonTransactionResponse.setGasPrice(transactionDataV2.getGasPrice());
            jsonTransactionResponse.setGasLimit(transactionDataV2.getGasLimit());
            jsonTransactionResponse.setMaxFeePerGas(transactionDataV2.getMaxFeePerGas());
            jsonTransactionResponse.setMaxPriorityFeePerGas(
                    transactionDataV2.getMaxPriorityFeePerGas());
        }
        return jsonTransactionResponse;
    }

    public static JsonTransactionResponse decodeTransaction(String hexString) throws JniException {
        Transaction transaction = TransactionStructBuilderJniObj.decodeTransactionStruct(hexString);
        JsonTransactionResponse jsonTransactionResponse = new JsonTransactionResponse();
        jsonTransactionResponse.setVersion(transaction.getTransactionData().getVersion());
        jsonTransactionResponse.setHash(
                Hex.toHexStringMaybeNullData(transaction.getDataHash(), ""));
        jsonTransactionResponse.setNonce(transaction.getTransactionData().getNonce());
        jsonTransactionResponse.setBlockLimit(transaction.getTransactionData().getBlockLimit());
        jsonTransactionResponse.setTo(transaction.getTransactionData().getTo());
        jsonTransactionResponse.setFrom(Hex.toHexStringMaybeNullData(transaction.getSender(), ""));
        jsonTransactionResponse.setAbi(transaction.getTransactionData().getAbi());
        jsonTransactionResponse.setInput(
                Hex.toHexStringMaybeNullData(transaction.getTransactionData().getInput(), ""));
        jsonTransactionResponse.setChainID(transaction.getTransactionData().getChainId());
        jsonTransactionResponse.setGroupID(transaction.getTransactionData().getGroupId());
        jsonTransactionResponse.setExtraData(transaction.getExtraData());
        jsonTransactionResponse.setSignature(
                Hex.toHexStringMaybeNullData(transaction.getSignature(), ""));
        jsonTransactionResponse.setImportTime(transaction.getImportTime());

        return jsonTransactionResponse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        JsonTransactionResponse that = (JsonTransactionResponse) o;
        return Objects.equals(this.version, that.version)
                && Objects.equals(this.from, that.from)
                && Objects.equals(this.hash, that.hash)
                && Objects.equals(this.txProof, that.txProof)
                && Objects.equals(this.input, that.input)
                && Objects.equals(this.nonce, that.nonce)
                && Objects.equals(this.to, that.to)
                && Objects.equals(this.abi, that.abi)
                && Objects.equals(this.blockLimit, that.blockLimit)
                && Objects.equals(this.chainID, that.chainID)
                && Objects.equals(this.groupID, that.groupID)
                && Objects.equals(this.extraData, that.extraData)
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
                this.abi,
                this.blockLimit,
                this.chainID,
                this.groupID,
                this.extraData,
                this.signature);
    }

    @Override
    public String toString() {
        return "JsonTransactionResponse{"
                + "version="
                + version
                + ", hash='"
                + hash
                + '\''
                + ", nonce='"
                + nonce
                + '\''
                + ", blockLimit="
                + blockLimit
                + ", to='"
                + to
                + '\''
                + ", from='"
                + from
                + '\''
                + ", abi='"
                + abi
                + '\''
                + ", input='"
                + input
                + '\''
                + ", chainID='"
                + chainID
                + '\''
                + ", groupID='"
                + groupID
                + '\''
                + ", extraData='"
                + extraData
                + '\''
                + ", signature='"
                + signature
                + '\''
                + ", importTime="
                + importTime
                + ", transactionProof="
                + transactionProof
                + ", txProof="
                + txProof
                + ", value='"
                + value
                + '\''
                + ", gasPrice='"
                + gasPrice
                + '\''
                + ", gasLimit="
                + gasLimit
                + ", maxFeePerGas='"
                + maxFeePerGas
                + '\''
                + ", maxPriorityFeePerGas='"
                + maxPriorityFeePerGas
                + '\''
                + '}';
    }
}
