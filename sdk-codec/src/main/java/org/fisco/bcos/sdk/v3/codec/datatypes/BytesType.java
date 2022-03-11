package org.fisco.bcos.sdk.v3.codec.datatypes;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import java.util.Arrays;
import org.fisco.bcos.sdk.v3.utils.Hex;

/** Binary sequence of bytes. */
public abstract class BytesType implements Type<byte[]> {

    private byte[] value;
    private String type;

    public BytesType(byte[] src, String type) {
        this.value = src;
        this.type = type;
    }

    @Override
    @JsonSerialize(using = bytesUsingHexSerializer.class)
    public byte[] getValue() {
        return value;
    }

    @Override
    public String getTypeAsString() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BytesType bytesType = (BytesType) o;

        if (!Arrays.equals(value, bytesType.value)) {
            return false;
        }
        return type.equals(bytesType.type);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(value);
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public int bytes32PaddedLength() {
        return value.length <= 32
                ? MAX_BYTE_LENGTH
                : (value.length / MAX_BYTE_LENGTH + 1) * MAX_BYTE_LENGTH;
    }

    public static class bytesUsingHexSerializer extends JsonSerializer<byte[]> {
        @Override
        public void serialize(byte[] value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }
            String hexString = Hex.toHexString(value);
            gen.writeString(hexString);
        }
    }
}
