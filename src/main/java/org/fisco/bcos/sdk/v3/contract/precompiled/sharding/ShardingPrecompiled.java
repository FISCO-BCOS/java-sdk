package org.fisco.bcos.sdk.v3.contract.precompiled.sharding;

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
public class ShardingPrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {
        "608060405234801561001057600080fd5b5061027f806100206000396000f3fe608060405234801561001057600080fd5b50600436106100415760003560e01c80631d82d998146100465780639c1284bc1461006d578063b7ede6cb14610092575b600080fd5b61005a61005436600461014b565b50600090565b6040519081526020015b60405180910390f35b61008461007b36600461014b565b60006060915091565b604051610064929190610188565b61005a6100a03660046101e5565b600092915050565b634e487b7160e01b600052604160045260246000fd5b600082601f8301126100cf57600080fd5b813567ffffffffffffffff808211156100ea576100ea6100a8565b604051601f8301601f19908116603f01168101908282118183101715610112576101126100a8565b8160405283815286602085880101111561012b57600080fd5b836020870160208301376000602085830101528094505050505092915050565b60006020828403121561015d57600080fd5b813567ffffffffffffffff81111561017457600080fd5b610180848285016100be565b949350505050565b82815260006020604081840152835180604085015260005b818110156101bc578581018301518582016060015282016101a0565b818111156101ce576000606083870101525b50601f01601f191692909201606001949350505050565b600080604083850312156101f857600080fd5b823567ffffffffffffffff8082111561021057600080fd5b61021c868387016100be565b9350602085013591508082111561023257600080fd5b5061023f858286016100be565b915050925092905056fea26469706673582212201d63f9baf4cb69a7122bb65d9774a882029582eb9471f8bcc9e1bccd9fb7636164736f6c634300080b0033"
    };

    public static final String BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "608060405234801561001057600080fd5b50610283806100206000396000f3fe608060405234801561001057600080fd5b50600436106100415760003560e01c806346be3ae114610046578063d31591d214610074578063f76f20ca14610098575b600080fd5b61005d61005436600461014f565b60006060915091565b60405161006b92919061018c565b60405180910390f35b61008a6100823660046101e9565b600092915050565b60405190815260200161006b565b61008a6100a636600461014f565b50600090565b63b95aa35560e01b600052604160045260246000fd5b600082601f8301126100d357600080fd5b813567ffffffffffffffff808211156100ee576100ee6100ac565b604051601f8301601f19908116603f01168101908282118183101715610116576101166100ac565b8160405283815286602085880101111561012f57600080fd5b836020870160208301376000602085830101528094505050505092915050565b60006020828403121561016157600080fd5b813567ffffffffffffffff81111561017857600080fd5b610184848285016100c2565b949350505050565b82815260006020604081840152835180604085015260005b818110156101c0578581018301518582016060015282016101a4565b818111156101d2576000606083870101525b50601f01601f191692909201606001949350505050565b600080604083850312156101fc57600080fd5b823567ffffffffffffffff8082111561021457600080fd5b610220868387016100c2565b9350602085013591508082111561023657600080fd5b50610243858286016100c2565b915050925092905056fea2646970667358221220afd1216fc346bdc04dbbace27779ff191659b571289147caa7781b74ab2a3b3064736f6c634300080b0033"
    };

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"inputs\":[{\"internalType\":\"string\",\"name\":\"absolutePath\",\"type\":\"string\"}],\"name\":\"getContractShard\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"},{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"shardName\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"_address\",\"type\":\"string\"}],\"name\":\"linkShard\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"shardName\",\"type\":\"string\"}],\"name\":\"makeShard\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.v3.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_GETCONTRACTSHARD = "getContractShard";

    public static final String FUNC_LINKSHARD = "linkShard";

    public static final String FUNC_MAKESHARD = "makeShard";

    protected ShardingPrecompiled(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public Tuple2<BigInteger, String> getContractShard(String absolutePath)
            throws ContractException {
        final Function function =
                new Function(
                        FUNC_GETCONTRACTSHARD,
                        Arrays.<Type>asList(new Utf8String(absolutePath)),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Int256>() {},
                                new TypeReference<Utf8String>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple2<BigInteger, String>(
                (BigInteger) results.get(0).getValue(), (String) results.get(1).getValue());
    }

    public TransactionReceipt linkShard(String shardName, String _address) {
        final Function function =
                new Function(
                        FUNC_LINKSHARD,
                        Arrays.<Type>asList(new Utf8String(shardName), new Utf8String(_address)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String linkShard(String shardName, String _address, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_LINKSHARD,
                        Arrays.<Type>asList(new Utf8String(shardName), new Utf8String(_address)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForLinkShard(String shardName, String _address) {
        final Function function =
                new Function(
                        FUNC_LINKSHARD,
                        Arrays.<Type>asList(new Utf8String(shardName), new Utf8String(_address)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public Tuple2<String, String> getLinkShardInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_LINKSHARD,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple2<String, String>(
                (String) results.get(0).getValue(), (String) results.get(1).getValue());
    }

    public Tuple1<BigInteger> getLinkShardOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_LINKSHARD,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public TransactionReceipt makeShard(String shardName) {
        final Function function =
                new Function(
                        FUNC_MAKESHARD,
                        Arrays.<Type>asList(new Utf8String(shardName)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return executeTransaction(function);
    }

    public String makeShard(String shardName, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_MAKESHARD,
                        Arrays.<Type>asList(new Utf8String(shardName)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForMakeShard(String shardName) {
        final Function function =
                new Function(
                        FUNC_MAKESHARD,
                        Arrays.<Type>asList(new Utf8String(shardName)),
                        Collections.<TypeReference<?>>emptyList(),
                        0);
        return createSignedTransaction(function);
    }

    public Tuple1<String> getMakeShardInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_MAKESHARD,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getMakeShardOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_MAKESHARD,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public static ShardingPrecompiled load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new ShardingPrecompiled(contractAddress, client, credential);
    }
}
