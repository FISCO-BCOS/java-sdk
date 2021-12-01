package org.fisco.bcos.sdk.contract.precompiled.crud;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.codec.datatypes.Bool;
import org.fisco.bcos.sdk.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.codec.datatypes.DynamicStruct;
import org.fisco.bcos.sdk.codec.datatypes.Function;
import org.fisco.bcos.sdk.codec.datatypes.Type;
import org.fisco.bcos.sdk.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.codec.datatypes.generated.Int256;
import org.fisco.bcos.sdk.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.codec.datatypes.generated.tuples.generated.Tuple3;
import org.fisco.bcos.sdk.contract.Contract;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class KVTablePrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {
        "608060405234801561001057600080fd5b5061084f806100206000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c80633e10510b1461005157806356004b6a146100825780635d0d6d54146100b2578063649a8428146100e3575b600080fd5b61006b60048036038101906100669190610326565b610113565b604051610079929190610647565b60405180910390f35b61009c60048036038101906100979190610392565b610124565b6040516100a99190610677565b60405180910390f35b6100cc60048036038101906100c791906102e5565b61012d565b6040516100da929190610692565b60405180910390f35b6100fd60048036038101906100f89190610429565b610135565b60405161010a9190610677565b60405180910390f35b600061011d61013e565b9250929050565b60009392505050565b606080915091565b60009392505050565b6040518060200160405280606081525090565b600082601f83011261016257600080fd5b8135610175610170826106f6565b6106c9565b9150818183526020840193506020810190508360005b838110156101bb57813586016101a18882610269565b84526020840193506020830192505060018101905061018b565b5050505092915050565b600082601f8301126101d657600080fd5b81356101e96101e48261071e565b6106c9565b9150808252602083016020830185838301111561020557600080fd5b6102108382846107c6565b50505092915050565b60006020828403121561022b57600080fd5b61023560206106c9565b9050600082013567ffffffffffffffff81111561025157600080fd5b61025d84828501610151565b60008301525092915050565b60006040828403121561027b57600080fd5b61028560406106c9565b9050600082013567ffffffffffffffff8111156102a157600080fd5b6102ad848285016101c5565b600083015250602082013567ffffffffffffffff8111156102cd57600080fd5b6102d9848285016101c5565b60208301525092915050565b6000602082840312156102f757600080fd5b600082013567ffffffffffffffff81111561031157600080fd5b61031d848285016101c5565b91505092915050565b6000806040838503121561033957600080fd5b600083013567ffffffffffffffff81111561035357600080fd5b61035f858286016101c5565b925050602083013567ffffffffffffffff81111561037c57600080fd5b610388858286016101c5565b9150509250929050565b6000806000606084860312156103a757600080fd5b600084013567ffffffffffffffff8111156103c157600080fd5b6103cd868287016101c5565b935050602084013567ffffffffffffffff8111156103ea57600080fd5b6103f6868287016101c5565b925050604084013567ffffffffffffffff81111561041357600080fd5b61041f868287016101c5565b9150509250925092565b60008060006060848603121561043e57600080fd5b600084013567ffffffffffffffff81111561045857600080fd5b610464868287016101c5565b935050602084013567ffffffffffffffff81111561048157600080fd5b61048d868287016101c5565b925050604084013567ffffffffffffffff8111156104aa57600080fd5b6104b686828701610219565b9150509250925092565b60006104cc8383610603565b905092915050565b60006104df8261075a565b6104e9818561077d565b9350836020820285016104fb8561074a565b8060005b85811015610537578484038952815161051885826104c0565b945061052383610770565b925060208a019950506001810190506104ff565b50829750879550505050505092915050565b610552816107b0565b82525050565b610561816107bc565b82525050565b600061057282610765565b61057c818561078e565b935061058c8185602086016107d5565b61059581610808565b840191505092915050565b60006105ab82610765565b6105b5818561079f565b93506105c58185602086016107d5565b6105ce81610808565b840191505092915050565b600060208301600083015184820360008601526105f682826104d4565b9150508091505092915050565b600060408301600083015184820360008601526106208282610567565b9150506020830151848203602086015261063a8282610567565b9150508091505092915050565b600060408201905061065c6000830185610549565b818103602083015261066e81846105d9565b90509392505050565b600060208201905061068c6000830184610558565b92915050565b600060408201905081810360008301526106ac81856105a0565b905081810360208301526106c081846105a0565b90509392505050565b6000604051905081810181811067ffffffffffffffff821117156106ec57600080fd5b8060405250919050565b600067ffffffffffffffff82111561070d57600080fd5b602082029050602081019050919050565b600067ffffffffffffffff82111561073557600080fd5b601f19601f8301169050602081019050919050565b6000819050602082019050919050565b600081519050919050565b600081519050919050565b6000602082019050919050565b600082825260208201905092915050565b600082825260208201905092915050565b600082825260208201905092915050565b60008115159050919050565b6000819050919050565b82818337600083830152505050565b60005b838110156107f35780820151818401526020810190506107d8565b83811115610802576000848401525b50505050565b6000601f19601f830116905091905056fea2646970667358221220d02540de7e512addebf9c4d2652eb7cdaa95d3b55087978ebd8a2fea5985447364736f6c634300060a0033"
    };

    public static final String BINARY =
            org.fisco.bcos.sdk.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "608060405234801561001057600080fd5b5061084f806100206000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c8063af50817414610051578063b885d5ac14610081578063c92a7801146100b2578063dcce5531146100e2575b600080fd5b61006b60048036038101906100669190610429565b610113565b6040516100789190610677565b60405180910390f35b61009b600480360381019061009691906102e5565b61011c565b6040516100a9929190610692565b60405180910390f35b6100cc60048036038101906100c79190610392565b610124565b6040516100d99190610677565b60405180910390f35b6100fc60048036038101906100f79190610326565b61012d565b60405161010a929190610647565b60405180910390f35b60009392505050565b606080915091565b60009392505050565b600061013761013e565b9250929050565b6040518060200160405280606081525090565b600082601f83011261016257600080fd5b8135610175610170826106f6565b6106c9565b9150818183526020840193506020810190508360005b838110156101bb57813586016101a18882610269565b84526020840193506020830192505060018101905061018b565b5050505092915050565b600082601f8301126101d657600080fd5b81356101e96101e48261071e565b6106c9565b9150808252602083016020830185838301111561020557600080fd5b6102108382846107c6565b50505092915050565b60006020828403121561022b57600080fd5b61023560206106c9565b9050600082013567ffffffffffffffff81111561025157600080fd5b61025d84828501610151565b60008301525092915050565b60006040828403121561027b57600080fd5b61028560406106c9565b9050600082013567ffffffffffffffff8111156102a157600080fd5b6102ad848285016101c5565b600083015250602082013567ffffffffffffffff8111156102cd57600080fd5b6102d9848285016101c5565b60208301525092915050565b6000602082840312156102f757600080fd5b600082013567ffffffffffffffff81111561031157600080fd5b61031d848285016101c5565b91505092915050565b6000806040838503121561033957600080fd5b600083013567ffffffffffffffff81111561035357600080fd5b61035f858286016101c5565b925050602083013567ffffffffffffffff81111561037c57600080fd5b610388858286016101c5565b9150509250929050565b6000806000606084860312156103a757600080fd5b600084013567ffffffffffffffff8111156103c157600080fd5b6103cd868287016101c5565b935050602084013567ffffffffffffffff8111156103ea57600080fd5b6103f6868287016101c5565b925050604084013567ffffffffffffffff81111561041357600080fd5b61041f868287016101c5565b9150509250925092565b60008060006060848603121561043e57600080fd5b600084013567ffffffffffffffff81111561045857600080fd5b610464868287016101c5565b935050602084013567ffffffffffffffff81111561048157600080fd5b61048d868287016101c5565b925050604084013567ffffffffffffffff8111156104aa57600080fd5b6104b686828701610219565b9150509250925092565b60006104cc8383610603565b905092915050565b60006104df8261075a565b6104e9818561077d565b9350836020820285016104fb8561074a565b8060005b85811015610537578484038952815161051885826104c0565b945061052383610770565b925060208a019950506001810190506104ff565b50829750879550505050505092915050565b610552816107b0565b82525050565b610561816107bc565b82525050565b600061057282610765565b61057c818561078e565b935061058c8185602086016107d5565b61059581610808565b840191505092915050565b60006105ab82610765565b6105b5818561079f565b93506105c58185602086016107d5565b6105ce81610808565b840191505092915050565b600060208301600083015184820360008601526105f682826104d4565b9150508091505092915050565b600060408301600083015184820360008601526106208282610567565b9150506020830151848203602086015261063a8282610567565b9150508091505092915050565b600060408201905061065c6000830185610549565b818103602083015261066e81846105d9565b90509392505050565b600060208201905061068c6000830184610558565b92915050565b600060408201905081810360008301526106ac81856105a0565b905081810360208301526106c081846105a0565b90509392505050565b6000604051905081810181811067ffffffffffffffff821117156106ec57600080fd5b8060405250919050565b600067ffffffffffffffff82111561070d57600080fd5b602082029050602081019050919050565b600067ffffffffffffffff82111561073557600080fd5b601f19601f8301169050602081019050919050565b6000819050602082019050919050565b600081519050919050565b600081519050919050565b6000602082019050919050565b600082825260208201905092915050565b600082825260208201905092915050565b600082825260208201905092915050565b60008115159050919050565b6000819050919050565b82818337600083830152505050565b60005b838110156107f35780820151818401526020810190506107d8565b83811115610802576000848401525b50505050565b6000601f19601f830116905091905056fea26469706673582212202cacd984b5f74b051c02c1aa803ca62d22fa157c8af692e6005a927cec10247164736f6c634300060a0033"
    };

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"inputs\":[{\"internalType\":\"string\",\"name\":\"tableName\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"valueFields\",\"type\":\"string\"}],\"name\":\"createTable\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"tableName\",\"type\":\"string\"}],\"name\":\"desc\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"tableName\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"}],\"name\":\"get\",\"outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"},{\"components\":[{\"components\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"value\",\"type\":\"string\"}],\"internalType\":\"struct KVField[]\",\"name\":\"fields\",\"type\":\"tuple[]\"}],\"internalType\":\"struct Entry\",\"name\":\"entry\",\"type\":\"tuple\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"tableName\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"},{\"components\":[{\"components\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"value\",\"type\":\"string\"}],\"internalType\":\"struct KVField[]\",\"name\":\"fields\",\"type\":\"tuple[]\"}],\"internalType\":\"struct Entry\",\"name\":\"entry\",\"type\":\"tuple\"}],\"name\":\"set\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_CREATETABLE = "createTable";

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

    public TransactionReceipt createTable(String tableName, String key, String valueFields) {
        final Function function =
                new Function(
                        FUNC_CREATETABLE,
                        Arrays.<Type>asList(
                                new Utf8String(tableName),
                                new Utf8String(key),
                                new Utf8String(valueFields)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void createTable(
            String tableName, String key, String valueFields, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_CREATETABLE,
                        Arrays.<Type>asList(
                                new Utf8String(tableName),
                                new Utf8String(key),
                                new Utf8String(valueFields)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForCreateTable(
            String tableName, String key, String valueFields) {
        final Function function =
                new Function(
                        FUNC_CREATETABLE,
                        Arrays.<Type>asList(
                                new Utf8String(tableName),
                                new Utf8String(key),
                                new Utf8String(valueFields)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<String, String, String> getCreateTableInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_CREATETABLE,
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

    public TransactionReceipt desc(String tableName) {
        final Function function =
                new Function(
                        FUNC_DESC,
                        Arrays.<Type>asList(new Utf8String(tableName)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void desc(String tableName, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_DESC,
                        Arrays.<Type>asList(new Utf8String(tableName)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForDesc(String tableName) {
        final Function function =
                new Function(
                        FUNC_DESC,
                        Arrays.<Type>asList(new Utf8String(tableName)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getDescInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_DESC,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple2<String, String> getDescOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_DESC,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, String>(
                (String) results.get(0).getValue(), (String) results.get(1).getValue());
    }

    public Tuple2<Boolean, Entry> get(String tableName, String key) throws ContractException {
        final Function function =
                new Function(
                        FUNC_GET,
                        Arrays.<Type>asList(new Utf8String(tableName), new Utf8String(key)),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Bool>() {}, new TypeReference<Entry>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple2<Boolean, Entry>(
                (Boolean) results.get(0).getValue(), (Entry) results.get(1).getValue());
    }

    public TransactionReceipt set(String tableName, String key, Entry entry) {
        final Function function =
                new Function(
                        FUNC_SET,
                        Arrays.<Type>asList(new Utf8String(tableName), new Utf8String(key), entry),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void set(String tableName, String key, Entry entry, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_SET,
                        Arrays.<Type>asList(new Utf8String(tableName), new Utf8String(key), entry),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForSet(String tableName, String key, Entry entry) {
        final Function function =
                new Function(
                        FUNC_SET,
                        Arrays.<Type>asList(new Utf8String(tableName), new Utf8String(key), entry),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<String, String, Entry> getSetInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_SET,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Entry>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<String, String, Entry>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (Entry) results.get(2).getValue());
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
                null,
                null,
                null);
    }

    public static class KVField extends DynamicStruct {
        public String key;

        public String value;

        public KVField(Utf8String key, Utf8String value) {
            super(key, value);
            this.key = key.getValue();
            this.value = value.getValue();
        }

        public KVField(String key, String value) {
            super(new Utf8String(key), new Utf8String(value));
            this.key = key;
            this.value = value;
        }
    }

    public static class Entry extends DynamicStruct {
        public DynamicArray<KVField> fields;

        public Entry(DynamicArray<KVField> fields) {
            super(fields);
            this.fields = fields;
        }

        public Entry(List<KVTablePrecompiled.KVField> fields) {
            this(new DynamicArray<>(KVTablePrecompiled.KVField.class, fields));
        }
    }
}
