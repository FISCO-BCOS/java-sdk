package org.fisco.bcos.sdk.v3.codec.wrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.NumericType;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int128;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int16;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int32;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int64;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint128;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint16;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint32;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint64;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObject.ListType;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.fisco.bcos.sdk.v3.utils.Numeric;
import org.fisco.bcos.sdk.v3.utils.ObjectMapperFactory;
import org.fisco.bcos.sdk.v3.utils.exceptions.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContractCodecJsonWrapper {
    public static final String HexEncodedDataPrefix = "hex://";

    private static final Logger logger = LoggerFactory.getLogger(ContractCodecJsonWrapper.class);

    private final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    private void errorReport(String path, String expected, String actual)
            throws InvalidParameterException {
        String errorMessage =
                "Arguments mismatch: " + path + ", expected: " + expected + ", actual: " + actual;
        logger.error(errorMessage);
        throw new InvalidParameterException(errorMessage);
    }

    private void errorReport(String errorMessage) {
        logger.error(errorMessage);
        throw new InvalidParameterException(errorMessage);
    }

    private void errorReport(String path, String expected, String actual, String exceptionReason)
            throws InvalidParameterException {
        String errorMessage =
                "Arguments mismatch: "
                        + path
                        + ", expected: "
                        + expected
                        + ", actual: "
                        + actual
                        + ", exception reason:"
                        + exceptionReason;
        logger.error(errorMessage);
        throw new InvalidParameterException(errorMessage);
    }

    private ABIObject encodeNode(String path, ABIObject template, JsonNode node) {
        ABIObject abiObject = template.newObject();

        switch (abiObject.getType()) {
            case VALUE:
                {
                    if (!node.isValueNode()) {
                        errorReport(
                                path,
                                abiObject.getType().toString(),
                                node.getNodeType().toString());
                    }

                    switch (template.getValueType()) {
                        case BOOL:
                            {
                                if (!node.isBoolean()) {
                                    errorReport(
                                            path,
                                            template.getValueType().toString(),
                                            node.getNodeType().toString());
                                }

                                abiObject.setBoolValue(new Bool(node.asBoolean()));
                                break;
                            }
                        case INT:
                            {
                                if (!node.isNumber() && !node.isBigInteger()) {
                                    errorReport(
                                            path,
                                            template.getValueType().toString(),
                                            node.getNodeType().toString());
                                }
                                BigInteger value;
                                if (node.isNumber()) {
                                    value = BigInteger.valueOf(node.asLong());
                                } else {
                                    value = node.bigIntegerValue();
                                }

                                NumericType numericType;
                                if (abiObject.getBytesLength() == 8) {
                                    numericType = new Int8(value);
                                } else if (abiObject.getBytesLength() == 16) {
                                    numericType = new Int16(value);
                                } else if (abiObject.getBytesLength() == 32) {
                                    numericType = new Int32(value);
                                } else if (abiObject.getBytesLength() == 64) {
                                    numericType = new Int64(value);
                                } else if (abiObject.getBytesLength() == 128) {
                                    numericType = new Int128(value);
                                } else {
                                    numericType = new Int256(value);
                                }
                                abiObject.setNumericValue(numericType);

                                break;
                            }
                        case UINT:
                            {
                                if (!node.isNumber() && !node.isBigInteger()) {
                                    errorReport(
                                            path,
                                            template.getValueType().toString(),
                                            node.getNodeType().toString());
                                }
                                BigInteger value;
                                if (node.isNumber()) {
                                    value = BigInteger.valueOf(node.asLong());
                                } else {
                                    value = node.bigIntegerValue();
                                }
                                NumericType numericType;
                                if (abiObject.getBytesLength() == 8) {
                                    numericType = new Uint8(value);
                                } else if (abiObject.getBytesLength() == 16) {
                                    numericType = new Uint16(value);
                                } else if (abiObject.getBytesLength() == 32) {
                                    numericType = new Uint32(value);
                                } else if (abiObject.getBytesLength() == 64) {
                                    numericType = new Uint64(value);
                                } else if (abiObject.getBytesLength() == 128) {
                                    numericType = new Uint128(value);
                                } else {
                                    numericType = new Uint256(value);
                                }
                                abiObject.setNumericValue(numericType);

                                break;
                            }
                        case ADDRESS:
                            {
                                if (!node.isTextual()) {
                                    errorReport(
                                            path,
                                            template.getValueType().toString(),
                                            node.getNodeType().toString());
                                }

                                try {
                                    abiObject.setAddressValue(new Address(node.asText()));
                                } catch (Exception e) {
                                    errorReport(
                                            "Invalid address value",
                                            template.getValueType().toString(),
                                            node.asText());
                                }
                                break;
                            }
                        case BYTES:
                            {
                                if (!node.isTextual()) {
                                    errorReport(
                                            path,
                                            template.getValueType().toString(),
                                            node.getNodeType().toString());
                                }
                                String value = node.asText();
                                byte[] bytesValue = tryDecodeInputData(value);
                                if (bytesValue == null) {
                                    bytesValue = value.getBytes();
                                }
                                if (abiObject.getBytesLength() > 0
                                        && bytesValue.length != abiObject.getBytesLength()) {
                                    errorReport(
                                            "Invalid input bytes, required length: "
                                                    + abiObject.getBytesLength()
                                                    + ", input data length:"
                                                    + bytesValue.length);
                                }
                                abiObject.setBytesValue(new Bytes(bytesValue.length, bytesValue));
                                break;
                            }
                        case DBYTES:
                            {
                                if (!node.isTextual()) {
                                    errorReport(
                                            path,
                                            template.getValueType().toString(),
                                            node.getNodeType().toString());
                                }
                                String value = node.asText();
                                byte[] bytesValue = tryDecodeInputData(value);
                                if (bytesValue == null) {
                                    bytesValue = value.getBytes();
                                }
                                abiObject.setDynamicBytesValue(new DynamicBytes(bytesValue));
                                break;
                            }
                        case STRING:
                            {
                                if (!node.isTextual()) {
                                    errorReport(
                                            path,
                                            template.getValueType().toString(),
                                            node.getNodeType().toString());
                                }

                                abiObject.setStringValue(new Utf8String(node.asText()));
                                break;
                            }
                        case FIXED:
                        case UFIXED:
                            {
                                throw new UnsupportedOperationException(
                                        " Unsupported fixed/unfixed type. ");
                            }
                    }
                    break;
                }
            case LIST:
                {
                    if (!node.isArray()) {
                        errorReport(
                                path,
                                abiObject.getType().toString(),
                                node.getNodeType().toString());
                    }

                    if ((abiObject.getListType() == ListType.FIXED)
                            && (node.size() != abiObject.getListLength())) {
                        errorReport(
                                "fixed list arguments size",
                                String.valueOf(abiObject.getListLength()),
                                String.valueOf(node.size()));
                    }

                    int i = 0;
                    Iterator<JsonNode> iterator = node.iterator();
                    while (iterator.hasNext()) {
                        abiObject
                                .getListValues()
                                .add(
                                        encodeNode(
                                                path + ".<" + i + ">",
                                                abiObject.getListValueType(),
                                                iterator.next()));
                    }

                    break;
                }
            case STRUCT:
                {
                    if (!node.isArray() && !node.isObject()) {
                        errorReport(
                                path,
                                abiObject.getType().toString(),
                                node.getNodeType().toString());
                    }

                    if (node.size() != abiObject.getStructFields().size()) {
                        errorReport(
                                "struct arguments size",
                                String.valueOf(abiObject.getListLength()),
                                String.valueOf(node.size()));
                    }

                    if (node.isArray()) {
                        for (int i = 0; i < abiObject.getStructFields().size(); i++) {
                            ABIObject field = abiObject.getStructFields().get(i);
                            abiObject
                                    .getStructFields()
                                    .set(
                                            i,
                                            encodeNode(
                                                    path + "." + field.getName(),
                                                    field,
                                                    node.get(i)));
                        }
                    } else {
                        for (int i = 0; i < abiObject.getStructFields().size(); ++i) {
                            ABIObject field = abiObject.getStructFields().get(i);
                            JsonNode structNode = node.get(field.getName());

                            if (structNode == null) {
                                errorReport(
                                        path + "miss field value, field name: " + field.getName(),
                                        template.getValueType().toString(),
                                        node.getNodeType().toString());
                            }

                            abiObject
                                    .getStructFields()
                                    .set(
                                            i,
                                            encodeNode(
                                                    path + "." + field.getName(),
                                                    field,
                                                    structNode));
                        }
                    }

                    break;
                }
        }

        return abiObject;
    }

    public static byte[] tryDecodeInputData(String inputData) {
        if (inputData.startsWith(HexEncodedDataPrefix)) {
            String hexString = inputData.substring(HexEncodedDataPrefix.length());
            return Hex.decode(hexString);
        } else {
            // try to decode hex input
            try {
                byte[] decode = Hex.decode(inputData);
                if (decode.length == 0) {
                    return null;
                }
                return decode;
            } catch (DecoderException ignored) {
                return null;
            }
        }
    }

    public ABIObject encode(ABIObject template, List<String> inputs) throws IOException {

        ABIObject abiObject = template.newObject();

        // check parameters match
        if (inputs.size() != abiObject.getStructFields().size()) {
            errorReport(
                    "arguments size",
                    String.valueOf(abiObject.getStructFields().size()),
                    String.valueOf(inputs.size()));
        }

        for (int i = 0; i < abiObject.getStructFields().size(); ++i) {

            ABIObject argObject = abiObject.getStructFields().get(i).newObject();
            String value = inputs.get(i);

            switch (argObject.getType()) {
                case VALUE:
                    {
                        try {
                            switch (argObject.getValueType()) {
                                case BOOL:
                                    {
                                        argObject.setBoolValue(new Bool(Boolean.valueOf(value)));
                                        break;
                                    }
                                case UINT:
                                    {
                                        NumericType numericType;
                                        if (argObject.getBytesLength() == 8) {
                                            numericType = new Uint8(Numeric.decodeQuantity(value));
                                        } else if (argObject.getBytesLength() == 16) {
                                            numericType = new Uint16(Numeric.decodeQuantity(value));
                                        } else if (argObject.getBytesLength() == 32) {
                                            numericType = new Uint32(Numeric.decodeQuantity(value));
                                        } else if (argObject.getBytesLength() == 64) {
                                            numericType = new Uint64(Numeric.decodeQuantity(value));
                                        } else if (argObject.getBytesLength() == 128) {
                                            numericType =
                                                    new Uint128(Numeric.decodeQuantity(value));
                                        } else {
                                            numericType =
                                                    new Uint256(Numeric.decodeQuantity(value));
                                        }
                                        argObject.setNumericValue(numericType);
                                        break;
                                    }
                                case INT:
                                    {
                                        NumericType numericType;
                                        if (argObject.getBytesLength() == 8) {
                                            numericType = new Int8(Numeric.decodeQuantity(value));
                                        } else if (argObject.getBytesLength() == 16) {
                                            numericType = new Int16(Numeric.decodeQuantity(value));
                                        } else if (argObject.getBytesLength() == 32) {
                                            numericType = new Int32(Numeric.decodeQuantity(value));
                                        } else if (argObject.getBytesLength() == 64) {
                                            numericType = new Int64(Numeric.decodeQuantity(value));
                                        } else if (argObject.getBytesLength() == 128) {
                                            numericType = new Int128(Numeric.decodeQuantity(value));
                                        } else {
                                            numericType = new Int256(Numeric.decodeQuantity(value));
                                        }
                                        argObject.setNumericValue(numericType);
                                        break;
                                    }
                                case ADDRESS:
                                    {
                                        argObject.setAddressValue(new Address(value));
                                        break;
                                    }
                                case BYTES:
                                    {
                                        // Binary data hex encoding
                                        byte[] bytesValue = tryDecodeInputData(value);
                                        if (bytesValue == null) {
                                            bytesValue = value.getBytes();
                                        }
                                        if (argObject.getBytesLength() > 0
                                                && bytesValue.length
                                                        != argObject.getBytesLength()) {
                                            errorReport(
                                                    "Invalid input bytes, required length: "
                                                            + argObject.getBytesLength()
                                                            + ", input data length:"
                                                            + bytesValue.length);
                                        }
                                        argObject.setBytesValue(
                                                new Bytes(bytesValue.length, bytesValue));
                                        break;
                                    }
                                case DBYTES:
                                    {
                                        // Binary data requires hex encoding
                                        byte[] bytesValue = tryDecodeInputData(value);
                                        if (bytesValue == null) {
                                            bytesValue = value.getBytes();
                                        }
                                        argObject.setDynamicBytesValue(
                                                new DynamicBytes(bytesValue));
                                        break;
                                    }
                                case STRING:
                                    {
                                        argObject.setStringValue(new Utf8String(value));
                                        break;
                                    }
                                default:
                                    {
                                        throw new UnsupportedOperationException(
                                                "Unrecognized valueType: "
                                                        + argObject.getValueType());
                                    }
                            }
                        } catch (Exception e) {
                            logger.error(" e: {}, argsObject: {}", e.getMessage(), argObject);
                            errorReport(
                                    "ROOT",
                                    argObject.getValueType().toString(),
                                    value,
                                    e.getMessage());
                        }

                        break;
                    }
                case STRUCT:
                case LIST:
                    {
                        JsonNode argNode = this.objectMapper.readTree(value.getBytes());
                        argObject = encodeNode("ROOT", argObject, argNode);
                        break;
                    }
            }

            abiObject.getStructFields().set(i, argObject);
        }

        return abiObject;
    }

    public JsonNode decode(ABIObject abiObject) {
        JsonNodeFactory jsonNodeFactory = this.objectMapper.getNodeFactory();

        switch (abiObject.getType()) {
            case VALUE:
                {
                    switch (abiObject.getValueType()) {
                        case BOOL:
                            {
                                return jsonNodeFactory.booleanNode(
                                        abiObject.getBoolValue().getValue());
                            }
                        case INT:
                        case UINT:
                            {
                                return jsonNodeFactory.numberNode(
                                        abiObject.getNumericValue().getValue());
                            }
                        case ADDRESS:
                            {
                                return jsonNodeFactory.textNode(
                                        abiObject.getAddressValue().toString());
                            }
                        case BYTES:
                            {
                                return jsonNodeFactory.textNode(
                                        Hex.toHexString(abiObject.getBytesValue().getValue()));
                            }
                        case DBYTES:
                            {
                                return jsonNodeFactory.textNode(
                                        Hex.toHexString(
                                                abiObject.getDynamicBytesValue().getValue()));
                            }
                        case STRING:
                            {
                                return jsonNodeFactory.textNode(
                                        abiObject.getStringValue().getValue());
                            }
                        case FIXED:
                        case UFIXED:
                            {
                                throw new UnsupportedOperationException(
                                        " Unsupported fixed/unfixed type. ");
                            }
                    }
                    break;
                }
            case LIST:
                {
                    ArrayNode arrayNode = jsonNodeFactory.arrayNode();

                    for (ABIObject listObject : abiObject.getListValues()) {
                        arrayNode.add(decode(listObject));
                    }

                    return arrayNode;
                }
            case STRUCT:
                {
                    ArrayNode structNode = jsonNodeFactory.arrayNode();

                    for (ABIObject listObject : abiObject.getStructFields()) {
                        structNode.add(decode(listObject));
                    }

                    return structNode;
                }
        }

        return null;
    }

    public List<String> decode(ABIObject template, byte[] buffer, boolean isWasm)
            throws ClassNotFoundException {

        if (logger.isTraceEnabled()) {
            logger.trace(" ABIObject: {}, abi: {}", template.toString(), buffer);
        }

        ABIObject abiObject = ContractCodecTools.decode(template, buffer, isWasm);

        JsonNode jsonNode = decode(abiObject);

        List<String> result = new ArrayList<String>();
        for (int i = 0; i < abiObject.getStructFields().size(); ++i) {
            ABIObject argObject = abiObject.getStructFields().get(i);
            JsonNode argNode = jsonNode.get(i);
            switch (argObject.getType()) {
                case VALUE:
                    {
                        switch (argObject.getValueType()) {
                            case BOOL:
                                {
                                    result.add(String.valueOf(argObject.getBoolValue().getValue()));
                                    break;
                                }
                            case UINT:
                            case INT:
                                {
                                    result.add(argObject.getNumericValue().getValue().toString());
                                    break;
                                }
                            case ADDRESS:
                                {
                                    result.add(
                                            String.valueOf(argObject.getAddressValue().toString()));
                                    break;
                                }
                            case BYTES:
                                {
                                    byte[] value = ContractCodecTools.formatBytesN(argObject);
                                    byte[] hexBytes = Hex.encode(value);
                                    result.add(HexEncodedDataPrefix + new String(hexBytes));
                                    break;
                                }
                            case DBYTES:
                                {
                                    byte[] hexBytes =
                                            Hex.encode(argObject.getDynamicBytesValue().getValue());
                                    result.add(HexEncodedDataPrefix + new String(hexBytes));
                                    break;
                                }
                            case STRING:
                                {
                                    result.add(
                                            String.valueOf(argObject.getStringValue().getValue()));
                                    break;
                                }
                            default:
                                {
                                    throw new UnsupportedOperationException(
                                            " Unsupported valueType: " + argObject.getValueType());
                                }
                        }
                        break;
                    }
                case LIST:
                case STRUCT:
                    {
                        // Note: when the argNode is text data, toPrettyString output the text data
                        //       if the argNode is binary data, toPrettyString output the
                        // hex-encoded data
                        result.add(argNode.toPrettyString());
                        break;
                    }
                default:
                    {
                        throw new UnsupportedOperationException(
                                " Unsupported objectType: " + argObject.getType());
                    }
            }
        }

        return result;
    }
}
