package org.fisco.bcos.sdk.abi.wrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import org.fisco.bcos.sdk.abi.datatypes.Address;
import org.fisco.bcos.sdk.abi.datatypes.Bool;
import org.fisco.bcos.sdk.abi.datatypes.Bytes;
import org.fisco.bcos.sdk.abi.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.abi.datatypes.Utf8String;
import org.fisco.bcos.sdk.abi.datatypes.generated.Int256;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.abi.wrapper.ABIObject.ListType;
import org.fisco.bcos.sdk.utils.Numeric;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ABICodecJsonWrapper {

    private static final Logger logger = LoggerFactory.getLogger(ABICodecJsonWrapper.class);

    private ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    private void errorReport(String path, String expected, String actual)
            throws InvalidParameterException {
        String errorMessage =
                "Arguments mismatch: " + path + ", expected: " + expected + ", actual: " + actual;
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

                                // Binary data requires base64 encoding
                                byte[] bytesValue = Base64.getDecoder().decode(node.asText());
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

                                byte[] bytesValue = Base64.getDecoder().decode(node.asText());
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
                                                path + ".<" + String.valueOf(i) + ">",
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
                                        byte[] bytesValue = Base64.getDecoder().decode(value);
                                        argObject.setBytesValue(
                                                new Bytes(bytesValue.length, bytesValue));
                                        break;
                                    }
                                case DBYTES:
                                    {
                                        // Binary data requires base64 encoding
                                        byte[] bytesValue = Base64.getDecoder().decode(value);
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
                            logger.error(" e: ", e);
                            errorReport("ROOT", argObject.getValueType().toString(), value);
                        }

                        break;
                    }
                case STRUCT:
                case LIST:
                    {
                        JsonNode argNode = objectMapper.readTree(value.getBytes());
                        argObject = encodeNode("ROOT", argObject, argNode);
                        break;
                    }
            }

            abiObject.getStructFields().set(i, argObject);
        }

        return abiObject;
    }

    public JsonNode decode(ABIObject abiObject) {
        JsonNodeFactory jsonNodeFactory = objectMapper.getNodeFactory();

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

    public List<String> decode(ABIObject template, String buffer) {

        if (logger.isTraceEnabled()) {
            logger.trace(" ABIObject: {}, abi: {}", template.toString(), buffer);
        }

        buffer = Numeric.cleanHexPrefix(buffer);

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
                                    byte[] base64Bytes =
                                            Base64.getEncoder()
                                                    .encode(argObject.getBytesValue().getValue());
                                    result.add(new String(base64Bytes));
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
                                    result.add(new String(base64Bytes));
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

    public ABIObject encodeJavaObject(ABIObject template, List<Object> inputs)
            throws InvalidParameterException, UnsupportedOperationException {

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
            Object value = inputs.get(i);

            switch (argObject.getType()) {
                case VALUE:
                    {
                        switch (argObject.getValueType()) {
                            case BOOL:
                                {
                                    if (value instanceof Boolean) {
                                        argObject.setBoolValue(new Bool((Boolean) value));
                                    } else {
                                        errorReport(
                                                " type mismatch",
                                                argObject.getValueType().getClass().getName(),
                                                value.getClass().getName());
                                    }
                                    break;
                                }
                            case UINT:
                                {
                                    if (value instanceof BigInteger) {
                                        argObject.setNumericValue(new Uint256((BigInteger) value));
                                    } else {
                                        errorReport(
                                                " type mismatch",
                                                argObject.getValueType().getClass().getName(),
                                                value.getClass().getName());
                                    }
                                    break;
                                }
                            case INT:
                                {
                                    if (value instanceof BigInteger) {
                                        argObject.setNumericValue(new Int256((BigInteger) value));
                                    } else {
                                        errorReport(
                                                " type mismatch",
                                                argObject.getValueType().getClass().getName(),
                                                value.getClass().getName());
                                    }
                                    break;
                                }
                            case ADDRESS:
                                {
                                    if (value instanceof String) {
                                        argObject.setAddressValue(new Address((String) value));
                                    } else {
                                        errorReport(
                                                " type mismatch",
                                                argObject.getValueType().getClass().getName(),
                                                value.getClass().getName());
                                    }
                                    break;
                                }
                            case BYTES:
                                {
                                    if (value instanceof ParameterizedType) {
                                        if (((ParameterizedType) value)
                                                .getRawType()
                                                .equals(Byte.class)) {
                                            byte[] bytesValue = (byte[]) value;
                                            argObject.setBytesValue(
                                                    new Bytes(bytesValue.length, bytesValue));
                                        }

                                    } else {
                                        errorReport(
                                                " type mismatch",
                                                argObject.getValueType().getClass().getName(),
                                                value.getClass().getName());
                                    }
                                    break;
                                }
                            case DBYTES:
                                {
                                    if (value instanceof ParameterizedType
                                            && (((ParameterizedType) value)
                                                    .getRawType()
                                                    .equals(Byte.class))) {
                                        byte[] bytesValue = (byte[]) value;
                                        argObject.setDynamicBytesValue(
                                                new DynamicBytes(bytesValue));
                                    } else {
                                        errorReport(
                                                " type mismatch",
                                                argObject.getValueType().getClass().getName(),
                                                value.getClass().getName());
                                        break;
                                    }
                                }
                            case STRING:
                                {
                                    if (value instanceof String) {
                                        argObject.setStringValue(new Utf8String((String) value));
                                    } else {
                                        errorReport(
                                                " type mismatch",
                                                argObject.getValueType().getClass().getName(),
                                                value.getClass().getName());
                                    }
                                    break;
                                }
                            default:
                                {
                                    throw new UnsupportedOperationException(
                                            "Unrecognized valueType: " + argObject.getValueType());
                                }
                        }
                    }
                case STRUCT:
                case LIST:
                    {
                        if (value instanceof ParameterizedType) {
                            List<Object> src = (List<Object>) value;
                            argObject = encodeJavaObject(argObject, src);
                        } else {
                            errorReport(
                                    " type mismatch",
                                    argObject.getValueType().getClass().getName(),
                                    value.getClass().getName());
                        }
                        break;
                    }
                default:
                    {
                        throw new UnsupportedOperationException(
                                " Unsupported objectType: " + argObject.getType());
                    }
            }

            abiObject.getStructFields().set(i, argObject);
        }
        return abiObject;
    }

    public List<Object> decodeJavaObject(ABIObject template, String argStr) {

        if (logger.isTraceEnabled()) {
            logger.trace(" ABIObject: {}, abi: {}", template.toString(), argStr);
        }

        argStr = Numeric.cleanHexPrefix(argStr);

        ABIObject abiObject = template.decode(argStr);

        // ABIObject -> java List<Object>
        List<Object> result = decodeJavaObject(abiObject);

        return result;
    }

    private List<Object> decodeJavaObject(ABIObject template) throws UnsupportedOperationException {
        List<Object> result = new ArrayList<Object>();
        for (int i = 0; i < template.getStructFields().size(); ++i) {
            ABIObject argObject = template.getStructFields().get(i);
            switch (argObject.getType()) {
                case VALUE:
                    {
                        switch (argObject.getValueType()) {
                            case BOOL:
                                {
                                    result.add(argObject.getBoolValue().getValue());
                                    break;
                                }
                            case UINT:
                            case INT:
                                {
                                    result.add(argObject.getNumericValue().getValue());
                                    break;
                                }
                            case ADDRESS:
                                {
                                    result.add(argObject.getAddressValue().toString());
                                    break;
                                }
                            case BYTES:
                                {
                                    result.add(new String(argObject.getBytesValue().getValue()));
                                    break;
                                }
                            case DBYTES:
                                {
                                    result.add(
                                            new String(
                                                    argObject.getDynamicBytesValue().getValue()));
                                    break;
                                }
                            case STRING:
                                {
                                    result.add(argObject.getStringValue());
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
                        List<Object> node = decodeJavaObject(argObject);
                        result.add(node);
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
