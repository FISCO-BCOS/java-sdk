package org.fisco.bcos.sdk.v3.contract.precompiled.crud;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.StaticStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint128;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint256;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint8;
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
public class TablePrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {};

    public static final String BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {};

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"inputs\":[],\"name\":\"desc\",\"outputs\":[{\"components\":[{\"internalType\":\"string\",\"name\":\"keyColumn\",\"type\":\"string\"},{\"internalType\":\"string[]\",\"name\":\"valueColumns\",\"type\":\"string[]\"}],\"internalType\":\"struct TableInfo\",\"name\":\"\",\"type\":\"tuple\"}],\"selector\":[1441878257,1966441124],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"components\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"},{\"internalType\":\"string[]\",\"name\":\"fields\",\"type\":\"string[]\"}],\"internalType\":\"struct Entry\",\"name\":\"entry\",\"type\":\"tuple\"}],\"name\":\"insert\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[1550717023,1284216112],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"}],\"name\":\"remove\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[2153356875,2260153337],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"components\":[{\"internalType\":\"enum ConditionOP\",\"name\":\"op\",\"type\":\"uint8\"},{\"internalType\":\"string\",\"name\":\"value\",\"type\":\"string\"}],\"internalType\":\"struct Condition[]\",\"name\":\"conditions\",\"type\":\"tuple[]\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"offset\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"count\",\"type\":\"uint256\"}],\"internalType\":\"struct Limit\",\"name\":\"limit\",\"type\":\"tuple\"}],\"name\":\"remove\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[4115950576,4212943453],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"components\":[{\"internalType\":\"enum ConditionOP\",\"name\":\"op\",\"type\":\"uint8\"},{\"internalType\":\"string\",\"name\":\"value\",\"type\":\"string\"}],\"internalType\":\"struct Condition[]\",\"name\":\"conditions\",\"type\":\"tuple[]\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"offset\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"count\",\"type\":\"uint256\"}],\"internalType\":\"struct Limit\",\"name\":\"limit\",\"type\":\"tuple\"}],\"name\":\"select\",\"outputs\":[{\"components\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"},{\"internalType\":\"string[]\",\"name\":\"fields\",\"type\":\"string[]\"}],\"internalType\":\"struct Entry[]\",\"name\":\"\",\"type\":\"tuple[]\"}],\"selector\":[1181197915,3336676491],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"}],\"name\":\"select\",\"outputs\":[{\"components\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"},{\"internalType\":\"string[]\",\"name\":\"fields\",\"type\":\"string[]\"}],\"internalType\":\"struct Entry\",\"name\":\"\",\"type\":\"tuple\"}],\"selector\":[4242006977,1530027384],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"index\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"value\",\"type\":\"string\"}],\"internalType\":\"struct UpdateField[]\",\"name\":\"updateFields\",\"type\":\"tuple[]\"}],\"name\":\"update\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[146372353,2184687559],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"components\":[{\"internalType\":\"enum ConditionOP\",\"name\":\"op\",\"type\":\"uint8\"},{\"internalType\":\"string\",\"name\":\"value\",\"type\":\"string\"}],\"internalType\":\"struct Condition[]\",\"name\":\"conditions\",\"type\":\"tuple[]\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"offset\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"count\",\"type\":\"uint256\"}],\"internalType\":\"struct Limit\",\"name\":\"limit\",\"type\":\"tuple\"},{\"components\":[{\"internalType\":\"uint256\",\"name\":\"index\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"value\",\"type\":\"string\"}],\"internalType\":\"struct UpdateField[]\",\"name\":\"updateFields\",\"type\":\"tuple[]\"}],\"name\":\"update\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"selector\":[615317943,1360754138],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_DESC = "desc";

    public static final String FUNC_INSERT = "insert";

    public static final String FUNC_REMOVE = "remove";

    public static final String FUNC_SELECT = "select";

    public static final String FUNC_UPDATE = "update";

    protected TablePrecompiled(String contractAddress, Client client, CryptoKeyPair credential) {
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

    public TransactionReceipt insert(Entry entry) {
        final Function function =
                new Function(
                        FUNC_INSERT,
                        Arrays.<Type>asList(entry),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String insert(Entry entry, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_INSERT,
                        Arrays.<Type>asList(entry),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForInsert(Entry entry) {
        final Function function =
                new Function(
                        FUNC_INSERT,
                        Arrays.<Type>asList(entry),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public Tuple1<Entry> getInsertInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_INSERT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Entry>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<Entry>((Entry) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getInsertOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_INSERT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt remove(String key) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(new Utf8String(key)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String remove(String key, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(new Utf8String(key)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForRemove(String key) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(new Utf8String(key)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public Tuple1<String> getRemoveStringInput(TransactionReceipt transactionReceipt) {
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

    public Tuple1<BigInteger> getRemoveStringOutput(TransactionReceipt transactionReceipt) {
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

    public TransactionReceipt remove(List<Condition> conditions, Limit limit) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(
                                new DynamicArray<Condition>(Condition.class, conditions), limit),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String remove(List<Condition> conditions, Limit limit, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(
                                new DynamicArray<Condition>(Condition.class, conditions), limit),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForRemove(DynamicArray<Condition> conditions, Limit limit) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(
                                new DynamicArray<Condition>(Condition.class, conditions.getValue()),
                                limit),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public Tuple2<DynamicArray<Condition>, Limit> getRemoveTupletupleTupleInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<DynamicArray<Condition>>() {},
                                new TypeReference<Limit>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<DynamicArray<Condition>, Limit>(
                (DynamicArray<Condition>) results.get(0).getValue(),
                (Limit) results.get(1).getValue());
    }

    public Tuple1<BigInteger> getRemoveTupletupleTupleOutput(
            TransactionReceipt transactionReceipt) {
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

    public List select(List<Condition> conditions, Limit limit) throws ContractException {
        final Function function =
                new Function(
                        FUNC_SELECT,
                        Arrays.<Type>asList(
                                new DynamicArray<Condition>(Condition.class, conditions), limit),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<DynamicArray<Entry>>() {}));
        List<Type> result = (List<Type>) executeCallWithSingleValueReturn(function, List.class);
        return result;
    }

    public Entry select(String key) throws ContractException {
        final Function function =
                new Function(
                        FUNC_SELECT,
                        Arrays.<Type>asList(new Utf8String(key)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Entry>() {}));
        return executeCallWithSingleValueReturn(function, Entry.class);
    }

    public TransactionReceipt update(String key, List<UpdateField> updateFields) {
        final Function function =
                new Function(
                        FUNC_UPDATE,
                        Arrays.<Type>asList(
                                new Utf8String(key),
                                new DynamicArray<UpdateField>(UpdateField.class, updateFields)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String update(String key, List<UpdateField> updateFields, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_UPDATE,
                        Arrays.<Type>asList(
                                new Utf8String(key),
                                new DynamicArray<UpdateField>(UpdateField.class, updateFields)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForUpdate(
            String key, DynamicArray<UpdateField> updateFields) {
        final Function function =
                new Function(
                        FUNC_UPDATE,
                        Arrays.<Type>asList(
                                new Utf8String(key),
                                new DynamicArray<UpdateField>(
                                        UpdateField.class, updateFields.getValue())),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public Tuple2<String, DynamicArray<UpdateField>> getUpdateStringTupletupleInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_UPDATE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<DynamicArray<UpdateField>>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, DynamicArray<UpdateField>>(
                (String) results.get(0).getValue(),
                (DynamicArray<UpdateField>) results.get(1).getValue());
    }

    public Tuple1<BigInteger> getUpdateStringTupletupleOutput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_UPDATE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt update(
            List<Condition> conditions, Limit limit, List<UpdateField> updateFields) {
        final Function function =
                new Function(
                        FUNC_UPDATE,
                        Arrays.<Type>asList(
                                new DynamicArray<Condition>(Condition.class, conditions),
                                limit,
                                new DynamicArray<UpdateField>(UpdateField.class, updateFields)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String update(
            List<Condition> conditions,
            Limit limit,
            List<UpdateField> updateFields,
            TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_UPDATE,
                        Arrays.<Type>asList(
                                new DynamicArray<Condition>(Condition.class, conditions),
                                limit,
                                new DynamicArray<UpdateField>(UpdateField.class, updateFields)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForUpdate(
            DynamicArray<Condition> conditions,
            Limit limit,
            DynamicArray<UpdateField> updateFields) {
        final Function function =
                new Function(
                        FUNC_UPDATE,
                        Arrays.<Type>asList(
                                new DynamicArray<Condition>(Condition.class, conditions.getValue()),
                                limit,
                                new DynamicArray<UpdateField>(
                                        UpdateField.class, updateFields.getValue())),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public Tuple3<DynamicArray<Condition>, Limit, DynamicArray<UpdateField>>
            getUpdateTupletupleTupleTupletupleInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_UPDATE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<DynamicArray<Condition>>() {},
                                new TypeReference<Limit>() {},
                                new TypeReference<DynamicArray<UpdateField>>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<DynamicArray<Condition>, Limit, DynamicArray<UpdateField>>(
                (DynamicArray<Condition>) results.get(0).getValue(),
                (Limit) results.get(1).getValue(),
                (DynamicArray<UpdateField>) results.get(2).getValue());
    }

    public Tuple1<BigInteger> getUpdateTupletupleTupleTupletupleOutput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_UPDATE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public static TablePrecompiled load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new TablePrecompiled(contractAddress, client, credential);
    }

    public static TablePrecompiled deploy(Client client, CryptoKeyPair credential)
            throws ContractException {
        return deploy(
                TablePrecompiled.class,
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

    public static class Entry extends DynamicStruct {
        public String key;

        public List<String> fields;

        public Entry(Utf8String key, DynamicArray<Utf8String> fields) {
            super(key, fields);
            this.key = key.getValue();
            this.fields =
                    fields.getValue().stream()
                            .map(Utf8String::getValue)
                            .collect(Collectors.toList());
        }

        public Entry(String key, List<String> fields) {
            super(
                    new Utf8String(key),
                    new DynamicArray<>(
                            Utf8String.class,
                            fields.stream().map(Utf8String::new).collect(Collectors.toList())));
            this.key = key;
            this.fields = fields;
        }
    }

    public static class Condition extends DynamicStruct {
        public BigInteger op;

        public String value;

        public Condition(Uint8 op, Utf8String value) {
            super(op, value);
            this.op = op.getValue();
            this.value = value.getValue();
        }

        public Condition(BigInteger op, String value) {
            super(new Uint8(op), new Utf8String(value));
            this.op = op;
            this.value = value;
        }
    }

    public static class Limit extends StaticStruct {
        private BigInteger offset;

        private BigInteger count;

        public Limit() {
            super(new Uint128(0), new Uint128(0));
            this.offset = BigInteger.ZERO;
            this.count = BigInteger.ZERO;
        }

        public Limit(Uint128 offset, Uint128 count) {
            super(offset, count);
            this.offset = offset.getValue();
            this.count = count.getValue();
        }

        public Limit(BigInteger offset, BigInteger count) {
            super(new Uint128(offset), new Uint128(count));
            this.offset = offset;
            this.count = count;
        }

        public Limit(int offset, int count) {
            super(new Uint128(offset), new Uint128(count));
            this.offset = BigInteger.valueOf(offset);
            this.count = BigInteger.valueOf(count);
        }

        public void setOffset(BigInteger offset) {
            this.offset = offset;
        }

        public void setCount(BigInteger count) {
            this.count = count;
        }
    }

    public static class UpdateField extends DynamicStruct {
        public BigInteger index;

        public String value;

        public UpdateField(Uint256 index, Utf8String value) {
            super(index, value);
            this.index = index.getValue();
            this.value = value.getValue();
        }

        public UpdateField(BigInteger index, String value) {
            super(new Uint256(index), new Utf8String(value));
            this.index = index;
            this.value = value;
        }
    }
}
