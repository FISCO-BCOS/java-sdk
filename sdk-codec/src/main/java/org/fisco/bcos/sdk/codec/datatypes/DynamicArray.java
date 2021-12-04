package org.fisco.bcos.sdk.codec.datatypes;

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
                        : (Class<T>) AbiTypes.getType(values[0].getTypeAsString()),
                values);
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public DynamicArray(List<T> values) {
        super(
                StructType.class.isAssignableFrom(values.get(0).getClass())
                        ? (Class<T>) values.get(0).getClass()
                        : (Class<T>) AbiTypes.getType(values.get(0).getTypeAsString()),
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
        String type;
        if (!value.isEmpty() && StructType.class.isAssignableFrom(value.get(0).getClass())) {
            type = value.get(0).getTypeAsString();
        } else {
            type = AbiTypes.getTypeAString(getComponentType());
        }
        return type + "[]";
    }
}
