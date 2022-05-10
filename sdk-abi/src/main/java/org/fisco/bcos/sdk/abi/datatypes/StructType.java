package org.fisco.bcos.sdk.abi.datatypes;

import java.util.List;

public interface StructType {
    List<Type> getComponentTypes();
}
