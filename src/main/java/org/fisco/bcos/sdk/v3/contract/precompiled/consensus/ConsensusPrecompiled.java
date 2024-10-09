package org.fisco.bcos.sdk.v3.contract.precompiled.consensus;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.contract.Contract;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class ConsensusPrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {""};

    public static final String BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {""};

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"name\":\"addObserver\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[671150016,635985174],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"addSealer\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[898721878,1358218505],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"name\":\"remove\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[2153356875,2260153337],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"setTermWeight\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[2627106231,3500870707],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"conflictFields\":[{\"kind\":5}],\"inputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"setWeight\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[3463423429,2682035704],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_ADDOBSERVER = "addObserver";

    public static final String FUNC_ADDSEALER = "addSealer";

    public static final String FUNC_REMOVE = "remove";

    public static final String FUNC_SETTERMWEIGHT = "setTermWeight";

    public static final String FUNC_SETWEIGHT = "setWeight";

    protected ConsensusPrecompiled(
            String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    /**
     * @return TransactionReceipt Get more transaction info (e.g. txhash, block) from
     *     TransactionReceipt use getAddObserverOutput(transactionReceipt) to get outputs
     */
    public TransactionReceipt addObserver(String param0) {
        final Function function =
                new Function(
                        FUNC_ADDOBSERVER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(param0)),
                        Collections.<TypeReference<?>>emptyList(),
                        4);
        return executeTransaction(function);
    }

    public Function getMethodAddObserverRawFunction(String param0) throws ContractException {
        final Function function =
                new Function(
                        FUNC_ADDOBSERVER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(param0)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        return function;
    }

    public String getSignedTransactionForAddObserver(String param0) {
        final Function function =
                new Function(
                        FUNC_ADDOBSERVER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(param0)),
                        Collections.<TypeReference<?>>emptyList(),
                        4);
        return createSignedTransaction(function);
    }

    /**
     * @param callback Get TransactionReceipt from TransactionCallback onResponse(TransactionReceipt
     *     receipt) use getAddObserverOutput(transactionReceipt) to get outputs
     * @return txHash Transaction hash of current transaction call
     */
    public String addObserver(String param0, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_ADDOBSERVER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(param0)),
                        Collections.<TypeReference<?>>emptyList(),
                        4);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple1<String> getAddObserverInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_ADDOBSERVER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getAddObserverOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_ADDOBSERVER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    /**
     * @return TransactionReceipt Get more transaction info (e.g. txhash, block) from
     *     TransactionReceipt use getAddSealerOutput(transactionReceipt) to get outputs
     */
    public TransactionReceipt addSealer(String param0, BigInteger param1) {
        final Function function =
                new Function(
                        FUNC_ADDSEALER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(param0),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(
                                        param1)),
                        Collections.<TypeReference<?>>emptyList(),
                        4);
        return executeTransaction(function);
    }

    public Function getMethodAddSealerRawFunction(String param0, BigInteger param1)
            throws ContractException {
        final Function function =
                new Function(
                        FUNC_ADDSEALER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(param0),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(
                                        param1)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        return function;
    }

    public String getSignedTransactionForAddSealer(String param0, BigInteger param1) {
        final Function function =
                new Function(
                        FUNC_ADDSEALER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(param0),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(
                                        param1)),
                        Collections.<TypeReference<?>>emptyList(),
                        4);
        return createSignedTransaction(function);
    }

    /**
     * @param callback Get TransactionReceipt from TransactionCallback onResponse(TransactionReceipt
     *     receipt) use getAddSealerOutput(transactionReceipt) to get outputs
     * @return txHash Transaction hash of current transaction call
     */
    public String addSealer(String param0, BigInteger param1, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_ADDSEALER,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(param0),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(
                                        param1)),
                        Collections.<TypeReference<?>>emptyList(),
                        4);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<String, BigInteger> getAddSealerInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_ADDSEALER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, BigInteger>(
                (String) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public Tuple1<BigInteger> getAddSealerOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_ADDSEALER,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    /**
     * @return TransactionReceipt Get more transaction info (e.g. txhash, block) from
     *     TransactionReceipt use getRemoveOutput(transactionReceipt) to get outputs
     */
    public TransactionReceipt remove(String param0) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(param0)),
                        Collections.<TypeReference<?>>emptyList(),
                        4);
        return executeTransaction(function);
    }

    public Function getMethodRemoveRawFunction(String param0) throws ContractException {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(param0)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        return function;
    }

    public String getSignedTransactionForRemove(String param0) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(param0)),
                        Collections.<TypeReference<?>>emptyList(),
                        4);
        return createSignedTransaction(function);
    }

    /**
     * @param callback Get TransactionReceipt from TransactionCallback onResponse(TransactionReceipt
     *     receipt) use getRemoveOutput(transactionReceipt) to get outputs
     * @return txHash Transaction hash of current transaction call
     */
    public String remove(String param0, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(param0)),
                        Collections.<TypeReference<?>>emptyList(),
                        4);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple1<String> getRemoveInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getRemoveOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    /**
     * @return TransactionReceipt Get more transaction info (e.g. txhash, block) from
     *     TransactionReceipt use getSetTermWeightOutput(transactionReceipt) to get outputs
     */
    public TransactionReceipt setTermWeight(String param0, BigInteger param1) {
        final Function function =
                new Function(
                        FUNC_SETTERMWEIGHT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(param0),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(
                                        param1)),
                        Collections.<TypeReference<?>>emptyList(),
                        4);
        return executeTransaction(function);
    }

    public Function getMethodSetTermWeightRawFunction(String param0, BigInteger param1)
            throws ContractException {
        final Function function =
                new Function(
                        FUNC_SETTERMWEIGHT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(param0),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(
                                        param1)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        return function;
    }

    public String getSignedTransactionForSetTermWeight(String param0, BigInteger param1) {
        final Function function =
                new Function(
                        FUNC_SETTERMWEIGHT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(param0),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(
                                        param1)),
                        Collections.<TypeReference<?>>emptyList(),
                        4);
        return createSignedTransaction(function);
    }

    /**
     * @param callback Get TransactionReceipt from TransactionCallback onResponse(TransactionReceipt
     *     receipt) use getSetTermWeightOutput(transactionReceipt) to get outputs
     * @return txHash Transaction hash of current transaction call
     */
    public String setTermWeight(String param0, BigInteger param1, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SETTERMWEIGHT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(param0),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(
                                        param1)),
                        Collections.<TypeReference<?>>emptyList(),
                        4);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<String, BigInteger> getSetTermWeightInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SETTERMWEIGHT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, BigInteger>(
                (String) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public Tuple1<BigInteger> getSetTermWeightOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_SETTERMWEIGHT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    /**
     * @return TransactionReceipt Get more transaction info (e.g. txhash, block) from
     *     TransactionReceipt use getSetWeightOutput(transactionReceipt) to get outputs
     */
    public TransactionReceipt setWeight(String param0, BigInteger param1) {
        final Function function =
                new Function(
                        FUNC_SETWEIGHT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(param0),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(
                                        param1)),
                        Collections.<TypeReference<?>>emptyList(),
                        4);
        return executeTransaction(function);
    }

    public Function getMethodSetWeightRawFunction(String param0, BigInteger param1)
            throws ContractException {
        final Function function =
                new Function(
                        FUNC_SETWEIGHT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(param0),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(
                                        param1)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        return function;
    }

    public String getSignedTransactionForSetWeight(String param0, BigInteger param1) {
        final Function function =
                new Function(
                        FUNC_SETWEIGHT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(param0),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(
                                        param1)),
                        Collections.<TypeReference<?>>emptyList(),
                        4);
        return createSignedTransaction(function);
    }

    /**
     * @param callback Get TransactionReceipt from TransactionCallback onResponse(TransactionReceipt
     *     receipt) use getSetWeightOutput(transactionReceipt) to get outputs
     * @return txHash Transaction hash of current transaction call
     */
    public String setWeight(String param0, BigInteger param1, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SETWEIGHT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(param0),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256(
                                        param1)),
                        Collections.<TypeReference<?>>emptyList(),
                        4);
        return asyncExecuteTransaction(function, callback);
    }

    public Tuple2<String, BigInteger> getSetWeightInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SETWEIGHT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Uint256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, BigInteger>(
                (String) results.get(0).getValue(), (BigInteger) results.get(1).getValue());
    }

    public Tuple1<BigInteger> getSetWeightOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_SETWEIGHT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public static ConsensusPrecompiled load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new ConsensusPrecompiled(contractAddress, client, credential);
    }
}
