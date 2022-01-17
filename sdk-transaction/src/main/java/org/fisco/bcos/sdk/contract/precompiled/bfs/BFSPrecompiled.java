package org.fisco.bcos.sdk.contract.precompiled.bfs;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.codec.datatypes.Address;
import org.fisco.bcos.sdk.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.codec.datatypes.DynamicStruct;
import org.fisco.bcos.sdk.codec.datatypes.Function;
import org.fisco.bcos.sdk.codec.datatypes.Type;
import org.fisco.bcos.sdk.codec.datatypes.TypeReference;
import org.fisco.bcos.sdk.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.codec.datatypes.generated.Int256;
import org.fisco.bcos.sdk.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.codec.datatypes.generated.tuples.generated.Tuple4;
import org.fisco.bcos.sdk.contract.Contract;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.model.callback.TransactionCallback;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

@SuppressWarnings("unchecked")
public class BFSPrecompiled extends Contract {
    public static final String[] BINARY_ARRAY = {
        "608060405234801561001057600080fd5b5061054a806100206000396000f3fe608060405234801561001057600080fd5b50600436106100415760003560e01c80634b7b08aa14610046578063876b0eb214610076578063fe42bf1a146100a6575b600080fd5b610060600480360381019061005b919061019b565b6100d7565b60405161006d9190610375565b60405180910390f35b610090600480360381019061008b919061015a565b6100e1565b60405161009d9190610375565b60405180910390f35b6100c060048036038101906100bb919061015a565b6100e8565b6040516100ce929190610390565b60405180910390f35b6000949350505050565b6000919050565b60006060915091565b600081359050610100816104fd565b92915050565b600082601f83011261011757600080fd5b813561012a610125826103ed565b6103c0565b9150808252602083016020830185838301111561014657600080fd5b6101518382846104aa565b50505092915050565b60006020828403121561016c57600080fd5b600082013567ffffffffffffffff81111561018657600080fd5b61019284828501610106565b91505092915050565b600080600080608085870312156101b157600080fd5b600085013567ffffffffffffffff8111156101cb57600080fd5b6101d787828801610106565b945050602085013567ffffffffffffffff8111156101f457600080fd5b61020087828801610106565b9350506040610211878288016100f1565b925050606085013567ffffffffffffffff81111561022e57600080fd5b61023a87828801610106565b91505092959194509250565b60006102528383610317565b905092915050565b600061026582610429565b61026f818561044c565b93508360208202850161028185610419565b8060005b858110156102bd578484038952815161029e8582610246565b94506102a98361043f565b925060208a01995050600181019050610285565b50829750879550505050505092915050565b6102d881610480565b82525050565b60006102e982610434565b6102f3818561045d565b93506103038185602086016104b9565b61030c816104ec565b840191505092915050565b6000606083016000830151848203600086015261033482826102de565b9150506020830151848203602086015261034e82826102de565b9150506040830151848203604086015261036882826102de565b9150508091505092915050565b600060208201905061038a60008301846102cf565b92915050565b60006040820190506103a560008301856102cf565b81810360208301526103b7818461025a565b90509392505050565b6000604051905081810181811067ffffffffffffffff821117156103e357600080fd5b8060405250919050565b600067ffffffffffffffff82111561040457600080fd5b601f19601f8301169050602081019050919050565b6000819050602082019050919050565b600081519050919050565b600081519050919050565b6000602082019050919050565b600082825260208201905092915050565b600082825260208201905092915050565b60006104798261048a565b9050919050565b6000819050919050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b82818337600083830152505050565b60005b838110156104d75780820151818401526020810190506104bc565b838111156104e6576000848401525b50505050565b6000601f19601f8301169050919050565b6105068161046e565b811461051157600080fd5b5056fea26469706673582212207e0578b39c21c41c892d8063f27db7f5d10de1184ed999511b8aec66644edde264736f6c634300060a0033"
    };

    public static final String BINARY =
            org.fisco.bcos.sdk.utils.StringUtils.joinAll("", BINARY_ARRAY);

