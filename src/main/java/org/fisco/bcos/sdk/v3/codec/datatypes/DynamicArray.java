package org.fisco.bcos.sdk.v3.codec.datatypes;

import static org.fisco.bcos.sdk.v3.codec.Utils.getTypeName;

import java.util.List;

/** Dynamic array type. */
public class DynamicArray<T extends Type> extends Array<T> {
    @Deprecated
    @SafeVarargs
    @SuppressWarnings({"unchecked"})
    public DynamicArray(T... values) {
        super(
                StructType.class.isAssignableFrom(values[0].getClass())
                        ? (Class<T>) values[0].getClass()
                        : (Array.class.isAssignableFrom(values[0].getClass())
                                ? (Class<T>) values[0].getClass()
                                : (Class<T>) AbiTypes.getType(values[0].getTypeAsString())),
                values);
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public DynamicArray(List<T> values) {
        super(
                StructType.class.isAssignableFrom(values.get(0).getClass())
                        ? (Class<T>) values.get(0).getClass()
                        : (Array.class.isAssignableFrom(values.get(0).getClass())
                                ? (Class<T>) values.get(0).getClass()
                                : (Class<T>) AbiTypes.getType(values.get(0).getTypeAsString())),
                values);
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    private DynamicArray(String type) {
        super((Class<T>) AbiTypes.getType(type));
    }

    @Deprecated
    public static DynamicArray empty(String type) {
        return new DynamicArray(type);
    }

    public DynamicArray(Class<T> type, List<T> values) {
        super(type, values);
    }

    @SafeVarargs
    public DynamicArray(Class<T> type, T... values) {
        super(type, values);
    }

    @Override
    public int bytes32PaddedLength() {
        return super.bytes32PaddedLength() + MAX_BYTE_LENGTH;
    }

    @Override
    public String getTypeAsString() {
        String paramsType;
        if (!value.isEmpty() && StructType.class.isAssignableFrom(value.get(0).getClass())) {
            paramsType = value.get(0).getTypeAsString();
        } else if (StructType.class.isAssignableFrom(this.type)) {
            try {
                T t = this.type.newInstance();
                paramsType = t.getTypeAsString();
            } catch (InstantiationException | IllegalAccessException e) {
                // struct type is not defined default constructor
                paramsType = AbiTypes.getTypeAString(getComponentType());
            }
        } else if (Array.class.isAssignableFrom(this.type)) {
            if (!value.isEmpty()) {
                paramsType = value.get(0).getTypeAsString();
            } else {
                paramsType = getTypeName((type));
            }
        } else {
            paramsType = AbiTypes.getTypeAString(getComponentType());
        }
        return paramsType + "[]";
    }

    public void setFixed(boolean fixed) {
        this.isFixed = fixed;
    }
}
