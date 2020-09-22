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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.fisco.bcos.sdk.client.protocol.model.JsonTransactionResponse;
import org.fisco.bcos.sdk.model.JsonRpcResponse;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BcosBlock extends JsonRpcResponse<BcosBlock.Block> {
    @Override
    @JsonDeserialize(using = BcosBlock.BlockDeserialiser.class)
    public void setResult(Block result) {
        super.setResult(result);
    }

    public Block getBlock() {
        return getResult();
    }

    public interface TransactionResult<T> {
        T get();
    }

    public static class TransactionHash implements TransactionResult<String> {
        private String value;

        public TransactionHash() {}

        public TransactionHash(String value) {
            this.value = value;
        }

        public String get() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TransactionHash that = (TransactionHash) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "TransactionHash{" + "value='" + value + '\'' + '}';
        }
    }

    public static class TransactionObject extends JsonTransactionResponse
            implements TransactionResult<JsonTransactionResponse> {
        public JsonTransactionResponse get() {
            return this;
        }
    }

    public static class Block extends BcosBlockHeader.BlockHeader {
        private List<TransactionResult> transactions;

        public List<TransactionResult> getTransactions() {
            return transactions;
        }

        @JsonDeserialize(using = TransactionResultDeserialiser.class)
        public void setTransactions(List<TransactionResult> transactions) {
            this.transactions = transactions;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            Block block = (Block) o;
            return Objects.equals(transactions, block.transactions);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), transactions);
        }

        @Override
        public String toString() {
            return "Block{"
                    + "transactions="
                    + transactions
                    + ", number='"
                    + number
                    + '\''
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

    // decode transactionResult
    public static class TransactionResultDeserialiser
            extends JsonDeserializer<List<TransactionResult>> {

        private ObjectReader objectReader = ObjectMapperFactory.getObjectReader();

        @Override
        public List<TransactionResult> deserialize(
                JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            List<TransactionResult> transactionResults = new ArrayList<>();
            JsonToken nextToken = jsonParser.nextToken();

            if (nextToken == JsonToken.START_OBJECT) {
                Iterator<TransactionObject> transactionObjectIterator =
                        objectReader.readValues(jsonParser, TransactionObject.class);
                while (transactionObjectIterator.hasNext()) {
                    transactionResults.add(transactionObjectIterator.next());
                }
            } else if (nextToken == JsonToken.VALUE_STRING) {
                jsonParser.getValueAsString();

                Iterator<TransactionHash> transactionHashIterator =
                        objectReader.readValues(jsonParser, TransactionHash.class);
                while (transactionHashIterator.hasNext()) {
                    transactionResults.add(transactionHashIterator.next());
                }
            }
            return transactionResults;
        }
    }

    // decode the block
    public static class BlockDeserialiser extends JsonDeserializer<Block> {
        private ObjectReader objectReader = ObjectMapperFactory.getObjectReader();

        @Override
        public Block deserialize(
                JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            if (jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
                return objectReader.readValue(jsonParser, Block.class);
            } else {
                return null; // null is wrapped by Optional in above getter
            }
        }
    }
}
