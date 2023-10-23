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

package org.fisco.bcos.sdk.v3.client.protocol.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.v3.client.protocol.model.JsonTransactionResponse;
import org.fisco.bcos.sdk.v3.model.JsonRpcResponse;
import org.fisco.bcos.sdk.v3.utils.ObjectMapperFactory;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BcosBlock extends JsonRpcResponse<BcosBlock.Block> {

    @Override
    @JsonDeserialize(using = BlockDeserializer.class)
    public void setResult(Block result) {
        super.setResult(result);
    }

    public Block getBlock() {
        return this.getResult();
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

        @Override
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
        @Override
        public JsonTransactionResponse get() {
            return this;
        }
    }

    public static class Block extends BcosBlockHeader.BlockHeader {
        private List<TransactionResult> transactions;

        @JsonSerialize(using = TransactionResultSerializer.class)
        public List<TransactionResult> getTransactions() {
            return transactions;
        }

        @JsonIgnore
        public List<TransactionHash> getTransactionHashes() {
            if (!transactions.isEmpty() && transactions.get(0) instanceof TransactionHash) {
                return transactions.stream()
                        .map(TransactionHash.class::cast)
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        }

        @JsonIgnore
        public List<TransactionObject> getTransactionObject() {
            if (!transactions.isEmpty() && transactions.get(0) instanceof TransactionObject) {
                return transactions.stream()
                        .map(TransactionObject.class::cast)
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        }

        @JsonDeserialize(using = TransactionResultDeserializer.class)
        public void setTransactions(List<TransactionResult> transactions) {
            this.transactions = transactions;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            Block block = (Block) o;
            return Objects.equals(this.transactions, block.transactions);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), this.transactions);
        }

        @Override
        public String toString() {
            return "{"
                    + "transactions="
                    + this.transactions
                    + ", number='"
                    + this.number
                    + '\''
                    + ", hash='"
                    + this.hash
                    + '\''
                    + ", logsBloom='"
                    + this.logsBloom
                    + '\''
                    + ", transactionsRoot='"
                    + this.transactionsRoot
                    + '\''
                    + ", receiptRoot='"
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

    // decode the block
    public static class BlockDeserializer extends JsonDeserializer<Block> {
        private final ObjectReader objectReader = ObjectMapperFactory.getObjectReader();

        @Override
        public Block deserialize(
                JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            if (jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
                return this.objectReader.readValue(jsonParser, Block.class);
            } else {
                return null; // null is wrapped by Optional in above getter
            }
        }
    }

    public static class TransactionResultSerializer
            extends JsonSerializer<List<TransactionResult>> {

        @Override
        public void serialize(
                List<TransactionResult> value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }
            if (value.isEmpty()) {
                gen.writeStartArray();
                gen.writeEndArray();
                return;
            }
            if (value.get(0) instanceof TransactionHash) {
                List<String> txHashList = new ArrayList<>();
                for (TransactionResult transactionResult : value) {
                    txHashList.add(((TransactionHash) transactionResult).get());
                }
                gen.writeObject(txHashList);
            } else if (value.get(0) instanceof TransactionObject) {
                List<JsonTransactionResponse> transactionObjectList = new ArrayList<>();
                for (TransactionResult transactionResult : value) {
                    transactionObjectList.add(((TransactionObject) transactionResult).get());
                }
                gen.writeObject(transactionObjectList);
            }
        }
    }

    // decode transactionResult
    public static class TransactionResultDeserializer
            extends JsonDeserializer<List<TransactionResult>> {

        private final ObjectReader objectReader = ObjectMapperFactory.getObjectReader();

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
}
