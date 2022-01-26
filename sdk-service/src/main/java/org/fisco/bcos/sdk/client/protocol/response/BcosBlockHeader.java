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
import com.qq.tars.protocol.tars.TarsOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.fisco.bcos.sdk.client.exceptions.ClientException;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.model.JsonRpcResponse;
import org.fisco.bcos.sdk.utils.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BcosBlockHeader extends JsonRpcResponse<BcosBlockHeader.BlockHeader> {
    private static final Logger logger = LoggerFactory.getLogger(BcosBlockHeader.class);

    @Override
    public void setResult(BlockHeader result) {
        super.setResult(result);
    }

    public BlockHeader getBlockHeader() {
        return this.getResult();
    }

    public static class Signature {
        @JsonProperty("sealerIndex")
        private Integer index;

        private String signature;

        public Signature() {}

        public Integer getIndex() {
            return this.index;
        }

        public void setIndex(Integer index) {
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
            return "{"
                    + "index='"
                    + this.index
                    + '\''
                    + ", signature='"
                    + this.signature
                    + '\''
                    + '}';
        }
    }

    public static class ParentInfo {
        private long blockNumber;

        private String blockHash;

        public ParentInfo() {}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            ParentInfo signature1 = (ParentInfo) o;
            return Objects.equals(this.blockNumber, signature1.blockNumber)
                    && Objects.equals(this.blockHash, signature1.blockHash);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.blockNumber, this.blockHash);
        }

        @Override
        public String toString() {
            return "{"
                    + "blockHash='"
                    + this.blockHash
                    + '\''
                    + ", blockNumber='"
                    + this.blockNumber
                    + '\''
                    + '}';
        }

        public long getBlockNumber() {
            return this.blockNumber;
        }

        public void setBlockNumber(long blockNumber) {
            this.blockNumber = blockNumber;
        }

        public String getBlockHash() {
            return this.blockHash;
        }

        public void setBlockHash(String blockHash) {
            this.blockHash = blockHash;
        }
    }

    public static class BlockHeader {
        protected long number;
        protected int version;
        protected String hash;
        protected String logsBloom;

        @JsonProperty("txsRoot")
        protected String transactionsRoot;

        protected String receiptsRoot;
        protected String stateRoot;
        protected int sealer;
        protected List<String> sealerList;
        protected String extraData;
        protected String gasUsed;
        protected long timestamp;
        protected List<ParentInfo> parentInfo;
        protected List<Signature> signatureList;
        protected List<Long> consensusWeights;

        public void setSignatureList(List<Signature> signatureList) {
            this.signatureList = signatureList;
        }

        public void setParentInfo(List<ParentInfo> parentInfo) {
            this.parentInfo = parentInfo;
        }

        public void setConsensusWeights(List<Long> consensusWeights) {
            this.consensusWeights = consensusWeights;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public List<Signature> getSignatureList() {
            return this.signatureList;
        }

        public void setNumber(long number) {
            this.number = number;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public void setLogsBloom(String logsBloom) {
            this.logsBloom = logsBloom;
        }

        public void setTransactionsRoot(String transactionsRoot) {
            this.transactionsRoot = transactionsRoot;
        }

        public void setReceiptsRoot(String receiptsRoot) {
            this.receiptsRoot = receiptsRoot;
        }

        public void setStateRoot(String stateRoot) {
            this.stateRoot = stateRoot;
        }

        public void setSealer(int sealer) {
            this.sealer = sealer;
        }

        public void setSealerList(List<String> sealerList) {
            this.sealerList = sealerList;
        }

        public void setExtraData(String extraData) {
            this.extraData = extraData;
        }

        public void setGasUsed(String gasUsed) {
            this.gasUsed = gasUsed;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public long getNumber() {
            return this.number;
        }

        public String getHash() {
            return this.hash;
        }

        public String getLogsBloom() {
            return this.logsBloom;
        }

        public String getTransactionsRoot() {
            return this.transactionsRoot;
        }

        public String getReceiptsRoot() {
            return this.receiptsRoot;
        }

        public String getStateRoot() {
            return this.stateRoot;
        }

        public List<ParentInfo> getParentInfo() {
            return parentInfo;
        }

        public List<Long> getConsensusWeights() {
            return consensusWeights;
        }

        public int getVersion() {
            return version;
        }

        public int getSealer() {
            return this.sealer;
        }

        public List<String> getSealerList() {
            return this.sealerList;
        }

        public String getExtraData() {
            return this.extraData;
        }

        public String getGasUsed() {
            return this.gasUsed;
        }

        public long getTimestamp() {
            return this.timestamp;
        }

        // calculate hash for the block or the block header
        public String calculateHash(CryptoSuite cryptoSuite) {
            try {
                List<byte[]> sealerList = new ArrayList<>();
                for (String sealer : this.sealerList) {
                    sealerList.add(Hex.decode(sealer));
                }
                List<org.fisco.bcos.sdk.client.protocol.model.tars.ParentInfo> parentInfoList =
                        new ArrayList<>();
                for (ParentInfo parentInfo : this.parentInfo) {
                    parentInfoList.add(
                            new org.fisco.bcos.sdk.client.protocol.model.tars.ParentInfo(
                                    parentInfo.getBlockNumber(),
                                    Hex.decode(parentInfo.getBlockHash())));
                }
                org.fisco.bcos.sdk.client.protocol.model.tars.BlockHeader blockHeader =
                        new org.fisco.bcos.sdk.client.protocol.model.tars.BlockHeader(
                                0,
                                parentInfoList,
                                Hex.decode(this.transactionsRoot),
                                Hex.decode(this.receiptsRoot),
                                Hex.decode(this.stateRoot),
                                this.number,
                                this.gasUsed,
                                this.timestamp,
                                this.sealer,
                                sealerList,
                                Hex.decode(this.extraData),
                                null,
                                this.consensusWeights);
                TarsOutputStream tarsOutputStream = new TarsOutputStream();
                blockHeader.writeTo(tarsOutputStream);
                return Hex.toHexStringWithPrefix(cryptoSuite.hash(tarsOutputStream.toByteArray()));
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
            return Objects.equals(this.number, that.number)
                    && Objects.equals(this.hash, that.hash)
                    && Objects.equals(this.logsBloom, that.logsBloom)
                    && Objects.equals(this.transactionsRoot, that.transactionsRoot)
                    && Objects.equals(this.receiptsRoot, that.receiptsRoot)
                    && Objects.equals(this.stateRoot, that.stateRoot)
                    && Objects.equals(this.sealer, that.sealer)
                    && Objects.equals(this.sealerList, that.sealerList)
                    && Objects.equals(this.extraData, that.extraData)
                    && Objects.equals(this.gasUsed, that.gasUsed)
                    && Objects.equals(this.timestamp, that.timestamp)
                    && Objects.equals(this.signatureList, that.signatureList);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    this.number,
                    this.hash,
                    this.logsBloom,
                    this.transactionsRoot,
                    this.receiptsRoot,
                    this.stateRoot,
                    this.sealer,
                    this.sealerList,
                    this.extraData,
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
                    + ", logsBloom='"
                    + this.logsBloom
                    + '\''
                    + ", transactionsRoot='"
                    + this.transactionsRoot
                    + '\''
                    + ", receiptsRoot='"
                    + this.receiptsRoot
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
