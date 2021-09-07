package org.fisco.bcos.sdk.codec.datatypes;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/** Fixed size array. */
public abstract class Array<T extends Type> implements Type<List<T>> {
    private final Class<T> type;
    protected final List<T> value;

    @SafeVarargs
    Array(Class<T> type, T... values) {
        this(type, Arrays.asList(values));
    }

    Array(Class<T> type, List<T> values) {
        valid(type, values);

        this.type = type;
        this.value = values;
    }

    @Override
    public int bytes32PaddedLength() {
        int length = 0;
        for (T t : value) {
            int valueLength = t.bytes32PaddedLength();
            length += valueLength;
        }
        return length;
    }

    public Class<T> getComponentType() {
        return type;
    }

    @Override
    public List<T> getValue() {
        return value;
    }

    @Override
    public abstract String getTypeAsString();

    private void valid(Class<T> type, List<T> values) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(values);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Array<?> array = (Array<?>) o;

        if (!type.equals(array.type)) {
            return false;
        }
        return Objects.equals(value, array.value);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
