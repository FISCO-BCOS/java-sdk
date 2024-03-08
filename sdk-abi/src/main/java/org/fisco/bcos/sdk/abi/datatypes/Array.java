package org.fisco.bcos.sdk.abi.datatypes;

import java.util.*;

/** Fixed size array. */
public abstract class Array<T extends Type> implements Type<List<T>> {

    private String type;
    protected Class<T> typeClass;
    protected final List<T> value;

    @SafeVarargs
    Array(String type, T... values) {
        if (!valid(values, type)) {
            throw new UnsupportedOperationException(
                    "If empty list is provided, use empty array instance");
        }

        this.type = type;
        this.value = Arrays.asList(values);
    }

    Array(String type, List<T> values) {
        if (!valid(values, type)) {
            throw new UnsupportedOperationException(
                    "If empty list is provided, use empty array instance");
        }

        this.type = type;
        this.value = values;
    }

    Array(String type) {
        this.type = type;
        this.value = Collections.emptyList();
    }

    Array(Class<T> type, List<T> values) {
        Objects.requireNonNull(type);
        if (!valid(values, type.getTypeName())) {
            throw new UnsupportedOperationException(
                    "If empty list is provided, use empty array instance");
        }
        this.typeClass = type;
        this.value = values;
    }

    @SafeVarargs
    Array(Class<T> type, T... values) {
        this(type, Arrays.asList(values));
    }

    @Override
    public List<T> getValue() {
        return value;
    }

    public List getNativeValue() {
        List list = new ArrayList(value.size());
        for (T t : value) {
            list.add(t.getValue());
        }

        return list;
    }

    @Override
    public String getTypeAsString() {
        return type;
    }

    private boolean valid(T[] values, String type) {
        return (values != null && values.length != 0) || type != null;
    }

    private boolean valid(List<T> values, String type) {
        return (values != null && !values.isEmpty()) || type != null;
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

        return value != null ? value.equals(array.value) : array.value == null;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public abstract boolean dynamicType();
}
