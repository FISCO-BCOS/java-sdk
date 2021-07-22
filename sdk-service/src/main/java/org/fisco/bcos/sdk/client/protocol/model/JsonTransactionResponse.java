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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JsonTransactionResponse {
    private static Logger logger = LoggerFactory.getLogger(JsonTransactionResponse.class);

    // the fields related to get-transaction
    private Integer version;
    private String from;
    private String hash;
    private String input;
    private String nonce;
    private String to;
    private Integer blockLimit;
    private String chainId;
    private String groupId;
    private SignatureResponse signature;

    public JsonTransactionResponse() {
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public static class SignatureResponse {
        private String r;
        private String s;
        private String v;
        private String signature;

        public SignatureResponse() {
        }

        public String getR() {
            return this.r;
        }

        public void setR(String r) {
            this.r = r;
        }

        public String getS() {
            return this.s;
        }

        public void setS(String s) {
            this.s = s;
        }

        public String getV() {
            return this.v;
        }

        public void setV(String v) {
            this.v = v;
        }

        public String getSignature() {
            return this.signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        @Override
        public String toString() {
            return "{"
                    + "r='"
                    + this.r
                    + '\''
                    + ", s='"
                    + this.s
                    + '\''
                    + ", v='"
                    + this.v
                    + '\''
                    + ", signature='"
                    + this.signature
                    + '\''
                    + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            SignatureResponse that = (SignatureResponse) o;
            return Objects.equals(this.r, that.r)
                    && Objects.equals(this.s, that.s)
                    && Objects.equals(this.v, that.v)
                    && Objects.equals(this.signature, that.signature);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.r, this.s, this.v, this.signature);
        }
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

    public Integer getBlockLimit() {
        return this.blockLimit;
    }

    public void setBlockLimit(Integer blockLimit) {
        this.blockLimit = blockLimit;
    }

    public String getChainId() {
        return this.chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public SignatureResponse getSignature() {
        return this.signature;
    }

    public void setSignature(SignatureResponse signature) {
        this.signature = signature;
    }

    private List<RlpType> encodeTransactionResponse(CryptoSuite cryptoSuite)
            throws ClientException {
        if (this.blockLimit == null
                || this.chainId == null
                || this.groupId == null
                || this.signature == null) {
            throw new ClientException(
                    "calculate hash for the transaction failed for missing fields! Please make sure FISCO BCOS version >= v2.7.0");
        }
        List<RlpType> result = new ArrayList<>();
        // nonce
        result.add(RlpString.create(Numeric.decodeQuantity(this.nonce)));
        // blockLimit
        result.add(RlpString.create(this.blockLimit));
        // to
        BigInteger receiveAddressValue = Numeric.decodeQuantity(this.to);
        if (receiveAddressValue.equals(BigInteger.ZERO)) {
            result.add(RlpString.create(""));
        } else {
            result.add(RlpString.create(Numeric.hexStringToByteArray(this.to)));
        }
        // input
        result.add(RlpString.create(Numeric.hexStringToByteArray(this.input)));
        // chainId
        result.add(RlpString.create(Numeric.decodeQuantity(this.chainId)));
        // groupId
        result.add(RlpString.create(Numeric.decodeQuantity(this.groupId)));

        int startIndex = 0;
        if (this.signature.getSignature().startsWith("0x")) {
            startIndex = 2;
        }
        // signature
        SignatureResult signatureResult;
        if (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE) {
            signatureResult =
                    new ECDSASignatureResult(this.signature.getSignature().substring(startIndex));
        } else {
            signatureResult =
                    new SM2SignatureResult(
                            this.signature.getV(), this.signature.getSignature().substring(startIndex));
        }
        result.addAll(signatureResult.encode());
        return result;
    }

    // calculate the hash for the transaction
    public String calculateHash(CryptoSuite cryptoSuite) throws ClientException {
        try {
            List<RlpType> encodedTransaction = this.encodeTransactionResponse(cryptoSuite);
            RlpList rlpList = new RlpList(encodedTransaction);
            return "0x" + Hex.toHexString(cryptoSuite.hash(RlpEncoder.encode(rlpList)));
        } catch (Exception e) {
            logger.warn(
                    "calculate hash for the transaction failed, transactionHash: {}, error info: {}",
                    this.hash,
                    e);
            throw new ClientException(
                    "calculate hash for transaction " + this.hash + " failed for " + e.getMessage(), e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        JsonTransactionResponse that = (JsonTransactionResponse) o;
        return Objects.equals(this.from, that.from)
                && Objects.equals(this.hash, that.hash)
                && Objects.equals(this.input, that.input)
                && Objects.equals(this.nonce, that.nonce)
                && Objects.equals(this.to, that.to)
                && Objects.equals(this.blockLimit, that.blockLimit)
                && Objects.equals(this.chainId, that.chainId)
                && Objects.equals(this.groupId, that.groupId)
                && Objects.equals(this.signature, that.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.from,
                this.hash,
                this.input,
                this.nonce,
                this.to,
                this.blockLimit,
                this.chainId,
                this.groupId,
                this.signature);
    }

    @Override
    public String toString() {
        return "{"
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
                + this.chainId
                + '\''
                + ", groupId='"
                + this.groupId
                + '\''
                + ", signature="
                + this.signature
                + '}';
    }
}
