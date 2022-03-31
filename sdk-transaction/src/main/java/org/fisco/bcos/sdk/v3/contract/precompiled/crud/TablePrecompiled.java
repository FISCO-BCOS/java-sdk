package org.fisco.bcos.sdk.v3.contract.precompiled.crud;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicStruct;
import org.fisco.bcos.sdk.v3.codec.datatypes.Function;
import org.fisco.bcos.sdk.v3.codec.datatypes.Type;
import org.fisco.bcos.sdk.v3.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.Int256;
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
import org.fisco.bcos.sdk.v3.utils.StringUtils;

/** This class not support in FISCO BCOS 3.0.0 rc1 Do not use it. */
@Deprecated
@SuppressWarnings("unchecked")
public class TablePrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {
        "608060405234801561001057600080fd5b50610b5e806100206000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c80631b5c585f146100675780633b747bf41461009757806345cfc70e146100c757806351c97b5b146100f757806356004b6a146101275780635d0d6d5414610157575b600080fd5b610081600480360381019061007c91906105fb565b610188565b60405161008e9190610921565b60405180910390f35b6100b160048036038101906100ac919061058f565b610190565b6040516100be9190610921565b60405180910390f35b6100e160048036038101906100dc919061058f565b610198565b6040516100ee91906108ff565b60405180910390f35b610111600480360381019061010c9190610667565b6101a0565b60405161011e9190610921565b60405180910390f35b610141600480360381019061013c91906104f8565b6101a9565b60405161014e9190610921565b60405180910390f35b610171600480360381019061016c91906104b7565b6101b2565b60405161017f92919061093c565b60405180910390f35b600092915050565b600092915050565b606092915050565b60009392505050565b60009392505050565b606080915091565b600082601f8301126101cb57600080fd5b81356101de6101d9826109a0565b610973565b9150818183526020840193506020810190508360005b83811015610224578135860161020a888261030b565b8452602084019350602083019250506001810190506101f4565b5050505092915050565b600082601f83011261023f57600080fd5b813561025261024d826109c8565b610973565b9150818183526020840193506020810190508360005b83811015610298578135860161027e888261043b565b845260208401935060208301925050600181019050610268565b5050505092915050565b6000813590506102b181610b18565b92915050565b600082601f8301126102c857600080fd5b81356102db6102d6826109f0565b610973565b915080825260208301602083018583830111156102f757600080fd5b610302838284610ac5565b50505092915050565b60006060828403121561031d57600080fd5b6103276060610973565b9050600082013567ffffffffffffffff81111561034357600080fd5b61034f848285016102b7565b600083015250602082013567ffffffffffffffff81111561036f57600080fd5b61037b848285016102b7565b602083015250604061038f848285016102a2565b60408301525092915050565b6000602082840312156103ad57600080fd5b6103b76020610973565b9050600082013567ffffffffffffffff8111156103d357600080fd5b6103df848285016101ba565b60008301525092915050565b6000602082840312156103fd57600080fd5b6104076020610973565b9050600082013567ffffffffffffffff81111561042357600080fd5b61042f8482850161022e565b60008301525092915050565b60006040828403121561044d57600080fd5b6104576040610973565b9050600082013567ffffffffffffffff81111561047357600080fd5b61047f848285016102b7565b600083015250602082013567ffffffffffffffff81111561049f57600080fd5b6104ab848285016102b7565b60208301525092915050565b6000602082840312156104c957600080fd5b600082013567ffffffffffffffff8111156104e357600080fd5b6104ef848285016102b7565b91505092915050565b60008060006060848603121561050d57600080fd5b600084013567ffffffffffffffff81111561052757600080fd5b610533868287016102b7565b935050602084013567ffffffffffffffff81111561055057600080fd5b61055c868287016102b7565b925050604084013567ffffffffffffffff81111561057957600080fd5b610585868287016102b7565b9150509250925092565b600080604083850312156105a257600080fd5b600083013567ffffffffffffffff8111156105bc57600080fd5b6105c8858286016102b7565b925050602083013567ffffffffffffffff8111156105e557600080fd5b6105f18582860161039b565b9150509250929050565b6000806040838503121561060e57600080fd5b600083013567ffffffffffffffff81111561062857600080fd5b610634858286016102b7565b925050602083013567ffffffffffffffff81111561065157600080fd5b61065d858286016103eb565b9150509250929050565b60008060006060848603121561067c57600080fd5b600084013567ffffffffffffffff81111561069657600080fd5b6106a2868287016102b7565b935050602084013567ffffffffffffffff8111156106bf57600080fd5b6106cb868287016103eb565b925050604084013567ffffffffffffffff8111156106e857600080fd5b6106f48682870161039b565b9150509250925092565b600061070a8383610891565b905092915050565b600061071e83836108bb565b905092915050565b600061073182610a3c565b61073b8185610a77565b93508360208202850161074d85610a1c565b8060005b85811015610789578484038952815161076a85826106fe565b945061077583610a5d565b925060208a01995050600181019050610751565b50829750879550505050505092915050565b60006107a682610a47565b6107b08185610a88565b9350836020820285016107c285610a2c565b8060005b858110156107fe57848403895281516107df8582610712565b94506107ea83610a6a565b925060208a019950506001810190506107c6565b50829750879550505050505092915050565b61081981610abb565b82525050565b600061082a82610a52565b6108348185610a99565b9350610844818560208601610ad4565b61084d81610b07565b840191505092915050565b600061086382610a52565b61086d8185610aaa565b935061087d818560208601610ad4565b61088681610b07565b840191505092915050565b600060208301600083015184820360008601526108ae828261079b565b9150508091505092915050565b600060408301600083015184820360008601526108d8828261081f565b915050602083015184820360208601526108f2828261081f565b9150508091505092915050565b600060208201905081810360008301526109198184610726565b905092915050565b60006020820190506109366000830184610810565b92915050565b600060408201905081810360008301526109568185610858565b9050818103602083015261096a8184610858565b90509392505050565b6000604051905081810181811067ffffffffffffffff8211171561099657600080fd5b8060405250919050565b600067ffffffffffffffff8211156109b757600080fd5b602082029050602081019050919050565b600067ffffffffffffffff8211156109df57600080fd5b602082029050602081019050919050565b600067ffffffffffffffff821115610a0757600080fd5b601f19601f8301169050602081019050919050565b6000819050602082019050919050565b6000819050602082019050919050565b600081519050919050565b600081519050919050565b600081519050919050565b6000602082019050919050565b6000602082019050919050565b600082825260208201905092915050565b600082825260208201905092915050565b600082825260208201905092915050565b600082825260208201905092915050565b6000819050919050565b82818337600083830152505050565b60005b83811015610af2578082015181840152602081019050610ad7565b83811115610b01576000848401525b50505050565b6000601f19601f8301169050919050565b60068110610b2557600080fd5b5056fea264697066735822122010ce50418f77e92d828d9b09666c842e0a15dfa279687227a9a34a9128a4571a64736f6c634300060a0033"
    };

    public static final String BINARY = StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "608060405234801561001057600080fd5b50610b5e806100206000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c80631f6c939314610067578063575b65411461009757806360620ca7146100c757806397390eed146100f7578063b885d5ac14610127578063c92a780114610158575b600080fd5b610081600480360381019061007c9190610667565b610188565b60405161008e9190610921565b60405180910390f35b6100b160048036038101906100ac919061058f565b610191565b6040516100be91906108ff565b60405180910390f35b6100e160048036038101906100dc919061058f565b610199565b6040516100ee9190610921565b60405180910390f35b610111600480360381019061010c91906105fb565b6101a1565b60405161011e9190610921565b60405180910390f35b610141600480360381019061013c91906104b7565b6101a9565b60405161014f92919061093c565b60405180910390f35b610172600480360381019061016d91906104f8565b6101b1565b60405161017f9190610921565b60405180910390f35b60009392505050565b606092915050565b600092915050565b600092915050565b606080915091565b60009392505050565b600082601f8301126101cb57600080fd5b81356101de6101d9826109a0565b610973565b9150818183526020840193506020810190508360005b83811015610224578135860161020a888261030b565b8452602084019350602083019250506001810190506101f4565b5050505092915050565b600082601f83011261023f57600080fd5b813561025261024d826109c8565b610973565b9150818183526020840193506020810190508360005b83811015610298578135860161027e888261043b565b845260208401935060208301925050600181019050610268565b5050505092915050565b6000813590506102b181610b18565b92915050565b600082601f8301126102c857600080fd5b81356102db6102d6826109f0565b610973565b915080825260208301602083018583830111156102f757600080fd5b610302838284610ac5565b50505092915050565b60006060828403121561031d57600080fd5b6103276060610973565b9050600082013567ffffffffffffffff81111561034357600080fd5b61034f848285016102b7565b600083015250602082013567ffffffffffffffff81111561036f57600080fd5b61037b848285016102b7565b602083015250604061038f848285016102a2565b60408301525092915050565b6000602082840312156103ad57600080fd5b6103b76020610973565b9050600082013567ffffffffffffffff8111156103d357600080fd5b6103df848285016101ba565b60008301525092915050565b6000602082840312156103fd57600080fd5b6104076020610973565b9050600082013567ffffffffffffffff81111561042357600080fd5b61042f8482850161022e565b60008301525092915050565b60006040828403121561044d57600080fd5b6104576040610973565b9050600082013567ffffffffffffffff81111561047357600080fd5b61047f848285016102b7565b600083015250602082013567ffffffffffffffff81111561049f57600080fd5b6104ab848285016102b7565b60208301525092915050565b6000602082840312156104c957600080fd5b600082013567ffffffffffffffff8111156104e357600080fd5b6104ef848285016102b7565b91505092915050565b60008060006060848603121561050d57600080fd5b600084013567ffffffffffffffff81111561052757600080fd5b610533868287016102b7565b935050602084013567ffffffffffffffff81111561055057600080fd5b61055c868287016102b7565b925050604084013567ffffffffffffffff81111561057957600080fd5b610585868287016102b7565b9150509250925092565b600080604083850312156105a257600080fd5b600083013567ffffffffffffffff8111156105bc57600080fd5b6105c8858286016102b7565b925050602083013567ffffffffffffffff8111156105e557600080fd5b6105f18582860161039b565b9150509250929050565b6000806040838503121561060e57600080fd5b600083013567ffffffffffffffff81111561062857600080fd5b610634858286016102b7565b925050602083013567ffffffffffffffff81111561065157600080fd5b61065d858286016103eb565b9150509250929050565b60008060006060848603121561067c57600080fd5b600084013567ffffffffffffffff81111561069657600080fd5b6106a2868287016102b7565b935050602084013567ffffffffffffffff8111156106bf57600080fd5b6106cb868287016103eb565b925050604084013567ffffffffffffffff8111156106e857600080fd5b6106f48682870161039b565b9150509250925092565b600061070a8383610891565b905092915050565b600061071e83836108bb565b905092915050565b600061073182610a3c565b61073b8185610a77565b93508360208202850161074d85610a1c565b8060005b85811015610789578484038952815161076a85826106fe565b945061077583610a5d565b925060208a01995050600181019050610751565b50829750879550505050505092915050565b60006107a682610a47565b6107b08185610a88565b9350836020820285016107c285610a2c565b8060005b858110156107fe57848403895281516107df8582610712565b94506107ea83610a6a565b925060208a019950506001810190506107c6565b50829750879550505050505092915050565b61081981610abb565b82525050565b600061082a82610a52565b6108348185610a99565b9350610844818560208601610ad4565b61084d81610b07565b840191505092915050565b600061086382610a52565b61086d8185610aaa565b935061087d818560208601610ad4565b61088681610b07565b840191505092915050565b600060208301600083015184820360008601526108ae828261079b565b9150508091505092915050565b600060408301600083015184820360008601526108d8828261081f565b915050602083015184820360208601526108f2828261081f565b9150508091505092915050565b600060208201905081810360008301526109198184610726565b905092915050565b60006020820190506109366000830184610810565b92915050565b600060408201905081810360008301526109568185610858565b9050818103602083015261096a8184610858565b90509392505050565b6000604051905081810181811067ffffffffffffffff8211171561099657600080fd5b8060405250919050565b600067ffffffffffffffff8211156109b757600080fd5b602082029050602081019050919050565b600067ffffffffffffffff8211156109df57600080fd5b602082029050602081019050919050565b600067ffffffffffffffff821115610a0757600080fd5b601f19601f8301169050602081019050919050565b6000819050602082019050919050565b6000819050602082019050919050565b600081519050919050565b600081519050919050565b600081519050919050565b6000602082019050919050565b6000602082019050919050565b600082825260208201905092915050565b600082825260208201905092915050565b600082825260208201905092915050565b600082825260208201905092915050565b6000819050919050565b82818337600083830152505050565b60005b83811015610af2578082015181840152602081019050610ad7565b83811115610b01576000848401525b50505050565b6000601f19601f8301169050919050565b60068110610b2557600080fd5b5056fea2646970667358221220610e6b467ac5657d294a12dfaa56aa9c5c8610283a6eb6327b5b33bdd2bd796864736f6c634300060a0033"
    };

    public static final String SM_BINARY = StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"inputs\":[{\"internalType\":\"string\",\"name\":\"tableName\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"valueFields\",\"type\":\"string\"}],\"name\":\"createTable\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"tableName\",\"type\":\"string\"}],\"name\":\"desc\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"tableName\",\"type\":\"string\"},{\"components\":[{\"components\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"value\",\"type\":\"string\"}],\"internalType\":\"struct KVField[]\",\"name\":\"fields\",\"type\":\"tuple[]\"}],\"internalType\":\"struct Entry\",\"name\":\"entry\",\"type\":\"tuple\"}],\"name\":\"insert\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"tableName\",\"type\":\"string\"},{\"components\":[{\"components\":[{\"internalType\":\"string\",\"name\":\"lvalue\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"rvalue\",\"type\":\"string\"},{\"internalType\":\"enum Comparator\",\"name\":\"cmp\",\"type\":\"uint8\"}],\"internalType\":\"struct CompareTriple[]\",\"name\":\"condFields\",\"type\":\"tuple[]\"}],\"internalType\":\"struct Condition\",\"name\":\"condition\",\"type\":\"tuple\"}],\"name\":\"remove\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"tableName\",\"type\":\"string\"},{\"components\":[{\"components\":[{\"internalType\":\"string\",\"name\":\"lvalue\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"rvalue\",\"type\":\"string\"},{\"internalType\":\"enum Comparator\",\"name\":\"cmp\",\"type\":\"uint8\"}],\"internalType\":\"struct CompareTriple[]\",\"name\":\"condFields\",\"type\":\"tuple[]\"}],\"internalType\":\"struct Condition\",\"name\":\"condition\",\"type\":\"tuple\"}],\"name\":\"select\",\"outputs\":[{\"components\":[{\"components\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"value\",\"type\":\"string\"}],\"internalType\":\"struct KVField[]\",\"name\":\"fields\",\"type\":\"tuple[]\"}],\"internalType\":\"struct Entry[]\",\"name\":\"\",\"type\":\"tuple[]\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"tableName\",\"type\":\"string\"},{\"components\":[{\"components\":[{\"internalType\":\"string\",\"name\":\"key\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"value\",\"type\":\"string\"}],\"internalType\":\"struct KVField[]\",\"name\":\"fields\",\"type\":\"tuple[]\"}],\"internalType\":\"struct Entry\",\"name\":\"entry\",\"type\":\"tuple\"},{\"components\":[{\"components\":[{\"internalType\":\"string\",\"name\":\"lvalue\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"rvalue\",\"type\":\"string\"},{\"internalType\":\"enum Comparator\",\"name\":\"cmp\",\"type\":\"uint8\"}],\"internalType\":\"struct CompareTriple[]\",\"name\":\"condFields\",\"type\":\"tuple[]\"}],\"internalType\":\"struct Condition\",\"name\":\"condition\",\"type\":\"tuple\"}],\"name\":\"update\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_CREATETABLE = "createTable";

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

    public TransactionReceipt insert(String tableName, Entry entry) {
        final Function function =
                new Function(
                        FUNC_INSERT,
                        Arrays.<Type>asList(new Utf8String(tableName), entry),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void insert(String tableName, Entry entry, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_INSERT,
                        Arrays.<Type>asList(new Utf8String(tableName), entry),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForInsert(String tableName, Entry entry) {
        final Function function =
                new Function(
                        FUNC_INSERT,
                        Arrays.<Type>asList(new Utf8String(tableName), entry),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<String, Entry> getInsertInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_INSERT,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {}, new TypeReference<Entry>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, Entry>(
                (String) results.get(0).getValue(), (Entry) results.get(1).getValue());
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

    public TransactionReceipt remove(String tableName, Condition condition) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(new Utf8String(tableName), condition),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void remove(String tableName, Condition condition, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(new Utf8String(tableName), condition),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForRemove(String tableName, Condition condition) {
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(new Utf8String(tableName), condition),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple2<String, Condition> getRemoveInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_REMOVE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Condition>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, Condition>(
                (String) results.get(0).getValue(), (Condition) results.get(1).getValue());
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

    public List select(String tableName, Condition condition) throws ContractException {
        final Function function =
                new Function(
                        FUNC_SELECT,
                        Arrays.<Type>asList(new Utf8String(tableName), condition),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<DynamicArray<Entry>>() {}));
        List<Type> result = (List<Type>) executeCallWithSingleValueReturn(function, List.class);
        return result;
    }

    public TransactionReceipt update(String tableName, Entry entry, Condition condition) {
        final Function function =
                new Function(
                        FUNC_UPDATE,
                        Arrays.<Type>asList(new Utf8String(tableName), entry, condition),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void update(
            String tableName, Entry entry, Condition condition, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_UPDATE,
                        Arrays.<Type>asList(new Utf8String(tableName), entry, condition),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForUpdate(
            String tableName, Entry entry, Condition condition) {
        final Function function =
                new Function(
                        FUNC_UPDATE,
                        Arrays.<Type>asList(new Utf8String(tableName), entry, condition),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple3<String, Entry, Condition> getUpdateInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_UPDATE,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Entry>() {},
                                new TypeReference<Condition>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple3<String, Entry, Condition>(
                (String) results.get(0).getValue(),
                (Entry) results.get(1).getValue(),
                (Condition) results.get(2).getValue());
    }

    public Tuple1<BigInteger> getUpdateOutput(TransactionReceipt transactionReceipt) {
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

    public static class CompareTriple extends DynamicStruct {
        public String lvalue;

        public String rvalue;

        public BigInteger cmp;

        public CompareTriple(Utf8String lvalue, Utf8String rvalue, Uint8 cmp) {
            super(lvalue, rvalue, cmp);
            this.lvalue = lvalue.getValue();
            this.rvalue = rvalue.getValue();
            this.cmp = cmp.getValue();
        }

        public CompareTriple(String lvalue, String rvalue, BigInteger cmp) {
            super(new Utf8String(lvalue), new Utf8String(rvalue), new Uint8(cmp));
            this.lvalue = lvalue;
            this.rvalue = rvalue;
            this.cmp = cmp;
        }
    }

    public static class Entry extends DynamicStruct {
        public DynamicArray<KVField> fields;

        public Entry(DynamicArray<KVField> fields) {
            super(fields);
            this.fields = fields;
        }

        public Entry(List<KVField> fields) {
            this(new DynamicArray<>(KVField.class, fields));
        }
    }

    public static class Condition extends DynamicStruct {
        public DynamicArray<CompareTriple> condFields;

        public Condition(DynamicArray<CompareTriple> condFields) {
            super(condFields);
            this.condFields = condFields;
        }

        public Condition(List<CompareTriple> condFields) {
            this(new DynamicArray<>(CompareTriple.class, condFields));
        }
    }
}
