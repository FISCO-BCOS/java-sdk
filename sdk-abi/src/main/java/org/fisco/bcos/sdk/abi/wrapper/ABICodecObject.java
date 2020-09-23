package org.fisco.bcos.sdk.abi.wrapper;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.abi.datatypes.Address;
import org.fisco.bcos.sdk.abi.datatypes.Bool;
import org.fisco.bcos.sdk.abi.datatypes.Bytes;
import org.fisco.bcos.sdk.abi.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.abi.datatypes.Utf8String;
import org.fisco.bcos.sdk.abi.datatypes.generated.Int256;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.abi.wrapper.ABIObject.ListType;
import org.fisco.bcos.sdk.utils.Numeric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ABICodecObject {

    private static final Logger logger = LoggerFactory.getLogger(ABICodecObject.class);

    private void errorReport(String path, String expected, String actual)
            throws InvalidParameterException {
        String errorMessage =
                "Arguments mismatch: " + path + ", expected: " + expected + ", actual: " + actual;
        logger.error(errorMessage);
        throw new InvalidParameterException(errorMessage);
    }

    public ABIObject encodeList(ABIObject template, Object value) {

        ABIObject abiObject = template.newObject();

        // check abi type
        if (abiObject.getType() != ABIObject.ObjectType.LIST
                && abiObject.getType() != ABIObject.ObjectType.STRUCT) {
            errorReport(
                    " abi type mismatch of " + abiObject.getName(),
                    "LIST/STRUCT",
                    abiObject.getType().toString());
        }

        List<Object> list = new ArrayList<Object>();
        if (value instanceof List) {
            list = (List<Object>) value;
        } else {
            Object[] objs = (Object[]) value;
            for (Object obj : objs) {
                list.add(obj);
            }
        }
        if ((abiObject.getListType() == ListType.FIXED)
                && (list.size() != abiObject.getListLength())) {
            errorReport(
                    "fixed list arguments size",
                    String.valueOf(abiObject.getListLength()),
                    String.valueOf(list.size()));
        }

        for (int i = 0; i < list.size(); i++) {
            ABIObject nodeObject = abiObject.getListValueType().newObject();
            switch (nodeObject.getType()) {
                case VALUE:
                    {
                        nodeObject = encodeValue(nodeObject, list.get(i));
                        break;
                    }
                case STRUCT:
                    {
                        nodeObject = encodeStruct(nodeObject, list.get(i));
                        break;
                    }
                case LIST:
                    {
                        nodeObject = encodeList(nodeObject, list.get(i));
                        break;
                    }
                default:
                    {
                        throw new UnsupportedOperationException(
                                " Unsupported objectType: " + nodeObject.getType());
                    }
            }
            abiObject.getListValues().add(nodeObject);
        }

        return abiObject;
    }

    public ABIObject encodeStruct(ABIObject template, Object value) {
        ABIObject abiObject = template.newObject();

        // check abi type
        if (abiObject.getType() != ABIObject.ObjectType.STRUCT) {
            errorReport(
                    " abi type mismatch of " + abiObject.getName(),
                    "STRUCT",
                    abiObject.getType().toString());
        }

        if (value instanceof java.util.List) {
            List<Object> list = (List<Object>) value;
            for (int i = 0; i < abiObject.getStructFields().size(); i++) {
                ABIObject nodeObject = abiObject.getStructFields().get(i);
                switch (nodeObject.getType()) {
                    case VALUE:
                        {
                            nodeObject = encodeValue(nodeObject, list.get(i));
                            break;
                        }
                    case STRUCT:
                        {
                            nodeObject = encodeStruct(nodeObject, list.get(i));
                            break;
                        }
                    case LIST:
                        {
                            nodeObject = encodeList(nodeObject, list.get(i));
                            break;
                        }
                    default:
                        {
                            throw new UnsupportedOperationException(
                                    " Unsupported objectType: " + nodeObject.getType());
                        }
                }
                abiObject.getStructFields().set(i, nodeObject);
            }
        } else {
            Field[] fields = value.getClass().getDeclaredFields();
            Map<String, Object> v = new HashMap<>();
            try {
                for (Field f : fields) {
                    f.setAccessible(true);
                    v.put(f.getName(), f.get(value));
                }
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage());
            }
            for (int i = 0; i < abiObject.getStructFields().size(); ++i) {
                ABIObject nodeObject = abiObject.getStructFields().get(i);
                switch (nodeObject.getType()) {
                    case VALUE:
                        {
                            nodeObject = encodeValue(nodeObject, v.get(nodeObject.getName()));
                            break;
                        }
                    case STRUCT:
                        {
                            nodeObject = encodeStruct(nodeObject, v.get(nodeObject.getName()));
                            break;
                        }
                    case LIST:
                        {
                            nodeObject = encodeList(nodeObject, v.get(nodeObject.getName()));
                            break;
                        }
                    default:
                        {
                            throw new UnsupportedOperationException(
                                    " Unsupported objectType: " + nodeObject.getType());
                        }
                }
                abiObject.getStructFields().set(i, nodeObject);
            }
        }

        return abiObject;
    }

    public ABIObject encodeValue(ABIObject template, Object value) {
        ABIObject abiObject = template.newObject();
        if (abiObject.getType() == ABIObject.ObjectType.LIST) {
            abiObject = encodeList(abiObject, value);
        } else if (abiObject.getType() == ABIObject.ObjectType.STRUCT) {
            abiObject = encodeStruct(abiObject, value);
        } else {
            switch (abiObject.getValueType()) {
                case BOOL:
                    {
                        if (value instanceof Boolean) {
                            abiObject.setBoolValue(new Bool((Boolean) value));
                        } else {
                            errorReport(
                                    " valueType mismatch",
                                    abiObject.getValueType().getClass().getName(),
                                    value.getClass().getName());
                        }
                        break;
                    }
                case UINT:
                    {
                        if (value instanceof BigInteger) {
                            abiObject.setNumericValue(new Uint256((BigInteger) value));
                        } else if (StringUtils.isNumeric(value.toString())) {
                            abiObject.setNumericValue(
                                    new Uint256((new BigInteger(value.toString()))));
                        } else {
                            errorReport(
                                    " valueType mismatch",
                                    abiObject.getValueType().getClass().getName(),
                                    value.getClass().getName());
                        }
                        break;
                    }
                case INT:
                    {
                        if (value instanceof BigInteger) {
                            abiObject.setNumericValue(new Int256((BigInteger) value));
                        } else if (StringUtils.isNumeric(value.toString())) {
                            abiObject.setNumericValue(
                                    new Uint256((new BigInteger(value.toString()))));
                        } else {
                            errorReport(
                                    " valueType mismatch",
                                    abiObject.getValueType().getClass().getName(),
                                    value.getClass().getName());
                        }
                        break;
                    }
                case ADDRESS:
                    {
                        if (value instanceof String) {
                            abiObject.setAddressValue(new Address((String) value));
                        } else {
                            errorReport(
                                    " valueType mismatch",
                                    abiObject.getValueType().getClass().getName(),
                                    value.getClass().getName());
                        }
                        break;
                    }
                case BYTES:
                    {
                        if (value instanceof byte[]) {
                            byte[] bytesValue = (byte[]) value;
                            abiObject.setBytesValue(new Bytes(bytesValue.length, bytesValue));

                        } else {
                            errorReport(
                                    " valueType mismatch",
                                    abiObject.getValueType().getClass().getName(),
                                    value.getClass().getName());
                        }
                        break;
                    }
                case DBYTES:
                    {
                        if (value instanceof byte[]) {
                            byte[] bytesValue = (byte[]) value;
                            abiObject.setDynamicBytesValue(new DynamicBytes(bytesValue));
                        } else {
                            errorReport(
                                    " valueType mismatch",
                                    abiObject.getValueType().getClass().getName(),
                                    value.getClass().getName());
                            break;
                        }
                        break;
                    }
                case STRING:
                    {
                        if (value instanceof String) {
                            abiObject.setStringValue(new Utf8String((String) value));
                        } else {
                            errorReport(
                                    " valueType mismatch",
                                    abiObject.getValueType().getClass().getName(),
                                    value.getClass().getName());
                        }
                        break;
                    }
                default:
                    {
                        throw new InvalidParameterException(
                                "Unrecognized valueType: " + abiObject.getValueType());
                    }
            }
        }

        return abiObject;
    }

    public List<Object> decodeJavaObject(ABIObject template, String input) {

        if (logger.isTraceEnabled()) {
            logger.trace(" ABIObject: {}, abi: {}", template.toString(), input);
        }

        input = Numeric.cleanHexPrefix(input);

        ABIObject abiObject = template.decode(input);

        // ABIObject -> java List<Object>
        List<Object> result = decodeJavaObject(abiObject);

        return result;
    }

    private List<Object> decodeJavaObject(ABIObject template) throws UnsupportedOperationException {
        List<Object> result = new ArrayList<Object>();
        List<ABIObject> argObjects;
        if (template.getType() == ABIObject.ObjectType.STRUCT) {
            argObjects = template.getStructFields();
        } else {
            argObjects = template.getListValues();
        }
        for (int i = 0; i < argObjects.size(); ++i) {
            ABIObject argObject = argObjects.get(i);
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
                                    result.add(argObject.getStringValue().toString());
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
