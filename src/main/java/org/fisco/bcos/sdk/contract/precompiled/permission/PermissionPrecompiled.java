package org.fisco.bcos.sdk.contract.precompiled.permission;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Address;
import org.fisco.bcos.sdk.abi.datatypes.Function;
import org.fisco.bcos.sdk.abi.datatypes.Type;
import org.fisco.bcos.sdk.abi.datatypes.Utf8String;
import org.fisco.bcos.sdk.abi.datatypes.generated.Int256;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.Contract;
import org.fisco.bcos.sdk.contract.exceptions.ContractException;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.callback.TransactionCallback;

@SuppressWarnings("unchecked")
public class PermissionPrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {};

    public static final String BINARY = String.join("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {};

    public static final String SM_BINARY = String.join("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"constant\":false,\"inputs\":[{\"name\":\"table_name\",\"type\":\"string\"},{\"name\":\"addr\",\"type\":\"string\"}],\"name\":\"insert\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"table_name\",\"type\":\"string\"}],\"name\":\"queryByName\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"table_name\",\"type\":\"string\"},{\"name\":\"addr\",\"type\":\"string\"}],\"name\":\"remove\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"contractAddr\",\"type\":\"address\"}],\"name\":\"queryPermission\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"contractAddr\",\"type\":\"address\"},{\"name\":\"user\",\"type\":\"address\"}],\"name\":\"grantWrite\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"contractAddr\",\"type\":\"address\"},{\"name\":\"user\",\"type\":\"address\"}],\"name\":\"revokeWrite\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = String.join("", ABI_ARRAY);

    public static final String FUNC_INSERT = "insert";

    public static final String FUNC_QUERYBYNAME = "queryByName";

    public static final String FUNC_REMOVE = "remove";

    public static final String FUNC_QUERYPERMISSION = "queryPermission";

    public static final String FUNC_GRANTWRITE = "grantWrite";

    public static final String FUNC_REVOKEWRITE = "revokeWrite";

    protected PermissionPrecompiled(
            String contractAddress, Client client, CryptoInterface credential) {
        super(getBinary(credential), contractAddress, client, credential);
    }

    public static String getBinary(CryptoInterface credential) {
        return (credential.getCryptoTypeConfig() == CryptoInterface.ECDSA_TYPE
                ? BINARY
                : SM_BINARY);
    }

    public TransactionReceipt insert(String table_name, String addr) {
        final Function function =
                new Function(
                        FUNC_INSERT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(table_name),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(addr)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void insert(String table_name, String addr, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_INSERT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(table_name),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(addr)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForInsert(String table_name, String addr) {
        final Function function =
                new Function(
                        FUNC_INSERT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(table_name),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(addr)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<String, String> getInsertInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_INSERT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, String>(
                (String) results.get(0).getValue(), (String) results.get(1).getValue());
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

    public String queryByName(String table_name) throws ContractException {
        final Function function =
                new Function(
                        FUNC_QUERYBYNAME,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(table_name)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public TransactionReceipt remove(String table_name, String addr) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(table_name),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(addr)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void remove(String table_name, String addr, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(table_name),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(addr)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForRemove(String table_name, String addr) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(table_name),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(addr)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<String, String> getRemoveInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, String>(
                (String) results.get(0).getValue(), (String) results.get(1).getValue());
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

    public String queryPermission(String contractAddr) throws ContractException {
        final Function function =
                new Function(
                        FUNC_QUERYPERMISSION,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(contractAddr)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public TransactionReceipt grantWrite(String contractAddr, String user) {
        final Function function =
                new Function(
                        FUNC_GRANTWRITE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(contractAddr),
                                new org.fisco.bcos.sdk.abi.datatypes.Address(user)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void grantWrite(String contractAddr, String user, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_GRANTWRITE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(contractAddr),
                                new org.fisco.bcos.sdk.abi.datatypes.Address(user)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForGrantWrite(String contractAddr, String user) {
        final Function function =
                new Function(
                        FUNC_GRANTWRITE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(contractAddr),
                                new org.fisco.bcos.sdk.abi.datatypes.Address(user)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<String, String> getGrantWriteInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_GRANTWRITE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {}, new TypeReference<Address>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, String>(
                (String) results.get(0).getValue(), (String) results.get(1).getValue());
    }

    public Tuple1<BigInteger> getGrantWriteOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_GRANTWRITE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt revokeWrite(String contractAddr, String user) {
        final Function function =
                new Function(
                        FUNC_REVOKEWRITE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(contractAddr),
                                new org.fisco.bcos.sdk.abi.datatypes.Address(user)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void revokeWrite(String contractAddr, String user, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_REVOKEWRITE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(contractAddr),
                                new org.fisco.bcos.sdk.abi.datatypes.Address(user)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForRevokeWrite(String contractAddr, String user) {
        final Function function =
                new Function(
                        FUNC_REVOKEWRITE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(contractAddr),
                                new org.fisco.bcos.sdk.abi.datatypes.Address(user)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<String, String> getRevokeWriteInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_REVOKEWRITE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {}, new TypeReference<Address>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, String>(
                (String) results.get(0).getValue(), (String) results.get(1).getValue());
    }

    public Tuple1<BigInteger> getRevokeWriteOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_REVOKEWRITE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public static PermissionPrecompiled load(
            String contractAddress, Client client, CryptoInterface credential) {
        return new PermissionPrecompiled(contractAddress, client, credential);
    }

    public static PermissionPrecompiled deploy(Client client, CryptoInterface credential)
            throws ContractException {
        return deploy(PermissionPrecompiled.class, client, credential, getBinary(credential), "");
    }
}
