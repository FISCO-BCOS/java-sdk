package org.fisco.bcos.sdk.client.protocol.response;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import java.util.Optional;
import org.fisco.bcos.sdk.client.protocol.model.JsonTransactionResponse;
import org.fisco.bcos.sdk.model.JsonRpcResponse;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;

public class BcosTransaction extends JsonRpcResponse<JsonTransactionResponse> {
    public Optional<JsonTransactionResponse> getTransaction() {
        return Optional.ofNullable(getResult());
    }

    public static class ResponseDeserialiser extends JsonDeserializer<JsonTransactionResponse> {
        private ObjectReader objectReader = ObjectMapperFactory.getObjectReader();

        @Override
        public JsonTransactionResponse deserialize(
                JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            if (jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
                return objectReader.readValue(jsonParser, JsonTransactionResponse.class);
            } else {
                return null; // null is wrapped by Optional in above getter
            }
        }
    }
}
