package org.fisco.bcos.sdk.v3.contract.auth.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Bytes4;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.utils.StringUtils;

@SuppressWarnings("unchecked")
public class ContractAuthPrecompiled extends Contract {
    public static final String[] ABI_ARRAY = {
        "[{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"contractAddr\",\"type\":\"address\"},{\"internalType\":\"bytes4\",\"name\":\"func\",\"type\":\"bytes4\"},{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"checkMethodAuth\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[3630574244,3556246924],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"closeDeployAuth\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[1455255684,438325841],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"contractAddr\",\"type\":\"address\"},{\"internalType\":\"bytes4\",\"name\":\"func\",\"type\":\"bytes4\"},{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"closeMethodAuth\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[3413924881,2232681703],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"_address\",\"type\":\"address\"}],\"name\":\"contractAvailable\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[747391567,2445967236],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[],\"name\":\"deployType\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"selector\":[390708905,1502262678],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"contractAddr\",\"type\":\"address\"}],\"name\":\"getAdmin\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[1693430315,3050872],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"path\",\"type\":\"address\"},{\"internalType\":\"bytes4\",\"name\":\"func\",\"type\":\"bytes4\"}],\"name\":\"getMethodAuth\",\"outputs\":[{\"internalType\":\"uint8\",\"name\":\"\",\"type\":\"uint8\"},{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"},{\"internalType\":\"string[]\",\"name\":\"\",\"type\":\"string[]\"}],\"selector\":[91771290,3467007364],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"hasDeployAuth\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"selector\":[1661302757,3314574843],\"stateMutability\":\"view\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"openDeployAuth\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[1632927897,40510205],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"contractAddr\",\"type\":\"address\"},{\"internalType\":\"bytes4\",\"name\":\"func\",\"type\":\"bytes4\"},{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"openMethodAuth\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[209893181,4148123484],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"contractAddr\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"admin\",\"type\":\"address\"}],\"name\":\"resetAdmin\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[3308279732,45314107],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"_address\",\"type\":\"address\"},{\"internalType\":\"bool\",\"name\":\"isFreeze\",\"type\":\"bool\"}],\"name\":\"setContractStatus\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[2177375452,2437471713],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"uint8\",\"name\":\"_type\",\"type\":\"uint8\"}],\"name\":\"setDeployAuthType\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[3138036748,2966063259],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"address\",\"name\":\"contractAddr\",\"type\":\"address\"},{\"internalType\":\"bytes4\",\"name\":\"func\",\"type\":\"bytes4\"},{\"internalType\":\"uint8\",\"name\":\"authType\",\"type\":\"uint8\"}],\"name\":\"setMethodAuthType\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[2630076943,1025843675],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_CHECKMETHODAUTH = "checkMethodAuth";

    public static final String FUNC_CLOSEDEPLOYAUTH = "closeDeployAuth";

    public static final String FUNC_CLOSEMETHODAUTH = "closeMethodAuth";

    public static final String FUNC_CONTRACTAVAILABLE = "contractAvailable";

    public static final String FUNC_DEPLOYTYPE = "deployType";

    public static final String FUNC_GETADMIN = "getAdmin";

    public static final String FUNC_GETMETHODAUTH = "getMethodAuth";

    public static final String FUNC_HASDEPLOYAUTH = "hasDeployAuth";

    public static final String FUNC_OPENDEPLOYAUTH = "openDeployAuth";

    public static final String FUNC_OPENMETHODAUTH = "openMethodAuth";

    public static final String FUNC_RESETADMIN = "resetAdmin";

    public static final String FUNC_SETCONTRACTSTATUS = "setContractStatus";

    public static final String FUNC_SETDEPLOYAUTHTYPE = "setDeployAuthType";

    public static final String FUNC_SETMETHODAUTHTYPE = "setMethodAuthType";

    protected ContractAuthPrecompiled(
            String contractAddress, Client client, CryptoKeyPair credential) {
        super("", contractAddress, client, credential);
    }

    public static String getABI() {
        return ABI;
    }

    public Boolean checkMethodAuth(String contractAddr, byte[] func, String account)
            throws ContractException {
        final Function function =
                new Function(
                        FUNC_CHECKMETHODAUTH,
                        Arrays.<Type>asList(
                                new Address(contractAddr), new Bytes4(func), new Address(account)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public TransactionReceipt closeDeployAuth(String account) {
        final Function function =
                new Function(
                        FUNC_CLOSEDEPLOYAUTH,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String closeDeployAuth(String account, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_CLOSEDEPLOYAUTH,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCloseDeployAuth(String account) {
        final Function function =
                new Function(
                        FUNC_CLOSEDEPLOYAUTH,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getCloseDeployAuthInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_CLOSEDEPLOYAUTH,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getCloseDeployAuthOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_CLOSEDEPLOYAUTH,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt closeMethodAuth(String contractAddr, byte[] func, String account) {
        final Function function =
                new Function(
                        FUNC_CLOSEMETHODAUTH,
                        Arrays.<Type>asList(
                                new Address(contractAddr), new Bytes4(func), new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String closeMethodAuth(
            String contractAddr, byte[] func, String account, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_CLOSEMETHODAUTH,
                        Arrays.<Type>asList(
                                new Address(contractAddr), new Bytes4(func), new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCloseMethodAuth(
            String contractAddr, byte[] func, String account) {
        final Function function =
                new Function(
                        FUNC_CLOSEMETHODAUTH,
                        Arrays.<Type>asList(
                                new Address(contractAddr), new Bytes4(func), new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<String, byte[], String> getCloseMethodAuthInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_CLOSEMETHODAUTH,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {},
                                new TypeReference<Bytes4>() {},
                                new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<String, byte[], String>(
                (String) results.get(0).getValue(),
                (byte[]) results.get(1).getValue(),
                (String) results.get(2).getValue());
    }

    public Tuple1<BigInteger> getCloseMethodAuthOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_CLOSEMETHODAUTH,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public Boolean contractAvailable(String address) throws ContractException {
        final Function function =
                new Function(
                        FUNC_CONTRACTAVAILABLE,
                        Arrays.<Type>asList(new Address(address)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public BigInteger deployType() throws ContractException {
        final Function function =
                new Function(
                        FUNC_DEPLOYTYPE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public String getAdmin(String contractAddr) throws ContractException {
        final Function function =
                new Function(
                        FUNC_GETADMIN,
                        Arrays.<Type>asList(new Address(contractAddr)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public Tuple3<BigInteger, List<String>, List<String>> getMethodAuth(String path, byte[] func)
            throws ContractException {
        final Function function =
                new Function(
                        FUNC_GETMETHODAUTH,
                        Arrays.<Type>asList(new Address(path), new Bytes4(func)),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Uint8>() {},
                                new TypeReference<DynamicArray<Utf8String>>() {},
                                new TypeReference<DynamicArray<Utf8String>>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple3<>(
                (BigInteger) results.get(0).getValue(),
                convertToNative((List<Utf8String>) results.get(1).getValue()),
                convertToNative((List<Utf8String>) results.get(2).getValue()));
    }

    public Boolean hasDeployAuth(String account) throws ContractException {
        final Function function =
                new Function(
                        FUNC_HASDEPLOYAUTH,
                        Arrays.<Type>asList(new Address(account)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallWithSingleValueReturn(function, Boolean.class);
    }

    public TransactionReceipt openDeployAuth(String account) {
        final Function function =
                new Function(
                        FUNC_OPENDEPLOYAUTH,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String openDeployAuth(String account, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_OPENDEPLOYAUTH,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForOpenDeployAuth(String account) {
        final Function function =
                new Function(
                        FUNC_OPENDEPLOYAUTH,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getOpenDeployAuthInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_OPENDEPLOYAUTH,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getOpenDeployAuthOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_OPENDEPLOYAUTH,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt openMethodAuth(String contractAddr, byte[] func, String account) {
        final Function function =
                new Function(
                        FUNC_OPENMETHODAUTH,
                        Arrays.<Type>asList(
                                new Address(contractAddr), new Bytes4(func), new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String openMethodAuth(
            String contractAddr, byte[] func, String account, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_OPENMETHODAUTH,
                        Arrays.<Type>asList(
                                new Address(contractAddr), new Bytes4(func), new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForOpenMethodAuth(
            String contractAddr, byte[] func, String account) {
        final Function function =
                new Function(
                        FUNC_OPENMETHODAUTH,
                        Arrays.<Type>asList(
                                new Address(contractAddr), new Bytes4(func), new Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<String, byte[], String> getOpenMethodAuthInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_OPENMETHODAUTH,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {},
                                new TypeReference<Bytes4>() {},
                                new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<String, byte[], String>(
                (String) results.get(0).getValue(),
                (byte[]) results.get(1).getValue(),
                (String) results.get(2).getValue());
    }

    public Tuple1<BigInteger> getOpenMethodAuthOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_OPENMETHODAUTH,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt resetAdmin(String contractAddr, String admin) {
        final Function function =
                new Function(
                        FUNC_RESETADMIN,
                        Arrays.<Type>asList(new Address(contractAddr), new Address(admin)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String resetAdmin(String contractAddr, String admin, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_RESETADMIN,
                        Arrays.<Type>asList(new Address(contractAddr), new Address(admin)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForResetAdmin(String contractAddr, String admin) {
        final Function function =
                new Function(
                        FUNC_RESETADMIN,
                        Arrays.<Type>asList(new Address(contractAddr), new Address(admin)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<String, String> getResetAdminInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_RESETADMIN,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {}, new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, String>(
                (String) results.get(0).getValue(), (String) results.get(1).getValue());
    }

    public Tuple1<BigInteger> getResetAdminOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_RESETADMIN,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt setContractStatus(String address, Boolean isFreeze) {
        final Function function =
                new Function(
                        FUNC_SETCONTRACTSTATUS,
                        Arrays.<Type>asList(new Address(address), new Bool(isFreeze)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String setContractStatus(
            String address, Boolean isFreeze, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SETCONTRACTSTATUS,
                        Arrays.<Type>asList(new Address(address), new Bool(isFreeze)),
                        Collections.emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSetContractStatus(String address, Boolean isFreeze) {
        final Function function =
                new Function(
                        FUNC_SETCONTRACTSTATUS,
                        Arrays.<Type>asList(new Address(address), new Bool(isFreeze)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<String, Boolean> getSetContractStatusInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SETCONTRACTSTATUS,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {}, new TypeReference<Bool>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, Boolean>(
                (String) results.get(0).getValue(), (Boolean) results.get(1).getValue());
    }

    public Tuple1<BigInteger> getSetContractStatusOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_SETCONTRACTSTATUS,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt setDeployAuthType(BigInteger _type) {
        final Function function =
                new Function(
                        FUNC_SETDEPLOYAUTHTYPE,
                        Arrays.<Type>asList(new Uint8(_type)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String setDeployAuthType(BigInteger _type, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SETDEPLOYAUTHTYPE,
                        Arrays.<Type>asList(new Uint8(_type)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSetDeployAuthType(BigInteger _type) {
        final Function function =
                new Function(
                        FUNC_SETDEPLOYAUTHTYPE,
                        Arrays.<Type>asList(new Uint8(_type)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<BigInteger> getSetDeployAuthTypeInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SETDEPLOYAUTHTYPE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getSetDeployAuthTypeOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_SETDEPLOYAUTHTYPE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt setMethodAuthType(
            String contractAddr, byte[] func, BigInteger authType) {
        final Function function =
                new Function(
                        FUNC_SETMETHODAUTHTYPE,
                        Arrays.<Type>asList(
                                new Address(contractAddr), new Bytes4(func), new Uint8(authType)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public String setMethodAuthType(
            String contractAddr, byte[] func, BigInteger authType, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SETMETHODAUTHTYPE,
                        Arrays.<Type>asList(
                                new Address(contractAddr), new Bytes4(func), new Uint8(authType)),
                        Collections.<TypeReference<?>>emptyList());
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSetMethodAuthType(
            String contractAddr, byte[] func, BigInteger authType) {
        final Function function =
                new Function(
                        FUNC_SETMETHODAUTHTYPE,
                        Arrays.<Type>asList(
                                new Address(contractAddr), new Bytes4(func), new Uint8(authType)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<String, byte[], BigInteger> getSetMethodAuthTypeInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SETMETHODAUTHTYPE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {},
                                new TypeReference<Bytes4>() {},
                                new TypeReference<Uint8>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<String, byte[], BigInteger>(
                (String) results.get(0).getValue(),
                (byte[]) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue());
    }

    public Tuple1<BigInteger> getSetMethodAuthTypeOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_SETMETHODAUTHTYPE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public static ContractAuthPrecompiled load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new ContractAuthPrecompiled(contractAddress, client, credential);
    }
}
