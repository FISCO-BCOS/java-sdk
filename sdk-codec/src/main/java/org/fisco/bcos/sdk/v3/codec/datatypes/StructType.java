package org.fisco.bcos.sdk.v3.codec.datatypes;

import java.util.List;

public interface StructType {
    List<Type> getComponentTypes();
}
