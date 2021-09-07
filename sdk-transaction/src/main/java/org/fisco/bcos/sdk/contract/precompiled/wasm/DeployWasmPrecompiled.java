package org.fisco.bcos.sdk.contract.precompiled.wasm;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.codec.datatypes.Function;
import org.fisco.bcos.sdk.codec.datatypes.Type;
import org.fisco.bcos.sdk.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.codec.datatypes.generated.Int256;
import org.fisco.bcos.sdk.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.codec.datatypes.generated.tuples.generated.Tuple4;
import org.fisco.bcos.sdk.contract.Contract;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class DeployWasmPrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {};

    public static final String BINARY =
            org.fisco.bcos.sdk.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {};

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"constant\":false,\"inputs\":[{\"name\":\"code\",\"type\":\"bytes\"},{\"name\":\"params\",\"type\":\"bytes\"},{\"name\":\"path\",\"type\":\"string\"},{\"name\":\"jsonAbi\",\"type\":\"string\"}],\"name\":\"deployWasm\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_DEPLOYWASM = "deployWasm";

    protected DeployWasmPrecompiled(
            String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public TransactionReceipt deployWasm(byte[] code, byte[] params, String path, String jsonAbi) {
        final Function function =
                new Function(
                        FUNC_DEPLOYWASM,
                        Arrays.<Type>asList(
                                new DynamicBytes(code),
                                new DynamicBytes(params),
                                new Utf8String(path),
                                new Utf8String(jsonAbi)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void deployWasm(
            byte[] code, byte[] params, String path, String jsonAbi, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_DEPLOYWASM,
                        Arrays.<Type>asList(
                                new DynamicBytes(code),
                                new DynamicBytes(params),
                                new Utf8String(path),
                                new Utf8String(jsonAbi)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForDeployWasm(
            byte[] code, byte[] params, String path, String jsonAbi) {
        final Function function =
                new Function(
                        FUNC_DEPLOYWASM,
                        Arrays.<Type>asList(
                                new DynamicBytes(code),
                                new DynamicBytes(params),
                                new Utf8String(path),
                                new Utf8String(jsonAbi)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple4<byte[], byte[], String, String> getDeployWasmInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_DEPLOYWASM,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<DynamicBytes>() {},
                                new TypeReference<DynamicBytes>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple4<byte[], byte[], String, String>(
                (byte[]) results.get(0).getValue(),
                (byte[]) results.get(1).getValue(),
                (String) results.get(2).getValue(),
                (String) results.get(3).getValue());
    }

    public Tuple1<BigInteger> getDeployWasmOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_DEPLOYWASM,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public static DeployWasmPrecompiled load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new DeployWasmPrecompiled(contractAddress, client, credential);
    }

    public static DeployWasmPrecompiled deploy(Client client, CryptoKeyPair credential)
            throws ContractException {
        return deploy(
                DeployWasmPrecompiled.class,
                client,
                credential,
                getBinary(client.getCryptoSuite()),
                null,
                null,
                null);
    }
}
