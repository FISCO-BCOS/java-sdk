package org.fisco.bcos.sdk.client.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.fisco.bcos.sdk.client.RPCResponse;

public class RawResponseDeserializer extends StdDeserializer<RPCResponse>
        implements ResolvableDeserializer {
    private final JsonDeserializer<?> defaultDeserializer;

    public RawResponseDeserializer(JsonDeserializer<?> defaultDeserializer) {
        super(RPCResponse.class);
        this.defaultDeserializer = defaultDeserializer;
    }

    @Override
    public RPCResponse deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        RPCResponse deserializedResponse = (RPCResponse) defaultDeserializer.deserialize(jp, ctxt);

        deserializedResponse.setRawResponse(getRawResponse(jp));
        return deserializedResponse;
    }

    // Must implement ResolvableDeserializer when modifying BeanDeserializer
    // otherwise deserializing throws JsonMappingException
    @Override
    public void resolve(DeserializationContext ctxt) throws JsonMappingException {
        ((ResolvableDeserializer) defaultDeserializer).resolve(ctxt);
    }

    private String getRawResponse(JsonParser jp) throws IOException {
        final InputStream inputSource = (InputStream) jp.getInputSource();

        if (inputSource == null) {
            return "";
        }

        inputSource.reset();

        return streamToString(inputSource);
    }

    private String streamToString(InputStream input) throws IOException {
        return new Scanner(input, StandardCharsets.UTF_8.name()).useDelimiter("\\Z").next();
    }
}
