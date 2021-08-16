package org.fisco.bcos.sdk.codec.datatypes;

import java.util.List;

public interface StructType {
    List<Type> getComponentTypes();
}
