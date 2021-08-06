package org.fisco.bcos.sdk.contract.precompiled.consensus;

import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.datatypes.Utf8String;
import org.fisco.bcos.sdk.abi.datatypes.generated.Int256;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.Contract;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConsensusPrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {};

    public static final String BINARY = String.join("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {};

    public static final String SM_BINARY = String.join("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
            "[{\"constant\":false,\"inputs\":[{\"name\":\"\",\"type\":\"string\"}],\"name\":\"addObserver\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"\",\"type\":\"string\"}],\"name\":\"remove\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"\",\"type\":\"string\"}],\"name\":\"addSealer\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = String.join("", ABI_ARRAY);

    public static final String FUNC_ADDOBSERVER = "addObserver";

    public static final String FUNC_REMOVE = "remove";

    public static final String FUNC_ADDSEALER = "addSealer";

    protected ConsensusPrecompiled(
            String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public TransactionReceipt addObserver(String param0) {
        final Function function =
                new Function(
                        FUNC_ADDOBSERVER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(param0)),
                        Collections.<TypeReference<?>>emptyList());
        return this.executeTransaction(function);
    }

    public void addObserver(String param0, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_ADDOBSERVER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(param0)),
                        Collections.<TypeReference<?>>emptyList());
        this.asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForAddObserver(String param0) {
        final Function function =
                new Function(
                        FUNC_ADDOBSERVER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(param0)),
                        Collections.<TypeReference<?>>emptyList());
        return this.createSignedTransaction(function);
    }

    public Tuple1<String> getAddObserverInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_ADDOBSERVER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {
                        }));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getAddObserverOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_ADDOBSERVER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {
                        }));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt remove(String param0) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(param0)),
                        Collections.<TypeReference<?>>emptyList());
        return this.executeTransaction(function);
    }

    public void remove(String param0, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(param0)),
                        Collections.<TypeReference<?>>emptyList());
        this.asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForRemove(String param0) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(param0)),
                        Collections.<TypeReference<?>>emptyList());
        return this.createSignedTransaction(function);
    }

    public Tuple1<String> getRemoveInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {
                        }));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getRemoveOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {
                        }));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt addSealer(String param0) {
        final Function function =
                new Function(
                        FUNC_ADDSEALER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(param0)),
                        Collections.<TypeReference<?>>emptyList());
        return this.executeTransaction(function);
    }

    public void addSealer(String param0, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_ADDSEALER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(param0)),
                        Collections.<TypeReference<?>>emptyList());
        this.asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForAddSealer(String param0) {
        final Function function =
                new Function(
                        FUNC_ADDSEALER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(param0)),
                        Collections.<TypeReference<?>>emptyList());
        return this.createSignedTransaction(function);
    }

    public Tuple1<String> getAddSealerInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_ADDSEALER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {
                        }));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getAddSealerOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_ADDSEALER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {
                        }));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public static ConsensusPrecompiled load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new ConsensusPrecompiled(contractAddress, client, credential);
    }

    public static ConsensusPrecompiled deploy(Client client, CryptoKeyPair credential)
            throws ContractException {
        return Contract.deploy(
                ConsensusPrecompiled.class,
                client,
                credential,
                getBinary(client.getCryptoSuite()),
                null);
    }
}
