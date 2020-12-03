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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

public class BcosBlockHeader extends JsonRpcResponse<BcosBlockHeader.BlockHeader> {
    private static Logger logger = LoggerFactory.getLogger(BcosBlockHeader.class);

    @Override
    public void setResult(BlockHeader result) {
        super.setResult(result);
    }

    public BlockHeader getBlockHeader() {
        return getResult();
    }

    public static class Signature {
        private String index;
        private String signature;

        public Signature() {}

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Signature signature1 = (Signature) o;
            return Objects.equals(index, signature1.index)
                    && Objects.equals(signature, signature1.signature);
        }

        @Override
        public int hashCode() {
            return Objects.hash(index, signature);
        }

        @Override
        public String toString() {
            return "{" + "index='" + index + '\'' + ", signature='" + signature + '\'' + '}';
        }
    }

    public static class BlockHeader {
        protected String number;
        protected String hash;
        protected String parentHash;
        protected String logsBloom;
        protected String transactionsRoot;
        protected String receiptsRoot;
        protected String dbHash;
        protected String stateRoot;
        protected String sealer;
        protected List<String> sealerList;
        protected List<String> extraData;
        protected String gasLimit;
        protected String gasUsed;
        protected String timestamp;
        protected List<Signature> signatureList;

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

        public void setReceiptsRoot(String receiptsRoot) {
            this.receiptsRoot = receiptsRoot;
        }

        public void setDbHash(String dbHash) {
            this.dbHash = dbHash;
        }

        public void setStateRoot(String stateRoot) {
            this.stateRoot = stateRoot;
        }

        public void setSealer(String sealer) {
            this.sealer = sealer;
        }

        public void setSealerList(List<String> sealerList) {
            this.sealerList = sealerList;
        }

        public void setExtraData(List<String> extraData) {
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
            return Numeric.decodeQuantity(number);
        }

        public String getHash() {
            return hash;
        }

        public String getParentHash() {
            return parentHash;
        }

        public String getLogsBloom() {
            return logsBloom;
        }

        public String getTransactionsRoot() {
            return transactionsRoot;
        }

        public String getReceiptsRoot() {
            return receiptsRoot;
        }

        public String getDbHash() {
            return dbHash;
        }

        public String getStateRoot() {
            return stateRoot;
        }

        public String getSealer() {
            return sealer;
        }

        public List<String> getSealerList() {
            return sealerList;
        }

        public List<String> getExtraData() {
            return extraData;
        }

        public String getGasLimit() {
            return gasLimit;
        }

        public String getGasUsed() {
            return gasUsed;
        }

        public String getTimestamp() {
            return timestamp;
        }

        private byte[] encodeBlockHeader() {
            List<RlpType> encodedRlp = new ArrayList<>();
            encodedRlp.add(RlpString.create(Numeric.hexStringToByteArray(parentHash)));
            encodedRlp.add(RlpString.create(Numeric.hexStringToByteArray(stateRoot)));
            encodedRlp.add(RlpString.create(Numeric.hexStringToByteArray(transactionsRoot)));
            encodedRlp.add(RlpString.create(Numeric.hexStringToByteArray(receiptsRoot)));
            encodedRlp.add(RlpString.create(Numeric.hexStringToByteArray(dbHash)));
            encodedRlp.add(RlpString.create(Numeric.hexStringToByteArray(logsBloom)));
            encodedRlp.add(RlpString.create(Numeric.decodeQuantity(number)));
            encodedRlp.add(RlpString.create(Numeric.decodeQuantity(gasLimit)));
            encodedRlp.add(RlpString.create(Numeric.decodeQuantity(gasUsed)));
            encodedRlp.add(RlpString.create(Numeric.decodeQuantity(timestamp)));

            List<RlpType> extraDataRlp = new ArrayList<>();
            for (String data : extraData) {
                extraDataRlp.add(RlpString.create(Numeric.hexStringToByteArray(data)));
            }
            encodedRlp.add(new RlpList(extraDataRlp));
            encodedRlp.add(RlpString.create(Numeric.decodeQuantity(sealer)));
            List<RlpType> sealerListRlp = new ArrayList<>();
            for (String sealerString : sealerList) {
                sealerListRlp.add(RlpString.create(Numeric.hexStringToByteArray(sealerString)));
            }
            encodedRlp.add(new RlpList(sealerListRlp));
            RlpList rlpList = new RlpList(encodedRlp);
            return RlpEncoder.encode(rlpList);
        }

        // calculate hash for the block or the block header
        public String calculateHash(CryptoSuite cryptoSuite) {
            try {
                byte[] hash = cryptoSuite.hash(encodeBlockHeader());
                return "0x" + Hex.toHexString(hash);
            } catch (Exception e) {
                logger.warn(
                        "calculateHash for the block failed, blockNumber: {}, blockHash: {}, error info: {}",
                        hash,
                        number,
                        e.getMessage());
                throw new ClientException(
                        "calculateHash for block "
                                + hash
                                + " failed, error info: "
                                + e.getMessage(),
                        e);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BlockHeader that = (BlockHeader) o;
            return Objects.equals(
                            Numeric.decodeQuantity(number), Numeric.decodeQuantity(that.number))
                    && Objects.equals(hash, that.hash)
                    && Objects.equals(parentHash, that.parentHash)
                    && Objects.equals(logsBloom, that.logsBloom)
                    && Objects.equals(transactionsRoot, that.transactionsRoot)
                    && Objects.equals(receiptsRoot, that.receiptsRoot)
                    && Objects.equals(dbHash, that.dbHash)
                    && Objects.equals(stateRoot, that.stateRoot)
                    && Objects.equals(sealer, that.sealer)
                    && Objects.equals(sealerList, that.sealerList)
                    && Objects.equals(extraData, that.extraData)
                    && Objects.equals(gasLimit, that.gasLimit)
                    && Objects.equals(gasUsed, that.gasUsed)
                    && Objects.equals(timestamp, that.timestamp)
                    && Objects.equals(signatureList, that.signatureList);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    Numeric.decodeQuantity(number),
                    hash,
                    parentHash,
                    logsBloom,
                    transactionsRoot,
                    receiptsRoot,
                    dbHash,
                    stateRoot,
                    sealer,
                    sealerList,
                    extraData,
                    gasLimit,
                    gasUsed,
                    timestamp,
                    signatureList);
        }

        @Override
        public String toString() {
            return "BlockHeader{"
                    + "number="
                    + number
                    + ", hash='"
                    + hash
                    + '\''
                    + ", parentHash='"
                    + parentHash
                    + '\''
                    + ", logsBloom='"
                    + logsBloom
                    + '\''
                    + ", transactionsRoot='"
                    + transactionsRoot
                    + '\''
                    + ", receiptsRoot='"
                    + receiptsRoot
                    + '\''
                    + ", dbHash='"
                    + dbHash
                    + '\''
                    + ", stateRoot='"
                    + stateRoot
                    + '\''
                    + ", sealer='"
                    + sealer
                    + '\''
                    + ", sealerList="
                    + sealerList
                    + ", extraData="
                    + extraData
                    + ", gasLimit='"
                    + gasLimit
                    + '\''
                    + ", gasUsed='"
                    + gasUsed
                    + '\''
                    + ", timestamp='"
                    + timestamp
                    + '\''
                    + ", signatureList="
                    + signatureList
                    + '}';
        }
    }
}
