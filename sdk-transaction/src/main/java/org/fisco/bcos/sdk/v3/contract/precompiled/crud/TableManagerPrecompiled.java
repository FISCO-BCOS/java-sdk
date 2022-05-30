package org.fisco.bcos.sdk.v3.contract.precompiled.crud;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.Address;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int256;
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
public class TableManagerPrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {};

    public static final String BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {};

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"inputs\":[{\"internalType\":\"string\",\"name\":\"path\",\"type\":\"string\"},{\"internalType\":\"string[]\",\"name\":\"newColumns\",\"type\":\"string[]\"}],\"name\":\"appendColumns\",\"outputs\":[{\"internalType\":\"int32\",\"name\":\"\",\"type\":\"int32\"}],\"selector\":[808169184,3960100422],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"tableName\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"keyField\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"valueField\",\"type\":\"string\"}],\"name\":\"createKVTable\",\"outputs\":[{\"internalType\":\"int32\",\"name\":\"\",\"type\":\"int32\"}],\"selector\":[2968034011,2488634795],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"path\",\"type\":\"string\"},{\"components\":[{\"internalType\":\"string\",\"name\":\"keyColumn\",\"type\":\"string\"},{\"internalType\":\"string[]\",\"name\":\"valueColumns\",\"type\":\"string[]\"}],\"internalType\":\"struct TableInfo\",\"name\":\"tableInfo\",\"type\":\"tuple\"}],\"name\":\"createTable\",\"outputs\":[{\"internalType\":\"int32\",\"name\":\"\",\"type\":\"int32\"}],\"selector\":[832939294,3403375714],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"tableName\",\"type\":\"string\"}],\"name\":\"desc\",\"outputs\":[{\"components\":[{\"internalType\":\"string\",\"name\":\"keyColumn\",\"type\":\"string\"},{\"internalType\":\"string[]\",\"name\":\"valueColumns\",\"type\":\"string[]\"}],\"internalType\":\"struct TableInfo\",\"name\":\"\",\"type\":\"tuple\"}],\"selector\":[1561161044,3095778732],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"path\",\"type\":\"string\"}],\"name\":\"openTable\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"selector\":[4064240585,1503955813],\"stateMutability\":\"view\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_APPENDCOLUMNS = "appendColumns";

    public static final String FUNC_CREATEKVTABLE = "createKVTable";

    public static final String FUNC_CREATETABLE = "createTable";

    public static final String FUNC_DESC = "desc";

    public static final String FUNC_OPENTABLE = "openTable";

    protected TableManagerPrecompiled(
            String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public TransactionReceipt appendColumns(String path, List<String> newColumns) {
        final Function function =
                new Function(
                        FUNC_APPENDCOLUMNS,
                        Arrays.<Type>asList(
                                new Utf8String(path),
                                new DynamicArray<Utf8String>(
                                        Utf8String.class,
                                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(
                                                newColumns, Utf8String.class))),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String appendColumns(
            String path, List<String> newColumns, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_APPENDCOLUMNS,
                        Arrays.<Type>asList(
                                new Utf8String(path),
                                new DynamicArray<Utf8String>(
                                        Utf8String.class,
                                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(
                                                newColumns, Utf8String.class))),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForAppendColumns(String path, List<String> newColumns) {
        final Function function =
                new Function(
                        FUNC_APPENDCOLUMNS,
                        Arrays.<Type>asList(
                                new Utf8String(path),
                                new DynamicArray<Utf8String>(
                                        Utf8String.class,
                                        org.fisco.bcos.sdk.v3.codec.Utils.typeMap(
                                                newColumns, Utf8String.class))),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public Tuple2<String, List<String>> getAppendColumnsInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_APPENDCOLUMNS,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<DynamicArray<Utf8String>>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, List<String>>(
                (String) results.get(0).getValue(),
                convertToNative((List<Utf8String>) results.get(1).getValue()));
    }

    public Tuple1<BigInteger> getAppendColumnsOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_APPENDCOLUMNS,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt createKVTable(String tableName, String keyField, String valueField) {
        final Function function =
                new Function(
                        FUNC_CREATEKVTABLE,
                        Arrays.<Type>asList(
                                new Utf8String(tableName),
                                new Utf8String(keyField),
                                new Utf8String(valueField)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String createKVTable(
            String tableName, String keyField, String valueField, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_CREATEKVTABLE,
                        Arrays.<Type>asList(
                                new Utf8String(tableName),
                                new Utf8String(keyField),
                                new Utf8String(valueField)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCreateKVTable(
            String tableName, String keyField, String valueField) {
        final Function function =
                new Function(
                        FUNC_CREATEKVTABLE,
                        Arrays.<Type>asList(
                                new Utf8String(tableName),
                                new Utf8String(keyField),
                                new Utf8String(valueField)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public Tuple3<String, String, String> getCreateKVTableInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_CREATEKVTABLE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<String, String, String>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (String) results.get(2).getValue());
    }

    public Tuple1<BigInteger> getCreateKVTableOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_CREATEKVTABLE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt createTable(String path, TableInfo tableInfo) {
        final Function function =
                new Function(
                        FUNC_CREATETABLE,
                        Arrays.<Type>asList(new Utf8String(path), tableInfo),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String createTable(String path, TableInfo tableInfo, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_CREATETABLE,
                        Arrays.<Type>asList(new Utf8String(path), tableInfo),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCreateTable(String path, TableInfo tableInfo) {
        final Function function =
                new Function(
                        FUNC_CREATETABLE,
                        Arrays.<Type>asList(new Utf8String(path), tableInfo),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public Tuple2<String, TableInfo> getCreateTableInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_CREATETABLE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<TableInfo>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, TableInfo>(
                (String) results.get(0).getValue(), (TableInfo) results.get(1).getValue());
    }

    public Tuple1<BigInteger> getCreateTableOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_CREATETABLE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TableInfo desc(String tableName) throws ContractException {
        final Function function =
                new Function(
                        FUNC_DESC,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(tableName)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<TableInfo>() {}));
        return executeCallWithSingleValueReturn(function, TableInfo.class);
    }

    public String openTable(String path) throws ContractException {
        final Function function =
                new Function(
                        FUNC_OPENTABLE,
                        Arrays.<Type>asList(new Utf8String(path)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallWithSingleValueReturn(function, String.class);
    }

    public static TableManagerPrecompiled load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new TableManagerPrecompiled(contractAddress, client, credential);
    }

    public static class TableInfo extends DynamicStruct {
        public String keyColumn;

        public List<String> valueColumns;

        public TableInfo() {
            super(new Utf8String(""), new DynamicArray<Utf8String>());
        }

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
