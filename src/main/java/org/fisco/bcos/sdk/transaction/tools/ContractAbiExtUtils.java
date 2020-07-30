package org.fisco.bcos.sdk.transaction.tools;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.fisco.bcos.sdk.abi.AbiDefinition;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.transaction.domain.DynamicArrayReference;
import org.fisco.bcos.sdk.transaction.domain.StaticArrayReference;
import org.fisco.bcos.sdk.transaction.exception.BaseException;

public class ContractAbiExtUtils {

    /**
     * Convert NamedType to TypeReference which refs to class of Solidity type(Address, Uint256,
     * etc..)
     *
     * @param solTypeDef
     * @return
     * @throws BaseException
     */
    public static TypeReference<?> paramInput(AbiDefinition.NamedType solTypeDef)
            throws BaseException {
        AbiDefinition.NamedType.Type type = new AbiDefinition.NamedType.Type(solTypeDef.getType());
        // nested array , not support now.
        if (type.getDepth() > 1) {
            throw new BaseException(
                    201202, String.format("type:%s unsupported array decoding", type.getName()));
        }

        TypeReference<?> typeReference = null;
        if (type.dynamicArray()) {
            typeReference =
                    DynamicArrayReference.create(type.getBaseName(), solTypeDef.isIndexed());
        } else if (type.staticArray()) {
            typeReference =
                    StaticArrayReference.create(
                            type.getBaseName(), type.getDimensions(), solTypeDef.isIndexed());
        } else {
            typeReference =
                    TypeReference.create(
                            ContractTypeUtil.getType(solTypeDef.getType()), solTypeDef.isIndexed());
        }
        return typeReference;
    }

    public static Type resolveArrayBasicType(TypeReference<?> typeReference) {
        java.lang.reflect.Type typeRefGenericClass =
                typeReference.getClass().getGenericSuperclass();
        ParameterizedType arrayType =
                (ParameterizedType)
                        ((ParameterizedType) typeRefGenericClass).getActualTypeArguments()[0];
        java.lang.reflect.Type elementType = (arrayType).getActualTypeArguments()[0];
        return elementType;
    }
}
