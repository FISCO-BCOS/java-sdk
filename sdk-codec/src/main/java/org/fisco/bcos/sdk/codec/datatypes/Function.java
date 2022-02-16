package org.fisco.bcos.sdk.codec.datatypes;

import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.codec.Utils;

/** Function type. */
public class Function {
    private String name;
    private List<Type> inputParameters;
    private List<TypeReference<Type>> outputParameters;
    private int transactionAttribute = 0;

    public Function(
            String name, List<Type> inputParameters, List<TypeReference<?>> outputParameters) {
        this.name = name;
        this.inputParameters = inputParameters;
        this.outputParameters = Utils.convert(outputParameters);
        this.transactionAttribute = 0;
    }

    public Function(
            String name,
            List<Type> inputParameters,
            List<TypeReference<?>> outputParameters,
            int transactionAttribute) {
        this.name = name;
        this.inputParameters = inputParameters;
        this.outputParameters = Utils.convert(outputParameters);
        this.transactionAttribute = transactionAttribute;
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

    public int getTransactionAttribute() {
        return transactionAttribute;
    }

    public void setTransactionAttribute(int transactionAttribute) {
        this.transactionAttribute = transactionAttribute;
    }
}
