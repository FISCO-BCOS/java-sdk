package org.fisco.bcos.sdk.contract.precompiled.cns;

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
import org.fisco.bcos.sdk.abi.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple4;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.contract.Contract;
import org.fisco.bcos.sdk.crypto.CryptoInterface;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class CNSPrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {};

    public static final String BINARY = String.join("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {};

    public static final String SM_BINARY = String.join("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"constant\":true,\"inputs\":[{\"name\":\"name\",\"type\":\"string\"}],\"name\":\"selectByName\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"version\",\"type\":\"string\"}],\"name\":\"selectByNameAndVersion\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"version\",\"type\":\"string\"},{\"name\":\"addr\",\"type\":\"string\"},{\"name\":\"abi\",\"type\":\"string\"}],\"name\":\"insert\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"version\",\"type\":\"string\"}],\"name\":\"getContractAddress\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"}]"
    };

    public static final String ABI = String.join("", ABI_ARRAY);

    public static final String FUNC_SELECTBYNAME = "selectByName";

    public static final String FUNC_SELECTBYNAMEANDVERSION = "selectByNameAndVersion";

    public static final String FUNC_INSERT = "insert";

    public static final String FUNC_GETCONTRACTADDRESS = "getContractAddress";

    protected CNSPrecompiled(String contractAddress, Client client, CryptoInterface credential) {
        super(getBinary(credential), contractAddress, client, credential);
    }

    public static String getBinary(CryptoInterface credential) {
        return (credential.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public String selectByName(String name) throws ContractException {
        final Function function =
                new Function(
                        FUNC_SELECTBYNAME,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Utf8String(name)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public String selectByNameAndVersion(String name, String version) throws ContractException {
        final Function function =
                new Function(
                        FUNC_SELECTBYNAMEANDVERSION,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(name),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(version)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public TransactionReceipt insert(String name, String version, String addr, String abi) {
        final Function function =
                new Function(
                        FUNC_INSERT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(name),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(version),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(addr),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(abi)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void insert(
            String name, String version, String addr, String abi, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_INSERT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(name),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(version),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(addr),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(abi)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForInsert(
            String name, String version, String addr, String abi) {
        final Function function =
                new Function(
                        FUNC_INSERT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(name),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(version),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(addr),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(abi)),
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
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public String getContractAddress(String name, String version) throws ContractException {
        final Function function =
                new Function(
                        FUNC_GETCONTRACTADDRESS,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(name),
                                new org.fisco.bcos.sdk.abi.datatypes.Utf8String(version)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public static CNSPrecompiled load(
            String contractAddress, Client client, CryptoInterface credential) {
        return new CNSPrecompiled(contractAddress, client, credential);
    }

    public static CNSPrecompiled deploy(Client client, CryptoInterface credential)
            throws ContractException {
        return deploy(CNSPrecompiled.class, client, credential, getBinary(credential), "");
    }
}
