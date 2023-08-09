package org.fisco.bcos.sdk.abi.datatypes;

import static org.fisco.bcos.sdk.abi.Utils.getTypeName;

import java.util.List;
import org.fisco.bcos.sdk.abi.datatypes.generated.AbiTypes;

/** Dynamic array type. */
public class DynamicArray<T extends Type> extends Array<T> {

    @SafeVarargs
    public DynamicArray(T... values) {
        super(
                StructType.class.isAssignableFrom(values[0].getClass())
                        ? (Class<T>) values[0].getClass()
                        : (Array.class.isAssignableFrom(values[0].getClass())
                                ? (Class<T>) values[0].getClass()
                                : (Class<T>) AbiTypes.getType(values[0].getTypeAsString())),
                values);
    }

    public DynamicArray(List<T> values) {
        super(
                StructType.class.isAssignableFrom(values.get(0).getClass())
                        ? (Class<T>) values.get(0).getClass()
                        : (Array.class.isAssignableFrom(values.get(0).getClass())
                                ? (Class<T>) values.get(0).getClass()
                                : (Class<T>) AbiTypes.getType(values.get(0).getTypeAsString())),
                values);
    }

    private DynamicArray(String type) {
        super(type);
    }

    public DynamicArray(Class<T> type, List<T> values) {
        super(type, values);
    }

    @SafeVarargs
    public DynamicArray(Class<T> type, T... values) {
        super(type, values);
    }

    public static DynamicArray empty(String type) {
        return new DynamicArray(type);
    }

    @Override
    public boolean dynamicType() {
        return true;
    }

    @Override
    public String getTypeAsString() {
        String paramsType;
        if (!value.isEmpty() && StructType.class.isAssignableFrom(value.get(0).getClass())) {
            paramsType = value.get(0).getTypeAsString();
        } else if (StructType.class.isAssignableFrom(this.typeClass)) {
            try {
                T t = this.typeClass.newInstance();
                paramsType = t.getTypeAsString();
            } catch (InstantiationException | IllegalAccessException e) {
                // struct type is not defined default constructor
                paramsType = AbiTypes.getTypeAString(this.typeClass);
            }
        } else if (Array.class.isAssignableFrom(this.typeClass)) {
            if (!value.isEmpty()) {
                paramsType = value.get(0).getTypeAsString();
            } else {
                paramsType = getTypeName((typeClass));
            }
        } else {
            paramsType = AbiTypes.getTypeAString(typeClass);
        }
        return paramsType + "[]";
    }

    @Override
    public int offset() {
        return 1;
    }
}
