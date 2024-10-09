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
    public static final String[] BINARY_ARRAY = {
        "608060405234801561001057600080fd5b50610411806100206000396000f3fe608060405234801561001057600080fd5b50600436106100575760003560e01c80632800efc01461005c578063359168561461008c57806380599e4b146100bc5780639c9675b7146100ec578063ce6fa5c51461011c575b600080fd5b610076600480360381019061007191906102cc565b61014c565b604051610083919061032e565b60405180910390f35b6100a660048036038101906100a1919061037f565b610153565b6040516100b3919061032e565b60405180910390f35b6100d660048036038101906100d191906102cc565b61015b565b6040516100e3919061032e565b60405180910390f35b6101066004803603810190610101919061037f565b610162565b604051610113919061032e565b60405180910390f35b6101366004803603810190610131919061037f565b61016a565b604051610143919061032e565b60405180910390f35b6000919050565b600092915050565b6000919050565b600092915050565b600092915050565b6000604051905090565b600080fd5b600080fd5b600080fd5b600080fd5b6000601f19601f8301169050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b6101d982610190565b810181811067ffffffffffffffff821117156101f8576101f76101a1565b5b80604052505050565b600061020b610172565b905061021782826101d0565b919050565b600067ffffffffffffffff821115610237576102366101a1565b5b61024082610190565b9050602081019050919050565b82818337600083830152505050565b600061026f61026a8461021c565b610201565b90508281526020810184848401111561028b5761028a61018b565b5b61029684828561024d565b509392505050565b600082601f8301126102b3576102b2610186565b5b81356102c384826020860161025c565b91505092915050565b6000602082840312156102e2576102e161017c565b5b600082013567ffffffffffffffff811115610300576102ff610181565b5b61030c8482850161029e565b91505092915050565b6000819050919050565b61032881610315565b82525050565b6000602082019050610343600083018461031f565b92915050565b6000819050919050565b61035c81610349565b811461036757600080fd5b50565b60008135905061037981610353565b92915050565b600080604083850312156103965761039561017c565b5b600083013567ffffffffffffffff8111156103b4576103b3610181565b5b6103c08582860161029e565b92505060206103d18582860161036a565b915050925092905056fea264697066735822122047f5d2f496fb0b4a5838d33a0ac5cbdbc87257e95b46bf2362b7eb01559d4f7e64736f6c634300080b0033"
    };

    public static final String BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "608060405234801561001057600080fd5b50610411806100206000396000f3fe608060405234801561001057600080fd5b50600436106100575760003560e01c806325e85d161461005c57806350f4c5091461008c57806386b733f9146100bc5780639fdc9df8146100ec578063d0ab0c331461011c575b600080fd5b610076600480360381019061007191906102cc565b61014c565b604051610083919061032e565b60405180910390f35b6100a660048036038101906100a1919061037f565b610153565b6040516100b3919061032e565b60405180910390f35b6100d660048036038101906100d191906102cc565b61015b565b6040516100e3919061032e565b60405180910390f35b6101066004803603810190610101919061037f565b610162565b604051610113919061032e565b60405180910390f35b6101366004803603810190610131919061037f565b61016a565b604051610143919061032e565b60405180910390f35b6000919050565b600092915050565b6000919050565b600092915050565b600092915050565b6000604051905090565b600080fd5b600080fd5b600080fd5b600080fd5b6000601f19601f8301169050919050565b7fb95aa35500000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b6101d982610190565b810181811067ffffffffffffffff821117156101f8576101f76101a1565b5b80604052505050565b600061020b610172565b905061021782826101d0565b919050565b600067ffffffffffffffff821115610237576102366101a1565b5b61024082610190565b9050602081019050919050565b82818337600083830152505050565b600061026f61026a8461021c565b610201565b90508281526020810184848401111561028b5761028a61018b565b5b61029684828561024d565b509392505050565b600082601f8301126102b3576102b2610186565b5b81356102c384826020860161025c565b91505092915050565b6000602082840312156102e2576102e161017c565b5b600082013567ffffffffffffffff811115610300576102ff610181565b5b61030c8482850161029e565b91505092915050565b6000819050919050565b61032881610315565b82525050565b6000602082019050610343600083018461031f565b92915050565b6000819050919050565b61035c81610349565b811461036757600080fd5b50565b60008135905061037981610353565b92915050565b600080604083850312156103965761039561017c565b5b600083013567ffffffffffffffff8111156103b4576103b3610181565b5b6103c08582860161029e565b92505060206103d18582860161036a565b915050925092905056fea264697066735822122099c593010df491a0ccc8a94d7b99660661b10c3de3589b2587d20cd44682a11664736f6c634300080b0033"
    };

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

    public static ConsensusPrecompiled deploy(Client client, CryptoKeyPair credential)
            throws ContractException {
        return deploy(
                ConsensusPrecompiled.class,
                client,
                credential,
                getBinary(client.getCryptoSuite()),
                getABI(),
                null,
                null);
    }
}
