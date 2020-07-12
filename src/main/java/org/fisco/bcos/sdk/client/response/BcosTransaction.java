package org.fisco.bcos.sdk.client.response;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import java.util.Optional;
import org.fisco.bcos.sdk.client.ObjectMapperFactory;
import org.fisco.bcos.sdk.client.RPCResponse;

/**
 * Transaction object returned by:
 *
 * <ul>
 *   <li>eth_getTransactionByHash
 *   <li>eth_getTransactionByBlockHashAndIndex
 *   <li>eth_getTransactionByBlockNumberAndIndex
 * </ul>
 *
 * <p>This differs slightly from the request {@link SendTransaction} Transaction object.
 *
 * <p>See <a href="https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_gettransactionbyhash">docs</a>
 * for further details.
 */
public class BcosTransaction extends RPCResponse<Transaction> {

    public Optional<Transaction> getTransaction() {
        return Optional.ofNullable(getResult());
    }

    public static class ResponseDeserialiser extends JsonDeserializer<Transaction> {

        private ObjectReader objectReader = ObjectMapperFactory.getObjectReader();

        @Override
        public Transaction deserialize(
                JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            if (jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
                return objectReader.readValue(jsonParser, Transaction.class);
            } else {
                return null; // null is wrapped by Optional in above getter
            }
        }
    }
}
