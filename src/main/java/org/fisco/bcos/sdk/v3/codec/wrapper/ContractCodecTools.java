package org.fisco.bcos.sdk.v3.codec.wrapper;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Array;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.Int;
import org.fisco.bcos.sdk.v3.codec.datatypes.StaticArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.StaticStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.StructType;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Uint;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes32;
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
import org.fisco.bcos.sdk.v3.codec.scale.ScaleCodecReader;
import org.fisco.bcos.sdk.v3.codec.wrapper.ABIObject.ListType;
import org.fisco.bcos.sdk.v3.utils.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContractCodecTools {

    private static final Logger logger = LoggerFactory.getLogger(ContractCodecTools.class);

    private static void errorReport(String path, String expected, String actual)
            throws InvalidParameterException {
        String errorMessage =
                "Arguments mismatch: " + path + ", expected: " + expected + ", actual: " + actual;
        logger.error(errorMessage);
        throw new InvalidParameterException(errorMessage);
    }

    public static ABIObject decodeAbiObjectListValue(ABIObject template, Object value) {

        ABIObject abiObject = template.newObject();

        // check abi type
        if (abiObject.getType() != ABIObject.ObjectType.LIST
                && abiObject.getType() != ABIObject.ObjectType.STRUCT) {
            errorReport(
                    " abi type mismatch of " + abiObject.getName(),
                    "LIST/STRUCT",
                    abiObject.getType().toString());
        }

        List<Object> list = new ArrayList<>();
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

        for (Object obj : list) {
            ABIObject nodeObject = abiObject.getListValueType().newObject();
            switch (nodeObject.getType()) {
                case VALUE:
                    {
                        nodeObject = decodeABIObjectValue(nodeObject, obj);
                        break;
                    }
                case STRUCT:
                    {
                        nodeObject = decodeAbiObjectStructValue(nodeObject, obj);
                        break;
                    }
                case LIST:
                    {
                        nodeObject = decodeAbiObjectListValue(nodeObject, obj);
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

    public static ABIObject decodeAbiObjectStructValue(ABIObject template, Object value) {
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
                            nodeObject = decodeABIObjectValue(nodeObject, list.get(i));
                            break;
                        }
                    case STRUCT:
                        {
                            nodeObject = decodeAbiObjectStructValue(nodeObject, list.get(i));
                            break;
                        }
                    case LIST:
                        {
                            nodeObject = decodeAbiObjectListValue(nodeObject, list.get(i));
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
                            nodeObject = decodeABIObjectValue(nodeObject, array.getValue().get(i));
                            break;
                        }
                    case STRUCT:
                        {
                            nodeObject =
                                    decodeAbiObjectStructValue(nodeObject, array.getValue().get(i));
                            break;
                        }
                    case LIST:
                        {
                            nodeObject =
                                    decodeAbiObjectListValue(nodeObject, array.getValue().get(i));
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
            // scan class define fields
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
                            nodeObject =
                                    decodeABIObjectValue(nodeObject, v.get(nodeObject.getName()));
                            break;
                        }
                    case STRUCT:
                        {
                            nodeObject =
                                    decodeAbiObjectStructValue(
                                            nodeObject, v.get(nodeObject.getName()));
                            break;
                        }
                    case LIST:
                        {
                            nodeObject =
                                    decodeAbiObjectListValue(
                                            nodeObject, v.get(nodeObject.getName()));
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

    public static ABIObject decodeABIObjectValue(ABIObject template, Object value) {
        ABIObject abiObject = template.newObject();
        if (abiObject.getType() == ABIObject.ObjectType.LIST) {
            abiObject = decodeAbiObjectListValue(abiObject, value);
        } else if (abiObject.getType() == ABIObject.ObjectType.STRUCT) {
            abiObject = decodeAbiObjectStructValue(abiObject, value);
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

    private static byte[] typeEncoderWrapper(Type parameter, boolean isWasm) throws IOException {
        return isWasm
                ? org.fisco.bcos.sdk.v3.codec.scale.TypeEncoder.encode(parameter)
                : org.fisco.bcos.sdk.v3.codec.abi.TypeEncoder.encode(parameter);
    }

    private static Type getABIObjectTypeValue(ABIObject abiObject) {
        switch (abiObject.getType()) {
            case VALUE:
                {
                    ABIObject.ValueType valueType = abiObject.getValueType();
                    switch (valueType) {
                        case UINT:
                        case INT:
                            {
                                return abiObject.getNumericValue();
                            }
                        case BOOL:
                            {
                                return abiObject.getBoolValue();
                            }
                        case FIXED:
                        case UFIXED:
                            {
                                throw new UnsupportedOperationException(
                                        " Unsupported fixed/unfixed type. ");
                            }
                        case BYTES:
                            {
                                byte[] bytes = formatBytesN(abiObject);
                                return new Bytes(bytes.length, bytes);
                            }
                        case ADDRESS:
                            {
                                return abiObject.getAddressValue();
                            }
                        case DBYTES:
                            {
                                return abiObject.getDynamicBytesValue();
                            }
                        case STRING:
                            {
                                return abiObject.getStringValue();
                            }
                        default:
                            {
                                throw new UnsupportedOperationException(
                                        " Unrecognized valueType: " + valueType);
                            }
                    }
                }
            case STRUCT:
                {
                    List<Type> typeList = new ArrayList<>();
                    for (ABIObject structField : abiObject.getStructFields()) {
                        typeList.add(getABIObjectTypeValue(structField));
                    }
                    if (abiObject.isDynamic()) {
                        return new DynamicStruct(typeList);
                    } else {
                        return new StaticStruct(typeList);
                    }
                }
            case LIST:
                {
                    List<Type> typeList = new ArrayList<>();
                    for (ABIObject listValue : abiObject.getListValues()) {
                        typeList.add(getABIObjectTypeValue(listValue));
                    }
                    if (abiObject.isDynamic()) {
                        DynamicArray dynamicArray = null;
                        if (typeList.isEmpty()) {
                            dynamicArray = new DynamicArray(ABIDefinition.Type.class, typeList);
                        } else {
                            dynamicArray = new DynamicArray(typeList.get(0).getClass(), typeList);
                        }
                        dynamicArray.setFixed(abiObject.getListType() == ListType.FIXED);
                        return dynamicArray;
                    } else {
                        if (typeList.isEmpty()) {
                            return new StaticArray(ABIDefinition.Type.class, typeList);
                        }
                        return new StaticArray(typeList.get(0).getClass(), typeList);
                    }
                }
            default:
                {
                    throw new UnsupportedOperationException(
                            " Unsupported type: " + abiObject.getType());
                }
        }
    }

    public static List<Type> getABIObjectTypeListResult(ABIObject abiObject) {
        Type structResult = getABIObjectTypeValue(abiObject);
        if (structResult instanceof DynamicStruct) {
            DynamicStruct result = (DynamicStruct) structResult;
            return result.getComponentTypes();
        }
        if (structResult instanceof StaticStruct) {
            StaticStruct result = (StaticStruct) structResult;
            return result.getComponentTypes();
        }
        return new ArrayList<>();
    }

    /**
     * encode this object
     *
     * @param abiObject abi object
     * @param isWasm if evm or wasm vm
     * @return the encoded object
     * @throws IOException throw when decode error
     */
    public static byte[] encode(ABIObject abiObject, boolean isWasm) throws IOException {
        return typeEncoderWrapper(getABIObjectTypeValue(abiObject), isWasm);
    }

    /**
     * decode abi object
     *
     * @param template decode template abi object, it means should know the actual abi type
     * @param input the string to be decoded into ABIObject
     * @param isWasm is evm or wasm vm
     * @return the decoded ABIObject
     * @throws ClassNotFoundException throw when decode class not found
     */
    public static ABIObject decode(ABIObject template, byte[] input, boolean isWasm)
            throws ClassNotFoundException {
        if (isWasm) {
            ScaleCodecReader reader = new ScaleCodecReader(input);
            return decodeScale(template, reader);
        }
        return decodeABI(template, input, 0);
    }

    /**
     * abi codec decode
     *
     * @param template decode template abi object, it means should know the actual abi type
     * @param input the string to be decoded into ABIObject
     * @param offset abi decode offset
     * @return the decoded ABIObject
     * @throws ClassNotFoundException throw when decode class not found
     */
    private static ABIObject decodeABI(ABIObject template, byte[] input, int offset)
            throws ClassNotFoundException {

        ABIObject abiObject = template.newObject();

        switch (abiObject.getType()) {
            case VALUE:
                {
                    switch (abiObject.getValueType()) {
                        case BOOL:
                            {
                                abiObject.setBoolValue(
                                        org.fisco.bcos.sdk.v3.codec.abi.TypeDecoder.decode(
                                                input, offset, TypeReference.create(Bool.class)));
                                break;
                            }
                        case UINT:
                            {
                                abiObject.setNumericValue(
                                        org.fisco.bcos.sdk.v3.codec.abi.TypeDecoder.decode(
                                                input,
                                                offset,
                                                TypeReference.create(Uint256.class)));
                                break;
                            }
                        case INT:
                            {
                                abiObject.setNumericValue(
                                        org.fisco.bcos.sdk.v3.codec.abi.TypeDecoder.decode(
                                                input, offset, TypeReference.create(Int256.class)));
                                break;
                            }
                        case FIXED:
                        case UFIXED:
                            {
                                throw new UnsupportedOperationException(
                                        " Unsupported fixed/unfixed type. ");
                            }
                        case BYTES:
                            {
                                abiObject.setBytesValue(
                                        org.fisco.bcos.sdk.v3.codec.abi.TypeDecoder.decode(
                                                input,
                                                offset,
                                                TypeReference.create(Bytes32.class)));
                                break;
                            }
                        case ADDRESS:
                            {
                                abiObject.setAddressValue(
                                        org.fisco.bcos.sdk.v3.codec.abi.TypeDecoder.decode(
                                                input,
                                                offset,
                                                TypeReference.create(Address.class)));
                                break;
                            }
                        case DBYTES:
                            {
                                abiObject.setDynamicBytesValue(
                                        org.fisco.bcos.sdk.v3.codec.abi.TypeDecoder.decode(
                                                input,
                                                offset,
                                                TypeReference.create(DynamicBytes.class)));
                                break;
                            }
                        case STRING:
                            {
                                abiObject.setStringValue(
                                        org.fisco.bcos.sdk.v3.codec.abi.TypeDecoder.decode(
                                                input,
                                                offset,
                                                TypeReference.create(Utf8String.class)));
                                break;
                            }
                    }
                    break;
                }
            case STRUCT:
                {
                    int structOffset = offset;
                    int initialOffset = offset;

                    for (int i = 0; i < abiObject.getStructFields().size(); ++i) {
                        ABIObject structObject = abiObject.getStructFields().get(i);
                        ABIObject itemObject = null;
                        if (structObject.isDynamic()) {
                            int structValueOffset =
                                    org.fisco.bcos.sdk.v3.codec.abi.TypeDecoder.decode(
                                                    input,
                                                    structOffset,
                                                    TypeReference.create(Uint256.class))
                                            .getValue()
                                            .intValue();
                            itemObject =
                                    decodeABI(
                                            structObject, input, initialOffset + structValueOffset);
                        } else {
                            itemObject = decodeABI(structObject, input, structOffset);
                        }

                        abiObject.getStructFields().set(i, itemObject);
                        structOffset += structObject.offsetAsByteLength();
                    }
                    break;
                }
            case LIST:
                {
                    int listOffset = offset;
                    int initialOffset = offset;

                    int listLength = 0;
                    if (abiObject.getListType() == ListType.DYNAMIC) {
                        // dynamic list length
                        listLength =
                                org.fisco.bcos.sdk.v3.codec.abi.TypeDecoder.decode(
                                                input,
                                                listOffset,
                                                TypeReference.create(Uint256.class))
                                        .getValue()
                                        .intValue();
                        listOffset += Type.MAX_BYTE_LENGTH;
                        initialOffset += Type.MAX_BYTE_LENGTH;
                    } else {
                        // fixed list length
                        listLength = abiObject.getListLength();
                    }

                    if (logger.isTraceEnabled()) {
                        logger.trace(
                                " listType: {}, listLength: {}",
                                abiObject.getListType(),
                                listLength);
                    }

                    ABIObject listValueObject = abiObject.getListValueType();

                    for (int i = 0; i < listLength; i++) {
                        ABIObject itemABIObject = null;

                        if (listValueObject.isDynamic()) {
                            int listValueOffset =
                                    org.fisco.bcos.sdk.v3.codec.abi.TypeDecoder.decode(
                                                    input,
                                                    listOffset,
                                                    TypeReference.create(Uint256.class))
                                            .getValue()
                                            .intValue();
                            itemABIObject =
                                    decodeABI(
                                            abiObject.getListValueType(),
                                            input,
                                            initialOffset + listValueOffset);
                        } else {
                            itemABIObject =
                                    decodeABI(abiObject.getListValueType(), input, listOffset);
                        }

                        listOffset += listValueObject.offsetAsByteLength();

                        abiObject.getListValues().add(itemABIObject);
                    }
                    break;
                }
        }

        return abiObject;
    }

    /**
     * scale codec decode
     *
     * @param template decode template abi object, it means should know the actual abi type
     * @param reader scale reader
     * @return the decoded ABIObject
     * @throws ClassNotFoundException throw when decode class not found
     */
    private static ABIObject decodeScale(ABIObject template, ScaleCodecReader reader)
            throws ClassNotFoundException {

        ABIObject abiObject = template.newObject();

        switch (abiObject.getType()) {
            case VALUE:
                {
                    switch (abiObject.getValueType()) {
                        case BOOL:
                            {
                                abiObject.setBoolValue(
                                        org.fisco.bcos.sdk.v3.codec.scale.TypeDecoder.decode(
                                                reader, TypeReference.create(Bool.class)));
                                break;
                            }
                        case UINT:
                            {
                                Class<? extends Uint> uintClass;
                                if (abiObject.getBytesLength() == 8) {
                                    uintClass = Uint8.class;
                                } else if (abiObject.getBytesLength() == 16) {
                                    uintClass = Uint16.class;
                                } else if (abiObject.getBytesLength() == 32) {
                                    uintClass = Uint32.class;
                                } else if (abiObject.getBytesLength() == 64) {
                                    uintClass = Uint64.class;
                                } else if (abiObject.getBytesLength() == 128) {
                                    uintClass = Uint128.class;
                                } else {
                                    uintClass = Uint256.class;
                                }
                                abiObject.setNumericValue(
                                        org.fisco.bcos.sdk.v3.codec.scale.TypeDecoder.decode(
                                                reader, TypeReference.create(uintClass)));
                                break;
                            }
                        case INT:
                            {
                                Class<? extends Int> intClass;
                                if (abiObject.getBytesLength() == 8) {
                                    intClass = Int8.class;
                                } else if (abiObject.getBytesLength() == 16) {
                                    intClass = Int16.class;
                                } else if (abiObject.getBytesLength() == 32) {
                                    intClass = Int32.class;
                                } else if (abiObject.getBytesLength() == 64) {
                                    intClass = Int64.class;
                                } else if (abiObject.getBytesLength() == 128) {
                                    intClass = Int128.class;
                                } else {
                                    intClass = Int256.class;
                                }
                                abiObject.setNumericValue(
                                        org.fisco.bcos.sdk.v3.codec.scale.TypeDecoder.decode(
                                                reader, TypeReference.create(intClass)));
                                break;
                            }
                        case FIXED:
                        case UFIXED:
                            {
                                throw new UnsupportedOperationException(
                                        " Unsupported fixed/unfixed type. ");
                            }
                        case BYTES:
                            {
                                abiObject.setBytesValue(
                                        org.fisco.bcos.sdk.v3.codec.scale.TypeDecoder.decode(
                                                reader, TypeReference.create(Bytes32.class)));
                                break;
                            }
                        case ADDRESS:
                            {
                                abiObject.setAddressValue(
                                        org.fisco.bcos.sdk.v3.codec.scale.TypeDecoder.decode(
                                                reader, TypeReference.create(Address.class)));
                                break;
                            }
                        case DBYTES:
                            {
                                abiObject.setDynamicBytesValue(
                                        org.fisco.bcos.sdk.v3.codec.scale.TypeDecoder.decode(
                                                reader, TypeReference.create(DynamicBytes.class)));
                                break;
                            }
                        case STRING:
                            {
                                abiObject.setStringValue(
                                        org.fisco.bcos.sdk.v3.codec.scale.TypeDecoder.decode(
                                                reader, TypeReference.create(Utf8String.class)));
                                break;
                            }
                    }
                    break;
                }
            case STRUCT:
                {
                    for (int i = 0; i < abiObject.getStructFields().size(); ++i) {
                        ABIObject structObject = abiObject.getStructFields().get(i);
                        ABIObject itemObject = null;
                        itemObject = decodeScale(structObject, reader);
                        abiObject.getStructFields().set(i, itemObject);
                    }
                    break;
                }
            case LIST:
                {
                    int listLength;
                    if (abiObject.getListType() == ListType.DYNAMIC) {
                        // dynamic list length
                        listLength = reader.readCompact();
                    } else {
                        // fixed list length
                        listLength = abiObject.getListLength();
                    }

                    if (logger.isTraceEnabled()) {
                        logger.trace(
                                " listType: {}, listLength: {}",
                                abiObject.getListType(),
                                listLength);
                    }
                    for (int i = 0; i < listLength; i++) {
                        ABIObject itemABIObject;

                        itemABIObject = decodeScale(abiObject.getListValueType(), reader);

                        abiObject.getListValues().add(itemABIObject);
                    }
                    break;
                }
        }

        return abiObject;
    }

    public static Pair<List<Object>, List<ABIObject>> decodeJavaObjectAndOutputObject(
            ABIObject template, String input, boolean isWasm) throws ClassNotFoundException {
        if (logger.isTraceEnabled()) {
            logger.trace(" ABIObject: {}, abi: {}", template, input);
        }

        ABIObject abiObject = decode(template, Hex.decode(input), isWasm);

        // ABIObject -> java List<Object>
        return decodeJavaObjectAndGetOutputObject(abiObject);
    }

    public static List<Object> decodeJavaObject(ABIObject template, String input, boolean isWasm)
            throws ClassNotFoundException {
        return decodeJavaObjectAndOutputObject(template, input, isWasm).getLeft();
    }

    private static List<Object> decodeJavaObject(ABIObject template)
            throws UnsupportedOperationException {
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

    public static Pair<List<Object>, List<ABIObject>> decodeJavaObjectAndGetOutputObject(
            ABIObject template) throws UnsupportedOperationException {
        List<Object> result = new ArrayList<>();
        List<ABIObject> argObjects;
        if (template.getType() == ABIObject.ObjectType.STRUCT) {
            argObjects = template.getStructFields();
        } else {
            argObjects = template.getListValues();
        }
        List<ABIObject> resultABIObject = new ArrayList<>();
        for (ABIObject argObject : argObjects) {
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
                                    result.add(formatBytesN(argObject));
                                    break;
                                }
                            case DBYTES:
                                {
                                    result.add(argObject.getDynamicBytesValue().getValue());
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
