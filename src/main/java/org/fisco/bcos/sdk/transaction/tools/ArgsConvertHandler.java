package org.fisco.bcos.sdk.transaction.tools;

import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.fisco.bcos.sdk.abi.AbiDefinition;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.Utils;
import org.fisco.bcos.sdk.abi.datatypes.Array;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArgsConvertHandler {
    protected static Logger log = LoggerFactory.getLogger(ArgsConvertHandler.class);

    public static List<Type> tryConvertToSolArgs(
            List<Object> javaArgs, AbiDefinition abiDefinition) {
        try {
            int size = javaArgs.size();
            List<Type> solArgs = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                AbiDefinition.NamedType solArgDef = abiDefinition.getInputs().get(i);
                Object javaArg = javaArgs.get(i);
                javaArg = unifyJavaArgs(solArgDef.getType(), javaArg);
                Type solArg = convertToSolType(solArgDef, javaArg);
                solArgs.add(solArg);
            }
            return solArgs;
        } catch (Exception ex) {
            log.debug("error converting to sol args from java args", ex);
            return null;
        }
    }

    private static Type convertToSolType(AbiDefinition.NamedType namedType, Object javaArg) {
        try {
            TypeReference<?> typeReference = ContractAbiUtil.paramInput(namedType);
            if (Array.class.isAssignableFrom(typeReference.getClassType())) {
                java.lang.reflect.Type elementType =
                        ContractAbiUtil.resolveArrayBasicType(typeReference);
                Class<?> elementClass = Utils.getClassType(elementType);
                List solidityArgs = Utils.typeMapWithoutGenericType((List) javaArg, elementClass);
                Class<?> arrayClass = Utils.getClassType(typeReference.getType());
                Constructor ctor = arrayClass.getConstructor(List.class);
                return (Type) ctor.newInstance(solidityArgs);
            }
            Class<?> solditityArgClass = ContractTypeUtil.getType(namedType.getType());
            Constructor ctor = solditityArgClass.getDeclaredConstructor(javaArg.getClass());
            return (Type) ctor.newInstance(javaArg);
        } catch (Exception ex) {
            throw new RuntimeException("Unexpected exception", ex);
        }
    }

    private static Object unifyJavaArgs(String typeName, Object javaArg) {
        AbiDefinition.NamedType.Type type = new AbiDefinition.NamedType.Type(typeName);
        if (type.arrayType()) {
            String elementType = type.baseName;
            List<Object> result = new ArrayList<>();
            // Array
            if (javaArg.getClass().isArray()) {
                int length = java.lang.reflect.Array.getLength(javaArg);
                for (int i = 0; i < length; i++) {
                    Object element = java.lang.reflect.Array.get(javaArg, i);
                    result.add(unifyBasic(elementType, element));
                }
                return result;
            }
            // Iteratable
            else if (javaArg instanceof Iterable) {
                Iterable iterable = (Iterable) javaArg;
                for (Object element : iterable) {
                    result.add(unifyBasic(elementType, element));
                }
                return result;
            } else {
                // [1,2]
                String arrayStr = javaArg.toString();
                int leftBraceIndex = arrayStr.indexOf('[');
                int rightBraceIndex = arrayStr.indexOf(']', leftBraceIndex);
                return unifyJavaArgs(
                        typeName,
                        arrayStr.substring(leftBraceIndex + 1, rightBraceIndex).split(","));
            }
        } else {
            return unifyBasic(typeName, javaArg);
        }
    }

    private static Object unifyBasic(String type, Object value) {
        String strVal = value.toString();
        switch (type) {
            case "address":
            case "string":
            case "bytes1":
            case "bytes2":
            case "bytes3":
            case "bytes4":
            case "bytes5":
            case "bytes6":
            case "bytes7":
            case "bytes8":
            case "bytes9":
            case "bytes10":
            case "bytes11":
            case "bytes12":
            case "bytes13":
            case "bytes14":
            case "bytes15":
            case "bytes16":
            case "bytes17":
            case "bytes18":
            case "bytes19":
            case "bytes20":
            case "bytes21":
            case "bytes22":
            case "bytes23":
            case "bytes24":
            case "bytes25":
            case "bytes26":
            case "bytes27":
            case "bytes28":
            case "bytes29":
            case "bytes30":
            case "bytes31":
            case "bytes32":
            case "bytes":
                return value;
            case "bool":
                return Boolean.valueOf(strVal);
            case "uint8":
            case "int8":
            case "uint16":
            case "int16":
            case "uint24":
            case "int24":
            case "uint32":
            case "int32":
            case "uint40":
            case "int40":
            case "uint48":
            case "int48":
            case "uint56":
            case "int56":
            case "uint64":
            case "int64":
            case "uint72":
            case "int72":
            case "uint80":
            case "int80":
            case "uint88":
            case "int88":
            case "uint96":
            case "int96":
            case "uint104":
            case "int104":
            case "uint112":
            case "int112":
            case "uint120":
            case "int120":
            case "uint128":
            case "int128":
            case "uint136":
            case "int136":
            case "uint144":
            case "int144":
            case "uint152":
            case "int152":
            case "uint160":
            case "int160":
            case "uint168":
            case "int168":
            case "uint176":
            case "int176":
            case "uint184":
            case "int184":
            case "uint192":
            case "int192":
            case "uint200":
            case "int200":
            case "uint208":
            case "int208":
            case "uint216":
            case "int216":
            case "uint224":
            case "int224":
            case "uint232":
            case "int232":
            case "uint240":
            case "int240":
            case "uint248":
            case "int248":
            case "uint256":
            case "int256":
                if (strVal.startsWith("0x") || strVal.startsWith("0X")) {
                    return new BigInteger(strVal.substring(2), 16);
                }
                return new BigInteger(strVal, 10);
            default:
                return null;
        }
    }
}
