package org.fisco.bcos.sdk.contract.precompiled.permission;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.Address;
import org.fisco.bcos.sdk.abi.datatypes.Bool;
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
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.callback.TransactionCallback;

@SuppressWarnings("unchecked")
public class ChainGovernancePrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {};

    public static final String BINARY = String.join("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {};

    public static final String SM_BINARY = String.join("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"constant\":true,\"inputs\":[],\"name\":\"listOperators\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"user\",\"type\":\"address\"},{\"name\":\"weight\",\"type\":\"int256\"}],\"name\":\"updateCommitteeMemberWeight\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"queryThreshold\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"user\",\"type\":\"address\"}],\"name\":\"queryCommitteeMemberWeight\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"},{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"user\",\"type\":\"address\"}],\"name\":\"grantCommitteeMember\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"account\",\"type\":\"address\"}],\"name\":\"unfreezeAccount\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"listCommitteeMembers\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"threshold\",\"type\":\"int256\"}],\"name\":\"updateThreshold\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"user\",\"type\":\"address\"}],\"name\":\"revokeCommitteeMember\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"user\",\"type\":\"address\"}],\"name\":\"grantOperator\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"account\",\"type\":\"address\"}],\"name\":\"freezeAccount\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"user\",\"type\":\"address\"}],\"name\":\"revokeOperator\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"account\",\"type\":\"address\"}],\"name\":\"getAccountStatus\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"}]"
    };

    public static final String ABI = String.join("", ABI_ARRAY);

    public static final String FUNC_LISTOPERATORS = "listOperators";

    public static final String FUNC_UPDATECOMMITTEEMEMBERWEIGHT = "updateCommitteeMemberWeight";

    public static final String FUNC_QUERYTHRESHOLD = "queryThreshold";

    public static final String FUNC_QUERYCOMMITTEEMEMBERWEIGHT = "queryCommitteeMemberWeight";

    public static final String FUNC_GRANTCOMMITTEEMEMBER = "grantCommitteeMember";

    public static final String FUNC_UNFREEZEACCOUNT = "unfreezeAccount";

    public static final String FUNC_LISTCOMMITTEEMEMBERS = "listCommitteeMembers";

    public static final String FUNC_UPDATETHRESHOLD = "updateThreshold";

    public static final String FUNC_REVOKECOMMITTEEMEMBER = "revokeCommitteeMember";

    public static final String FUNC_GRANTOPERATOR = "grantOperator";

    public static final String FUNC_FREEZEACCOUNT = "freezeAccount";

    public static final String FUNC_REVOKEOPERATOR = "revokeOperator";

    public static final String FUNC_GETACCOUNTSTATUS = "getAccountStatus";

    protected ChainGovernancePrecompiled(
            String contractAddress, Client client, CryptoInterface credential) {
        super(getBinary(credential), contractAddress, client, credential);
    }

    public static String getBinary(CryptoInterface credential) {
        return (credential.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public String listOperators() throws ContractException {
        final Function function =
                new Function(
                        FUNC_LISTOPERATORS,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public TransactionReceipt updateCommitteeMemberWeight(String user, BigInteger weight) {
        final Function function =
                new Function(
                        FUNC_UPDATECOMMITTEEMEMBERWEIGHT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(user),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Int256(weight)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void updateCommitteeMemberWeight(
            String user, BigInteger weight, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_UPDATECOMMITTEEMEMBERWEIGHT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(user),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Int256(weight)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForUpdateCommitteeMemberWeight(
            String user, BigInteger weight) {
        final Function function =
                new Function(
                        FUNC_UPDATECOMMITTEEMEMBERWEIGHT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.Address(user),
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Int256(weight)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<String, BigInteger> getUpdateCommitteeMemberWeightInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_UPDATECOMMITTEEMEMBERWEIGHT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {}, new TypeReference<Int256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, BigInteger>(
                (String) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public Tuple1<BigInteger> getUpdateCommitteeMemberWeightOutput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_UPDATECOMMITTEEMEMBERWEIGHT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public BigInteger queryThreshold() throws ContractException {
        final Function function =
                new Function(
                        FUNC_QUERYTHRESHOLD,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
    }

    public Tuple2<Boolean, BigInteger> queryCommitteeMemberWeight(String user)
            throws ContractException {
        final Function function =
                new Function(
                        FUNC_QUERYCOMMITTEEMEMBERWEIGHT,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(user)),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Bool>() {}, new TypeReference<Int256>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple2<Boolean, BigInteger>(
                (Boolean) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public TransactionReceipt grantCommitteeMember(String user) {
        final Function function =
                new Function(
                        FUNC_GRANTCOMMITTEEMEMBER,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(user)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void grantCommitteeMember(String user, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_GRANTCOMMITTEEMEMBER,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(user)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForGrantCommitteeMember(String user) {
        final Function function =
                new Function(
                        FUNC_GRANTCOMMITTEEMEMBER,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(user)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getGrantCommitteeMemberInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_GRANTCOMMITTEEMEMBER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getGrantCommitteeMemberOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_GRANTCOMMITTEEMEMBER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt unfreezeAccount(String account) {
        final Function function =
                new Function(
                        FUNC_UNFREEZEACCOUNT,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void unfreezeAccount(String account, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_UNFREEZEACCOUNT,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForUnfreezeAccount(String account) {
        final Function function =
                new Function(
                        FUNC_UNFREEZEACCOUNT,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getUnfreezeAccountInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_UNFREEZEACCOUNT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getUnfreezeAccountOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_UNFREEZEACCOUNT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public String listCommitteeMembers() throws ContractException {
        final Function function =
                new Function(
                        FUNC_LISTCOMMITTEEMEMBERS,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public TransactionReceipt updateThreshold(BigInteger threshold) {
        final Function function =
                new Function(
                        FUNC_UPDATETHRESHOLD,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Int256(threshold)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void updateThreshold(BigInteger threshold, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_UPDATETHRESHOLD,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Int256(threshold)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForUpdateThreshold(BigInteger threshold) {
        final Function function =
                new Function(
                        FUNC_UPDATETHRESHOLD,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.abi.datatypes.generated.Int256(threshold)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<BigInteger> getUpdateThresholdInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_UPDATETHRESHOLD,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getUpdateThresholdOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_UPDATETHRESHOLD,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt revokeCommitteeMember(String user) {
        final Function function =
                new Function(
                        FUNC_REVOKECOMMITTEEMEMBER,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(user)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void revokeCommitteeMember(String user, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_REVOKECOMMITTEEMEMBER,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(user)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForRevokeCommitteeMember(String user) {
        final Function function =
                new Function(
                        FUNC_REVOKECOMMITTEEMEMBER,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(user)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getRevokeCommitteeMemberInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_REVOKECOMMITTEEMEMBER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getRevokeCommitteeMemberOutput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_REVOKECOMMITTEEMEMBER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt grantOperator(String user) {
        final Function function =
                new Function(
                        FUNC_GRANTOPERATOR,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(user)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void grantOperator(String user, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_GRANTOPERATOR,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(user)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForGrantOperator(String user) {
        final Function function =
                new Function(
                        FUNC_GRANTOPERATOR,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(user)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getGrantOperatorInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_GRANTOPERATOR,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getGrantOperatorOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_GRANTOPERATOR,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt freezeAccount(String account) {
        final Function function =
                new Function(
                        FUNC_FREEZEACCOUNT,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void freezeAccount(String account, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_FREEZEACCOUNT,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForFreezeAccount(String account) {
        final Function function =
                new Function(
                        FUNC_FREEZEACCOUNT,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(account)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getFreezeAccountInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_FREEZEACCOUNT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getFreezeAccountOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_FREEZEACCOUNT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt revokeOperator(String user) {
        final Function function =
                new Function(
                        FUNC_REVOKEOPERATOR,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(user)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void revokeOperator(String user, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_REVOKEOPERATOR,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(user)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForRevokeOperator(String user) {
        final Function function =
                new Function(
                        FUNC_REVOKEOPERATOR,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(user)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getRevokeOperatorInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_REVOKEOPERATOR,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getRevokeOperatorOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_REVOKEOPERATOR,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public String getAccountStatus(String account) throws ContractException {
        final Function function =
                new Function(
                        FUNC_GETACCOUNTSTATUS,
                        Arrays.<Type>asList(new org.fisco.bcos.sdk.abi.datatypes.Address(account)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public static ChainGovernancePrecompiled load(
            String contractAddress, Client client, CryptoInterface credential) {
        return new ChainGovernancePrecompiled(contractAddress, client, credential);
    }

    public static ChainGovernancePrecompiled deploy(Client client, CryptoInterface credential)
            throws ContractException {
        return deploy(
                ChainGovernancePrecompiled.class, client, credential, getBinary(credential), "");
    }
}
