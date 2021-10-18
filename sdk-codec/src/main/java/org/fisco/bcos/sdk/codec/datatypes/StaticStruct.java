package org.fisco.bcos.sdk.codec.datatypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaticStruct extends StaticArray<Type> implements StructType {
    private final List<Class<Type>> itemTypes = new ArrayList<>();
    private final List<Type> componentTypes = new ArrayList<>();

    public StaticStruct(List<Type> values) {
        super(Type.class, values.size(), values);
        for (Type value : values) {
            itemTypes.add((Class<Type>) value.getClass());
            componentTypes.add(value);
        }
    }

    public StaticStruct(Type... values) {
        this(Arrays.asList(values));
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
}
