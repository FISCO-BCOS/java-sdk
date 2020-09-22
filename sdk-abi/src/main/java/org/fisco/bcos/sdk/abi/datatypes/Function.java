package org.fisco.bcos.sdk.abi.datatypes;

import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.Utils;

/** Function type. */
public class Function {
    private String name;
    private List<Type> inputParameters;
    private List<TypeReference<Type>> outputParameters;

    public Function(
            String name, List<Type> inputParameters, List<TypeReference<?>> outputParameters) {
        this.name = name;
        this.inputParameters = inputParameters;
        this.outputParameters = Utils.convert(outputParameters);
    }

    public Function() {
        this.name = "";
        this.inputParameters = Collections.<Type>emptyList();
        this.outputParameters = Collections.<TypeReference<Type>>emptyList();
    }

    public String getName() {
        return name;
    }

    public List<Type> getInputParameters() {
        return inputParameters;
    }

    public List<TypeReference<Type>> getOutputParameters() {
        return outputParameters;
    }
}
