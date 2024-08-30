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
import org.fisco.bcos.sdk.v3.model.JsonRpcResponse;
import org.fisco.bcos.sdk.v3.utils.ObjectMapperFactory;

public class LogWrapper extends JsonRpcResponse<List<LogWrapper.LogResult>> {

    @Override
    @JsonDeserialize(using = LogResultDeserializer.class)
    public void setResult(List<LogResult> result) {
        super.setResult(result);
    }

    public List<LogResult> getLogs() {
        return getResult();
    }

    public interface LogResult<T> {
        T get();
    }

    public static class LogObject extends Log implements LogResult<Log> {

        public LogObject() {}

        public LogObject(
                boolean removed,
                String logIndex,
                String transactionIndex,
                String transactionHash,
                String blockHash,
                String blockNumber,
                String address,
                String data,
                String type,
                List<String> topics) {
            super(
                    removed,
                    logIndex,
                    transactionIndex,
                    transactionHash,
                    blockHash,
                    blockNumber,
                    address,
                    data,
                    type,
                    topics);
        }

        @Override
        public Log get() {
            return this;
        }
    }

    public static class Hash implements LogResult<String> {
        private String value;

        public Hash() {}

        public Hash(String value) {
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
            if (this == o) {
                return true;
            }
            if (!(o instanceof Hash)) {
                return false;
            }

            Hash hash = (Hash) o;

            return value != null ? value.equals(hash.value) : hash.value == null;
        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }
    }

    public static class LogResultDeserializer extends JsonDeserializer<List<LogResult>> {

        private ObjectReader objectReader = ObjectMapperFactory.getObjectReader();

        @Override
        public List<LogResult> deserialize(
                JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {

            List<LogResult> logResults = new ArrayList<>();
            JsonToken nextToken = jsonParser.nextToken();

            if (nextToken == JsonToken.START_OBJECT) {
                Iterator<LogObject> logObjectIterator =
                        objectReader.readValues(jsonParser, LogObject.class);
                while (logObjectIterator.hasNext()) {
                    logResults.add(logObjectIterator.next());
                }
            } else if (nextToken == JsonToken.VALUE_STRING) {
                jsonParser.getValueAsString();

                Iterator<Hash> transactionHashIterator =
                        objectReader.readValues(jsonParser, Hash.class);
                while (transactionHashIterator.hasNext()) {
                    logResults.add(transactionHashIterator.next());
                }
            }
            return logResults;
        }
    }
}
