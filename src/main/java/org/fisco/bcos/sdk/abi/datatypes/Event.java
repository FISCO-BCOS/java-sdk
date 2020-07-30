package org.fisco.bcos.sdk.abi.datatypes;

import java.util.List;

import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.Utils;


/** Event wrapper type. */
public class Event {
    private String name;
    private List<TypeReference<Type>> parameters;

    public Event(String name, List<TypeReference<?>> parameters) {
        this.name = name;
        this.parameters = Utils.convert(parameters);
    }

    public String getName() {
        return name;
    }

    public List<TypeReference<Type>> getParameters() {
        return parameters;
    }

}
