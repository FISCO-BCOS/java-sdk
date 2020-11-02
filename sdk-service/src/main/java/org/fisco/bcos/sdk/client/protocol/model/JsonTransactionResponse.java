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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.fisco.bcos.sdk.client.exceptions.ClientException;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.signature.ECDSASignatureResult;
import org.fisco.bcos.sdk.crypto.signature.SM2SignatureResult;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.rlp.RlpEncoder;
import org.fisco.bcos.sdk.rlp.RlpList;
import org.fisco.bcos.sdk.rlp.RlpString;
import org.fisco.bcos.sdk.rlp.RlpType;
import org.fisco.bcos.sdk.utils.Hex;
import org.fisco.bcos.sdk.utils.Numeric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonTransactionResponse {
    private static Logger logger = LoggerFactory.getLogger(JsonTransactionResponse.class);

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
    private String gasPrice;
    private String blockLimit;
    private String chainId;
    private String groupId;
    private String extraData;
    private SignatureResponse signature;

    public JsonTransactionResponse() {}

    public static class SignatureResponse {
        private String r;
        private String s;
        private String v;
        private String signature;

        public SignatureResponse() {}

        public String getR() {
            return r;
        }

        public void setR(String r) {
            this.r = r;
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        public String getV() {
            return v;
        }

        public void setV(String v) {
            this.v = v;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        @Override
        public String toString() {
            return "{"
                    + "r='"
                    + r
                    + '\''
                    + ", s='"
                    + s
                    + '\''
                    + ", v='"
                    + v
                    + '\''
                    + ", signature='"
                    + signature
                    + '\''
                    + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SignatureResponse that = (SignatureResponse) o;
            return Objects.equals(r, that.r)
                    && Objects.equals(s, that.s)
                    && Objects.equals(v, that.v)
                    && Objects.equals(signature, that.signature);
        }

        @Override
        public int hashCode() {
            return Objects.hash(r, s, v, signature);
        }
    }

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

    public String getBlockLimit() {
        return blockLimit;
    }

    public void setBlockLimit(String blockLimit) {
        this.blockLimit = blockLimit;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    public SignatureResponse getSignature() {
        return signature;
    }

    public void setSignature(SignatureResponse signature) {
        this.signature = signature;
    }

    private List<RlpType> encodeTransactionResponse(CryptoSuite cryptoSuite)
            throws ClientException {
        if (blockLimit == null
                || chainId == null
                || groupId == null
                || extraData == null
                || signature == null) {
            throw new ClientException(
                    "calculate hash for the transaction failed for missing fields! Please make sure FISCO BCOS version >= v2.7.0");
        }
        List<RlpType> result = new ArrayList<>();
        // nonce
        result.add(RlpString.create(Numeric.decodeQuantity(nonce)));
        // gasPrice
        result.add(RlpString.create(Numeric.decodeQuantity(gasPrice)));
        // gas
        result.add(RlpString.create(Numeric.decodeQuantity(gas)));
        // blockLimit
        result.add(RlpString.create(Numeric.decodeQuantity(blockLimit)));
        // to
        result.add(RlpString.create(Numeric.hexStringToByteArray(to)));
        // value
        result.add(RlpString.create(Numeric.decodeQuantity(value)));
        // input
        result.add(RlpString.create(Numeric.hexStringToByteArray(input)));
        // chainId
        result.add(RlpString.create(Numeric.decodeQuantity(chainId)));
        // groupId
        result.add(RlpString.create(Numeric.decodeQuantity(groupId)));
        // extraData
        if (extraData.equals("0x")) {
            result.add(RlpString.create(""));
        } else {
            result.add(RlpString.create(Numeric.hexStringToByteArray(extraData)));
        }
        int startIndex = 0;
        if (signature.getSignature().startsWith("0x")) {
            startIndex = 2;
        }
        // signature
        SignatureResult signatureResult;
        if (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE) {
            signatureResult =
                    new ECDSASignatureResult(signature.getSignature().substring(startIndex));
        } else {
            signatureResult =
                    new SM2SignatureResult(
                            signature.getV(), signature.getSignature().substring(startIndex));
        }
        result.addAll(signatureResult.encode());
        return result;
    }

    // calculate the hash for the transaction
    public String calculateHash(CryptoSuite cryptoSuite) throws ClientException {
        try {
            List<RlpType> encodedTransaction = encodeTransactionResponse(cryptoSuite);
            RlpList rlpList = new RlpList(encodedTransaction);
            return "0x" + Hex.toHexString(cryptoSuite.hash(RlpEncoder.encode(rlpList)));
        } catch (Exception e) {
            logger.warn(
                    "calculate hash for the transaction failed, blockHash: {}, blockNumber: {}, transactionHash: {}, error info: {}",
                    blockHash,
                    blockNumber,
                    hash,
                    e);
            throw new ClientException(
                    "calculate hash for transaction " + hash + " failed for " + e.getMessage(), e);
        }
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
                && Objects.equals(gasPrice, that.gasPrice)
                && Objects.equals(blockLimit, that.blockLimit)
                && Objects.equals(chainId, that.chainId)
                && Objects.equals(groupId, that.groupId)
                && Objects.equals(extraData, that.extraData)
                && Objects.equals(signature, that.signature);
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
                gasPrice,
                blockLimit,
                chainId,
                groupId,
                extraData,
                signature);
    }

    @Override
    public String toString() {
        return "{"
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
                + ", blockLimit='"
                + blockLimit
                + '\''
                + ", chainId='"
                + chainId
                + '\''
                + ", groupId='"
                + groupId
                + '\''
                + ", extraData='"
                + extraData
                + '\''
                + ", signature="
                + signature
                + '}';
    }
}