    public static final String[] SM_BINARY_ARRAY = {
        "608060405234801561001057600080fd5b5061054a806100206000396000f3fe608060405234801561001057600080fd5b50600436106100415760003560e01c80631d7fd3ad146100465780636cd2749f1461007657806381e30b7a146100a7575b600080fd5b610060600480360381019061005b919061015a565b6100d7565b60405161006d9190610375565b60405180910390f35b610090600480360381019061008b919061015a565b6100de565b60405161009e929190610390565b60405180910390f35b6100c160048036038101906100bc919061019b565b6100e7565b6040516100ce9190610375565b60405180910390f35b6000919050565b60006060915091565b6000949350505050565b600081359050610100816104fd565b92915050565b600082601f83011261011757600080fd5b813561012a610125826103ed565b6103c0565b9150808252602083016020830185838301111561014657600080fd5b6101518382846104aa565b50505092915050565b60006020828403121561016c57600080fd5b600082013567ffffffffffffffff81111561018657600080fd5b61019284828501610106565b91505092915050565b600080600080608085870312156101b157600080fd5b600085013567ffffffffffffffff8111156101cb57600080fd5b6101d787828801610106565b945050602085013567ffffffffffffffff8111156101f457600080fd5b61020087828801610106565b9350506040610211878288016100f1565b925050606085013567ffffffffffffffff81111561022e57600080fd5b61023a87828801610106565b91505092959194509250565b60006102528383610317565b905092915050565b600061026582610429565b61026f818561044c565b93508360208202850161028185610419565b8060005b858110156102bd578484038952815161029e8582610246565b94506102a98361043f565b925060208a01995050600181019050610285565b50829750879550505050505092915050565b6102d881610480565b82525050565b60006102e982610434565b6102f3818561045d565b93506103038185602086016104b9565b61030c816104ec565b840191505092915050565b6000606083016000830151848203600086015261033482826102de565b9150506020830151848203602086015261034e82826102de565b9150506040830151848203604086015261036882826102de565b9150508091505092915050565b600060208201905061038a60008301846102cf565b92915050565b60006040820190506103a560008301856102cf565b81810360208301526103b7818461025a565b90509392505050565b6000604051905081810181811067ffffffffffffffff821117156103e357600080fd5b8060405250919050565b600067ffffffffffffffff82111561040457600080fd5b601f19601f8301169050602081019050919050565b6000819050602082019050919050565b600081519050919050565b600081519050919050565b6000602082019050919050565b600082825260208201905092915050565b600082825260208201905092915050565b60006104798261048a565b9050919050565b6000819050919050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b82818337600083830152505050565b60005b838110156104d75780820151818401526020810190506104bc565b838111156104e6576000848401525b50505050565b6000601f19601f8301169050919050565b6105068161046e565b811461051157600080fd5b5056fea2646970667358221220aa0ca3e6aa3a69ca8dc88fc9dcb511433b41b5915cbda21c1ccb0396a803c2c064736f6c634300060a0033"
    };

