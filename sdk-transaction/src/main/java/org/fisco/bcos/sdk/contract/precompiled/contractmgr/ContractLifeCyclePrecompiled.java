package org.fisco.bcos.sdk.contract.precompiled.contractmgr;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Address;
import org.fisco.bcos.sdk.abi.datatypes.DynamicArray;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.datatypes.Utf8String;
import org.fisco.bcos.sdk.abi.datatypes.generated.Int256;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.Contract;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class ContractLifeCyclePrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {};

    public static final String BINARY = String.join("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {};

    public static final String SM_BINARY = String.join("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"constant\":true,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"getStatus\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"},{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"unfreeze\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"freeze\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"contractAddr\",\"type\":\"address\"},{\"name\":\"userAddr\",\"type\":\"address\"}],\"name\":\"grantManager\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"addr\",\"type\":\"address\"}],\"name\":\"listManager\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"},{\"name\":\"\",\"type\":\"address[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"}]"
    };

    public static final String ABI = String.join("", ABI_ARRAY);

    public static final String FUNC_GETSTATUS = "getStatus";

    public static final String FUNC_UNFREEZE = "unfreeze";

    public static final String FUNC_FREEZE = "freeze";

    public static final String FUNC_GRANTMANAGER = "grantManager";

    public static final String FUNC_LISTMANAGER = "listManager";

    protected ContractLifeCyclePrecompiled(
            String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoInterface()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoInterface cryptoInterface) {
        return (cryptoInterface.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE
                ? BINARY
                : SM_BINARY);
    }

    public Tuple2<BigInteger, String> getStatus(String addr) throws ContractException {
        final Function function =
                new Function(
                        FUNC_GETSTATUS,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(addr)),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Int256>() {},
                                new TypeReference<Utf8String>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple2<BigInteger, String>(
                (BigInteger) results.get(0).getValue(), (String) results.get(1).getValue());
    }

    public TransactionReceipt unfreeze(String addr) {
        final Function function =
                new Function(
                        FUNC_UNFREEZE,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(addr)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void unfreeze(String addr, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_UNFREEZE,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(addr)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForUnfreeze(String addr) {
        final Function function =
                new Function(
                        FUNC_UNFREEZE,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(addr)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getUnfreezeInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_UNFREEZE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getUnfreezeOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_UNFREEZE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt freeze(String addr) {
        final Function function =
                new Function(
                        FUNC_FREEZE,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(addr)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void freeze(String addr, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_FREEZE,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(addr)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForFreeze(String addr) {
        final Function function =
                new Function(
                        FUNC_FREEZE,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(addr)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getFreezeInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_FREEZE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getFreezeOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_FREEZE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt grantManager(String contractAddr, String userAddr) {
        final Function function =
                new Function(
                        FUNC_GRANTMANAGER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(contractAddr),
                                new org.fisco.bcos.sdk.abi.datatypes.Address(userAddr)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void grantManager(String contractAddr, String userAddr, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_GRANTMANAGER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(contractAddr),
                                new org.fisco.bcos.sdk.abi.datatypes.Address(userAddr)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForGrantManager(String contractAddr, String userAddr) {
        final Function function =
                new Function(
                        FUNC_GRANTMANAGER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(contractAddr),
                                new org.fisco.bcos.sdk.abi.datatypes.Address(userAddr)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<String, String> getGrantManagerInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_GRANTMANAGER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {}, new TypeReference<Address>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, String>(
                (String) results.get(0).getValue(), (String) results.get(1).getValue());
    }

    public Tuple1<BigInteger> getGrantManagerOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_GRANTMANAGER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public Tuple2<BigInteger, List<String>> listManager(String addr) throws ContractException {
        final Function function =
                new Function(
                        FUNC_LISTMANAGER,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(addr)),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Int256>() {},
                                new TypeReference<DynamicArray<Address>>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple2<BigInteger, List<String>>(
                (BigInteger) results.get(0).getValue(),
                convertToNative((List<Address>) results.get(1).getValue()));
    }

    public static ContractLifeCyclePrecompiled load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new ContractLifeCyclePrecompiled(contractAddress, client, credential);
    }

    public static ContractLifeCyclePrecompiled deploy(Client client, CryptoKeyPair credential)
            throws ContractException {
        return deploy(
                ContractLifeCyclePrecompiled.class,
                client,
                credential,
                getBinary(client.getCryptoInterface()),
                "");
    }
}
