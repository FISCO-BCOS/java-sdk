package org.fisco.bcos.sdk.v3.contract.precompiled.crud;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Bool;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int256;
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
public class KVTablePrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {};

    public static final String BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {};

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"inputs\":[],\"name\":\"desc\",\"outputs\":[{\"components\":[{\"internalType\":\"string\",\"name\":\"keyColumn\",\"type\":\"string\"},{\"internalType\":\"string[]\",\"name\":\"valueColumns\",\"type\":\"string[]\"}],\"internalType\":\"struct TableInfo\",\"name\":\"\",\"type\":\"tuple\"}],\"selector\":[1441878257,1966441124],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"}],\"name\":\"get\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"},{\"components\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"},{\"internalType\":\"string[]\",\"name\":\"fields\",\"type\":\"string[]\"}],\"internalType\":\"struct Entry\",\"name\":\"\",\"type\":\"tuple\"}],\"selector\":[1765722206,2065403395],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"value\",\"type\":\"string\"}],\"name\":\"set\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[3913463062,439950516],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_DESC = "desc";

    public static final String FUNC_GET = "get";

    public static final String FUNC_SET = "set";

    protected KVTablePrecompiled(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public TableInfo desc() throws ContractException {
        final Function function =
                new Function(
                        FUNC_DESC,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<TableInfo>() {}));
        return executeCallWithSingleValueReturn(function, TableInfo.class);
    }

    public Tuple2<Boolean, String> get(String key) throws ContractException {
        final Function function =
                new Function(
                        FUNC_GET,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(key)),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Bool>() {}, new TypeReference<Utf8String>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple2<Boolean, String>(
                (Boolean) results.get(0).getValue(), (String) results.get(1).getValue());
    }

    public TransactionReceipt set(String key, String value) {
        final Function function =
                new Function(
                        FUNC_SET,
                        Arrays.<Type>asList(new Utf8String(key), new Utf8String(value)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String set(String key, String value, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SET,
                        Arrays.<Type>asList(new Utf8String(key), new Utf8String(value)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSet(String key, String value) {
        final Function function =
                new Function(
                        FUNC_SET,
                        Arrays.<Type>asList(new Utf8String(key), new Utf8String(value)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public Tuple2<String, String> getSetInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SET,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, String>(
                (String) results.get(0).getValue(), (String) results.get(1).getValue());
    }

    public Tuple1<BigInteger> getSetOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_SET,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public static KVTablePrecompiled load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new KVTablePrecompiled(contractAddress, client, credential);
    }

    public static KVTablePrecompiled deploy(Client client, CryptoKeyPair credential)
            throws ContractException {
        return deploy(
                KVTablePrecompiled.class,
                client,
                credential,
                getBinary(client.getCryptoSuite()),
                getABI(),
                null,
                null);
    }

    public static class TableInfo extends DynamicStruct {
        public String keyColumn;

        public List<String> valueColumns;

        public TableInfo(Utf8String keyColumn, DynamicArray<Utf8String> valueColumns) {
            super(keyColumn, valueColumns);
            this.keyColumn = keyColumn.getValue();
            this.valueColumns =
                    valueColumns.getValue().stream()
                            .map(Utf8String::getValue)
                            .collect(Collectors.toList());
        }

        public TableInfo(String keyColumn, List<String> valueColumns) {
            super(
                    new Utf8String(keyColumn),
                    new DynamicArray<>(
                            Utf8String.class,
                            valueColumns.stream()
                                    .map(Utf8String::new)
                                    .collect(Collectors.toList())));
            this.keyColumn = keyColumn;
            this.valueColumns = valueColumns;
        }
    }
}
