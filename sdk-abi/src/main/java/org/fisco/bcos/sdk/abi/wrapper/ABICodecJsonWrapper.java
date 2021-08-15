package org.fisco.bcos.sdk.abi.wrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import org.fisco.bcos.sdk.abi.datatypes.*;
import org.fisco.bcos.sdk.abi.datatypes.generated.Int256;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.abi.wrapper.ABIObject.ListType;
import org.fisco.bcos.sdk.utils.Hex;
import org.fisco.bcos.sdk.utils.Numeric;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ABICodecJsonWrapper {
    public static final String Base64EncodedDataPrefix = "base64://";
    public static final String HexEncodedDataPrefix = "hex://";

    private static final Logger logger = LoggerFactory.getLogger(ABICodecJsonWrapper.class);

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

                                if (node.isNumber()) {
                                    abiObject.setNumericValue(new Int256(node.asLong()));
                                } else {
                                    abiObject.setNumericValue(new Int256(node.bigIntegerValue()));
                                }

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

                                if (node.isNumber()) {
                                    abiObject.setNumericValue(new Uint256(node.asLong()));
                                } else {
                                    abiObject.setNumericValue(new Uint256(node.bigIntegerValue()));
                                }

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
        if (inputData.startsWith(Base64EncodedDataPrefix)) {
            return Base64.getDecoder()
                    .decode(inputData.substring(Base64EncodedDataPrefix.length()));
        } else if (inputData.startsWith(HexEncodedDataPrefix)) {
            String hexString = inputData.substring(HexEncodedDataPrefix.length());
            if (hexString.startsWith("0x")) {
                return Hex.decode(hexString.substring(2));
            } else {
                return Hex.decode(hexString);
            }
        }
        return null;
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
                                        argObject.setNumericValue(
                                                new Uint256(Numeric.decodeQuantity(value)));
                                        break;
                                    }
                                case INT:
                                    {
                                        argObject.setNumericValue(
                                                new Int256(Numeric.decodeQuantity(value)));
                                        break;
                                    }
                                case ADDRESS:
                                    {
                                        argObject.setAddressValue(new Address(value));
                                        break;
                                    }
                                case BYTES:
                                    {
                                        // Binary data requires base64 encoding
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
                                        // Binary data requires base64 encoding
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
                                return jsonNodeFactory.binaryNode(
                                        abiObject.getBytesValue().getValue());
                            }
                        case DBYTES:
                            {
                                return jsonNodeFactory.binaryNode(
                                        abiObject.getDynamicBytesValue().getValue());
                            }
                        case STRING:
                            {
                                return jsonNodeFactory.textNode(
                                        abiObject.getStringValue().getValue());
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

    public List<String> decode(ABIObject template, byte[] buffer) {

        if (logger.isTraceEnabled()) {
            logger.trace(" ABIObject: {}, abi: {}", template.toString(), buffer);
        }

        ABIObject abiObject = template.decode(buffer);

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
                                    byte[] value = ABICodecObject.formatBytesN(argObject);
                                    byte[] base64Bytes = Base64.getEncoder().encode(value);
                                    result.add(Base64EncodedDataPrefix + new String(base64Bytes));
                                    break;
                                }
                            case DBYTES:
                                {
                                    byte[] base64Bytes =
                                            Base64.getEncoder()
                                                    .encode(
                                                            argObject
                                                                    .getDynamicBytesValue()
                                                                    .getValue());
                                    result.add(Base64EncodedDataPrefix + new String(base64Bytes));
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
                        // base64-encoded data
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
