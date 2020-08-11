package org.fisco.bcos.sdk.contract.precompiled.crud;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.datatypes.Utf8String;
import org.fisco.bcos.sdk.abi.datatypes.generated.Int256;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple4;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple5;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.Contract;
import org.fisco.bcos.sdk.contract.exceptions.ContractException;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.callback.TransactionSucCallback;

@SuppressWarnings("unchecked")
public class CRUD extends Contract {
    public static final String[] BINARY_ARRAY = {};

    public static final String BINARY = String.join("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {};

    public static final String SM_BINARY = String.join("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"constant\":false,\"inputs\":[{\"name\":\"tableName\",\"type\":\"string\"},{\"name\":\"key\",\"type\":\"string\"},{\"name\":\"entry\",\"type\":\"string\"},{\"name\":\"condition\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"}],\"name\":\"update\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"tableName\",\"type\":\"string\"}],\"name\":\"desc\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"tableName\",\"type\":\"string\"},{\"name\":\"key\",\"type\":\"string\"},{\"name\":\"condition\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"}],\"name\":\"select\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"tableName\",\"type\":\"string\"},{\"name\":\"key\",\"type\":\"string\"},{\"name\":\"entry\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"}],\"name\":\"insert\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"tableName\",\"type\":\"string\"},{\"name\":\"key\",\"type\":\"string\"},{\"name\":\"condition\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"}],\"name\":\"remove\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = String.join("", ABI_ARRAY);

    public static final String FUNC_UPDATE = "update";

    public static final String FUNC_DESC = "desc";

    public static final String FUNC_SELECT = "select";

    public static final String FUNC_INSERT = "insert";

    public static final String FUNC_REMOVE = "remove";

    protected CRUD(String contractAddress, Client client, CryptoInterface credential) {
        super(getBinary(credential), contractAddress, client, credential);
    }

    public static String getBinary(CryptoInterface credential) {
        return (credential.getCryptoTypeConfig() == CryptoInterface.ECDSA_TYPE
                ? BINARY
                : SM_BINARY);
    }

    public TransactionReceipt update(
            String tableName, String key, String entry, String condition, String param4) {
        final Function function =
                new Function(
                        FUNC_UPDATE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(tableName),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(key),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(entry),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(condition),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(param4)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void update(
            String tableName,
            String key,
            String entry,
            String condition,
            String param4,
            TransactionSucCallback callback) {
        final Function function =
                new Function(
                        FUNC_UPDATE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(tableName),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(key),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(entry),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(condition),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(param4)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForUpdate(
            String tableName, String key, String entry, String condition, String param4) {
        final Function function =
                new Function(
                        FUNC_UPDATE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(tableName),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(key),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(entry),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(condition),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(param4)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple5<String, String, String, String, String> getUpdateInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_UPDATE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple5<String, String, String, String, String>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (String) results.get(2).getValue(),
                (String) results.get(3).getValue(),
                (String) results.get(4).getValue());
    }

    public Tuple1<BigInteger> getUpdateOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_UPDATE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public Tuple2<String, String> desc(String tableName) throws ContractException {
        final Function function =
                new Function(
                        FUNC_DESC,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(tableName)),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple2<String, String>(
                (String) results.get(0).getValue(), (String) results.get(1).getValue());
    }

    public String select(String tableName, String key, String condition, String param3)
            throws ContractException {
        final Function function =
                new Function(
                        FUNC_SELECT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(tableName),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(key),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(condition),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(param3)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public TransactionReceipt insert(String tableName, String key, String entry, String param3) {
        final Function function =
                new Function(
                        FUNC_INSERT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(tableName),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(key),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(entry),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(param3)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void insert(
            String tableName,
            String key,
            String entry,
            String param3,
            TransactionSucCallback callback) {
        final Function function =
                new Function(
                        FUNC_INSERT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(tableName),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(key),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(entry),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(param3)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForInsert(
            String tableName, String key, String entry, String param3) {
        final Function function =
                new Function(
                        FUNC_INSERT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(tableName),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(key),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(entry),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(param3)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple4<String, String, String, String> getInsertInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_INSERT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple4<String, String, String, String>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (String) results.get(2).getValue(),
                (String) results.get(3).getValue());
    }

    public Tuple1<BigInteger> getInsertOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_INSERT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt remove(
            String tableName, String key, String condition, String param3) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(tableName),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(key),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(condition),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(param3)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void remove(
            String tableName,
            String key,
            String condition,
            String param3,
            TransactionSucCallback callback) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(tableName),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(key),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(condition),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(param3)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForRemove(
            String tableName, String key, String condition, String param3) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(tableName),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(key),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(condition),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(param3)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple4<String, String, String, String> getRemoveInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple4<String, String, String, String>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (String) results.get(2).getValue(),
                (String) results.get(3).getValue());
    }

    public Tuple1<BigInteger> getRemoveOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public static CRUD load(String contractAddress, Client client, CryptoInterface credential) {
        return new CRUD(contractAddress, client, credential);
    }

    public static CRUD deploy(Client client, CryptoInterface credential) throws ContractException {
        return deploy(CRUD.class, client, credential, getBinary(credential), "");
    }
}
