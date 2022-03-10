package org.fisco.bcos.sdk.codec.wrapper;

import java.math.BigInteger;
import java.util.List;
import org.fisco.bcos.sdk.utils.Numeric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ABIObjectFactory {

    private static final Logger logger = LoggerFactory.getLogger(ABIObjectFactory.class);

    public static ABIObject createInputObject(ABIDefinition abiDefinition) {
        return createObject(abiDefinition.getName(), abiDefinition.getInputs());
    }

    public static ABIObject createOutputObject(ABIDefinition abiDefinition) {
        return createObject(abiDefinition.getName(), abiDefinition.getOutputs());
    }

    private static ABIObject createObject(String name, List<ABIDefinition.NamedType> namedTypes) {
        try {
            ABIObject abiObject = new ABIObject(ABIObject.ObjectType.STRUCT);

            for (ABIDefinition.NamedType namedType : namedTypes) {
                abiObject.getStructFields().add(buildTypeObject(namedType));
            }

            logger.info(" name: {}", name);

            return abiObject;

        } catch (Exception e) {
            logger.error("namedTypes: {},  e: ", namedTypes, e);
        }

        return null;
    }

    public static ABIObject createEventInputObject(ABIDefinition abiDefinition) {
        return creatEventObjectWithOutIndexed(abiDefinition.getInputs());
    }

    public static ABIObject creatEventObjectWithOutIndexed(
            List<ABIDefinition.NamedType> namedTypes) {
        try {
            ABIObject abiObject = new ABIObject(ABIObject.ObjectType.STRUCT);

            for (ABIDefinition.NamedType namedType : namedTypes) {
                if (!namedType.isIndexed()) {
                    abiObject.getStructFields().add(buildTypeObject(namedType));
                }
            }
            return abiObject;
        } catch (Exception e) {
            logger.error("namedTypes: {},  e: ", namedTypes, e);
        }
        return null;
    }

    /**
     * build ABIObject by raw type name
     *
     * @param rawType the rawType of the object
     * @return the built ABIObject
     */
    public static ABIObject buildRawTypeObject(String rawType) {

        ABIObject abiObject = null;

        if (rawType.startsWith("uint")) {
            abiObject = new ABIObject(ABIObject.ValueType.UINT);
        } else if (rawType.startsWith("int")) {
            abiObject = new ABIObject(ABIObject.ValueType.INT);
        } else if (rawType.startsWith("bool")) {
            abiObject = new ABIObject(ABIObject.ValueType.BOOL);
        } else if (rawType.startsWith("string")) {
            abiObject = new ABIObject(ABIObject.ValueType.STRING);
        } else if (rawType.equals("bytes")) {
            abiObject = new ABIObject(ABIObject.ValueType.DBYTES);
        } else if (rawType.startsWith("bytes")) {
            try {
                BigInteger bytesLength =
                        Numeric.decodeQuantity(rawType.substring("bytes".length()));
                abiObject = new ABIObject(ABIObject.ValueType.BYTES, bytesLength.intValue());
            } catch (Exception e) {
                abiObject = new ABIObject(ABIObject.ValueType.BYTES);
            }
        } else if (rawType.startsWith("address")) {
            abiObject = new ABIObject(ABIObject.ValueType.ADDRESS);
        } else if (rawType.startsWith("fixed") || rawType.startsWith("ufixed")) {
            throw new UnsupportedOperationException("Unsupported type:" + rawType);
        } else {
            throw new UnsupportedOperationException("Unrecognized type:" + rawType);
        }

        return abiObject;
    }

    private static ABIObject buildTupleObject(ABIDefinition.NamedType namedType) {
        return createObject(namedType.getName(), namedType.getComponents());
    }

    private static ABIObject buildListObject(
            ABIDefinition.Type typeObj, ABIDefinition.NamedType namedType) {

        ABIObject abiObject = null;
        if (typeObj.isList()) {
            ABIObject listObject = new ABIObject(ABIObject.ObjectType.LIST);
            listObject.setListType(
                    typeObj.isFixedList() ? ABIObject.ListType.FIXED : ABIObject.ListType.DYNAMIC);
            if (typeObj.isFixedList()) {
                listObject.setListLength(typeObj.getLastDimension());
            }

            listObject.setListValueType(
                    buildListObject(typeObj.reduceDimensionAndGetType(), namedType));
            abiObject = listObject;
        } else if (typeObj.getRawType().startsWith("tuple")) {
            abiObject = buildTupleObject(namedType);
        } else {
            abiObject = buildRawTypeObject(typeObj.getRawType());
        }

        return abiObject;
    }

    public static ABIObject buildTypeObject(ABIDefinition.NamedType namedType) {
        try {
            String type = namedType.getType();
            // String name = namedType.getName();
            // boolean indexed = namedType.isIndexed();

            ABIDefinition.Type typeObj = new ABIDefinition.Type(type);
            String rawType = typeObj.getRawType();

            ABIObject abiObject;
            if (typeObj.isList()) {
                abiObject = buildListObject(typeObj, namedType);
            } else if (rawType.startsWith("tuple")) {
                abiObject = buildTupleObject(namedType);
            } else {
                abiObject = buildRawTypeObject(rawType);
            }

            abiObject.setName(namedType.getName());

            return abiObject;
        } catch (Exception e) {
            logger.error(" e: ", e);
        }

        return null;
    }
}
