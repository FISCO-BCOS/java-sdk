package org.fisco.bcos.sdk.v3.codec.datatypes;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.codec.Utils;

/** Function type. */
public class Function {
    private String name;
    private List<Type> inputParameters;
    private List<TypeReference<Type>> outputParameters;
    private int transactionAttribute = 0;
    private BigInteger value;
    private String nonce;
    private BigInteger blockLimit;

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

    public Function(
            String name,
            List<Type> inputParameters,
            List<TypeReference<?>> outputParameters,
            int transactionAttribute,
            BigInteger value) {
        this(name, inputParameters, outputParameters, transactionAttribute);
        this.value = value;
    }

    public Function(
            String name,
            List<Type> inputParameters,
            List<TypeReference<?>> outputParameters,
            int transactionAttribute,
            BigInteger value,
            String nonce,
            BigInteger blockLimit) {
        this(name, inputParameters, outputParameters, transactionAttribute, value);
        this.nonce = nonce;
        this.blockLimit = blockLimit;
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

    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public BigInteger getBlockLimit() {
        return blockLimit;
    }

    public void setBlockLimit(BigInteger blockLimit) {
        this.blockLimit = blockLimit;
    }
}
