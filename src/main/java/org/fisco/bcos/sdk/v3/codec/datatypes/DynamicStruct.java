package org.fisco.bcos.sdk.v3.codec.datatypes;

import java.util.*;

public class DynamicStruct extends DynamicArray<Type> implements StructType {
    private final List<Class<Type>> itemTypes = new ArrayList<>();
    private final List<Type> componentTypes = new ArrayList<>();

    private DynamicStruct(Class<Type> type, List<Type> values) {
        super(type, values);
        for (Type value : values) {
            itemTypes.add((Class<Type>) value.getClass());
            componentTypes.add(value);
        }
    }

    public DynamicStruct(List<Type> values) {
        super(Type.class, values);
        for (Type value : values) {
            itemTypes.add((Class<Type>) value.getClass());
            componentTypes.add(value);
        }
    }

    @SafeVarargs
    public DynamicStruct(Class<Type> type, Type... values) {
        this(type, Arrays.asList(values));
    }

    public DynamicStruct(Type... values) {
        this(Type.class, Arrays.asList(values));
    }

    @Override
    public String getTypeAsString() {
        final StringBuilder type = new StringBuilder("(");
        for (int i = 0; i < itemTypes.size(); ++i) {
            final Class<Type> cls = itemTypes.get(i);
            if (StructType.class.isAssignableFrom(cls) || Array.class.isAssignableFrom(cls)) {
                type.append(getValue().get(i).getTypeAsString());
            } else {
                type.append(AbiTypes.getTypeAString(cls));
            }
            if (i < itemTypes.size() - 1) {
                type.append(",");
            }
        }
        type.append(")");
        return type.toString();
    }

    @Override
    public List<Type> getComponentTypes() {
        return componentTypes;
    }

    @Override
    public int bytes32PaddedLength() {
        return super.bytes32PaddedLength() + 32;
    }
}
