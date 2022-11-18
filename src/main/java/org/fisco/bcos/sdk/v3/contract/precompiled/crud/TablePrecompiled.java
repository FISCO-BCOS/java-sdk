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
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int32;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Uint32;
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
        "[{\"inputs\":[{\"components\":[{\"internalType\":\"enum ConditionOP\",\"name\":\"op\",\"type\":\"uint8\"},{\"internalType\":\"string\",\"name\":\"value\",\"type\":\"string\"}],\"internalType\":\"struct Condition[]\",\"name\":\"conditions\",\"type\":\"tuple[]\"}],\"name\":\"count\",\"outputs\":[{\"internalType\":\"uint32\",\"name\":\"\",\"type\":\"uint32\"}],\"selector\":[3625360167,2327356356],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"components\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"},{\"internalType\":\"string[]\",\"name\":\"fields\",\"type\":\"string[]\"}],\"internalType\":\"struct Entry\",\"name\":\"entry\",\"type\":\"tuple\"}],\"name\":\"insert\",\"outputs\":[{\"internalType\":\"int32\",\"name\":\"\",\"type\":\"int32\"}],\"selector\":[1550717023,1284216112],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"components\":[{\"internalType\":\"enum ConditionOP\",\"name\":\"op\",\"type\":\"uint8\"},{\"internalType\":\"string\",\"name\":\"value\",\"type\":\"string\"}],\"internalType\":\"struct Condition[]\",\"name\":\"conditions\",\"type\":\"tuple[]\"},{\"components\":[{\"internalType\":\"uint32\",\"name\":\"offset\",\"type\":\"uint32\"},{\"internalType\":\"uint32\",\"name\":\"count\",\"type\":\"uint32\"}],\"internalType\":\"struct Limit\",\"name\":\"limit\",\"type\":\"tuple\"}],\"name\":\"remove\",\"outputs\":[{\"internalType\":\"int32\",\"name\":\"\",\"type\":\"int32\"}],\"selector\":[1751202047,277135530],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"}],\"name\":\"remove\",\"outputs\":[{\"internalType\":\"int32\",\"name\":\"\",\"type\":\"int32\"}],\"selector\":[2153356875,2260153337],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"components\":[{\"internalType\":\"enum ConditionOP\",\"name\":\"op\",\"type\":\"uint8\"},{\"internalType\":\"string\",\"name\":\"value\",\"type\":\"string\"}],\"internalType\":\"struct Condition[]\",\"name\":\"conditions\",\"type\":\"tuple[]\"},{\"components\":[{\"internalType\":\"uint32\",\"name\":\"offset\",\"type\":\"uint32\"},{\"internalType\":\"uint32\",\"name\":\"count\",\"type\":\"uint32\"}],\"internalType\":\"struct Limit\",\"name\":\"limit\",\"type\":\"tuple\"}],\"name\":\"select\",\"outputs\":[{\"components\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"},{\"internalType\":\"string[]\",\"name\":\"fields\",\"type\":\"string[]\"}],\"internalType\":\"struct Entry[]\",\"name\":\"\",\"type\":\"tuple[]\"}],\"selector\":[1020609838,1062557692],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"}],\"name\":\"select\",\"outputs\":[{\"components\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"},{\"internalType\":\"string[]\",\"name\":\"fields\",\"type\":\"string[]\"}],\"internalType\":\"struct Entry\",\"name\":\"\",\"type\":\"tuple\"}],\"selector\":[4242006977,1530027384],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"},{\"components\":[{\"internalType\":\"string\",\"name\":\"columnName\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"value\",\"type\":\"string\"}],\"internalType\":\"struct UpdateField[]\",\"name\":\"updateFields\",\"type\":\"tuple[]\"}],\"name\":\"update\",\"outputs\":[{\"internalType\":\"int32\",\"name\":\"\",\"type\":\"int32\"}],\"selector\":[1107285855,33194060],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"components\":[{\"internalType\":\"enum ConditionOP\",\"name\":\"op\",\"type\":\"uint8\"},{\"internalType\":\"string\",\"name\":\"value\",\"type\":\"string\"}],\"internalType\":\"struct Condition[]\",\"name\":\"conditions\",\"type\":\"tuple[]\"},{\"components\":[{\"internalType\":\"uint32\",\"name\":\"offset\",\"type\":\"uint32\"},{\"internalType\":\"uint32\",\"name\":\"count\",\"type\":\"uint32\"}],\"internalType\":\"struct Limit\",\"name\":\"limit\",\"type\":\"tuple\"},{\"components\":[{\"internalType\":\"string\",\"name\":\"columnName\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"value\",\"type\":\"string\"}],\"internalType\":\"struct UpdateField[]\",\"name\":\"updateFields\",\"type\":\"tuple[]\"}],\"name\":\"update\",\"outputs\":[{\"internalType\":\"int32\",\"name\":\"\",\"type\":\"int32\"}],\"selector\":[2572410770,107820592],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_COUNT = "count";

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

    public BigInteger count(List<Condition> conditions) throws ContractException {
        final Function function =
                new Function(
                        FUNC_COUNT,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray<Condition>(
                                        Condition.class, conditions)),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Uint32>() {}));
        return executeCallWithSingleValueReturn(function, BigInteger.class);
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
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int32>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt remove(List<Condition> conditions, Limit limit) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray<Condition>(
                                        Condition.class, conditions),
                                limit),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String remove(List<Condition> conditions, Limit limit, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray<Condition>(
                                        Condition.class, conditions),
                                limit),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForRemove(List<Condition> conditions, Limit limit) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray<Condition>(
                                        Condition.class, conditions),
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

    public TransactionReceipt remove(String key) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(key)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String remove(String key, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(key)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForRemove(String key) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(key)),
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

    public Tuple1<BigInteger> getRemoveOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int32>() {}));
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
        return (List<Type>) executeCallWithSingleValueReturn(function, List.class);
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

    public String getSignedTransactionForUpdate(String key, List<UpdateField> updateFields) {
        final Function function =
                new Function(
                        FUNC_UPDATE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(key),
                                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray<UpdateField>(
                                        UpdateField.class, updateFields)),
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
            List<Condition> conditions, Limit limit, List<UpdateField> updateFields) {
        final Function function =
                new Function(
                        FUNC_UPDATE,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray<Condition>(
                                        Condition.class, conditions),
                                limit,
                                new org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray<UpdateField>(
                                        UpdateField.class, updateFields)),
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

    public Tuple1<BigInteger> getUpdateOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_UPDATE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int32>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public static TablePrecompiled load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new TablePrecompiled(contractAddress, client, credential);
    }

    public static class Entry extends DynamicStruct {
        public String key;

        public List<String> fields;

        public Entry() {
            super(new Utf8String(""), new DynamicArray<Utf8String>());
        }

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
        private BigInteger op;

        private String condValue;

        public Condition() {
            super(new Uint8(0), new Utf8String(""));
        }

        public Condition(Uint8 op, Utf8String value) {
            super(op, value);
            this.op = op.getValue();
            this.condValue = value.getValue();
        }

        public Condition(BigInteger op, String value) {
            super(new Uint8(op), new Utf8String(value));
            this.op = op;
            this.condValue = value;
        }

        @Override
        public String toString() {
            return "Condition{" + "op=" + op + ", condValue='" + condValue + '\'' + '}';
        }
    }

    public static class Limit extends StaticStruct {
        public static final long MAX_ROW_COUNT = 500;

        private BigInteger offset;

        private BigInteger count;

        public Limit() {
            super(new Uint32(0), new Uint32(MAX_ROW_COUNT));
            this.offset = BigInteger.ZERO;
            this.count = BigInteger.valueOf(MAX_ROW_COUNT);
        }

        public Limit(Uint32 offset, Uint32 count) {
            super(offset, count);
            this.offset = offset.getValue();
            this.count = count.getValue();
        }

        public Limit(BigInteger offset, BigInteger count) {
            super(new Uint32(offset), new Uint32(count));
            this.offset = offset;
            this.count = count;
        }

        public Limit(int offset, int count) {
            super(new Uint32(offset), new Uint32(count));
            this.offset = BigInteger.valueOf(offset);
            this.count = BigInteger.valueOf(count);
        }

        public void setOffset(BigInteger offset) {
            this.offset = offset;
        }

        public void setCount(BigInteger count) {
            this.count = count;
        }

        @Override
        public String toString() {
            return "Limit{" + "offset=" + offset + ", count=" + count + '}';
        }
    }

    public static class UpdateField extends DynamicStruct {
        private String columnName;

        private String columnValue;

        public UpdateField() {
            super(new Utf8String(""), new Utf8String(""));
        }

        public UpdateField(Utf8String columnName, Utf8String value) {
            super(columnName, value);
            this.columnName = columnName.getValue();
            this.columnValue = value.getValue();
        }

        public UpdateField(String columnName, String value) {
            super(
                    new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(columnName),
                    new org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String(value));
            this.columnName = columnName;
            this.columnValue = value;
        }

        @Override
        public String toString() {
            return "UpdateField{"
                    + "columnName="
                    + columnName
                    + ", columnValue='"
                    + columnValue
                    + '\''
                    + '}';
        }
    }
}
