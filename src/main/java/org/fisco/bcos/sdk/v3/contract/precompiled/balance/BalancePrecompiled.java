package org.fisco.bcos.sdk.v3.contract.precompiled.balance;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class BalancePrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {
        "608060405234801561001057600080fd5b506102a1806100206000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c806321e5383a146100675780633b9a32de146100b5578063afb4bfbd146100f9578063beabacc81461013d578063cf8eeb7e146101ab578063f8b2cb4f146101f9575b600080fd5b6100b36004803603604081101561007d57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610251565b005b6100f7600480360360208110156100cb57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610255565b005b61013b6004803603602081101561010f57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610258565b005b6101a96004803603606081101561015357600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff1690602001909291908035906020019092919050505061025b565b005b6101f7600480360360408110156101c157600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610260565b005b61023b6004803603602081101561020f57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610264565b6040518082815260200191505060405180910390f35b5050565b50565b50565b505050565b5050565b600091905056fea2646970667358221220da8396121dde206095627ca823eab9af2c8d6040ca3970663298d03a114c6f5a64736f6c634300060a0033"
    };

    public static final String BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "608060405234801561001057600080fd5b506102a1806100206000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c80633f1baf841461006757806379bf5daf146100bf5780637b11403e1461010d578063a5d20ed61461015b578063b628e9351461019f578063e308a886146101e3575b600080fd5b6100a96004803603602081101561007d57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610251565b6040518082815260200191505060405180910390f35b61010b600480360360408110156100d557600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610258565b005b6101596004803603604081101561012357600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291908035906020019092919050505061025c565b005b61019d6004803603602081101561017157600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610260565b005b6101e1600480360360208110156101b557600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610263565b005b61024f600480360360608110156101f957600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610266565b005b6000919050565b5050565b5050565b50565b50565b50505056fea2646970667358221220caa58314d27a2c44947a474a344ad87fc034bdda8106ec558bcdf97af5782fc264736f6c634300060a0033"
    };

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"addBalance\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"getBalance\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"registerCaller\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"subBalance\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"from\",\"type\":\"address\"},{\"internalType\":\"address\",\"name\":\"to\",\"type\":\"address\"},{\"internalType\":\"uint256\",\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"transfer\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"address\",\"name\":\"account\",\"type\":\"address\"}],\"name\":\"unregisterCaller\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_ADDBALANCE = "addBalance";

    public static final String FUNC_GETBALANCE = "getBalance";

    public static final String FUNC_REGISTERCALLER = "registerCaller";

    public static final String FUNC_SUBBALANCE = "subBalance";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_UNREGISTERCALLER = "unregisterCaller";

    protected BalancePrecompiled(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public TransactionReceipt addBalance(String account, BigInteger amount) {
        final Function function =
                new Function(
                        FUNC_ADDBALANCE,
                        Arrays.<Type>asList(new Address(account), new Uint256(amount)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String getSignedTransactionForAddBalance(String account, BigInteger amount) {
        final Function function =
                new Function(
                        FUNC_ADDBALANCE,
                        Arrays.<Type>asList(new Address(account), new Uint256(amount)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public String addBalance(String account, BigInteger amount, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_ADDBALANCE,
                        Arrays.<Type>asList(new Address(account), new Uint256(amount)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<String, BigInteger> getAddBalanceInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_ADDBALANCE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, BigInteger>(
                (String) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public TransactionReceipt getBalance(String account) {
        final Function function =
                new Function(
                        FUNC_GETBALANCE,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String getSignedTransactionForGetBalance(String account) {
        final Function function =
                new Function(
                        FUNC_GETBALANCE,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public String getBalance(String account, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_GETBALANCE,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple1<String> getGetBalanceInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_GETBALANCE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getGetBalanceOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_GETBALANCE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt registerCaller(String account) {
        final Function function =
                new Function(
                        FUNC_REGISTERCALLER,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String getSignedTransactionForRegisterCaller(String account) {
        final Function function =
                new Function(
                        FUNC_REGISTERCALLER,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public String registerCaller(String account, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_REGISTERCALLER,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<String, BigInteger> getRegisterCallerInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_REGISTERCALLER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, BigInteger>(
                (String) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public TransactionReceipt subBalance(String account, BigInteger amount) {
        final Function function =
                new Function(
                        FUNC_SUBBALANCE,
                        Arrays.<Type>asList(new Address(account), new Uint256(amount)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String getSignedTransactionForSubBalance(String account, BigInteger amount) {
        final Function function =
                new Function(
                        FUNC_SUBBALANCE,
                        Arrays.<Type>asList(new Address(account), new Uint256(amount)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public String subBalance(String account, BigInteger amount, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SUBBALANCE,
                        Arrays.<Type>asList(new Address(account), new Uint256(amount)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<String, BigInteger> getSubBalanceInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SUBBALANCE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, BigInteger>(
                (String) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public TransactionReceipt transfer(String from, String to, BigInteger amount) {
        final Function function =
                new Function(
                        FUNC_TRANSFER,
                        Arrays.<Type>asList(
                                new Address(from), new Address(to), new Uint256(amount)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String getSignedTransactionForTransfer(String from, String to, BigInteger amount) {
        final Function function =
                new Function(
                        FUNC_TRANSFER,
                        Arrays.<Type>asList(
                                new Address(from), new Address(to), new Uint256(amount)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public String transfer(
            String from, String to, BigInteger amount, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_TRANSFER,
                        Arrays.<Type>asList(
                                new Address(from), new Address(to), new Uint256(amount)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple3<String, String, BigInteger> getTransferInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_TRANSFER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Address>() {},
                                new TypeReference<Address>() {},
                                new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<String, String, BigInteger>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (BigInteger) results.get(2).getValue());
    }

    public TransactionReceipt unregisterCaller(String account) {
        final Function function =
                new Function(
                        FUNC_UNREGISTERCALLER,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String getSignedTransactionForUnregisterCaller(String account) {
        final Function function =
                new Function(
                        FUNC_UNREGISTERCALLER,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public String unregisterCaller(String account, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_UNREGISTERCALLER,
                        Arrays.<Type>asList(new Address(account)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<String, BigInteger> getUnregisterCallerInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_UNREGISTERCALLER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, BigInteger>(
                (String) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public static BalancePrecompiled load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new BalancePrecompiled(contractAddress, client, credential);
    }

    public static BalancePrecompiled deploy(Client client, CryptoKeyPair credential)
            throws ContractException {
        return deploy(
                BalancePrecompiled.class,
                client,
                credential,
                getBinary(client.getCryptoSuite()),
                getABI(),
                null,
                null);
    }
}
