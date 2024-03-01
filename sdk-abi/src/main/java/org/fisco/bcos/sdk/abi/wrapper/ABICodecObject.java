package org.fisco.bcos.sdk.abi.wrapper;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.fisco.bcos.sdk.abi.datatypes.Address;
import org.fisco.bcos.sdk.abi.datatypes.Array;
import org.fisco.bcos.sdk.abi.datatypes.Bool;
import org.fisco.bcos.sdk.abi.datatypes.Bytes;
import org.fisco.bcos.sdk.abi.datatypes.DynamicArray;
import org.fisco.bcos.sdk.abi.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.abi.datatypes.DynamicStruct;
import org.fisco.bcos.sdk.abi.datatypes.Int;
import org.fisco.bcos.sdk.abi.datatypes.StaticArray;
import org.fisco.bcos.sdk.abi.datatypes.StaticStruct;
import org.fisco.bcos.sdk.abi.datatypes.StructType;
import org.fisco.bcos.sdk.abi.datatypes.Uint;
import org.fisco.bcos.sdk.abi.datatypes.Utf8String;
import org.fisco.bcos.sdk.abi.datatypes.generated.Int128;
import org.fisco.bcos.sdk.abi.datatypes.generated.Int16;
import org.fisco.bcos.sdk.abi.datatypes.generated.Int256;
import org.fisco.bcos.sdk.abi.datatypes.generated.Int32;
import org.fisco.bcos.sdk.abi.datatypes.generated.Int64;
import org.fisco.bcos.sdk.abi.datatypes.generated.Int8;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint128;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint16;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint32;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint64;
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint8;
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
        } else if (value instanceof DynamicArray) {
            list = (List<Object>) ((DynamicArray<?>) value).getValue();
        } else if (value instanceof StaticArray) {
            list = (List<Object>) ((StaticArray<?>) value).getValue();
        } else {
            Object[] objs = (Object[]) value;
            Collections.addAll(list, objs);
        }
        if ((abiObject.getListType() == ListType.FIXED)
                && (list.size() != abiObject.getListLength())) {
            errorReport(
                    "fixed list arguments size",
                    String.valueOf(abiObject.getListLength()),
                    String.valueOf(list.size()));
        }

        for (Object o : list) {
            ABIObject nodeObject = abiObject.getListValueType().newObject();
            switch (nodeObject.getType()) {
                case VALUE:
                    {
                        nodeObject = encodeValue(nodeObject, o);
                        break;
                    }
                case STRUCT:
                    {
                        nodeObject = encodeStruct(nodeObject, o);
                        break;
                    }
                case LIST:
                    {
                        nodeObject = encodeList(nodeObject, o);
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
        } else if (StructType.class.isAssignableFrom(value.getClass())) {
            // raw struct
            Array<?> array;
            if (template.isDynamic()) {
                array = (DynamicStruct) value;
            } else {
                array = (StaticStruct) value;
            }
            for (int i = 0; i < abiObject.getStructFields().size(); ++i) {
                ABIObject nodeObject = abiObject.getStructFields().get(i);
                switch (nodeObject.getType()) {
                    case VALUE:
                        {
                            nodeObject = encodeValue(nodeObject, array.getValue().get(i));
                            break;
                        }
                    case STRUCT:
                        {
                            nodeObject = encodeStruct(nodeObject, array.getValue().get(i));
                            break;
                        }
                    case LIST:
                        {
                            nodeObject = encodeList(nodeObject, array.getValue().get(i));
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
                        } else if (value instanceof Bool) {
                            abiObject.setBoolValue((Bool) value);
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
                        BigInteger num = null;
                        if (value instanceof BigInteger) {
                            num = (BigInteger) value;
                        } else if (Uint.class.isAssignableFrom(value.getClass())) {
                            num = ((Uint) value).getValue();
                        } else if (NumberUtils.isCreatable(value.toString())) {
                            num = new BigInteger(value.toString());
                        } else {
                            errorReport(
                                    " valueType mismatch",
                                    abiObject.getValueType().getClass().getName(),
                                    value.getClass().getName());
                        }
                        if (abiObject.getBytesLength() == 8) {
                            abiObject.setNumericValue(new Uint8(num));
                        } else if (abiObject.getBytesLength() == 16) {
                            abiObject.setNumericValue(new Uint16(num));
                        } else if (abiObject.getBytesLength() == 32) {
                            abiObject.setNumericValue(new Uint32(num));
                        } else if (abiObject.getBytesLength() == 64) {
                            abiObject.setNumericValue(new Uint64(num));
                        } else if (abiObject.getBytesLength() == 128) {
                            abiObject.setNumericValue(new Uint128(num));
                        } else {
                            abiObject.setNumericValue(new Uint256(num));
                        }
                        break;
                    }
                case INT:
                    {
                        BigInteger num = null;
                        if (value instanceof BigInteger) {
                            num = (BigInteger) value;
                        } else if (Int.class.isAssignableFrom(value.getClass())) {
                            num = ((Int) value).getValue();
                        } else if (NumberUtils.isCreatable(value.toString())) {
                            num = new BigInteger(value.toString());
                        } else {
                            errorReport(
                                    " valueType mismatch",
                                    abiObject.getValueType().getClass().getName(),
                                    value.getClass().getName());
                        }
                        if (abiObject.getBytesLength() == 8) {
                            abiObject.setNumericValue(new Int8(num));
                        } else if (abiObject.getBytesLength() == 16) {
                            abiObject.setNumericValue(new Int16(num));
                        } else if (abiObject.getBytesLength() == 32) {
                            abiObject.setNumericValue(new Int32(num));
                        } else if (abiObject.getBytesLength() == 64) {
                            abiObject.setNumericValue(new Int64(num));
                        } else if (abiObject.getBytesLength() == 128) {
                            abiObject.setNumericValue(new Int128(num));
                        } else {
                            abiObject.setNumericValue(new Int256(num));
                        }
                        break;
                    }
                case ADDRESS:
                    {
                        if (value instanceof String) {
                            abiObject.setAddressValue(new Address((String) value));
                        } else if (value instanceof Address) {
                            abiObject.setAddressValue((Address) value);
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
                        } else if (value instanceof Bytes) {
                            abiObject.setBytesValue((Bytes) value);
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
                        } else if (value instanceof DynamicBytes) {
                            abiObject.setDynamicBytesValue((DynamicBytes) value);
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
                        } else if (value instanceof Utf8String) {
                            abiObject.setStringValue((Utf8String) value);
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

    public Pair<List<Object>, List<ABIObject>> decodeJavaObjectAndOutputObject(
            ABIObject template, String input) {
        if (logger.isTraceEnabled()) {
            logger.trace(" ABIObject: {}, abi: {}", template.toString(), input);
        }

        input = Numeric.cleanHexPrefix(input);

        ABIObject abiObject = template.decode(input);

        // ABIObject -> java List<Object>
        return decodeJavaObjectAndGetOutputObject(abiObject);
    }

    public List<Object> decodeJavaObject(ABIObject template, String input) {
        return decodeJavaObjectAndOutputObject(template, input).getLeft();
    }

    private List<Object> decodeJavaObject(ABIObject template) throws UnsupportedOperationException {
        return decodeJavaObjectAndGetOutputObject(template).getLeft();
    }

    public static byte[] formatBytesN(ABIObject abiObject) {
        if (abiObject.getBytesLength() > 0
                && abiObject.getBytesValue().getValue().length > abiObject.getBytesLength()) {
            byte[] value = new byte[abiObject.getBytesLength()];
            System.arraycopy(
                    abiObject.getBytesValue().getValue(), 0, value, 0, abiObject.getBytesLength());
            return value;
        } else {
            return abiObject.getBytesValue().getValue();
        }
    }

    private Pair<List<Object>, List<ABIObject>> decodeJavaObjectAndGetOutputObject(
            ABIObject template) throws UnsupportedOperationException {
        List<Object> result = new ArrayList<Object>();
        List<ABIObject> argObjects;
        if (template.getType() == ABIObject.ObjectType.STRUCT) {
            argObjects = template.getStructFields();
        } else {
            argObjects = template.getListValues();
        }
        List<ABIObject> resultABIObject = new ArrayList<>();
        for (int i = 0; i < argObjects.size(); ++i) {
            ABIObject argObject = argObjects.get(i);
            switch (argObject.getType()) {
                case VALUE:
                    {
                        resultABIObject.add(argObject);
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
                                    result.add(new String(formatBytesN(argObject)));
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
                        Pair<List<Object>, List<ABIObject>> nodeAndAbiObject =
                                decodeJavaObjectAndGetOutputObject(argObject);
                        result.add(nodeAndAbiObject.getLeft());
                        ABIObject listABIObject = new ABIObject(argObject.getValueType());
                        listABIObject.setListValues(nodeAndAbiObject.getRight());
                        resultABIObject.add(listABIObject);
                        break;
                    }
                default:
                    {
                        throw new UnsupportedOperationException(
                                " Unsupported objectType: " + argObject.getType());
                    }
            }
        }
        return new ImmutablePair<>(result, resultABIObject);
    }
}