    public static final String SM_BINARY =
            org.fisco.bcos.sdk.utils.StringUtils.joinAll("", SM_BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {
        "[{\"inputs\":[{\"internalType\":\"string\",\"name\":\"name\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"version\",\"type\":\"string\"},{\"internalType\":\"address\",\"name\":\"_address\",\"type\":\"address\"},{\"internalType\":\"string\",\"name\":\"_abi\",\"type\":\"string\"}],\"name\":\"link\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"absolutPath\",\"type\":\"string\"}],\"name\":\"list\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"},{\"components\":[{\"internalType\":\"string\",\"name\":\"file_name\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"file_type\",\"type\":\"string\"},{\"internalType\":\"string\",\"name\":\"ext\",\"type\":\"string\"}],\"internalType\":\"struct BfsInfo[]\",\"name\":\"\",\"type\":\"tuple[]\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"absolutPath\",\"type\":\"string\"}],\"name\":\"mkdir\",\"outputs\":[{\"internalType\":\"int256\",\"name\":\"\",\"type\":\"int256\"}],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
    };

    public static final String ABI = org.fisco.bcos.sdk.utils.StringUtils.joinAll("", ABI_ARRAY);

    public static final String FUNC_LINK = "link";

    public static final String FUNC_LIST = "list";

    public static final String FUNC_MKDIR = "mkdir";

    protected BFSPrecompiled(String contractAddress, Client client, CryptoKeyPair credential) {
        super(getBinary(client.getCryptoSuite()), contractAddress, client, credential);
    }

    public static String getBinary(CryptoSuite cryptoSuite) {
        return (cryptoSuite.getCryptoTypeConfig() == CryptoType.ECDSA_TYPE ? BINARY : SM_BINARY);
    }

    public static String getABI() {
        return ABI;
    }

    public TransactionReceipt link(String name, String version, String address, String abi) {
        final Function function =
                new Function(
                        FUNC_LINK,
                        Arrays.<Type>asList(
                                new Utf8String(name),
                                new Utf8String(version),
                                new Address(address),
                                new Utf8String(abi)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void link(
            String name,
            String version,
            String _address,
            String _abi,
            TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_LINK,
                        Arrays.<Type>asList(
                                new Utf8String(name),
                                new Utf8String(version),
                                new Address(_address),
                                new Utf8String(_abi)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForLink(
            String name, String version, String _address, String _abi) {
        final Function function =
                new Function(
                        FUNC_LINK,
                        Arrays.<Type>asList(
                                new Utf8String(name),
                                new Utf8String(version),
                                new Address(_address),
                                new Utf8String(_abi)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple4<String, String, String, String> getLinkInput(
            TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_LINK,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Address>() {},
                                new TypeReference<Utf8String>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple4<String, String, String, String>(
                (String) results.get(0).getValue(),
                (String) results.get(1).getValue(),
                (String) results.get(2).getValue(),
                (String) results.get(3).getValue());
    }

    public Tuple1<BigInteger> getLinkOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_LINK,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public Tuple2<BigInteger, DynamicArray<BfsInfo>> list(String absolutPath)
            throws ContractException {
        final Function function =
                new Function(
                        FUNC_LIST,
                        Arrays.<Type>asList(
                                new org.fisco.bcos.sdk.codec.datatypes.Utf8String(absolutPath)),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Int256>() {},
                                new TypeReference<DynamicArray<BfsInfo>>() {}));
        List<Type> results = executeCallWithMultipleValueReturn(function);
        return new Tuple2<>(
                (BigInteger) results.get(0).getValue(),
                new DynamicArray<>(BfsInfo.class, (List<BfsInfo>) results.get(1).getValue()));
    }

    public String getSignedTransactionForList(String absolutPath) {
        final Function function =
                new Function(
                        FUNC_LIST,
                        Arrays.<Type>asList(new Utf8String(absolutPath)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public TransactionReceipt mkdir(String absolutPath) {
        final Function function =
                new Function(
                        FUNC_MKDIR,
                        Arrays.<Type>asList(new Utf8String(absolutPath)),
                        Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public void mkdir(String absolutPath, TransactionCallback callback) {
        final Function function =
                new Function(
                        FUNC_MKDIR,
                        Arrays.<Type>asList(new Utf8String(absolutPath)),
                        Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String getSignedTransactionForMkdir(String absolutPath) {
        final Function function =
                new Function(
                        FUNC_MKDIR,
                        Arrays.<Type>asList(new Utf8String(absolutPath)),
                        Collections.<TypeReference<?>>emptyList());
        return createSignedTransaction(function);
    }

    public Tuple1<String> getMkdirInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function =
                new Function(
                        FUNC_MKDIR,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<String>((String) results.get(0).getValue());
    }

    public Tuple1<BigInteger> getMkdirOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function =
                new Function(
                        FUNC_MKDIR,
                        Arrays.<Type>asList(),
                        Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results =
                this.functionReturnDecoder.decode(data, function.getOutputParameters());
        return new Tuple1<BigInteger>((BigInteger) results.get(0).getValue());
    }

    public static BFSPrecompiled load(
            String contractAddress, Client client, CryptoKeyPair credential) {
        return new BFSPrecompiled(contractAddress, client, credential);
    }

    public static BFSPrecompiled deploy(Client client, CryptoKeyPair credential)
            throws ContractException {
        return deploy(
                BFSPrecompiled.class,
                client,
                credential,
                getBinary(client.getCryptoSuite()),
                null,
                null,
                null);
    }

    public static class BfsInfo extends DynamicStruct {
        private String fileName;

        private String fileType;

        private List<String> ext;

        public BfsInfo(Utf8String fileName, Utf8String fileType, DynamicArray<Utf8String> ext) {
            super(fileName, fileType, ext);
            this.fileName = fileName.getValue();
            this.fileType = fileType.getValue();
            this.ext =
                    ext.getValue().stream().map(Utf8String::getValue).collect(Collectors.toList());
        }

        public BfsInfo(String fileName, String fileType, List<String> ext) {
            super(
                    new Utf8String(fileName),
                    new Utf8String(fileType),
                    new DynamicArray<>(
                            Utf8String.class,
                            ext.stream().map(Utf8String::new).collect(Collectors.toList())));
            this.fileName = fileName;
            this.fileType = fileType;
            this.ext = ext;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public List<String> getExt() {
            return ext;
        }

        public void setExt(List<String> ext) {
            this.ext = ext;
        }

        @Override
        public String toString() {
            return "BfsInfo{"
                    + "fileName='"
                    + fileName
                    + '\''
                    + ", fileType='"
                    + fileType
                    + '\''
                    + ", ext='"
                    + ext
                    + '\''
                    + '}';
        }
    }
}
