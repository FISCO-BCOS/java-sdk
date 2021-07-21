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
import org.fisco.bcos.sdk.client.exceptions.ClientException;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.model.JsonRpcResponse;
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

public class BcosBlockHeader extends JsonRpcResponse<BcosBlockHeader.BlockHeader> {
    private static Logger logger = LoggerFactory.getLogger(BcosBlockHeader.class);

    @Override
    public void setResult(BlockHeader result) {
        super.setResult(result);
    }

    public BlockHeader getBlockHeader() {
        return this.getResult();
    }

    public static class Signature {
        private String index;
        private String signature;

        public Signature() {
        }

        public String getIndex() {
            return this.index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getSignature() {
            return this.signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            Signature signature1 = (Signature) o;
            return Objects.equals(this.index, signature1.index)
                    && Objects.equals(this.signature, signature1.signature);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.index, this.signature);
        }

        @Override
        public String toString() {
            return "{" + "index='" + this.index + '\'' + ", signature='" + this.signature + '\'' + '}';
        }
    }

    public static class BlockHeader {
        @JsonProperty("blockNumber")
        protected String number;

        protected Integer version;

        protected String hash;
        protected String parentHash;
        protected String logsBloom;

        @JsonProperty("txsRoot")
        protected String transactionsRoot;

        protected String receiptRoot;
        protected String dbHash;
        protected String stateRoot;
        protected Integer sealer;
        protected List<String> sealerList;

        protected String extraData;
        protected String gasLimit;
        protected String gasUsed;
        protected String timestamp;
        protected List<Signature> signatureList;
        protected List<Integer> consensusWeights;

        public void setSignatureList(List<Signature> signatureList) {
            this.signatureList = signatureList;
        }

        public List<Signature> getSignatureList() {
            return this.signatureList;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public void setParentHash(String parentHash) {
            this.parentHash = parentHash;
        }

        public void setLogsBloom(String logsBloom) {
            this.logsBloom = logsBloom;
        }

        public void setTransactionsRoot(String transactionsRoot) {
            this.transactionsRoot = transactionsRoot;
        }

        public void setReceiptRoot(String receiptRoot) {
            this.receiptRoot = receiptRoot;
        }

        public void setDbHash(String dbHash) {
            this.dbHash = dbHash;
        }

        public void setStateRoot(String stateRoot) {
            this.stateRoot = stateRoot;
        }

        public void setSealer(Integer sealer) {
            this.sealer = sealer;
        }

        public void setSealerList(List<String> sealerList) {
            this.sealerList = sealerList;
        }

        public void setExtraData(String extraData) {
            this.extraData = extraData;
        }

        public void setGasLimit(String gasLimit) {
            this.gasLimit = gasLimit;
        }

        public void setGasUsed(String gasUsed) {
            this.gasUsed = gasUsed;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public BigInteger getNumber() {
            return Numeric.decodeQuantity(this.number);
        }

        public String getHash() {
            return this.hash;
        }

        public String getParentHash() {
            return this.parentHash;
        }

        public String getLogsBloom() {
            return this.logsBloom;
        }

        public String getTransactionsRoot() {
            return this.transactionsRoot;
        }

        public String getReceiptRoot() {
            return this.receiptRoot;
        }

        public String getDbHash() {
            return this.dbHash;
        }

        public String getStateRoot() {
            return this.stateRoot;
        }

        public Integer getSealer() {
            return this.sealer;
        }

        public List<String> getSealerList() {
            return this.sealerList;
        }

        public String getExtraData() {
            return this.extraData;
        }

        public String getGasLimit() {
            return this.gasLimit;
        }

        public String getGasUsed() {
            return this.gasUsed;
        }

        public String getTimestamp() {
            return this.timestamp;
        }

        private byte[] encodeBlockHeader() {
            List<RlpType> encodedRlp = new ArrayList<>();
            encodedRlp.add(RlpString.create(Numeric.hexStringToByteArray(this.parentHash)));
            encodedRlp.add(RlpString.create(Numeric.hexStringToByteArray(this.stateRoot)));
            encodedRlp.add(RlpString.create(Numeric.hexStringToByteArray(this.transactionsRoot)));
            encodedRlp.add(RlpString.create(Numeric.hexStringToByteArray(this.receiptRoot)));
            encodedRlp.add(RlpString.create(Numeric.hexStringToByteArray(this.dbHash)));
            encodedRlp.add(RlpString.create(Numeric.hexStringToByteArray(this.logsBloom)));
            encodedRlp.add(RlpString.create(Numeric.decodeQuantity(this.number)));
            encodedRlp.add(RlpString.create(Numeric.decodeQuantity(this.gasLimit)));
            encodedRlp.add(RlpString.create(Numeric.decodeQuantity(this.gasUsed)));
            encodedRlp.add(RlpString.create(Numeric.decodeQuantity(this.timestamp)));

            encodedRlp.add(RlpString.create(this.extraData));
            encodedRlp.add(RlpString.create(this.sealer));
            List<RlpType> sealerListRlp = new ArrayList<>();
            for (String sealerString : this.sealerList) {
                sealerListRlp.add(RlpString.create(Numeric.hexStringToByteArray(sealerString)));
            }
            encodedRlp.add(new RlpList(sealerListRlp));
            RlpList rlpList = new RlpList(encodedRlp);
            return RlpEncoder.encode(rlpList);
        }

        // calculate hash for the block or the block header
        public String calculateHash(CryptoSuite cryptoSuite) {
            try {
                byte[] hash = cryptoSuite.hash(this.encodeBlockHeader());
                return "0x" + Hex.toHexString(hash);
            } catch (Exception e) {
                BcosBlockHeader.logger.warn(
                        "calculateHash for the block failed, blockNumber: {}, blockHash: {}, error info: {}",
                        this.hash,
                        this.number,
                        e.getMessage());
                throw new ClientException(
                        "calculateHash for block "
                                + this.hash
                                + " failed, error info: "
                                + e.getMessage(),
                        e);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            BlockHeader that = (BlockHeader) o;
            return Objects.equals(
                    Numeric.decodeQuantity(this.number), Numeric.decodeQuantity(that.number))
                    && Objects.equals(this.hash, that.hash)
                    && Objects.equals(this.parentHash, that.parentHash)
                    && Objects.equals(this.logsBloom, that.logsBloom)
                    && Objects.equals(this.transactionsRoot, that.transactionsRoot)
                    && Objects.equals(this.receiptRoot, that.receiptRoot)
                    && Objects.equals(this.dbHash, that.dbHash)
                    && Objects.equals(this.stateRoot, that.stateRoot)
                    && Objects.equals(this.sealer, that.sealer)
                    && Objects.equals(this.sealerList, that.sealerList)
                    && Objects.equals(this.extraData, that.extraData)
                    && Objects.equals(this.gasLimit, that.gasLimit)
                    && Objects.equals(this.gasUsed, that.gasUsed)
                    && Objects.equals(this.timestamp, that.timestamp)
                    && Objects.equals(this.signatureList, that.signatureList);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    Numeric.decodeQuantity(this.number),
                    this.hash,
                    this.parentHash,
                    this.logsBloom,
                    this.transactionsRoot,
                    this.receiptRoot,
                    this.dbHash,
                    this.stateRoot,
                    this.sealer,
                    this.sealerList,
                    this.extraData,
                    this.gasLimit,
                    this.gasUsed,
                    this.timestamp,
                    this.signatureList);
        }

        @Override
        public String toString() {
            return "BlockHeader{"
                    + "number="
                    + this.number
                    + ", hash='"
                    + this.hash
                    + '\''
                    + ", parentHash='"
                    + this.parentHash
                    + '\''
                    + ", logsBloom='"
                    + this.logsBloom
                    + '\''
                    + ", transactionsRoot='"
                    + this.transactionsRoot
                    + '\''
                    + ", receiptRoot='"
                    + this.receiptRoot
                    + '\''
                    + ", dbHash='"
                    + this.dbHash
                    + '\''
                    + ", stateRoot='"
                    + this.stateRoot
                    + '\''
                    + ", sealer='"
                    + this.sealer
                    + '\''
                    + ", sealerList="
                    + this.sealerList
                    + ", extraData="
                    + this.extraData
                    + ", gasLimit='"
                    + this.gasLimit
                    + '\''
                    + ", gasUsed='"
                    + this.gasUsed
                    + '\''
                    + ", timestamp='"
                    + this.timestamp
                    + '\''
                    + ", signatureList="
                    + this.signatureList
                    + '}';
        }
    }
}
